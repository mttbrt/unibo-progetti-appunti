package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

/**
 *  Classe che possiede il metodo main e viene eseguita per prima. 
 */

public class Game extends Application {

    /**
     *  Metodo astratto su cui viene applicato un override. 
     * 
     *  E' il metodo iniziale di tutte le applicazioni JavxFX
     * 
     *  @param stage
     *  @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Game.class.getResource("view/Home.fxml"));

        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);

        scene.getStylesheets().add("game/view/HomeTheme.css");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Home Page");
        stage.show();
    }
   
    public static void main(String[] args) {
       launch(args);
    }
   
}