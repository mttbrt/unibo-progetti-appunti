package game.control;

import game.Game;
import game.model.Player;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Classe controller della schermata principale, in cui l'utente immette nome e cognome.
 */

public class HomeController {

    /**
     * Variabili derivanti da fxml.
     * 
     * @param name                   campo fxml in cui l'utente immette il proprio nome
     * @param surname              campo fxml in cui l'utente immette il proprio cognome
     * @param errorMessage     scritta fxml in cui viene mostrato un eventuale messaggio di errore
     */
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private Label errorMessage;  
   
    /**
     * 
     * Metodo che gestisce la pressione del bottone di login.
     * 
     * Subito viene effettuato un semplice controllo per verificare che entrambi i campi siano stati compilati. 
     * In caso negativo viene mostrato un messaggio di errore.
     * Se tutto è compilato si crea la finestra di impostazioni di gioco, richiamandone il controller e il relativo metodo di impostazione della frame
     * 
     * @param event         evento di pressione del bottone di login
     * @throws IOException 
     */
    @FXML
    private void loginBtn(ActionEvent event) throws IOException {
        // Controllo che nome e cognome siano stati inseriti
        if (name.getText().trim().isEmpty() || surname.getText().trim().isEmpty()) {
            errorMessage.setText("Hey, fill both fields below.");
        } else {
            Player player = new Player(name.getText().trim(), surname.getText().trim()); // Creo l'oggetto informazione

            ((Node)event.getSource()).getScene().getWindow().hide(); // Faccio in modo che non si creino molteplici finestre ripetute
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Game.class.getResource("view/Settings.fxml"));

            Parent root = (Parent) loader.load();
            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle("Settings Page");
            stage.setScene(scene);

            // Invio al controller della scena (fxml) caricata, l'oggetto player creato tramite l'apposito metodo
            SettingsController settingsController = loader.getController();
            settingsController.setSettingsFrame(player);

            scene.getStylesheets().add("game/view/SettingsTheme.css");
            stage.setResizable(false);
            stage.show();
        }
   }
}
