package game.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Classe che gestisce le operazioni sui file.
 * Ovvero scrittura, lettura e riordino della classifica.
 */

public class ScoresFileManager {
   
    /**
     * Nome del file su cui si andrà a lavorare.
     */
    private static String NOME_FILE = "Scores.txt";

    /**
     * Legge tutti i dati nel file, li riordina e ne restituisce un massimo di 10 (in ordine decrescente).
     * 
     * @return ArrayList delle 10 migliori giocate.
     */
    public static ArrayList<PlayerScore> getTenBestResults() {
        Scanner letturaFile = null;
        ArrayList<PlayerScore> results = new ArrayList<PlayerScore>();

        try { // Riempio l'arraylist con i risultati in ordine di stampa su file
            letturaFile = new Scanner(new File(NOME_FILE));
            while(letturaFile.hasNextLine()) { // Riempio l'arrey list con le righe del file dei punteggi
               String row = letturaFile.nextLine();
               String[] values = row.split(Pattern.quote(".")); // Divido la riga in base agli elementi separati dal punto
               // Creo un oggetto PlayerScore per ogni riga (ovvero ogni giocata)
               PlayerScore playerScore = new PlayerScore(values[0], values[1], Integer.parseInt(values[2]), values[3]); 
               results.add(playerScore); // Lo aggiungo all'arraylist
            }
        } catch (FileNotFoundException e) {
           System.out.println("ERROR READ - File Not Found.");
        }

        results = sortResults(results); // Riordino i risultati

        ArrayList<PlayerScore> bestResults = null;

        if (results.size() > 10) 
            bestResults = new ArrayList<PlayerScore>(results.subList(0, 10)); // Prelevo solo i 10 migliori
        else
            bestResults = results;

        return bestResults;
    }

    /**
     * 
     * Dato un ArrayList contenente tutti i risultati del gioco, li ridispone in ordine decrescente.
     * 
     * @param results risultati in ordine sparso
     * @return     risultati in ordine decrescente
     */
    private static ArrayList<PlayerScore> sortResults(ArrayList<PlayerScore> results) {
        int j;
        PlayerScore key = new PlayerScore(); // Elemento da posizionare davanti
        int i;

        for (j = 1; j < results.size(); j++) { // Numero di elementi ordinati
            key = results.get(j);

            for (i = j-1; (i >= 0) && (results.get(i).getScore() < key.getScore()); i--) 
                results.set(i+1, results.get(i));
           
            results.set(i+1, key);
        }

        return results; // ArrayList ordinato in base al punteggio
    }

    /**
     * 
     * Scrive su file i dati di una determinata partita effettuata.
     * 
     * @param playerScore  form contenente i dati della giocata
     */
    public static void updateScoresFile(PlayerScore playerScore) {
        PrintWriter outputStream = null;
        FileOutputStream addText = null;

        try {
           // Creo l'oggetto con il quale aggiungerò dati al file testuale
           addText = new FileOutputStream(NOME_FILE, true);
           outputStream = new PrintWriter(addText);
        } catch (FileNotFoundException e) {
           System.out.println("ERROR WRITE - File Not Found.");
        }

        String outputString = playerScore.getName() + "." + playerScore.getSurname() + "." + playerScore.getScore() + "." + playerScore.getDate();

        outputStream.println(outputString); //Stampa vera e propria
        outputStream.close();
    }
}
