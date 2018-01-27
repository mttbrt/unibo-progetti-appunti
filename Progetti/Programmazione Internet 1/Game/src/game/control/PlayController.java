package game.control;

import game.Game;
import game.model.Shot;
import game.model.Formulas;
import game.model.Plane;
import game.model.Player;
import game.model.SetGame;
import java.io.IOException;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Classe controller per la schermata di gioco, qui l'utente gioca realmente.
 */

public class PlayController {

    /**
     * Larghezza cannone in pixel.
     */
    final static double CANNON_WIDTH = 180;
    /**
     * Altezza cannone in pixel.
     */
    final static double CANNON_HEIGHT = 45;
    /**
     * Larghezza aereo in pixel.
     */
    final static double PLANE_WIDTH = 100;
    /**
     * Altezza aereo in pixel.
     */
    final static double PLANE_HEIGHT = 50;
    /**
     * Raggio proiettile in pixel.
     */
    final static double BULLET_RADIUS = 15;
    /**
     * Larghezza finestra di gioco in pixel.
     */
    final static double FRAME_WIDTH = 1000;
    /**
     * Altezza finestra di gioco in pixel.
     */
    final static double FRAME_HEIGHT = 533;

    /**
     * Oggetto giocatore (nome e cognome).
     */
    private Player playerObject;
    /**
     * Oggetto aereo (icona e velocità).
     */
    private Plane planeObject;
    /**
     * Oggetto proiettile (numero di proiettili e velocità).
     */
    private Shot bulletObject;
    /**
     * Punti totali effettuati in un determinato momento.
     */
    private int totalScore;

    // Variabili riguardanti il background view
    private Group root; 
    private Stage stage;
    private Scene scene;
    /**
     * Audio che verrà emesso quando si colpisce un aereo.
     */
    private final AudioClip SMASH_AUDIO = new AudioClip(Game.class.getResource("view/sounds/smash.wav").toString());

    // Variabili riguardanti l'aereo
    /**
     * Icona dell'aereo in movimento.
     */
    private ImageView planeIcon;
    /**
     * Forma dell'aereo in movimento.
     */
    private Rectangle planeShape;
    /**
     * Punto da cui l'aereo ha iniziato la "tratta" che sta effettuando in un dato momento.
     */
    private String planeStartingSide;

    // Variabili riguardanti il cannone
    private Arc arc;
    private Rectangle base;
    /**
     * Icona del cannone.
     */
    private ImageView cannon;
    /**
     * Transizione del cannone (sul suo asse).
     */
    private RotateTransition cannonTransition;

    // Variabili riguardanti il proiettile
    /**
     * Icona del primo proiettile.
     */
    private ImageView bullet1Icon;
    /**
     * Icona del secondo proiettile.
     */
    private ImageView bullet2Icon;
    /**
     * Icona del terzo proiettile.
     */
    private ImageView bullet3Icon;
    /**
     * Variabile che assegna un diverso peso al punteggio del giocatore in relazione al numero di proiettili in uno sparo.
     */
    private double scoreWeight;
   
    @FXML
    private Label playerLabel;
    @FXML
    private Label planesLabel;
    @FXML
    private Label shotsLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label bulletXCoordinate;
    @FXML
    private Label bulletYCoordinate;
    @FXML
    private Label planeXCoordinate;
    @FXML
    private Label planeYCoordinate;
    @FXML
    private Label cannonAngleLabel;
    @FXML
    private Button gameOver; 
 
   /**
    * 
    * Imposta il campo di gioco e tutto ciò che è stato scelto dall'utente.
    * 
    * Inoltre fa partire la timeline, parte fondamentale nell'architettura del gioco.
    * 
    * @param p    variabile parent
    */
    public void setGame(Parent p) {
        setBackgroundView(p);

        setCannonStation();

        setCannonView();

        setCannonTransition();

        setBulletsView();

        setPlaneView(-1, -1); // Valori che indicano che il lato e l'altezza dev'essere generata casualmente

        setScoreWeightValue();

        gameTimeline();

        playerLabel.setText(playerObject.getName() + " " + playerObject.getSurname());

        stage.setWidth(FRAME_WIDTH); // Fisso la larghezza
        stage.setResizable(false);
        stage.show();
    }
   
    // Imposto le variabili d'istanza che mi servono per il gioco
    /**
     * 
     * Imposta le variabili d'istanza relative alle scelte effettuate dall'utente.
     * 
     * Utilizzando la classe SetGame che rappresenta appunto il concetto di "impostazioni di gioco".
     * 
     * @param settings      insieme di impostazioni scelte dall'utente
     */
    public void settings(SetGame settings) { 
       playerObject = settings.getSettedPlayer();
       planeObject = settings.getSettedPlane();
       bulletObject = settings.getSettedBullet();
    }
   
    /**
     * Crea l'oggetto rettangolo che idealizza la forma di un "aereo" stilizzato.
     */
    private void createPlaneShape() {
        planeShape = new Rectangle(100, 50, Color.web("#e1e9eb"));
        planeShape.setArcWidth(120);
        planeShape.setArcHeight(120);
        root.getChildren().add(planeShape);
    }
   
    /**
     * 
     * Imposta la visione del campo di gioco.
     * 
     * @param p    parent
     * @return 
     */
    private Stage setBackgroundView(Parent p) {
        // Setto la finestra
        root = new Group(p);
        stage = new Stage();
        scene = new Scene(root);
        stage.setTitle("Game Page");
        stage.setScene(scene);

        return stage;
    }
   
    /**
     * 
     * Imposta la visione della stazione del cannone.
     * 
     * Formata da un arco e una base rettangolare.
     * 
     */
    private void setCannonStation() {
        // Postazione
        arc = new Arc(FRAME_WIDTH/2, FRAME_HEIGHT-25, 50, 50, 0, 180);
        arc.setFill(Color.web("#80ee4f"));

        // Base della postazione
        base = new Rectangle(100, 25);
        base.setX(FRAME_WIDTH/2-50);
        base.setY(FRAME_HEIGHT-25);
        base.setFill(Color.web("#80ee4f"));
    }
   
    /**
     * 
     * Imposta la visione del cannone.
     * 
     */
    private void setCannonView() {
       // Setto l'immagine del cannone
       cannon = new ImageView(new Image(Game.class.getResource("view/images/cannonIcon.png").toExternalForm()));
       cannon.setX(FRAME_WIDTH/2-CANNON_WIDTH/2);
       cannon.setY(FRAME_HEIGHT-CANNON_HEIGHT);
    }
   
    /**
     * 
     * Imposta la visione dei proiettili.
     * 
     */
    private void setBulletsView() {
        // Imposto il proiettile 1
        bullet1Icon = new ImageView(new Image(Game.class.getResource("view/images/bulletIcon.png").toExternalForm()));
        // Situazione di partenza del proiettile: "dentro" alla stazione del cannone
        bullet1Icon.setX(FRAME_WIDTH/2-BULLET_RADIUS/2);
        bullet1Icon.setY(FRAME_HEIGHT-BULLET_RADIUS*2);
        root.getChildren().add(bullet1Icon);

        // Setto il proiettile 2 (in caso di scelta di raffica di 2 proiettili) solo se richiesto, per evitare di caricare oggetti inutili
        if (bulletObject.getBulletsNum() >= 2) {
           bullet2Icon = new ImageView(new Image(Game.class.getResource("view/images/bulletIcon.png").toExternalForm()));
           // Situazione di partenza del proiettile: "dentro" alla stazione del cannone
           bullet2Icon.setX(FRAME_WIDTH/2-BULLET_RADIUS/2);
           bullet2Icon.setY(FRAME_HEIGHT-BULLET_RADIUS*2);
           root.getChildren().add(bullet2Icon);
        }

        if (bulletObject.getBulletsNum() == 3) {
           // Setto il proiettile 3 (in caso di scelta di raffica di 3 proiettili) solo se richiesto, per evitare di caricare oggetti inutili
           bullet3Icon = new ImageView(new Image(Game.class.getResource("view/images/bulletIcon.png").toExternalForm()));
           // Situazione di partenza del proiettile: "dentro" alla stazione del cannone
           bullet3Icon.setX(FRAME_WIDTH/2-BULLET_RADIUS/2);
           bullet3Icon.setY(FRAME_HEIGHT-BULLET_RADIUS*2);
           root.getChildren().add(bullet3Icon);
        }
   }
   
    /**
     * 
     * Imposta la visione dell'aereo. 
     * 
     * Tuttavia ogni volta che esce dallo scermo un aereo dev'essere ricreato facendolo ripartire dalla stessa altezza ma cambiato di verso.
     * Inoltre se un aereo è colpito riparte casualmente.
     * 
     * @param planeSide           può essere solo 0, 1, -1 -> con 0 parte da sinistra, con 1 parte da destra con -1 viene generato casualmente
     * @param planeStartingPointY          può essere un valore numerico (>0) o -1, in tal caso viene venerato casualmente
     */
    public void setPlaneView(int planeSide, int planeStartingPointY) { 
       Random random = new Random();
       int insidePlaneSide = planeSide; // Variabile interna al metodo per la gestione del lato di partenza dell'aereo
       int insidePlaneStartingPointY = planeStartingPointY; // Variabile interna al metodo per la gestione dell'altitudine dell'aereo

        // Se il lato di partenza non viene specificato come parametro (o meglio viene dato il valore -1 per convenzione) genero casualmente
        if (planeSide == -1) {
            insidePlaneSide = random.nextInt(2); // Lato sinistro(0) o destro(1)
            planeObject.randomSpeed(); // Genero una velocità random per l'aereo che manterrà finchè non sarà abbattuto (lo faccio qui perchè se viene dato -1 significa che l'aereo inizia il suo ciclo di vita, ovvero o è stato precedentemente abbattuto o inizia il gioco)
        }
        // Se l'altezza dell'aereo non viene specificata come parametro (o meglio viene dato il valore -1 per convenzione) genero casualmente
        if (planeStartingPointY < 0)
            insidePlaneStartingPointY = random.nextInt((int) (FRAME_HEIGHT/2 - PLANE_HEIGHT)); // Altezza di volo dell'aereo
       
        // Gestisco il lato di partenza (sia generato casualmente che passato come parametro)
        switch (insidePlaneSide) {
            case 0:  // Parte dal lato sinistro
                if (planeObject.getIcon().equals("Plane 1")) { // Distinguo se l'aereo è una forma o un'icona
                    createPlaneShape();
                    planeStartingSide = "L"; // Faccio si che globalmente si sappia il lato di partenza dell'aereo
                    planeShape.setY(insidePlaneStartingPointY);
                    planeShape.setX(-PLANE_WIDTH);
                } else {
                    planeLeftSide();
                    planeStartingSide = "L"; // Faccio si che globalmente si sappia il lato di partenza dell'aereo
                    planeIcon.setY(insidePlaneStartingPointY);
                    planeIcon.setX(-PLANE_WIDTH);
                }
                break;
            case 1:  // Parte dal lato destro
                if (planeObject.getIcon().equals("Plane 1")) {
                    createPlaneShape();
                    planeStartingSide = "R"; // Faccio si che globalmente si sappia il lato di partenza dell'aereo
                    planeShape.setY(insidePlaneStartingPointY);
                    planeShape.setX(FRAME_WIDTH);
                } else {
                    planeRightSide();
                    planeStartingSide = "R"; // Faccio si che globalmente si sappia il lato di partenza dell'aereo
                    planeIcon.setY(insidePlaneStartingPointY);
                    planeIcon.setX(FRAME_WIDTH);
                }
                break;
            default:
                System.out.println("ERROR PLANE SIDE");
                break;
        }
    }
   
    /**
     * Imposta l'aereo con l'icona scelta considerando che è partito dal lato destro dello schermo (quindi punterà verso sinistra).
     */
    public void planeRightSide() {  // Imposto l'icona dell'aereo proveniente dal lato destro
        switch (planeObject.getIcon()) {
            case "Plane 2":
                planeIcon = new ImageView(new Image(Game.class.getResource("view/images/planeIcon2R.png").toExternalForm()));
                root.getChildren().add(planeIcon);
                break;
            case "Plane 3":
                planeIcon = new ImageView(new Image(Game.class.getResource("view/images/planeIcon3R.gif").toExternalForm()));
                root.getChildren().add(planeIcon);
                break;
            default:
                System.out.println("ERROR 404 PLANE NOT FOUND");
                break;
        }
    }
   
    /**
     * Imposta l'aereo con l'icona scelta considerando che è partito dal lato sinistro dello schermo (quindi punterà verso destra).
     */
    public void planeLeftSide() {  // Imposto l'icona dell'aereo proveniente dal lato sinistro
        switch (planeObject.getIcon()) {
            case "Plane 2":
                planeIcon = new ImageView(new Image(Game.class.getResource("view/images/planeIcon2L.png").toExternalForm()));
                root.getChildren().add(planeIcon);
                break;
            case "Plane 3":
                planeIcon = new ImageView(new Image(Game.class.getResource("view/images/planeIcon3L.gif").toExternalForm()));
                root.getChildren().add(planeIcon);
                break;
            default:
                System.out.println("ERROR 404 PLANE NOT FOUND");
                break;
        }
    }
   
    /**
     * Imposta la trasizione del cannone su suo asse.
     */
    private void setCannonTransition() {
       cannonTransition = new RotateTransition();
       cannonTransition.setNode(cannon);
       cannonTransition.setDuration(new Duration(0.1));

       cannonTransition.setFromAngle(90); // Posizione di base 90°
       cannonTransition.play();
    }
   
    /**
     * Imposta la percentuale per cui sarà moltiplicato il punteggio del giocatore in relazione al numero di proiettili in uno sparo.
     * E' un importante metodo con il quale si tenta di rendere più equa l'assegnazione del punteggio e la relativa comparazione in classifica. 
     * In quanto è stato deciso di rendere la traiettoria del fascio di proiettili (in caso di scelta multipla) tale per cui più sono i proiettili in un colpo, più il raggio di colpita è ampio. Ciò facilità abbastanza l'abbattimento dell'aereo e quindi anche il punteggio verrà rapportato di conseguenza.
     */
    private void setScoreWeightValue() {
        switch (bulletObject.getBulletsNum()) {
            case 1:
                scoreWeight = 1; // 100% del valore del punteggio
                break;
            case 2:
                scoreWeight = 0.9; // 90% del valore del punteggio
                break;
            default:
                scoreWeight = 0.8; // 80% del valore del punteggio
                break;
        }
    }
   
    /**
     * 
     * Gestisce il caso di terminazione della giocata.
     * 
     * Si viene mandati alla finestra di visualizzazione del punteggio.
     * 
     * @param event      evento di pessione del bottone gameOver
     * @throws IOException 
     */
    @FXML
    public void gameOverBtn(ActionEvent event) throws IOException {
        ((Node)event.getSource()).getScene().getWindow().hide(); // Faccio in modo che non si creino molteplici finestre ripetute
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Game.class.getResource("view/Score.fxml"));
        loader.load();

        Parent scoreRoot = loader.getRoot();
        Scene scoreScene = new Scene(scoreRoot);

        Stage scoreStage = new Stage();
        scoreStage.setScene(scoreScene); // La imposto come scena della finestra che creo

        ScoreController scoreController = loader.getController();
        scoreController.setScoreFrame(totalScore, playerObject);

        scoreStage.setTitle("Score Page");
        scoreStage.setResizable(false);
        scoreStage.show();
    }
   
    /**
     * 
     * Timeline del gioco. Formata da 7 parti principali:
     * 
     * PARTE 1: gestisco la direzione dell'aereo facendolo avanzare a destra o sinistra.
     * PARTE 2: gestisco l'eventualità in cui viene premuto (o rilasciato) un pulsate da tastiera (freccia sx, dx, enter).
     * PARTE 3: gestisco il caso in cui uno o più proiettili siamo sparati, e determino il percorso che dovrà effettuare.
     * PARTE 4: gestisco l'eventualità in cui i proiettili superino i bordi della finestra di gioco.
     * PARTE 5: gestisco l'eventualità in cui l'aereo superi i bordi della finestra di gioco.
     * PARTE 6: gestisco l'eventualità in cui un proiettile colpisca l'aereo.
     * PARTE 7: se ho già sparato 10 volte fermo il gioco.
     * 
     */
    private void gameTimeline() {
        final Timeline loop = new Timeline(new KeyFrame(Duration.seconds(0.0075), new EventHandler<ActionEvent>() {
            double countFrames = 0; // Utilizzata per decidere quando sparare più proiettili in sequenza
            double cannonAngle = 90; // Angolo del cannone
            double shotAngle = 90; // Angolo da cui il proiettile è stato sparato (questo per evitare che il movimento del cannone dopo lo sparo, influenzi la traiettoria del proiettile in movimento)
            boolean movingBullet = false; // Proiettile/i in movimento
            double x = 0; // Variabile x della funzione parabola data nel testo del progetto
            int numBulletsShot = 10; // Massimo numero di proiettili sparabili
            int numPlanesDropped = 0; // Numero di aerei colpiti

            @Override
            public void handle(final ActionEvent t) {

                // PARTE 1: gestisco la direzione dell'aereo facendolo avanzare a destra o sinistra
                if (planeStartingSide.equals("L")) {
                    if(planeObject.getIcon().equals("Plane 1")) { // Distinguo se l'aereo è una forma o un'icona
                        planeShape.setX(planeShape.getX() + planeObject.getPlaneSpeed());
                        planeXCoordinate.setText("" + (int)planeShape.getX());
                        planeYCoordinate.setText("" + (int)planeShape.getY());
                    } else {
                        planeIcon.setX(planeIcon.getX() + planeObject.getPlaneSpeed());
                        planeXCoordinate.setText("" + (int)planeIcon.getX());
                        planeYCoordinate.setText("" + (int)planeIcon.getY());
                    }
                }
                if (planeStartingSide.equals("R")) {
                    if(planeObject.getIcon().equals("Plane 1")) {
                        planeShape.setX(planeShape.getX() - planeObject.getPlaneSpeed());
                        planeXCoordinate.setText("" + (int)planeShape.getX());
                        planeYCoordinate.setText("" + (int)planeShape.getY());
                    } else {
                        planeIcon.setX(planeIcon.getX() - planeObject.getPlaneSpeed());
                        planeXCoordinate.setText("" + (int)planeIcon.getX());
                        planeYCoordinate.setText("" + (int)planeIcon.getY());
                    }
                }

                // PARTE 2: gestisco l'eventualità in cui viene premuto (o rilasciato) un pulsate da tastiera (freccia sx, dx, enter)

                scene.setOnKeyPressed(event -> { // Pressione di un pulsante da tastiera

                    if(event.getCode().equals(KeyCode.LEFT) && cannonAngle>20) { // Freccia sinistra e angolo maggiore di 20°
                        cannonTransition.setFromAngle(cannonAngle);
                        cannonTransition.setToAngle(cannonAngle -= 2);
                        cannonTransition.play();
                        cannonAngleLabel.setText(cannonAngle + "°");
                    }
                    if(event.getCode().equals(KeyCode.RIGHT) && cannonAngle<160) { // Freccia destra e angolo minore di 160°
                        cannonTransition.setFromAngle(cannonAngle);
                        cannonTransition.setToAngle(cannonAngle += 2);
                        cannonTransition.play();
                        cannonAngleLabel.setText(cannonAngle + "°");
                    }
                    if(event.getCode().equals(KeyCode.ENTER) && !movingBullet) { // Pressione enter e il proiettile ha dato l'esito colpito aereo/uscito dalla finestra (ovvero non sta volando)
                        countFrames = 0; // Azzero il conto dei frame trascorsi
                        shotAngle = cannonAngle;

                        // Definisco il punto di partenza del proiettile
                        double startBulletX = Formulas.startingPointBulletX(CANNON_WIDTH, shotAngle) + (FRAME_WIDTH/2-BULLET_RADIUS/2);
                        double startBulletY = Formulas.startingPointBulletY(CANNON_WIDTH, shotAngle) + FRAME_HEIGHT - (CANNON_HEIGHT/2+BULLET_RADIUS/2);

                        // Imposto il/i proiettile/i in posizione di partenza
                        bullet1Icon.setX(startBulletX);
                        bullet1Icon.setY(startBulletY);

                        if (bulletObject.getBulletsNum() >= 2) { // Se sono 2 o più
                            bullet2Icon.setX(startBulletX);
                            bullet2Icon.setY(startBulletY);
                        }
                        if (bulletObject.getBulletsNum() == 3) { // Se sono 3
                            bullet3Icon.setX(startBulletX);
                            bullet3Icon.setY(startBulletY);
                        }

                        numBulletsShot--; // Decremento i proiettili sparati
                        x = 0; // Faccio partire da 0 la ariabile x della funzione
                        movingBullet = true; // Indico che il proiettile è in movimento
                        shotsLabel.setText("" + numBulletsShot);
                    }
                });

               scene.setOnKeyReleased(event -> { // Rilascio dei pulsanti da tastiera (è stato aggiunto per completezza)

                  if(event.getCode().equals(KeyCode.LEFT)) { }
                  if(event.getCode().equals(KeyCode.RIGHT)) { }
                  if(event.getCode().equals(KeyCode.SPACE)) { }

               });

               // PARTE 3: gestisco il caso in cui uno o più proiettili siamo sparati, e determino il percorso che dovrà effettuare

                if (movingBullet) { // Il proiettile è in movimento (pressione tasto enter)
                    if (shotAngle <= 90) { // Angolo di sparo minore uguale di 90°
                        // Calcolo la y della funzione parabola data nel testo
                        double y = Formulas.gravityParabolaYValue(shotAngle, bulletObject.getBulletSpeed(), x);

                        double intersection = Formulas.gravityParabolaXIntersection(shotAngle, bulletObject.getBulletSpeed());
                        double increment = intersection/(shotAngle*200/90); // Incrementa la coordinata X per il moto del proiettile. Lo spazio percorso diviso un numero variabile di frame calcolato sulla base di 200 frame per i 90 gradi, ovvero 1.5 secondi (90:200=angolo:x)

                        bullet1Icon.setX(bullet1Icon.getX() - x*100); // 100 moltiplicatore
                        bullet1Icon.setY(bullet1Icon.getY() - y*100); // 100 moltiplicatore

                        if (bulletObject.getBulletsNum() >= 2 && countFrames >= 20) { // Se i proiettili sono 2
                           bullet2Icon.setX(bullet2Icon.getX() - (x-increment)*100); // 100 moltiplicatore
                           bullet2Icon.setY(bullet2Icon.getY() - y*100); // 100 moltiplicatore
                        }
                        if (bulletObject.getBulletsNum() == 3 && countFrames >= 27.5) { // Se i proiettili sono 3
                           bullet3Icon.setX(bullet3Icon.getX() - (x-(increment*2))*100); // 100 moltiplicatore
                           bullet3Icon.setY(bullet3Icon.getY() - y*100); // 100 moltiplicatore
                        }

                        bulletXCoordinate.setText("" + (int)bullet1Icon.getX());
                        bulletYCoordinate.setText("" + (int)bullet1Icon.getY());

                        x += increment;
                    } else { // Angolo di sparo maggiore di 90°
                       // Calcolo la y della funzione parabola data nel testo
                       double y = Formulas.gravityParabolaYValue(-shotAngle, bulletObject.getBulletSpeed(), x);

                       double intersection = Formulas.gravityParabolaXIntersection(-shotAngle, bulletObject.getBulletSpeed());
                       double increment = intersection/((180-shotAngle)*200/90); // Lo spazio percorso diviso un numero variabile di frame calcolato sulla base di 200 frame per i 90 gradi, ovvero 1 secondo (90:200=angolo:x)

                       bullet1Icon.setX(bullet1Icon.getX() + x*100); // 100 moltiplicatore
                       bullet1Icon.setY(bullet1Icon.getY() - y*100); // 100 moltiplicatore

                       if (bulletObject.getBulletsNum() >= 2 && countFrames >= 20) {
                          bullet2Icon.setX(bullet2Icon.getX() + (x-increment)*100); // 100 moltiplicatore
                          bullet2Icon.setY(bullet2Icon.getY() - y*100); // 100 moltiplicatore
                       }
                       if (bulletObject.getBulletsNum() == 3 && countFrames >= 27.5) {
                          bullet3Icon.setX(bullet3Icon.getX() + (x-(increment*2))*100); // 100 moltiplicatore
                          bullet3Icon.setY(bullet3Icon.getY() - y*100); // 100 moltiplicatore
                       }

                       bulletXCoordinate.setText("" + (int)bullet1Icon.getX());
                       bulletYCoordinate.setText("" + (int)bullet1Icon.getY());

                       x += increment;
                    }
                }

                // PARTE 4: gestisco l'eventualità in cui i proiettili superino i bordi della finestra di gioco

                // Scelgo l'ultimo proiettile come proiettile da controlloare in caso superi i bordi
                ImageView lastBullet = new ImageView();
                if(bulletObject.getBulletsNum() == 1) 
                   lastBullet = bullet1Icon;
                if(bulletObject.getBulletsNum() == 2) 
                   lastBullet = bullet2Icon;
                if(bulletObject.getBulletsNum() == 3) 
                   lastBullet = bullet3Icon;

                // Se l'ultimo proiettile supera i bordi fermo il gioco
                final boolean atRightBorder = lastBullet.getX() > (FRAME_WIDTH + BULLET_RADIUS);
                final boolean atLeftBorder = lastBullet.getX() < (-BULLET_RADIUS);
                final boolean atBottomBorder = lastBullet.getY() > (FRAME_HEIGHT + BULLET_RADIUS);
                final boolean atTopBorder = lastBullet.getY() < (-BULLET_RADIUS);

                if (atRightBorder || atLeftBorder || atBottomBorder || atTopBorder)
                   movingBullet = false;

                // PARTE 5: gestisco l'eventualità in cui l'aereo superi i bordi della finestra di gioco

                final boolean atLeftBorderPlane;
                final boolean atRightBorderPlane;

                if(planeObject.getIcon().equals("Plane 1")) { // Distinguo se l'aereo è una forma o un'icona
                   atLeftBorderPlane = planeShape.getX() <= -PLANE_WIDTH;
                   atRightBorderPlane = planeShape.getX() >= FRAME_WIDTH;
                } else {
                   atLeftBorderPlane = planeIcon.getX() <= -PLANE_WIDTH;
                   atRightBorderPlane = planeIcon.getX() >= FRAME_WIDTH;
                }


                if (atLeftBorderPlane) {
                   // Gira l'aereo ma sulla stessa y e stessa velocità
                   if(planeObject.getIcon().equals("Plane 1")) // Distinguo se l'aereo è una forma o un'icona
                      setPlaneView(0, (int) planeShape.getY());
                   else 
                      setPlaneView(0, (int) planeIcon.getY());
                } else if (atRightBorderPlane) {
                   // Gira l'aereo ma sulla stessa y e stessa velocità
                   if(planeObject.getIcon().equals("Plane 1")) 
                      setPlaneView(1, (int) planeShape.getY());
                   else 
                      setPlaneView(1, (int) planeIcon.getY());
                }

                // PARTE 6: gestisco l'eventualità in cui un proiettile colpisca l'aereo

                boolean result1 = false;
                boolean result2 = false;
                boolean result3 = false;

                if (bulletObject.getBulletsNum() >= 1) {
                    // Con queste 4 variabili indico i punti in cui se il poiettile e l'aereo si incontrano, l'aereo è colpito
                    // BULLET 1
                    boolean leftBottomCornerY1;
                    boolean leftTopCornerY1;
                    boolean leftBottomCornerX1;
                    boolean rightBottomCornerX1;

                    if(planeObject.getIcon().equals("Plane 1")) { // Distinguo se l'aereo è una forma o un'icona
                        leftBottomCornerY1 = bullet1Icon.getY() <= planeShape.getY() + PLANE_HEIGHT;
                        leftTopCornerY1 = bullet1Icon.getY() >= planeShape.getY();
                        leftBottomCornerX1 = bullet1Icon.getX() >= planeShape.getX();
                        rightBottomCornerX1 = bullet1Icon.getX()+BULLET_RADIUS <= planeShape.getX() + PLANE_WIDTH;
                    } else {
                        leftBottomCornerY1 = bullet1Icon.getY() <= planeIcon.getY() + PLANE_HEIGHT;
                        leftTopCornerY1 = bullet1Icon.getY() >= planeIcon.getY();
                        leftBottomCornerX1 = bullet1Icon.getX() >= planeIcon.getX();
                        rightBottomCornerX1 = bullet1Icon.getX()+BULLET_RADIUS <= planeIcon.getX() + PLANE_WIDTH;
                    }

                    result1 = (leftBottomCornerY1 && leftTopCornerY1) && (leftBottomCornerX1 && rightBottomCornerX1);
                }

                if (bulletObject.getBulletsNum() >= 2) {
                    // BULLET 2
                    boolean leftBottomCornerY2;
                    boolean leftTopCornerY2;
                    boolean leftBottomCornerX2;
                    boolean rightBottomCornerX2;

                    if(planeObject.getIcon().equals("Plane 1")) {
                       leftBottomCornerY2 = bullet2Icon.getY() <= planeShape.getY() + PLANE_HEIGHT;
                       leftTopCornerY2 = bullet2Icon.getY() >= planeShape.getY();
                       leftBottomCornerX2 = bullet2Icon.getX() >= planeShape.getX();
                       rightBottomCornerX2 = bullet2Icon.getX()+BULLET_RADIUS <= planeShape.getX() + PLANE_WIDTH;
                    } else {
                       leftBottomCornerY2 = bullet2Icon.getY() <= planeIcon.getY() + PLANE_HEIGHT;
                       leftTopCornerY2 = bullet2Icon.getY() >= planeIcon.getY();
                       leftBottomCornerX2 = bullet2Icon.getX() >= planeIcon.getX();
                       rightBottomCornerX2 = bullet2Icon.getX()+BULLET_RADIUS <= planeIcon.getX() + PLANE_WIDTH;
                    }

                    result2 = (leftBottomCornerY2 && leftTopCornerY2) && (leftBottomCornerX2 && rightBottomCornerX2);
                }

                if (bulletObject.getBulletsNum() == 3) {
                    // BULLET 3
                    boolean leftBottomCornerY3;
                    boolean leftTopCornerY3;
                    boolean leftBottomCornerX3;
                    boolean rightBottomCornerX3;

                    if(planeObject.getIcon().equals("Plane 1")) {
                       leftBottomCornerY3 = bullet3Icon.getY() <= planeShape.getY() + PLANE_HEIGHT;
                       leftTopCornerY3 = bullet3Icon.getY() >= planeShape.getY();
                       leftBottomCornerX3 = bullet3Icon.getX() >= planeShape.getX();
                       rightBottomCornerX3 = bullet3Icon.getX()+BULLET_RADIUS <= planeShape.getX() + PLANE_WIDTH;
                    } else {
                       leftBottomCornerY3 = bullet3Icon.getY() <= planeIcon.getY() + PLANE_HEIGHT;
                       leftTopCornerY3 = bullet3Icon.getY() >= planeIcon.getY();
                       leftBottomCornerX3 = bullet3Icon.getX() >= planeIcon.getX();
                       rightBottomCornerX3 = bullet3Icon.getX()+BULLET_RADIUS <= planeIcon.getX() + PLANE_WIDTH;
                    }

                    result3 = (leftBottomCornerY3 && leftTopCornerY3) && (leftBottomCornerX3 && rightBottomCornerX3);
                }

                // Se almeno uno dei proiettili sparati colpisce l'aereo
                if (result1 || result2 || result3) {
                    // Tolgo dalla scena l'aereo colpito
                    if (planeObject.getIcon().equals("Plane 1")) { // Distinguo se l'aereo è una forma o un'icona
                       root.getChildren().remove(planeShape);
                       SMASH_AUDIO.play(); // Emetto il suono di quando un aereo è stato colpito
                    } else {
                       root.getChildren().remove(planeIcon); 
                       SMASH_AUDIO.play();
                    }

                    setPlaneView(-1, -1); // Nuovo aereo in posizione random
                    numPlanesDropped++; // Incremento gli aerei colpiti
                    planesLabel.setText("" + numPlanesDropped);
                }

                // PARTE 7: se ho già sparato 10 volte fermo il gioco

                if (numBulletsShot == -1) {
                    gameOver.fire();
                    numBulletsShot--;
                }

                // PARTE STATICA: (è una fase a se stante) conto i punti

                if (10-numBulletsShot > 0) {
                   totalScore = (int) ((numPlanesDropped*1000/(10-numBulletsShot))*scoreWeight); // Punteggio equo
                   scoreLabel.setText("" + totalScore);
                }

                countFrames++; // Contro i frame di tempo che passano per la distanza tra un proiettile e l'altro
            }
        }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();

        root.getChildren().addAll(cannon, arc, base); // Aggiungi gli elementi alla finestra
    }
}
