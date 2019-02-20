import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class Esercizio2 {
   
   private static String fileInput;
   private static String fileOutput;
   
   public static void main(String[] args) {
      int size = 16;

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
      
      if(fileInput.toLowerCase().contains("input3es2") || fileInput.toLowerCase().contains("input4es2")) 
         size = 32;
         
      HashTable ht = new HashTable(size);
      
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
         outputStream.println("key\tTablePos\tListLength");
         int numElem = 0; // Numero di elementi inseriti
         String row = inputStream.readLine();
         
         while (row != null) {
            
            outputStream.println(row.trim() + 
                    "\t\t|" + String.format("%02d", ht.hashFunction(row.trim())) + 
                    "\t\t|" + ht.insert(row.trim()));
            
            numElem++;
            row = inputStream.readLine(); // Legge una nuova riga
         }
         
         outputStream.println("CollisioniVerificate: " + ht.countCollisions());
         
         System.out.println("Collisioni Verificate: " + ht.countCollisions());
         System.out.println("Collisioni Aspettate: " + ht.collisionsExpected(numElem));
         System.out.println("Efficienza Funzione Hash: " + 
                 (Math.round((1-ht.countCollisions()/(double)numElem)*100.0)/100.0)*100 + "%");
         
      } catch (IOException e) {
         System.out.println("Errore Lettura " + fileInput);
      }
      
      System.out.println();
      ht.print();
      
      // Chiusura streams
      try {
         outputStream.close();
         inputStream.close();
      } catch (IOException ex) {
         System.out.println("IOException nella Chiusura Streams.");
      }
      
   }
   
}

