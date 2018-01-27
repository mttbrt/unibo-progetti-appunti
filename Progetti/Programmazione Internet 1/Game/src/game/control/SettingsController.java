package game.control;

import game.Game;
import game.model.Shot;
import game.model.Plane;
import game.model.Player;
import game.model.PlayerScore;
import game.model.ScoresFileManager;
import game.model.SetGame;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import static javafx.geometry.HPos.CENTER;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Classe controller della schermata di impostazioni di gioco, l'utente qui ha la possibilità di personalizzare il gioco e visualizzare varie informazioni.
 */

public class SettingsController implements EventHandler<ActionEvent> {

    /**
     * Identifica il giocatore che sta giocando.
     */
    private Player player;
    /**
     * Identifica l'icona dell'aereo.
     */
    private String plane;
    /**
     * Identifica il numero di proiettili che si vogliono sparare in una volta.
     */
    private int shoots;
    /**
     * Identifica la velocità dei proiettili.
     */
    private double speed;

    @FXML
    private Label welcomeLbl;
    // Layout aereo
    @FXML
    private Button plane1;
    @FXML
    private Button plane2;
    @FXML
    private Button plane3;
    // Proiettili da sparare in una volta
    @FXML
    private Button shoot1;
    @FXML
    private Button shoot2;
    @FXML
    private Button shoot3;
    // Velocità dei proiettili
    @FXML
    private Button speed1;
    @FXML
    private Button speed2;
    @FXML
    private Button speed3;
    // Pannello dei 10 migliori risultati in classifica
    @FXML
    private GridPane rankingPane;
    // Immagini che mostrano all'utente quale tipologià di layout, numero di proiettili, velocità ha selezionato
    @FXML
    private ImageView planeSetImage;
    @FXML
    private ImageView shotsSetImage;
    @FXML
    private ImageView speedSetImage;  

    /**
     * Imposta la schermata di default della sezione Settings.
     * 
     * Il giocatore, il listener dei bottoni, seleziona le impostazioni di default e aggiorna la classifica.
     * 
     * @param player  giocatore che sta effettuando la partita
     */
    public void setSettingsFrame(Player player) {
        setPlayer(player);
        initializeButtons();
        setDefaultSettings();
        updateRanking();
    }

    /**
     * Crea il giocatore e imposta la frase di benvenuto personalizzata.
     * 
     * @param loggedPlayer  giocatore loggato
     */
    private void setPlayer(Player loggedPlayer) {
        player = new Player(loggedPlayer.getName(), loggedPlayer.getSurname()); // Creo il giocatore
        welcomeLbl.setText("Welcome " + player.getName() + " " + player.getSurname() + " customize the game: ");
    }

    // Indico che i bottoni hanno come ascoltatore questa classe, nello specifico sono gestiti da un HandleEvents (il metodo handle)
    /**
     * Indica che i bottoni hanno come ascoltatore questa classe.
     * 
     * Nello specifico sono gestiti da un HandleEvents (il metodo handle).
     * 
     */
    private void initializeButtons() {
        plane1.setOnAction(this);
        plane2.setOnAction(this);
        plane3.setOnAction(this);

        shoot1.setOnAction(this);
        shoot2.setOnAction(this);
        shoot3.setOnAction(this);

        speed1.setOnAction(this);
        speed2.setOnAction(this);
        speed3.setOnAction(this);

        setButtonImages();
    }

    /**
     * Imposta le immagini dei bottoni, assegnando ad ognuno l'immagine relativa all'impostazione a cui corrisponde.
     */
    private void setButtonImages() {
        plane1.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/planeIcon1.png"))));
        plane2.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/planeIcon2L.png"))));
        plane3.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/planeIcon3L.gif"))));

        shoot1.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/1BulletIcon.png"))));
        shoot2.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/2BulletIcons.png"))));
        shoot3.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/3BulletIcons.png"))));

        speed1.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/x1.png"))));
        speed2.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/x2.png"))));
        speed3.setGraphic(new ImageView(new Image(Game.class.getResourceAsStream("view/images/x3.png"))));
    }

    // Con questo metodo è come se "cliccassi sui bottoni" in modo da assegnare le varibaili di default
    /**
     * Impostazione di default: layout aereo 1, colpo singolo, velocità unitaria.
     */
    private void setDefaultSettings() {
        plane1.fire();
        shoot1.fire();
        speed1.fire();
    }

    /**
     * Aggiorna la classifica con i 10 migliori risultati in ordine decrescente.
     */
    private void updateRanking() {
        ArrayList<PlayerScore> ranking = new ArrayList<PlayerScore>();
        ranking = ScoresFileManager.getTenBestResults(); // Ho i 10 migliori risultati ordinati in modo decrescente

        for (int i = 0; i < ranking.size(); i++) {
            // Creo nodi che contengano i valori da inserire in classifica
            Text name = new Text(ranking.get(i).getName());
            Text surname = new Text(ranking.get(i).getSurname());
            Text score = new Text();
            score.setText("" + ranking.get(i).getScore());
            Text date = new Text(ranking.get(i).getDate());

            // Aggiungo la riga contenente i valori al grid pane
            rankingPane.addRow(i+1, name, surname, score, date);

            // Li allineo al centro
            rankingPane.setHalignment(name, CENTER);
            rankingPane.setHalignment(surname, CENTER);
            rankingPane.setHalignment(score, CENTER);
            rankingPane.setHalignment(date, CENTER);

            // Cambio il colore dei font alternandoli
            if(i%2 == 0) {
                name.setFill(Color.web("#aaee84"));
                surname.setFill(Color.web("#aaee84"));
                score.setFill(Color.web("#aaee84"));
                date.setFill(Color.web("#aaee84"));
            } else {
                name.setFill(Color.web("#71d3e2"));
                surname.setFill(Color.web("#71d3e2"));
                score.setFill(Color.web("#71d3e2"));
                date.setFill(Color.web("#71d3e2"));    
            }
        }
    }
   
    /**
     * Cattura l'evento generato dalla pressione di un bottone delle impostazioni, e si comporta in base alla scelta.
     * 
     * Imposta le variabili opportune e aggiorna le immagini utili all'utente.
     * 
     * @param event      pressione di un bottone relativo alle impostazioni
     */
    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() == plane1) {
            planeSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/planeIcon1.png")));
            plane = "Plane 1"; // Stringa per l'icona dell'aereo
        } else if(event.getSource() == plane2) {
            planeSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/planeIcon2L.png")));
            plane = "Plane 2";
        } else if(event.getSource() == plane3) {
            planeSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/planeIcon3L.gif")));
            plane = "Plane 3";
        } else if(event.getSource() == shoot1) {
            shotsSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/1BulletIcon.png")));
            shoots = 1; // Numero di proiettili in un lancio
        } else if(event.getSource() == shoot2) {
            shotsSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/2BulletIcons.png")));
            shoots = 2;
        } else if(event.getSource() == shoot3) {
           shotsSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/3BulletIcons.png")));
           shoots = 3;
        } else if(event.getSource() == speed1) {
            speedSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/x1.png")));
            speed = 1; // Velocità dei proiettili
        } else if(event.getSource() == speed2) {
            speedSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/x2.png")));
            speed = 1.25;
        } else if(event.getSource() == speed3) {
            speedSetImage.setImage(new Image(Game.class.getResourceAsStream("view/images/x3.png")));
            speed = 1.5;
        }
    }

    // Inizio del gioco (invio dei dati scelti dall'utente alla pagina di gioco)
    /**
     * 
     * Bottone di inizio gioco.
     * 
     * I dati sono inviati tramite gli appositi metodi che contribuiscono a impostare il gioco secondo le scelte dell'utente
     * 
     * @param event      pressione del bottone di inizio del gioco
     * @throws IOException 
     */
    public void startBtn(ActionEvent event) throws IOException {
        Plane settedPlane = new Plane(plane);// Creo l'aereoplano settato dall'utente
        Shot settedBullet = new Shot(shoots, speed); // Creo i proiettili settati dall'utente

        ((Node)event.getSource()).getScene().getWindow().hide(); // Faccio in modo che non si creino molteplici finestre ripetute
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Game.class.getResource("view/Play.fxml"));
        loader.load();

        Parent root = loader.getRoot();

        // Classe che cra un profilo delle impostazioni di gioco scelte
        SetGame settings = new SetGame(player, settedPlane, settedBullet); 

        PlayController playController = loader.getController();
        playController.settings(settings); // passo le informazioni al Play Controller 
        playController.setGame(root);
    }
}