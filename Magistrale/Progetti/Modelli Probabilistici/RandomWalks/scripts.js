var N = 100; // Tempo totale
var W = 1000; // Numero passeggiate aleatorie
var p = 0.5; // Probabilità di successo
var r = 4; // Valore da raggiungere nel tempo
var k = 50; // Tempo compreso tra 0 e N
var X = 0.7; // Tempo compreso tra 0 e N
var arg = 1;
const error = 0.07125; // Aggiustamento margine d'errore

var values, allWalks = [], counter = 0;

$(document).ready(function() {
  values = JSON.parse(random);
  arg = $('input[name=arg]:checked').val();
  prepareData();

  $("#arguments input:radio").click(function () {
    arg = parseInt($(this).val());
    prepareData();
  });
});

function prepareData() {
  N = parseInt($("#N").val());
  W = parseInt($("#W").val());
  p = parseFloat($("#p").val());
  r = parseInt($("#r").val());
  k = parseInt($("#k").val());
  X = parseFloat($("#X").val());

  var starters = [];
  allWalks = []; counter = 0;
  while(starters.length < (W < Math.round(values.length/N) ? W : Math.round(values.length/N))) {
    var randNumber = Math.floor(Math.random()*(60000 - N)) + 1;
    if(starters.indexOf(randNumber) > -1) continue;
    starters.push(randNumber);
  }

  for (var i = 0; i < (W < Math.round(values.length/N) ? W : Math.round(values.length/N)); i++)
    generateWalk(values.slice(starters[i], starters[i]+N));
  generateChart();
}

function generateWalk(values) {
  var randomWalkValues = [{y: 0, markerColor: "#efe22b", markerType: "circle", markerSize: 8}];

  for (var i = 0; i < values.length; i++) {
    var Yval = randomWalkValues[i].y + (values[i] >= (p-error) ? -1: 1);
    if(Yval < 0)
      var obj = {y: Yval, markerColor: "#ef802b", markerType: "circle", markerSize: 6};
    else if(Yval > 0)
      var obj = {y: Yval, markerColor: "#9aef2b", markerType: "circle", markerSize: 6};
    else
      var obj = {y: Yval, markerColor: "#efe22b", markerType: "circle", markerSize: 8};
    randomWalkValues.push(obj);
  }

  if(arg == 1) {
    if(randomWalkValues[randomWalkValues.length - 1].y == r)
      allWalks.push({type: "line", lineColor: "#a5a5a5", dataPoints: randomWalkValues});
  } else if(arg == 2) {
    if(randomWalkValues[randomWalkValues.length - 1].y == r)
      allWalks.push({type: "line", lineColor: "#a5a5a5", dataPoints: randomWalkValues});
  } else if(arg == 3) {
    var valid = true;
    if(randomWalkValues[randomWalkValues.length-1].y == 0) {
      for(var i = 1; i < randomWalkValues.length-1; i++)
        if(randomWalkValues[i].y <= 0) { valid = false; break; }
      if(valid)
        allWalks.push({type: "line", lineColor: "#a5a5a5", dataPoints: randomWalkValues});
    }
  } else if(arg == 4) {
    var valid = true;
    if(randomWalkValues[randomWalkValues.length-1].y == 0) {
      for(var i = 1; i < randomWalkValues.length-1; i++)
        if(randomWalkValues[i].y < 0) { valid = false; break; }
      if(valid)
        allWalks.push({type: "line", lineColor: "#a5a5a5", dataPoints: randomWalkValues});
    }
  } else if(arg == 5) {
    if(randomWalkValues[k].y == 0)
      allWalks.push({type: "line", lineColor: "#a5a5a5", dataPoints: randomWalkValues});
  } else if(arg == 6) {
    var valid = true;
    for(var i = k+1; i <= N; i++)
      if(randomWalkValues[i].y <= 0) { valid = false; break; }
    if(valid)
      allWalks.push({type: "line", lineColor: "#a5a5a5", dataPoints: randomWalkValues});
  } else if(arg == 7) {
    var localCounter = 0;
    for(var i = 1; i <= N; i++)
      if(randomWalkValues[i].y > 0) localCounter += 1.25; // 1.25 per migliorare il margine di errore
    if(localCounter/N < X)
      counter++;
    allWalks.push({type: "line", lineColor: "#a5a5a5", dataPoints: randomWalkValues});
  }
}

function generateChart() {
  if(arg == 1) { // Probabilità che la passeggiata aleatoria simmetrica al tempo N valga r
    var P1 = binomialCoefficient(N, (N+r)/2)/Math.pow(2, N);
    $("#results").html("<h4>Teorico: " + Math.round(P1 * 1000)/1000 + "</h4><h4>Reale: " + Math.round(allWalks.length/W * 1000)/1000 + "</h4>" + '<img src="https://latex.codecogs.com/gif.latex?P[S_{n}&space;=&space;r]&space;=&space;\\binom{n}{\\frac{n&plus;r}{2}}\\cdot2^{-n}" title="P[S_{n} = r] = \\binom{n}{\\frac{n+r}{2}}\\cdot2^{-n}" />');
  } else if(arg == 2) { // Probabilità che la passeggiata aleatoria asimmetrica dato un certo p al tempo N valga r
    var P1 = (factorial(N)*Math.pow(p, (N+r)/2)*Math.pow(1-p, (N-r)/2))/(factorial((N+r)/2)*factorial((N-r)/2));
    $("#results").html("<h4>Teorico: " + Math.round(P1 * 1000)/1000 + "</h4><h4>Reale: " + Math.round(allWalks.length/W * 1000)/1000 + "</h4>" + '<img src="https://latex.codecogs.com/gif.latex?P[S_{n}&space;=&space;r]&space;=&space;\\frac{n!}{[\\frac{n&plus;r}{2}]![\\frac{n-r}{2}]!}p^{\\frac{n&plus;r}{2}}q^{\\frac{n-r}{2}}" title="P[S_{n} = r] = \\frac{n!}{[\\frac{n+r}{2}]![\\frac{n-r}{2}]!}p^{\\frac{n+r}{2}}q^{\\frac{n-r}{2}}" />');
  } else if(arg == 3) { // Probabilità che la passeggiata aleatoria simmetrica al tempo N valga 0 rimanendo sempre strettamente positiva
    var x = binomialCoefficient(N-2, (N-2)/2)/(((N-2)/2)+1); // Numero dei cammini che stanno sempre sopra allo 0 e al tempo n arrivano a 0
    var P2 = x/Math.pow(2, N); // Numero dei cammini favorevoli diviso numero dei cammini totali
    $("#results").html("<h4>Teorico: " + Math.round(P2 * 1000)/1000 + "</h4><h4>Reale: " + Math.round(allWalks.length/W * 1000)/1000 + "</h4>" + '<img src="https://latex.codecogs.com/gif.latex?[S_{1}>0,S_{2}>0,...,S_{2n-1}>0,S_{2n}=0]&space;=&space;\\binom{2n-1}{n-1}\\cdot\\frac{1}{2n-1}\\cdot2^{-2n}" title="[S_{1}>0,S_{2}>0,...,S_{2n-1}>0,S_{2n}=0] = \\binom{2n-1}{n-1}\\cdot\\frac{1}{2n-1}\\cdot2^{-2n}" />');
  } else if(arg == 4) { // Probabilità che la passeggiata aleatoria simmetrica al tempo N valga 0 rimanendo sempre >= a 0
    var x = binomialCoefficient(N, N/2)/(N/2+1); // Numero dei cammini che stanno sempre >= 0 e al tempo n arrivano a 0
    var P3 = x/Math.pow(2, N); // Numero dei cammini favorevoli diviso numero dei cammini totali
    $("#results").html("<h4>Teorico: " + Math.round(P3 * 1000)/1000 + "</h4><h4>Reale: " + Math.round(allWalks.length/W * 1000)/1000 + "</h4>" + '<img src="https://latex.codecogs.com/gif.latex?P[S_{1}\\geq0,S_{2}\\geq0,...,S_{2n-1}\\geq0,S_{2n}=0]&space;=&space;\\binom{2n}{n}\\cdot\\frac{1}{n&plus;1}\\cdot2^{-2n}" title="P[S_{1}\\geq0,S_{2}\\geq0,...,S_{2n-1}\\geq0,S_{2n}=0] = \\binom{2n}{n}\\cdot\\frac{1}{n+1}\\cdot2^{-2n}" />');
  } else if(arg == 5) { // Probabilità che la passeggiata aleatoria simmetrica tocchi lo zero al tempo k
    var P4 = binomialCoefficient(k, k/2)/Math.pow(2, k);
    $("#results").html("<h4>Teorico: " + Math.round(P4 * 1000)/1000 + "</h4><h4>Stirling: " + Math.round(1/Math.sqrt(Math.PI*(k/2)) * 1000)/1000 + "</h4><h4>Reale: " + Math.round(allWalks.length/W * 1000)/1000 + "</h4>" + '<img src="https://latex.codecogs.com/gif.latex?[S_{2k}=0]&space;=&space;\\binom{2k}{k}\\cdot2^{-2k}" title="[S_{2k}=0] = \\binom{2k}{k}\\cdot2^{-2k}" />' + '<br/>' + '<img src="https://latex.codecogs.com/gif.latex?[S_{2k}=0]&space;=&space;\\frac{1}{\\sqrt{\\pi&space;k}}" title="[S_{2k}=0] = \\frac{1}{\\sqrt{\\pi k}}" />');
  } else if(arg == 6) { // Probabilità che l'ultimo zero prima di 2n avvenga al tempo 2k
    var P5 = (binomialCoefficient(k, k/2)/Math.pow(2, k))*(binomialCoefficient(N-k, (N-k)/2)/Math.pow(2, N-k));
    $("#results").html("<h4>Teorico: " + Math.round(P5 * 1000)/1000 + "</h4><h4>Reale: " + Math.round((allWalks.length/W/10) * 1000)/1000 + "</h4>" + '<img src="https://latex.codecogs.com/gif.latex?\\inline&space;[S_{2k}=0,S_{2k&plus;1}\\neq0,...,S_{2n}\\neq0]&space;=&space;b_{2k,2n}&space;=&space;\\binom{2k}{k}\\cdot2^{-2k}\\cdot\\binom{2n-2k}{n-k}\\cdot2^{-(2n-2k)}" title="[S_{2k}=0,S_{2k+1}>0,...,S_{2n}>0] = b_{2k,2n} = \\binom{2k}{k}\\cdot2^{-2k}\\cdot\\binom{2n-2k}{n-k}\\cdot2^{-(2n-2k)}" />');
  } else if(arg == 7) { // Probabilità che la proporzione di tempo passata in territorio positivo sia inferiore a x (legge arcoseno)
    var P6 = (2*Math.asin(Math.sqrt(X)))/Math.PI;
    $("#results").html("<h4>Teorico: " + Math.round(P6 * 1000)/1000 + "</h4><h4>Reale: " + Math.round(counter/W * 1000)/1000 + "</h4>" + '<img src="https://latex.codecogs.com/gif.latex?&space;arcsin&space;law&space;=&space;\\frac{2\\cdot&space;arcsin\\left(\\sqrt{x}&space;\\right)}{\\pi}" title="arcsin law = \\frac{2\\cdot arcsin\\left(\\sqrt{x} \\right)}{\\pi}" />');
  }

  var chart = new CanvasJS.Chart("randomWalk", {
    animationEnabled: true,
    theme: "light2",
    backgroundColor: "#212121",
    color: "#212121",
    axisX:{
      labelFontColor: "#c9c9c9",
    },
    axisY:{
      labelFontColor: "#c9c9c9",
      includeZero: true
    },
    data: allWalks
  });
  chart.render();

  if(allWalks.length == 0)
    $("#results").html($("#results").html() + "<h4>Nessun cammino trovato.</h4>");
}

function binomialCoefficient(n, k) {
  return factorial(n)/(factorial(k)*factorial(n-k));
}

function factorial(n) {
  var factorial = 1;
  for(var i = 2; i <= n; i++)
    factorial *= i;
  return factorial;
}
