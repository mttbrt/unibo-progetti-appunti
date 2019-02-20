package game.model;

import java.util.Random;

/**
 * Classe che idealizza il concetto di aereo.
 * Che consiste in un icona (scelta dall'utente) ed una velocità (variabile a seconda dei casi).
 */

public class Plane {
    /**
     * Stringa che identifica l'icona dell'aereo.
     */
    private String icon;
    /**
     * Variabile che mi indica la velocità.
     */
    private double speed;

    /**
     * Costruttore di default. 
     * 
     * Non necessario, ma per completezza è stato aggiunto.
     */
    public Plane() {
        new Plane(null);
        speed = 0;
    }

    /**
     * 
     * Costruttore: imposta l'icona dell'aereo.
     * 
     * @param icon    stringa che indica l'icona.
     */
    public Plane(String icon) {
        this.icon = icon;
    }

    /**
     * Crea una velocità random per l'aereo.
     * Solitamente utilizzato in caso sia stato colpito o inizi il gioco.
     * 
     * @return velocià aereo.
     */
    public double randomSpeed() {
        Random random = new Random();
        speed = random.nextInt(3);

        if(speed == 0) {
           speed = 1;
        } else if(speed == 1) {
           speed = 1.5;
        } else if(speed == 2) {
           speed = 2;
        }

        return speed;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return icona dell'aereo.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return velocità dell'aereo.
     */
    public double getPlaneSpeed() {
        return speed;
    }
   
}
