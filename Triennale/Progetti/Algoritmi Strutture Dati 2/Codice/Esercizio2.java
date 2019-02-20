/**
 * Matteo
 * Berti
 *
 * CONSIDERAZIONI:
 * Ho scelto di utilizzare un albero bilanciato AVL in quanto garantisce complessità O(logn) nel caso pessimo per l'operazione di inserimento (che è l'unica di cui si ha bisogno in questo esercizio).
 * In totale si ha O(nlogn) in quanto il recupero delle posizioni dei pazienti richiede n operazioni da logn.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Esercizio2 {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("Type:\n$  java -cp . Esercizio2 <file_input> <file_output>");
            System.exit(1);
        }

        // Input
        String fileInput = args[0];
        BufferedReader inputStream = null;
        FileReader reader = null;

        // Output
        String fileOutput = args[1];
        PrintWriter outputStream = null;
        FileOutputStream writer = null;

        try {
            reader = new FileReader(fileInput);
            inputStream = new BufferedReader(reader);
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException in the input stream.");
            System.exit(1);
        }

        try {
            writer = new FileOutputStream(fileOutput, true);
            outputStream = new PrintWriter(writer);
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException in the output stream.");
            System.exit(1);
        }

        BinaryTree hospitalTree = new BinaryTree();

        try {
            int patientsNum = Integer.parseInt(inputStream.readLine());

            for(int k = 0; k < patientsNum; k++) {
                String line = inputStream.readLine();
                String[] values = line.split(" ");
                hospitalTree.insert(Integer.parseInt(values[0]), Double.parseDouble(values[1]));
            }

            for(int pos : hospitalTree.getPositions())
                outputStream.println(pos);

        } catch (IOException e) {
            System.err.println("Error reading " + fileInput);
            System.exit(1);
        }

        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Exception in closing I/O streams.");
            System.exit(1);
        }

    }

}

/**
 * Questa classe rappresenta un albero binario di ricerca AVL che ha complessità O(logn) per le principali operazioni
 */
class BinaryTree {

    private Patient root = null;
    private int idPatient = 1; // I pazienti sono identificati da un valore 1..n
    private ArrayList<Integer> positions = new ArrayList<>(); // array con le posizioni dei pazienti in ordine di visita (soluzione)

    public void insert(int priority, double waitingTime) {
        root = insert(root, priority, waitingTime, idPatient++);
    }

    /**
     * Metodo che ricorsivamente partendo dalla radice fino alla giusta posizione, aggiunge un paziente all'albero.
     * Un nodo da inserire va a sinistra di un altro se ha priorità minore oppure priorità uguale ma tempo di attesa maggiore.
     * In questo modo con una stampa decrescente dei nodi ottengo il corretto ordine.
     *
     * @param node          Il paziente che si sta valutando al momento (essendo ricorsivo)
     * @param priority      La priorità del nuovo paziente da inserire
     * @param waitingTime   Il tempo che questo paziente approssimativamente impiegherà per la visità
     * @param idPat         L'identificativo attribuito al nuovo paziente
     * @return              Il paziente radice dell'albero
     */
    private Patient insert(Patient node, int priority, double waitingTime, int idPat) {

        if (node == null)
            return new Patient(priority, waitingTime, idPat);

        if (priority < node.priority || (priority == node.priority && waitingTime > node.waitingTime))
            node.left = insert(node.left, priority, waitingTime, idPat);
        else if (priority > node.priority || (priority == node.priority && waitingTime < node.waitingTime))
            node.right = insert(node.right, priority, waitingTime, idPat);
        else
            return node;

        // Il fattore di bilanciamento lo calcolo sottraendo al livello destro, il livello sinistro
        int balanceFactor = node == null ? 0 : getLevel(node.right) - getLevel(node.left);
        // Aggiorno il livello del nodo prendendo il maggiore tra i livelli dei figli e aggiungo 1 che è il livello di questo nodo
        node.level = ((getLevel(node.left) > getLevel(node.right)) ? getLevel(node.left) : getLevel(node.right)) + 1;

        // Se il nodo è sbilanciato effettuo le giuste rotazioni
        if(balanceFactor < -1 && node.left != null) { // Si ha uno sbilanciamento a sinistra
            if(priority < node.left.priority || (priority == node.left.priority && waitingTime > node.left.waitingTime))
                return rotateRight(node);
            else if(priority > node.left.priority || (priority == node.left.priority && waitingTime < node.left.waitingTime)) {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        } else if(balanceFactor > 1 && node.right != null) { // Si ha uno sbilanciamento a destra
            if(priority > node.right.priority || (priority == node.right.priority && waitingTime < node.right.waitingTime))
                return rotateLeft(node);
            else if(priority < node.right.priority || (priority == node.right.priority && waitingTime > node.right.waitingTime)) {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }

        return node;
    }

    /**
     * Effettua una rotazione a destra partendo da un nodo e restituendo il nuovo nodo che sarà in posizione di quello di partenza
     *
     * @param start     Nodo su cui partirà la rotazione a destra
     * @return          Nodo su cui che fatto da cardine alla rotazione
     */
    private Patient rotateRight(Patient start) {
        if(start.left == null)
            return start;

        Patient pivot = start.left;
        Patient Rpivot = pivot.right;

        // Rotazione e aggiornamento dei livelli dei nodi
        pivot.right = start;
        start.left = Rpivot;
        start.level = ((getLevel(start.left) > getLevel(start.right)) ? getLevel(start.left) : getLevel(start.right)) + 1;
        pivot.level = ((getLevel(pivot.left) > getLevel(pivot.right)) ? getLevel(pivot.left) : getLevel(pivot.right)) + 1;

        return pivot;
    }

    /**
     * Effettua una rotazione a sinistra partendo da un nodo e restituendo il nuovo nodo che sarà in posizione di quello di partenza
     *
     * @param start     Nodo su cui partirà la rotazione a sinistra
     * @return          Nodo su cui che fatto da cardine alla rotazione
     */
    private Patient rotateLeft(Patient start) {
        if(start.right == null)
            return start;

        Patient pivot = start.right;
        Patient Lpivot = pivot.left;

        // Rotazione e aggiornamento dei livelli dei nodi
        pivot.left = start;
        start.right = Lpivot;
        pivot.level = ((getLevel(pivot.left) > getLevel(pivot.right)) ? getLevel(pivot.left) : getLevel(pivot.right)) + 1;
        start.level = ((getLevel(start.left) > getLevel(start.right)) ? getLevel(start.left) : getLevel(start.right)) + 1;

        return pivot;
    }

    private int getLevel(Patient patient) {
        return patient != null ? patient.level : 0;
    }

    /**
     * Metodo che invocando printTree() che attraversa il nodo partendo dal nodo di valore massimo fino la minimo, restituisce la lista di pazienti in ordine corretto
     * @return  posizioni dei pazienti in ordine di priorità (alta - bassa)
     */
    public ArrayList<Integer> getPositions() {
        System.out.println("ID - Prior | WaitTime\t[Left ; Right]");
        printTree(root);
        return positions;
    }

    private void printTree(Patient patient) {
        if(patient != null) {
            printTree(patient.right);
            positions.add(patient.idPatient);
            System.out.println(patient.idPatient + " - " + patient.priority + " | " + patient.waitingTime + "\t[" + (patient.left != null ? patient.left.priority + "(" + patient.left.waitingTime + ")" : "nil") + ";" + (patient.right != null ? patient.right.priority + "(" + patient.right.waitingTime + ")" : "nil") + "]");
            printTree(patient.left);
        }
    }

    /**
     * Un paziente è un nodo dell'albero e memorizza la propria priorità, il tempo previsto per la visita, l'identificativo che gli viene attribuito, i nodi sinistro e destro
     * e il livello (con livello si intende la sua distanza al termine dell'albero, se è foglia ha 1 se è un nodo con un figlio è 2, se è un nodo con un figlio con livello 2 e uno con livello 1 è 3, si prende il max + se stesso)
     */
    private class Patient {
        int priority, idPatient, level = 1;
        double waitingTime;
      	Patient left, right = null;

      	Patient(int priority, double waitingTime, int idPatient) {
            this.priority = priority;
            this.waitingTime = waitingTime;
            this.idPatient = idPatient;
        }
    }

}
