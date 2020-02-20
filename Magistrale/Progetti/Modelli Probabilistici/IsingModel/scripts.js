var Xpix = 64, Ypix = 64;
var J = 0.5, sigma = 0.5;
var O = [], D = [], I = [], M = [];
var max = 0;
var cellSize = 2;

$(document).ready(function() {
  setupMatrix(parseInt($('#type').find(":selected").text()));
  drawMatrix(O, 1);
  disturbeImage();
  drawMatrix(D, 2);
  drawMatrix(I, 3);
  drawMatrix(M, 4);
  executeAlgorithm();

  window.setInterval(function() {
    executeAlgorithm();
    drawMatrix(I, 3);
    drawMatrix(M, 4);
  }, 500);

  function disturbeImage() {
    for (var i = 0; i < O.length; i++) {
      D.push([]);
      for (var j = 0; j < O[i].length; j++)
        D[i].push(O[i][j] + (O[i][j] > sigma ? -Math.random()*sigma : Math.random()*sigma));
    }
  }

  function executeAlgorithm() {
    for (var k = 0; k < Xpix*Ypix*10; k++) {
      var ix = Math.floor(Math.random()*Xpix), iy = Math.floor(Math.random()*Ypix); // Select a random pixel
      var px = I[iy][ix]; // pixel
      var pxi = 1-I[iy][ix]; // pixel inverse

      // Remove those outside picture and count pixels which disagree with the selected
      var disagreePX = 0, disagreePXI = 0;

      if(iy-1 >= 0) { // pixel sopra
        if(I[iy-1][ix] != px) disagreePX++;
        if(I[iy-1][ix] != pxi) disagreePXI++;
      }

      if(iy+1 < Ypix) { // pixel sotto
        if(I[iy+1][ix] != px) disagreePX++;
        if(I[iy+1][ix] != pxi) disagreePXI++;
      }

      if(ix-1 >= 0) { // pixel a sinistra
        if(I[iy][ix-1] != px) disagreePX++;
        if(I[iy][ix-1] != pxi) disagreePXI++;
      }

      if(ix+1 < Xpix) { // pixel a destra
        if(I[iy][ix+1] != px) disagreePX++;
        if(I[iy][ix+1] != pxi) disagreePXI++;
      }

      var alpha = Math.pow(Math.E, (D[iy][ix]*(pxi-px))/Math.pow(sigma, 2))*Math.pow(Math.E, 2*J*(disagreePX-disagreePXI));
      if(Math.random()/6 < alpha)
        I[iy][ix] = pxi;

      if(k % 10 == 0) {
        for (var i = 0; i < M.length; i++)
          for (var j = 0; j < M[i].length; j++) {
            M[i][j] += I[i][j];
            if(M[i][j] > max) max = M[i][j];
          }
      }
    }
  }

  function drawMatrix(matrix, cont) {
    var html = '<table style="border:1px solid black; margin: 10px auto;">';
    for (var i = 0; i < matrix.length; i++) {
      html += '<tr>';
      for (var j = 0; j < matrix[i].length; j++) {
        var cell = matrix[i][j];
        if(cont == 4) {
          cell /= max;
          if($("#sat").is(':checked') && cell < 0.925) cell = 0; // Per togliere le sfumature e fare solo nero o bianco
        }
        var hex = (Math.round(cell*255)).toString(16);
        html += '<td width=' + cellSize + 'px height=' + cellSize + 'px bgcolor="#' + hex + '' + hex + '' + hex + '"></td>';
      }
      html += '</tr>';
    }
    html += '</table>';
    $('#container' + cont).html(html);
  }

  function setupMatrix(type) {
    O = [], I = [], M = [];
    for (var i = 0; i < Xpix; i++) {
      I.push([]);
      M.push([]);
      O.push([]);
      for (var j = 0; j < Ypix; j++) {
        I[i].push(1);
        M[i].push(1);
        if(type === 1) // Croce
          O[i].push(j == i || j+1 == i || j-1 == i || j+2 == i || j-2 == i || j+3 == i || j-3 == i || j+4 == i || j-4 == i ||
                    j == (Xpix-i) || j+1 == (Xpix-i) || j-1 == (Xpix-i) || j+2 == (Xpix-i) || j-2 == (Xpix-i) || j+3 == (Xpix-i) || j-3 == (Xpix-i) || j+4 == (Xpix-i) || j-4 == (Xpix-i)
                    ? 0 : 1);
        else if(type === 2) // Rombo
          O[i].push(i == Math.round(Xpix/2)-j || i+1 == Math.round(Xpix/2)-j || i-1 == Math.round(Xpix/2)-j || i+2 == Math.round(Xpix/2)-j || i-2 == Math.round(Xpix/2)-j || i+3 == Math.round(Xpix/2)-j || i-3 == Math.round(Xpix/2)-j || i+4 == Math.round(Xpix/2)-j || i-4 == Math.round(Xpix/2)-j ||
                    i == Math.round(Xpix/2)+j || i+1 == Math.round(Xpix/2)+j || i-1 == Math.round(Xpix/2)+j || i+2 == Math.round(Xpix/2)+j || i-2 == Math.round(Xpix/2)+j || i+3 == Math.round(Xpix/2)+j || i-3 == Math.round(Xpix/2)+j || i+4 == Math.round(Xpix/2)+j || i-4 == Math.round(Xpix/2)+j ||
                    j == Math.round(Xpix/2)+i || j+1 == Math.round(Xpix/2)+i || j-1 == Math.round(Xpix/2)+i || j+2 == Math.round(Xpix/2)+i || j-2 == Math.round(Xpix/2)+i || j+3 == Math.round(Xpix/2)+i || j-3 == Math.round(Xpix/2)+i || j+4 == Math.round(Xpix/2)+i || j-4 == Math.round(Xpix/2)+i ||
                    j == Math.round(Xpix/2)+Xpix-i-2 || j+1 == Math.round(Xpix/2)+Xpix-i-2 || j-1 == Math.round(Xpix/2)+Xpix-i-2 || j+2 == Math.round(Xpix/2)+Xpix-i-2 || j-2 == Math.round(Xpix/2)+Xpix-i-2 || j+3 == Math.round(Xpix/2)+Xpix-i-2 || j-3 == Math.round(Xpix/2)+Xpix-i-2 || j+4 == Math.round(Xpix/2)+Xpix-i-2 || j-4 == Math.round(Xpix/2)+Xpix-i-2
                    ? 0 : 1);
        else if(type === 3) // Quadrato
          O[i].push(i >= Math.round(Ypix/6)-2 && i <= Math.round(Ypix-Ypix/6)+2 && (j == Math.round(Xpix/6) || j == Math.round(Xpix-Xpix/6) || j+1 == Math.round(Xpix/6) || j-1 == Math.round(Xpix-Xpix/6) || j+2 == Math.round(Xpix/6) || j-2 == Math.round(Xpix-Xpix/6) || j+3 == Math.round(Xpix/6) || j-3 == Math.round(Xpix-Xpix/6) || j+4 == Math.round(Xpix/6) || j-4 == Math.round(Xpix-Xpix/6)) ||
                    j >= Math.round(Ypix/6)-2 && j <= Math.round(Ypix-Ypix/6)+2 && (i == Math.round(Xpix/6) || i == Math.round(Xpix-Xpix/6) || i+1 == Math.round(Xpix/6) || i-1 == Math.round(Xpix-Xpix/6) || i+2 == Math.round(Xpix/6) || i-2 == Math.round(Xpix-Xpix/6) || i+3 == Math.round(Xpix/6) || i-3 == Math.round(Xpix-Xpix/6) || i+4 == Math.round(Xpix/6) || i-4 == Math.round(Xpix-Xpix/6))
                    ? 0 : 1);
        else if(type === 4) // Quadrato pieno
          O[i].push(i >= Math.round(Ypix/6) && i <= Math.round(Ypix-Ypix/6) &&
                    j >= Math.round(Ypix/6) && j <= Math.round(Ypix-Ypix/6)
                    ? 0 : 1);
      }
    }
  }

  $('#type option').click(function() {
    O = [], D = [], I = [], M = [], max = 0;
    setupMatrix(parseInt($('#type').find(":selected").text()));
    drawMatrix(O, 1);
    disturbeImage();
    drawMatrix(D, 2);
    drawMatrix(I, 3);
    drawMatrix(M, 4);
    executeAlgorithm();
  });
});
