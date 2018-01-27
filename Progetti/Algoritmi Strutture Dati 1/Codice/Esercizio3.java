import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Esercizio3 {
   
   private static String fileInput;
   private static String fileOutput1; // Array minHeap
   private static String fileOutput2; // Array ordinato
   private static String fileOutput3; // Json
   
   public static void main(String[] args) {
      
      if(args.length < 2) {
         System.out.println("Immettere file di input e di output come argomenti.");
         System.exit(0);
      }
      
      // Input
      fileInput = args[0];
      BufferedReader inputStream = null;
      FileReader reader = null;
      // Output 1
      fileOutput1 = args[1];
      PrintWriter outputStream1 = null;
      FileOutputStream writer1 = null;
      // Output 2
      fileOutput2 = "Output2Es3.txt"; //args[2];
      PrintWriter outputStream2 = null;
      FileOutputStream writer2 = null;
      // Output 3
      fileOutput3 = "JSON.txt";
      PrintWriter outputStream3 = null;
      FileOutputStream writer3 = null;
      
      BinaryTree minHeapTree = new BinaryTree();
      
      try {
         reader = new FileReader(fileInput);
         inputStream = new BufferedReader(reader);
      } catch (FileNotFoundException e) {
         System.out.println("FileNotFoundException nello Stream Input.");
      }
      
      try {
         writer1 = new FileOutputStream(fileOutput1, true);
         outputStream1 = new PrintWriter(writer1);
         
         writer2 = new FileOutputStream(fileOutput2, true);
         outputStream2 = new PrintWriter(writer2);
         
         writer3 = new FileOutputStream(fileOutput3, true);
         outputStream3 = new PrintWriter(writer3);
      } catch (FileNotFoundException e) {
         System.out.println("FileNotFoundException nello Stream Output.");
      }
      
      try {
         String row = inputStream.readLine();
         
         while (row != null) {
            
            minHeapTree.insert(Integer.parseInt(row.trim()));
            
            row = inputStream.readLine(); // Legge una nuova riga
         }

      } catch (IOException e) {
         System.out.println("Errore Lettura " + fileInput);
      }
      
      minHeapTree.printTree();
      
      // Trasformo l'albero in array
      ArrayList<Integer> array = minHeapTree.treeInArray();
      // Stampo nel file l'array ottenuto
      outputStream1.println("Stampati tramite Level Order Traversal:");
      for(int i=0; i<array.size(); i++) 
         outputStream1.println(array.get(i));
      
      // Ordino l'array
      sortArray(array);
      // Stampo l'array ordinato
      for(int i=0; i<array.size(); i++) 
         outputStream2.println(array.get(i));

      outputStream3.print(minHeapTree.jsonTree());
      
      // Chiusura streams
      try {
         outputStream1.close();
         outputStream2.close();
         outputStream3.close();
         inputStream.close();
      } catch (IOException ex) {
         System.out.println("IOException nella Chiusura Streams.");
      }
      
   }
   
   // Ordina un array tramite l'algoritmo di ordinamento MergeSort
   public static void sortArray(ArrayList<Integer> array) { // Complessità O(nlogn)
      int[] arrayTemp = new int[array.size()];
      mergeSortRecursive(array, arrayTemp, 0, array.size()-1);
   }

   // Ricorsivamente prima divide e poi fonde i blocchi divisi ordinando un array
   private static void mergeSortRecursive(ArrayList<Integer> array, int[] arrayTemp, int lower, int higher) {
      if (lower < higher) {
         int middle = (lower + higher)/2;
         mergeSortRecursive(array, arrayTemp, lower, middle);
         mergeSortRecursive(array, arrayTemp, middle+1, higher);
         merge(array, arrayTemp, lower, middle+1, higher);
      }
   }

   // Dato un array, il punto di inizio del primo segmento, il punto di inizio e quello di fine del secondo segmento, confronta gli elementi nelle posizioni e li ordina
   private static void merge(ArrayList<Integer> array, int[] arrayTemp, int startLeft, int startRight, int endRight) {
      int endLeft = startRight-1;
      int posIter = startLeft; // Iterazione dell'array temporaneo
      int numElem = endRight-startLeft+1;

      // Finchè entrambi gli iteratori (che partono dall'inizio di entrambi i blocchi) non hanno raggiunto la fine del loro blocco
      while(startLeft <= endLeft && startRight <= endRight) {
         if (array.get(startLeft) < array.get(startRight)) // Se l'elemento itarato a sinistra è < di quello di destra
            arrayTemp[posIter++] = array.get(startLeft++); // Nell'array temporaneo metto l'elemento del blocco di sinistra (incremento entrambi i contatori)
         else
            arrayTemp[posIter++] = array.get(startRight++); // Nell'array temporaneo metto l'elemento del blocco di destra (incremento entrambi i contatori)
      }

      // Se il blocco sinistro non ha raggiunto la fine (perchè il destro era più corto) lo completo con i suoi valori rimanenti
      while (startLeft <= endLeft) 
         arrayTemp[posIter++] = array.get(startLeft++);

      // Se il blocco destro non ha raggiunto la fine (perchè il sinistro era più corto) lo completo con i suoi valori rimanenti
      while (startRight <= endRight) 
         arrayTemp[posIter++] = array.get(startRight++);

      // Sostituisci gli elementi dell'array originario con quelli dell'array di appoggio
      for (int i=0; i<numElem; i++, endRight--) 
         array.set(endRight, arrayTemp[endRight]);
      
   }
   
}

