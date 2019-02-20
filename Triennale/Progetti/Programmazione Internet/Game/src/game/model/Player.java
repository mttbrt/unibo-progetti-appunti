package game.model;

/**
 * Classe che rappresenta il giocatore.
 */

public class Player {
    /**
     * Nome del giocatore.
     */
    private String name;
    /**
     * Cognome del giocatore.
     */
    private String surname;

    /**
     * Costruttore di default. 
     * 
     * Non necessario, ma per completezza è stato aggiunto.
     */
    public Player() {
       new Player(null, null);
    }

    /**
     * 
     * Costruttore: imposta nome e cognome del giocatore.
     * 
     * @param name
     * @param surname 
     */
    public Player(String name, String surname) {
       this.name = name;
       this.surname = surname;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return nome giocatore
     */
    public String getName() {
       return name;
    }

    /**
     * 
     * Metodo getter.
     * 
     * @return cognome giocatore
     */
    public String getSurname() {
       return surname;
    }
}
