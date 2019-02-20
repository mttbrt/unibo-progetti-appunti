/**
 * Matteo
 * Berti
 *
 * CONSIDERAZIONI:
 * Utilizzando la programmazione lineare si riesce ad avere un costo O(d*n^2) con d = numero giorni e n = numero nodi se si considera un DAG in cui ogni nodo punta a tutti gli altri.
 * Tuttavia tale tipo di grafo estremamente denso è inusuale, specialmente "i grafi derivanti da reti stradali e luoghi sono spesso sparsi"*.  [*fonte: internet]
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Esercizio4 {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("Type:\n$  java -cp . Esercizio4 <file_input> <file_output>");
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


        try {
            String firstLine = inputStream.readLine();
            String[] graphInfo = firstLine.split(" ");
            int n = Integer.parseInt(graphInfo[0]); // numero di nodi
            int m = Integer.parseInt(graphInfo[1]); // numero di archi
            int days = Integer.parseInt(graphInfo[2]); // numero di giorni di vacanza
            int start = Integer.parseInt(graphInfo[3]); // nodo di partenza
            int end = Integer.parseInt(graphInfo[4]); // nodo di arrivo
            Node[] nodes = new Node[n]; // nodi presenti nel grafo

            for(int u = 0; u < n; u++) // Nodi
                nodes[u] = new Node(u, Integer.parseInt(inputStream.readLine()));

            for(int v = 0; v < m; v++) { // Archi
                String line = inputStream.readLine();
                String[] edge = line.split(" ");
                int s = Integer.parseInt(edge[0]), t = Integer.parseInt(edge[1]), w = Integer.parseInt(edge[2]); // sorgente, destinazione, peso
                if(s != t) // Nodi che puntano a se stessi non sono accettati, salvo i nodi adiacenti di ogni nodo
                    nodes[s].adjacents.add(new Edge(nodes[s], nodes[t], w));
            }

            ArrayList<Integer> solution = getBestItinerary(nodes, days, start, end);

            // Stampo la soluzione
            for(int e = solution.size()-1; e >= 0; e--)
                outputStream.println(solution.get(e));

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

    /**
     * Questo metodo utilizza una matrice che ha come colonne i giorni e come righe i nodi.
     * In ogni colonna viene infatti mostrato per ogni nodo la massima soddisfazione raggiungibile fino a quel giorno, da s fino a quello specifico nodo.
     * In questo modo dopo g giorni al nodo t si avrà la massima soddisfazione da un nodo s a quel nodo.
     * Si parte il primo giorno avendo tutti i nodi a -Infinito tranne quello di partenza che ha il proprio valore di soddisfazione, si procede poi ogni giorno per ogni nodo a valutare se conviene rimanere un giorno lì o spostarsi in ognuno degli altri nodi, quando una scelta conviene si sotituisce il valore nella giusta casella.
     * Man mano che trovo le combinazioni massimali salvo il padre (ovvero la posizione del nodo che mi ha permesso di raggiungere tale valore), così risalendo all'indietro giorno dopo giorno trovo la sequenza ottimale.
     *
     * @param nodes     Lista con i nodi del grafo
     * @param days      Numero di giorni di vacanza
     * @param start     Nodo di inizio
     * @param end       Nodo di fine
     * @return          Lista con i nodi in cui si sosterà per ottenere la massima soddisfazione
     */
    private static ArrayList<Integer> getBestItinerary(Node[] nodes, int days, int start, int end) {
        Data[][] matrix = new Data[nodes.length][days];
        ArrayList<Integer> solutions = new ArrayList<Integer>();

        for(int b = 0; b < nodes.length; b++) // Inizializzo la matrice al giorno 0: il nodo di partenza ha il suo valore di soddisfazione, gli altri -Infinito
            matrix[b][0] = b != start ? new Data(Double.NEGATIVE_INFINITY, b) : new Data(nodes[b].satisfaction, b);

        for(int i = 1; i < days; i++) {
            for(int c = 0; c < nodes.length; c++) // Inizializzo i nodi con il valore che avrebbero se si rimanesse un giorno qui
                matrix[c][i] = new Data(matrix[c][i-1].maxValue + nodes[c].satisfaction, c);
            for(int j = 0; j < nodes.length; j++) {
                if(matrix[j][i-1].maxValue != Double.NEGATIVE_INFINITY) { // Nel nuovo giorno analizzo i nodi che il giorno precedente sono stati adiacenti di qualcuno, perchè se non sono stati adiacenti di qualcuno significa che non possono essere raggiunti
                    for(Edge e : nodes[j].adjacents) { // Se conviene modifico i pesi dei nodi raggiungibili
                        int adjPos = e.t.id; // Posizione in matrice del nodo di arrivo di quest'arco
                        double valueThisDay = matrix[e.s.id][i-1].maxValue + e.t.satisfaction - e.effort; // Il valore giornaliero è dato dal valore del nodo sorgente del giorno prima, più la soddisfazione che da questo nodo per un giorno, meno il costo dell'arco per arrivarci
                        if(valueThisDay > matrix[adjPos][i].maxValue) // Se conviene rispetto al valore attualmente presente lo sostituisco
                            matrix[adjPos][i] = new Data(valueThisDay, e.s.id);
                    }
                }
            }
        }

        System.out.println("Soddisfazione totale: " + matrix[end][days-1].maxValue);
        // Salvo le soluzioni
        int step = end;
        double last = -1;
        for(int d = days-1; d > 0; d--, step = matrix[step][d].parent)
            if(last != step) { // Salvo solo se c'è un cambio di città
                solutions.add(step);
                last = step;
            }

        return solutions;
    }

    private static class Node {
        int id;
        int satisfaction;
        ArrayList<Edge> adjacents = new ArrayList<Edge>();

        Node(int id, int satisfaction) {
            this.id = id;
            this.satisfaction = satisfaction;
        }
    }

    private static class Edge {
        Node s;
        Node t;
        int effort;

        Edge(Node s, Node t) {
            this.s = s;
            this.t= t;
            this.effort = 0;
        }

        Edge(Node s, Node t, int effort) {
            this.s = s;
            this.t= t;
            this.effort = effort;
        }

        @Override
        public boolean equals(Object object) {
            boolean isEqual= false;

            if (object != null && object instanceof Edge)
                isEqual = (this.s.id == ((Edge) object).s.id) && (this.t.id == ((Edge) object).t.id);

            return isEqual;
        }
    }

    private static class Data {
        double maxValue;
        int parent;

        Data(double maxValue, int parent) {
            this.maxValue = maxValue;
            this.parent = parent;
        }
    }

}
