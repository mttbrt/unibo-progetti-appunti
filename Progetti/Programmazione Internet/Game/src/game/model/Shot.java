package game.model;

/**
 * Classe che rappresenta il concetto di sparo del cannone. 
 * 
 * Può essere costituito da vari proiettili in gruppo e ha varie velocità.
 */

public class Shot {
    /**
     * Numero di proiettili presenti in un colpo del cannone.
     */
    private int bulletsNum;
    /**
     * Velocità dei proiettili sparati.
     */
    private double speed;

    /**
     * Costruttore di default. 
     * 
     * Non necessario, ma per completezza è stato aggiunto.
     */
    public Shot() {
       new Shot(0, 0);
    }

    /**
     * 
     * Costruttore: imposta il numero di proiettili in uno sparo e la velocità di ogni singolo proiettile.
     * 
     * @param bulletsNum numero di proiettili in uno sparo di cannone
     * @param speed velocità dei proiettili sparati
     */
    public Shot(int bulletsNum, double speed) {
       this.bulletsNum = bulletsNum;
       this.speed = speed;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return numero di proiettili in uno sparo del cannone.
     */
    public int getBulletsNum() {
       return bulletsNum;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return velocità di ogni proiettile.
     */
    public double getBulletSpeed() {
       return speed;
    }
}
