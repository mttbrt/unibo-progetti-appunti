package game.control;

import game.Game;
import game.model.Player;
import game.model.PlayerScore;
import game.model.ScoresFileManager;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Classe controller della schermata in cui mostro i risultati.
 */

public class ScoreController {

    // Variabili lato view
    @FXML
    private Label playerLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label dateLabel;

    /**
     * Identifica il punteggio.
     */
    private int score;
    /**
     * Identifica il giocatore.
     */
    private Player playerObject;
    private DateFormat dateFormat;
    private Date date;
    /**
     * Identifica la data in cui è stata effettuata la partita.
     */
    private String printDate;
    /**
     * Identifica il form di giocata: ovvero l'insieme delle informazioni di giocatore, punteggio, e data.
     */
    private PlayerScore scoreForm; 
   
    /**
     * 
     * Imposta la schermata che mostra i punti
     * 
     * @param totalScore          punteggio ottenuto
     * @param player           utente che ha giocato
     */
    public void setScoreFrame(int totalScore, Player player) {
       playerObject = player;
       score = totalScore;

       showPlayer();
       showScore();
       showDate();
       writeScore();
    }
   
    /**
     * Mostra i dati del giocatore.
     */
    private void showPlayer() {
       playerLabel.setText(playerObject.getName() + " " + playerObject.getSurname());
    }
   
    /**
     * Mostra il punteggio ottenuto.
     */
    private void showScore() {
       scoreLabel.setText("" + score);
    }
   
    /**
     * Mostra la data di giocata.
     */
    private void showDate() {
       dateFormat = new SimpleDateFormat("dd MMM yyy");
       date = new Date();

       printDate = dateFormat.format(date);
       dateLabel.setText(printDate);
    }
   
    /**
     * Salvo i dati di questa giocata aggiornando il file dei punteggi.
     */
    private void writeScore() {
       scoreForm = new PlayerScore(playerObject.getName(), playerObject.getSurname(), score, printDate);
       ScoresFileManager.updateScoresFile(scoreForm); // Scrivo i nuovi dati nel file
    }
   
    /**
     * 
     * Chiusura del gioco
     * 
     * @param event 
     */
    @FXML
    private void quitBtn(ActionEvent event) {
       ((Node)event.getSource()).getScene().getWindow().hide();
    }
   
    /**
     * 
     * Nuova giocata.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void playAgainBtn(ActionEvent event) throws IOException {
        ((Node)event.getSource()).getScene().getWindow().hide(); // Faccio in modo che non si creino molteplici finestre ripetute
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Game.class.getResource("view/Settings.fxml"));

        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setTitle("Settings Page");
        stage.setScene(scene); // La imposto come scena della finestra che creo

        // Invio al controller della scena (fxml) impostata l'oggetto info creato
        SettingsController settingsController = loader.getController();
        settingsController.setSettingsFrame(playerObject);

        stage.setResizable(false);
        stage.show();
    }
}
