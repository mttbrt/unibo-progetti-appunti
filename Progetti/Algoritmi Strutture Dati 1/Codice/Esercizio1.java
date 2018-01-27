import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Esercizio1 {
   
   private static String fileInput;
   private static String fileOutput;
   
   public static void main(String[] args) {
      RedBlackTree balancedTree = new RedBlackTree();
      
      if(args.length < 2) {
         System.out.println("Immettere file di input e di output come argomenti.");
         System.exit(0);
      }
      
      // Input
      fileInput = args[0];
      BufferedReader inputStream = null;
      FileReader reader = null;
      // Output
      fileOutput = args[1];
      PrintWriter outputStream = null;
      FileOutputStream writer = null;
      
      try {
         reader = new FileReader(fileInput);
         inputStream = new BufferedReader(reader);
      } catch (FileNotFoundException e) {
         System.out.println("FileNotFoundException nello Stream Input.");
         System.exit(0);
      }
      
      try {
         writer = new FileOutputStream(fileOutput, true);
         outputStream = new PrintWriter(writer);
      } catch (FileNotFoundException e) {
         System.out.println("FileNotFoundException nello Stream Output.");
         System.exit(0);
      }
      
      try {
         outputStream.println("nNodiTree \tN istr \tnNodiList \tN istr \t\tTipo di Op. \t\t\tEsito");
         String row = inputStream.readLine();
         
         while (row != null) {
            String[] data = row.split(" ");
            
            switch(data[0]) {
               case "I":   
                  RedBlackTree.resetElemOp();
                  balancedTree.insert(data[1], data[2]);
                  outputStream.println(String.format("%02d", RedBlackTree.dimension()) + 
                          "\t\t\t\t|" + String.format("%02d", RedBlackTree.getElemOp()) + 
                          "\t\t|" + String.format("%02d", RedBlackTree.getLinkedListElements()) +
                          "\t\t\t|" + String.format("%02d", RedBlackTree.getLinkedListOp()) +
                          " \t\t\t|Inserimento \t\t\t|Riuscito");
                  break;
                  
               case "C":  
                  RedBlackTree.resetElemOp();
                  boolean deletion = balancedTree.delete(data[1], data[2]);
                  outputStream.println(String.format("%02d", RedBlackTree.dimension()) + 
                          "\t\t\t\t|" + String.format("%02d", RedBlackTree.getElemOp()) + 
                          "\t\t|" + String.format("%02d", RedBlackTree.getLinkedListElements()) +
                          "\t\t\t|" + String.format("%02d", RedBlackTree.getLinkedListOp()) +
                          " \t\t\t|Cancellazione \t\t" + 
                          (deletion ? "|Riuscita" : "|Non Riuscita"));
                  break;
                  
               case "R":   
                  RedBlackTree.resetElemOp();
                  int search = balancedTree.search(data[1], data[2]);
                  String result;
                  
                  if (search > 0)
                     result = "|OK " + search;
                  else if (search == 0)
                     result = "|Luogo NotFound";
                  else
                     result = "|ID NotFound";
                                    
                  outputStream.println(String.format("%02d", RedBlackTree.dimension()) + 
                          "\t\t\t\t|" + String.format("%02d", RedBlackTree.getElemOp()) + 
                          "\t\t|" + String.format("%02d", RedBlackTree.getLinkedListElements()) +
                          "\t\t\t|" + String.format("%02d", RedBlackTree.getLinkedListOp()) +
                          " \t\t\t|Ricerca \t\t\t\t" + result);
                  break;
            }
            
            row = inputStream.readLine(); // Legge una nuova riga
         }

      } catch (IOException e) {
         System.out.println("Errore Lettura " + fileInput);
      }
      
      balancedTree.printTree();
         
      // Chiusura streams
      try {
         outputStream.close();
         inputStream.close();
      } catch (IOException ex) {
         System.out.println("IOException nella Chiusura Streams.");
      }
   }

   
}

