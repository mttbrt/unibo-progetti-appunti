// Classe che estende giocatore: aggiunge i punti fatti nella partita e la data della giocata

package game.model;

/**
 * Classe che idealizza il concetto di giocatore dopo una giocata, comprensivo di punteggio e data.
 * Estende la classe Player, aggiungendovi i punti fatti e la data della giocata.
 */

public class PlayerScore extends Player {

    /**
     * Punteggio effettuato.
     */
    private int score;
    /**
     * Data della giocata.
     */
    private String date;

    /**
     * Costruttore di default. 
     * 
     * Non necessario, ma per completezza è stato aggiunto.
     */
    public PlayerScore() {
        new PlayerScore(null, null, -1, null);
    }

    /**
     * 
     * Costruttore: imposta nome cognome richiamando la classe padre, il punteggio e la data
     * 
     * @param name
     * @param surname
     * @param score
     * @param date 
     */
    public PlayerScore(String name, String surname, int score, String date) {
        super (name, surname);
        this.score = score;
        this.date = date;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return punteggio effettuato dal giocatore.
     */
    public int getScore() {
        return score;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return data della giocata.
     */
    public String getDate() {
        return date;
    }
}
