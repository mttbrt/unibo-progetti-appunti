import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class InputFileCreator {
   
   private static String fileOutput;
   
   public static void main(String[] args) {
      
      ArrayList<String> ids = new ArrayList<String>();
      ids.add("Apple_iMac");
      ids.add("Dell_Inspiron");
      ids.add("Apple_Mac_Mini");
      ids.add("Asus_VivoMini_UN45");
      ids.add("Acer_Revo_Build");
      ids.add("HP_Pavilion_Wave");
      ids.add("Intel_Computer_Stick");
      ids.add("Lenovo_IdeaCentre_710");
      ids.add("Acer_Chromebase_24");
      ids.add("Dell_XPS_13_9350");
      ids.add("Xiaomi_Deluxe_99");
      ids.add("Lenovo_Yoga_710");
      ids.add("Asus_ZenBook_UX310UA");
      ids.add("Dell_Latitude_13_7370");
      ids.add("HP_Envy_13");
      ids.add("Microsoft_Surface_Book");
      ids.add("Toshiba_Chromebook_2");
      ids.add("MacBook_Air");
      ids.add("Acer_S_13_S5-371");
      ids.add("HP_Spectre_13");
      
      String[] choices = new String[3];
      choices[0] = "I";
      choices[1] = "C";
      choices[2] = "R";
      
      // Output
      fileOutput = "Input2Es1.txt";
      PrintWriter outputStream = null;
      FileOutputStream writer = null;

      try {
         writer = new FileOutputStream(fileOutput, true);
         outputStream = new PrintWriter(writer);
      } catch (FileNotFoundException e) {
         System.out.println("FileNotFoundException nello Stream Output.");
      }
      
      for(int i=0; i<1000; i++) {
         String line = "";
         
         // I, C, R
         if(i>200) 
            line += choices[getRandomNum(0,choices.length-1)]; 
         else
            line += choices[0]; // Primi 200 solo inserimenti
         
         line+=" "+ids.get(getRandomNum(0,ids.size()-1)); // Id nodo
         
         line+=" "+"lab"+getRandomNum(0, 100);
            
         outputStream.println(line);
      }
            
      outputStream.close();
   }
 
   private static int getRandomNum(int min, int max) {
      Random rand = new Random();
      return rand.nextInt((max - min) + 1) + min;
   }
   
}

