import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Esercizio4 {

   private static String fileInput;
   private static String fileOutput;
   
   public static void main(String args[]) {
      Graph graph = new Graph();
      
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
      }
      
      try {
         writer = new FileOutputStream(fileOutput, true);
         outputStream = new PrintWriter(writer);
      } catch (FileNotFoundException e) {
         System.out.println("FileNotFoundException nello Stream Output.");
      }
      
      try {
         String row = inputStream.readLine();
         int a = 0; // Dato identificativo del grafo
         
         while (row != null) {
            String[] matrixLine = row.split(" ");
            
            graph.addVertex(a); // Creo il nodo a prescindere (eventualmente aggiungo archi e nodi collegati)
            
            for(int b=0; b<matrixLine.length; b++)  // Collegamento o meno con un altro nodo con dato b
               if(matrixLine[b].equals("1")) 
                  graph.addEdge(a, b);
            
            row = inputStream.readLine(); // Legge una nuova riga
            a++;
         }

      } catch (IOException e) {
         System.out.println("Error Lettura " + fileInput);
      }
      
      //System.out.println(graph);
      
      ArrayList<Integer> entrances = graph.getEntrances();
      ArrayList<Integer> exits = graph.getExits();
      boolean acyclic = graph.isAcyclic();
      
      String mapResult = "MAPPA" + (entrances.size() > 0 && exits.size() > 0 && acyclic ? " AUTENTICA " : " FASULLA ");
      System.out.println(mapResult); // Schermo
      outputStream.println(mapResult); // File
      
      System.out.println("Grafo Aciclico: " + (acyclic ? "SI" : "NO"));
      
      System.out.println("Entrate: " + (entrances.size() > 0 ? entrances.size() : "NO") + "\n" + entrances);
      
      System.out.println("Uscite: " + (exits.size() > 0 ? exits.size() : "NO") + "\n" + exits);
      
      if(entrances.size() < 1) {
         System.out.println("Nessun ingresso presente: non è possibile accedere al percorso");
         outputStream.println("Nessun ingresso presente: non è possibile accedere al percorso");
      }
      
      if(exits.size() < 1) {
         System.out.println("Nessuna uscita presente: non è possibile uscire dal percorso");
         outputStream.println("Nessuna uscita presente: non è possibile uscire dal percorso");
      }
      
      if (entrances.size() > 0 && exits.size() > 0) {
         System.out.println("Tutti i percorsi:");
         int sumPaths = 0;
         for(int i=0; i<entrances.size(); i++) {
            for(int j=0; j<exits.size(); j++) {
               ArrayList<ArrayList<Integer>> paths = graph.paths(entrances.get(i), exits.get(j));
               if (paths.size()>0) {
                  sumPaths++;
                  System.out.println(paths.size() 
                       + (paths.size()==1 ? " percorso" : " percorsi") 
                       + " da " + entrances.get(i) + " a " + exits.get(j));
                  for(ArrayList<Integer> path : paths) {
                     System.out.println("     " + path);
                     outputStream.println(path);
                  }
                  System.out.println();
                  outputStream.println();
               }
            }
         }
         System.out.println("Rilevati in totale " + sumPaths + " congiunzioni.");
      }
      
      // Chiusura streams
      try {
         outputStream.close();
         inputStream.close();
      } catch (IOException ex) {
         System.out.println("IOException nella Chiusura Streams.");
      }
      
   }
   
}
