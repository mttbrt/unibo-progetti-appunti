/* Classe per le impostazioni del gioco*/
package game.model;

/**
 * Classe che rappresenta l'insieme delle informazioni necessarie per impostare una partita.
 */

public class SetGame {
    /**
     * Giocatore che ha effettuato il login.
     */
    private Player settedPlayer;
    /**
     * Layout selezionato per l'aereo.
     */
    private Plane settedPlane;
    /**
     * Impostazioni dei proiettili (numero e velocità).
     */
    private Shot settedBullet;

    /**
     * Costruttore di default. 
     * 
     * Non necessario, ma per completezza è stato aggiunto.
     */
    public SetGame() {
       new SetGame(null, null, null);
    }

    /**
     * 
     * Costruttore: imposta il giocatore, l'aereo e le modalità dei proiettili selezionate.
     * 
     * @param settedPlayer     giocatore selezionato
     * @param settedPlane      aereo selezionato dall'utente
     * @param settedBullet     impostazioni dei proiettili selezionate dall'utente
     */
    public SetGame(Player settedPlayer, Plane settedPlane, Shot settedBullet) {
       this.settedPlayer = settedPlayer;
       this.settedPlane = settedPlane;
       this.settedBullet = settedBullet;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return giocatore impostato
     */
    public Player getSettedPlayer() {
       return settedPlayer;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return aereo impostato
     */
    public Plane getSettedPlane() {
       return settedPlane;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return impostazioni proiettili impostate
     */
    public Shot getSettedBullet() {
       return settedBullet;
    }
}
