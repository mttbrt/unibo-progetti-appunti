<?php

class Comparison {
  private const UNMODIFIED = 0;
  private const DELETED = 1;
  private const INSERTED = 2;

  private static $fileA = "A";
  private static $fileB = "B";

  public static function compareFiles($path1, $path2) {
    $splittedA = explode('/', rtrim($path1, '/'));
    self::$fileA = array_pop($splittedA);
    $splittedB = explode('/', rtrim($path2, '/'));
    self::$fileB = array_pop($splittedB);

    return self::compare(file_get_contents($path1), file_get_contents($path2));
  }

  // Compares two files given as strings
  private static function compare($string1, $string2) {
    $start = 0;
    $sequence1 = preg_split('/\R/', $string1);
    $end1 = count($sequence1) - 1;
    $sequence2 = preg_split('/\R/', $string2);
    $end2 = count($sequence2) - 1;

    // Skip any common prefix and suffix
    while ($start <= $end1 && $start <= $end2 && $sequence1[$start] == $sequence2[$start])
      $start++;
    while ($end1 >= $start && $end2 >= $start && $sequence1[$end1] == $sequence2[$end2]) {
      $end1--;
      $end2--;
    }

    $table = self::computeTable($sequence1, $sequence2, $start, $end1, $end2);
    $partialDiff = self::generatePartialDiff($table, $sequence1, $sequence2, $start);

    // Compute the full diff
    $diff = array();
    for ($index = 0; $index < $start; $index++)
      $diff[] = array($sequence1[$index], self::UNMODIFIED);
    while (count($partialDiff) > 0)
      $diff[] = array_pop($partialDiff);
    for ($index = $end1 + 1; $index < ($compareCharacters ? strlen($sequence1) : count($sequence1)); $index++)
      $diff[] = array($sequence1[$index], self::UNMODIFIED);

    return $diff;
  }

  // Computes the table of longest common subsequence lengths for the specified sequences
  private static function computeTable($sequence1, $sequence2, $start, $end1, $end2){
    $length1 = $end1 - $start + 1;
    $length2 = $end2 - $start + 1;
    $table = array(array_fill(0, $length2 + 1, 0)); // Initialise the table

    for ($index1 = 1; $index1 <= $length1; $index1++) {
      $table[$index1] = array(0); // Create row
      for ($index2 = 1; $index2 <= $length2; $index2++) // In each field store the longest common subsequence length
        if ($sequence1[$index1 + $start - 1] == $sequence2[$index2 + $start - 1])
          $table[$index1][$index2] = $table[$index1-1][$index2-1]+1;
        else
          $table[$index1][$index2] = max($table[$index1-1][$index2], $table[$index1][$index2-1]);
    }
    return $table;
  }

  // Computes the partial diff for the specificed sequences, in reverse order
  private static function generatePartialDiff($table, $sequence1, $sequence2, $start){
    $diff = array();
    $index1 = count($table) - 1;
    $index2 = count($table[0]) - 1;

    while ($index1 > 0 || $index2 > 0)
      if ($index1 > 0 && $index2 > 0 && $sequence1[$index1 + $start - 1] == $sequence2[$index2 + $start - 1]) { // Check what has happened to the items at these indices
        $diff[] = array($sequence1[$index1 + $start - 1], self::UNMODIFIED);
        $index1--;
        $index2--;
      } else if ($index2 > 0 && $table[$index1][$index2] == $table[$index1][$index2 - 1]) {
        $diff[] = array($sequence2[$index2 + $start - 1], self::INSERTED);
        $index2--;
      } else {
        $diff[] = array($sequence1[$index1 + $start - 1], self::DELETED);
        $index1--;
      }

    return $diff;
  }

  // Returns a diff as an HTML table
  public static function toTable($diff, $indentation = '', $separator = '<br/>') {
    $html = $indentation.
            "<table class=\"diff\">\n"
            ."<tr> <td class='file-name'>"
            .self::$fileA
            ."</td> <td class='file-name'>"
            .self::$fileB
            ."</td> </tr>";

    $index = 0;
    while ($index < count($diff)) {
      switch ($diff[$index][1]) { // Line type
        case self::UNMODIFIED:
          $leftCell = self::getCellContent($diff, $indentation, $separator, $index, self::UNMODIFIED);
          $rightCell = $leftCell;
          break;
        case self::DELETED:
          $leftCell = self::getCellContent($diff, $indentation, $separator, $index, self::DELETED);
          $rightCell = self::getCellContent($diff, $indentation, $separator, $index, self::INSERTED);
          break;
        case self::INSERTED:
          $leftCell = '';
          $rightCell = self::getCellContent($diff, $indentation, $separator, $index, self::INSERTED);
          break;
      }

      $html .=
          $indentation
          ."  <tr>\n"
          .$indentation
          .'    <td class="diff'
          .($leftCell == $rightCell ? 'Unmodified' : ($leftCell == '' ? 'Blank' : 'Deleted'))
          .'">'
          .$leftCell
          ."</td>\n"
          .$indentation
          .'    <td class="diff'
          .($leftCell == $rightCell ? 'Unmodified' : ($rightCell == '' ? 'Blank' : 'Inserted'))
          .'">'
          .$rightCell
          ."</td>\n"
          .$indentation
          ."  </tr>\n";
    }

    return $html.$indentation."</table>\n";
  }

  // Returns the content of the cell
  private static function getCellContent($diff, $indentation, $separator, &$index, $type){
    $html = '';

    while ($index < count($diff) && $diff[$index][1] == $type) {
      $html .= '<span>'.htmlspecialchars($diff[$index][0]).'</span>'.$separator;
      $index++;
    }

    return $html;
  }

}

?>
