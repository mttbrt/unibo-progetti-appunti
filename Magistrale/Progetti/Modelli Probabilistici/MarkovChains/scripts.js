// 1 classe, periodo 3, ricorrente/massimale, irriducibile
// [0, 0, 0.5, 0.25, 0.25, 0, 0],
// [0, 0, 0.333, 0, 0.666, 0, 0],
// [0, 0, 0, 0, 0, 0.333, 0.666],
// [0, 0, 0, 0, 0, 0.5, 0.5],
// [0, 0, 0, 0, 0, 0.75, 0.25],
// [0.5, 0.5, 0, 0, 0, 0, 0],
// [0.25, 0.75, 0, 0, 0, 0, 0]

// 2 classi, periodo 2, transiente + ricorrente/massimale, NON irriducibile
// [0, 0.5, 0, 0.5],
// [0.5, 0, 0.5, 0],
// [0, 0, 0, 1],
// [0, 0, 1, 0]

// Prof
// [0, 0, 0, 0.5, 0, 0.5, 0, 0],
// [0.125, 0, 0, 0, 0.5, 0.375, 0, 0],
// [0, 0, 0, 0.25, 0, 0.75, 0, 0],
// [0, 0, 0, 0, 0.33, 0, 0.66, 0],
// [0.2, 0, 0.8, 0, 0, 0, 0, 0],
// [0, 0, 0, 0, 0.6, 0, 0.4, 0],
// [0.66, 0, 0.33, 0, 0, 0, 0, 0],
// [0.25, 0, 0.25, 0, 0.125, 0, 0, 0.375]

var matrix = [
  [0.25, 0.75, 0, 0, 0],
  [0.5, 0.5, 0, 0, 0],
  [0, 0, 1, 0, 0],
  [0, 0, 0.33, 0.66, 0],
  [1, 0, 0, 0, 0]
];
var I = [], A = [], C = [], classes = [], paths = [];

$(document).ready(function() {
  if($('#data textarea').val() !== '')
    matrix = JSON.parse("[" + $('#data textarea').val() + "]");
  else
    $('#data textarea').val("[0.25, 0.75, 0, 0, 0],\n[0.5, 0.5, 0, 0, 0],\n[0, 0, 1, 0, 0],\n[0, 0, 0.33, 0.66, 0],\n[1, 0, 0, 0, 0]");

  // Creazione archi e nodi da matrice
  var nodesValues = [], edgesValues = [];
  for(var i = 0; i < matrix.length; i++) {
    nodesValues.push({id: (i + 1), label: '' + (i + 1), heightConstraint: { minimum: 30 }, font: {size:18, color:'#e0e0e0', face:'arial', align: 'centre'}, margin: { top: 0, right: 0, bottom: 0, left: 10 }, shape: 'circle', color:'#d64242', physics:false});
    for(var j = 0; j < matrix[i].length; j++)
      if(matrix[i][j] != 0)
        edgesValues.push({from: (i + 1), to: (j + 1), arrows:'to', color:'#d33030', label: '' + matrix[i][j], font: {size:12, color:'#e0e0e0', strokeWidth: 0, face:'arial', align: 'horizontal'}});
  }

  // Creazione matrice identità da matrice di transizione
  for (var i = 0; i < matrix.length; i++) {
    var row = [];
    for (var j = 0; j < matrix[i].length; j++)
      row.push((i == j ? 1 : 0));
    I.push(row);
  }

  // Creazione matrice A
  for (var i = 0; i < matrix.length; i++) {
    var row = [];
    for (var j = 0; j < matrix[i].length; j++)
      row.push((matrix[i][j] != 0 ? 1 : 0));
    A.push(row);
  }

  // Somma booleana tra I e A
  for (var i = 0; i < A.length; i++) {
    var row = [];
    for (var j = 0; j < A[i].length; j++)
      row.push((A[i][j] || I[i][j]));
    C.push(row);
  }

  for (var k = 2; k <= matrix.length; k++) {   // Somma booleana C = I + A + A^1 + ... + A^|S|
    var pow = matrixPower(A, k);

    for (var i = 0; i < pow.length; i++) {
      var row = [];
      for (var j = 0; j < pow[i].length; j++)
        row.push((pow[i][j] || C[i][j] ? 1 : 0));
      C[i] = row;
    }
  }
  $(".results").html($(".results").html() + "<h3>Matrice di Comunicazione</h3>" + printMatrix(C));

  for(var i = 0; i < C.length; i++) {
    var row = [];
    for(var j = 0; j < C[i].length; j++)
      if(C[i][j] == 1 && C[j][i] == 1)
        row.push(j+1);
    classes.push(row);
  }
  classes = removeDuplicates(classes);
  $(".results").html($(".results").html() + "<h3>Classi di equivalenza</h3>");

  // Compute all paths for each class
  for(var i = 0; i < classes.length; i++) { // Each class
    paths.push([]);
    var recurrent = true;
    for(var p = 0; p < classes[i].length; p++) { // Each node in each class
      var node = classes[i][p] - 1;
      var isVisited = [];
      for(var j = 0; j < A.length; j++) {
        isVisited[j] = false;

        // Calcolo ricorrenza/transienza
        if(matrix[node][j] != 0 && classes[i].indexOf((j+1)) == -1) // Se il nodo si collega ad un altro, il quale non è in questa classe, lo stato è transiente
          recurrent = false;
      }
      getPaths(node, node, isVisited, [node]);
    }
    pathsToArrays(i);
    cleanPaths(i);

    var pathLengths = [];
    for(var k = 0; k < paths[i].length; k++)
      pathLengths.push(paths[i][k].length - 1);

    var cl = "<h4>{ <strong>";
    for(var l = 0; l < classes[i].length; l++)
      cl += classes[i][l] + (l < classes[i].length-1 ? ", " : "");
    cl += "</strong> }</h4>";
    $(".results").html($(".results").html() + cl);
    var period = GCD(pathLengths);
    $(".results").html($(".results").html() + "Periodo: <strong>" + (period == 1 ? period + "</strong> (aperiodica)" : (period == 0 ? "</strong> non definito" : period + "</strong> (periodica)")) + "<br/>");
    $(".results").html($(".results").html() + "Classe: <strong>" + (recurrent ? "ricorrente - massimale" : "transiente") + "</strong><br/><br/>");
  }

  if(classes.length == 1)
    $(".results").html($(".results").html() + "La catena di Markov &egrave; <strong>irriducibile</strong>." + "<br/>");

  var network = new vis.Network(document.getElementById('mynetwork'), {
      nodes: new vis.DataSet(nodesValues),
      edges: new vis.DataSet(edgesValues)
  }, {});
});

function matrixPower(a, p) {
  var m = a;
  for(var i = 1; i < p; i++)
    m = multiplyMatrices(m, a);
  return m;
}

function multiplyMatrices(a, b) {
  var aNumRows = a.length, aNumCols = a[0].length,
      bNumRows = b.length, bNumCols = b[0].length,
      m = new Array(aNumRows);
  for (var r = 0; r < aNumRows; ++r) {
    m[r] = new Array(bNumCols);
    for (var c = 0; c < bNumCols; ++c) {
      m[r][c] = 0;
      for (var i = 0; i < aNumCols; ++i)
        m[r][c] += a[r][i] * b[i][c];
    }
  }
  return m;
}

function removeDuplicates(a) {
  for(var i = 0; i < a.length; i++)
    if(a[i] != null)
      for(var j = 0; j < a.length; j++)
        if(i != j && arraysEqual(a[i], a[j]))
          a[j] = null;

  var k = 0;
  while(true) {
    if(k == a.length) break;
    if(a[k] == null) a.splice(k, 1);
    else k++;
  }

  return a;
}

function arraysEqual(a, b) {
  if(a === b) return true;
  if(a == null || b == null) return false;
  if(a.length != b.length) return false;

  for(var i = 0; i < a.length; i++)
    if(a[i] !== b[i])
      return false;
  return true;
}

function getPaths(u, d, isVisited, localPathList) {
  if(u == d)
    paths[paths.length - 1].push(localPathList.toString());
  else
    isVisited[u] = true;

  for(i in A[u]) // Recur for all the vertices adjacent to current vertex
    if(A[u][i] == 1 && i != u && !isVisited[i]) {
      localPathList.push(parseInt(i)); // store current node in path[]
      getPaths(i, d, isVisited, localPathList);
      localPathList.splice(localPathList.indexOf(i), 1); // remove current node in path[]
    }
  isVisited[u] = false;
}

function pathsToArrays(index) {
  var temp = [];
  for(var i = 0; i < paths[index].length; i++) {
    var splitted = paths[index][i].split(",");
    for(var j = 0; j < splitted.length; j++)
      splitted[j] = parseInt(splitted[j]);
    temp.push(splitted);
  }
  paths[index] = temp;
}

function cleanPaths(index) {
  var valids = paths;
  for (var i = 0; i < paths[index].length; i++)
    if(paths[index][i].length == 1 && A[paths[index][i][0]][paths[index][i][0]] == 1)
      valids[index][i].push(paths[index][i][0]);
  paths = valids;
}

function GCD(array) {
  var result = array[0];
  for(var i = 1; i < array.length; i++) result = gcd(array[i], result);
  return result;
}

function gcd(a, b) {
  if(!b) return a;
  return gcd(b, a % b);
}

function printMatrix(a) {
  var print = "";
  for (var i = 0; i < a.length; i++) {
    var row = "";
    for (var j = 0; j < a[i].length; j++)
      row += a[i][j] + (j < a[i].length-1 ? "&#8194;&#8194;&#8194;" : "");
    print += row + "<br/>"
  }
  return print;
}

function printClasses(c) {
  var print = "";
  for (var i = 0; i < c.length; i++) {
    var row = "{";
    for (var j = 0; j < c[i].length; j++)
      row += c[i][j] + (j != c[i].length - 1 ? ", " : "}");
    print += row + "<br/>"
  }
  return print;
}
