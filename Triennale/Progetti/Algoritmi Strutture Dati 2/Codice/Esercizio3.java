/**
 * Matteo
 * Berti
 *
 * CONSIDERAZIONI:
 * Ho implementato l'algoritmo di Kruskal per trovare il MST ma invece che aggiungere un arco all'albero se è corretto, aggiungo il peso dell'arco a una sommatoria se non è corretto.
 * In questo modo al termine avrò la somma dei pesi degli archi che non sono stati utilizzati per il Minimum Spanning Tree del grafo.
 * Utilizzo un QuickFind in quanto vengono effettuati 2m find e (n-1) merge, avendo in tutti i file di input numero di archi (m) > numero di nodi (n), è più conveniente.
 * Ho anche implementato la versione di Prim e mostrato a schermo il risultato (mentre Kruskal su file), questo perchè nel caso specifico di grafi densi, in cui il numero di archi è molto maggiore al numero di nodi,
 * sarebbe stata preferibile un'implementazione dell'algoritmo di Prim con Fibonacci Heap (invece che Priority Queue) che avrebbe assicurato un O(m + nlogn) invece che l'attuale O(mlogn).
 * Tuttavia la versione di Prim di questo esercizio usa la normale PriorityQueue vista a lezione.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Esercizio3 {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("Type:\n$  java -cp . Esercizio3 <file_input> <file_output>");
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
            String[] edgesInfo = firstLine.split(" ");
            int n = Integer.parseInt(edgesInfo[0]); // numero di nodi
            int m = Integer.parseInt(edgesInfo[1]); // numero di archi
            ArrayList<Edge> edges = new ArrayList<Edge>(); // Per Kruskal
            ArrayList<Node> nodes = new ArrayList<Node>(); // Per Prim

            for(int k = 1, t = 0; k <= m; k++) {
                String[] edgeData = inputStream.readLine().split(" ");
                int s = Integer.parseInt(edgeData[0]);
                int d = Integer.parseInt(edgeData[1]);
                double w = Double.parseDouble(edgeData[2]);

                // Kruskal
                edges.add(new Edge(s, d, w));

                // Prim (ogni nodo ha una lista con i suoi nodi adiacenti)
                Node src = new Node(s);
                Node des = new Node(d);
                int srcPos = nodes.indexOf(src), desPos = nodes.indexOf(des);

                if(srcPos < 0) {
                    src.pos = t;
                    nodes.add(t, src);
                    t++;
                } else
                    src = nodes.get(srcPos);

                if(desPos < 0) {
                    des.pos = t;
                    nodes.add(t, des);
                    t++;
                } else
                    des = nodes.get(desPos);

                if(!nodes.get(src.pos).adjacents.contains(des)) { // Se il nodo di destinazione non è già tra i nodi adiacenti del nodo di partenza lo aggiungo
                    nodes.get(src.pos).adjacents.add(des); // Nodo di destinazione
                    nodes.get(src.pos).weights.add(w); // Peso di tale arco
                }

                if(!nodes.get(des.pos).adjacents.contains(src)) { // Essendo un grafo non orientato effettuo anche il contrario
                    nodes.get(des.pos).adjacents.add(src); // Nodo di partenza
                    nodes.get(des.pos).weights.add(w); // Peso di tale arco
                }
            }

            // Ordinamento archi per peso (Kruskal)
            Collections.sort(edges, new Comparator<Edge>() { // O(mlogm) in quanto il metodo sort() usa un mergesort
                public int compare(Edge edge1, Edge edge2) {
                    if(edge1.weight < edge2.weight)
                        return -1;
                    else if(edge1.weight > edge2.weight)
                        return 1;
                    return 0;
                }
            });

            double totSavingK = kruskal(edges, n);
            double totSavingP = prim(nodes);

            outputStream.println(totSavingK);
            System.out.println("Kruskal: " + totSavingK);
            System.out.println("Prim:    " + totSavingP);

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
     * Implementazione dell'algoritmo di Kruskal per il MST, evito di memorizzare l'albero in quanto non serve all'esercizio.
     * Prendo in ordine crescente uno ad uno gli archi del grafo aggiungendo i nodi che ne fanno parte a un insieme quickfind che mi permetterà di capire quando due nodi sono già uniti in qualche modo o meno.
     * Se non lo sono li "unisco" con un merge, se lo sono salvo il peso dell'arco scartato.
     *
     * @param edges     Archi del grafo
     * @param n         Numero di nodi del grafo
     * @return          Peso risparmiato (somma degli archi che non fanno parte del MST)
     */
    private static double kruskal(ArrayList<Edge> edges, int n) {
        QF quickFindSet = new QF(n);
        double saving = 0;
        for(Edge edge : edges) {
            int srcRep = quickFindSet.find(edge.source);
            int desRep = quickFindSet.find(edge.destination);
            if(srcRep != desRep)  // Evito cicli
                quickFindSet.merge(srcRep, desRep);
            else
                saving += edge.weight;
        }
        return saving;
    }

    /**
     * Implementazione dell'algoritmo di Prim per il MST, anche qui non memorizzo l'albero.
     * L'array di supporto d memorizza il valore di un certo nodo i-esimo (basato sull'array di nodi "nodes") e un flag se tale nodo è già stato visitato o meno
     * Parto dal nodo 0 e man mano che procedo aggiungo gli adiacenti a una PriorityQueue ordinata in base al peso (minimo) dell'arco che lo raggiunge.
     * Man mano che scorro i nodi (prelevandoli dalla queue) ne analizzo gli adiacenti e aggiorno i valori dei nodi se si trova un arco con peso minore a quello attualmente associato al nodo.
     * Se un arco punta a un nodo già visitato sommo il peso dell'arco, altrimenti lo sottraggo.
     *
     * @param nodes     Nodi del grafo
     * @return          Peso risparmiato (somma degli archi che non fanno parte del MST)
     */
    private static double prim(ArrayList<Node> nodes) {
        double totSaving = 0;
        Data[] d = new Data[nodes.size()];
        PriorityQueue Q = new PriorityQueue(nodes.size());
        Q.insert(nodes.get(0), 0); // Parto sempre dal nodo 0

        d[0] = new Data(0, false);
        for(int i = 1; i < nodes.size(); i++) // Inizializzo l'array di supporto
            d[i] = new Data(Double.POSITIVE_INFINITY, false);

        while(!Q.isEmpty()) {
            Node u = Q.find();
            d[u.pos].visited = true;
            Q.deleteMin();
            for(Node v : u.adjacents) {
                int vPosInUAdj = u.adjacents.indexOf(v); // La posizione di v nella lista degli adiacenti di u è la stessa della posizione del peso UV
                double weigthUV = u.weights.get(vPosInUAdj); // Peso dell'arco (u,v)
                if(!d[v.pos].visited) {
                    if(d[v.pos].val == Double.POSITIVE_INFINITY) {
                        totSaving -= weigthUV;
                        d[v.pos].val = weigthUV;
                        Q.insert(v, d[v.pos].val);
                    } else if(weigthUV < d[v.pos].val) {
                        totSaving -= (weigthUV - d[v.pos].val);
                        d[v.pos].val = weigthUV;
                        Q.decreaseKey(v, d[v.pos].val);
                    }
                } else
                    totSaving += weigthUV;
            }
        }

        return totSaving;
    }

    // (Kruskal)
    private static class Edge {
        int source, destination;
        double weight;

        Edge(int source, int destination, double weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }

    // (Prim)
    private static class Node {
        int id;
        int pos; // La sua posizione nell'array di nodi (questo mi evita ogni volta delle ricerce in tutto l'array per trovare la sua posizione)
        ArrayList<Node> adjacents = new ArrayList<Node>();
        ArrayList<Double> weights = new ArrayList<Double>(); // Pesi relativi agli archi this-adjacents[i] - l'alternativa era un unico array che comprendesse sia il nodo adiacente che il peso dell'arco per raggiungerlo

        Node(int id) {
            this.id = id;
        }

        // Considero due nodi uguali se hanno id uguale
        @Override
        public boolean equals(Object object) {
            boolean isEqual= false;

            if (object != null && object instanceof Node)
                isEqual = (this.id == ((Node) object).id);

            return isEqual;
        }
    }

    /**
     * Struttura dati utilizzata dall'algoritmo di Kruskal
     */
    private static class QF {

        private class QFNode {
            public int value;
            private QFNode next;
            private Rep representative;
        }

        private class Rep {
            public int value, size;
            private QFNode head, tail;
        }

        private QFNode[] nodes;

        public QF(int n) {
            nodes = new QFNode[n];
            Rep rep;

            for (int i = 0; i < n; i++) {
                nodes[i] = new QFNode();
                nodes[i].value = i;
                nodes[i].next = null;

                rep = new Rep();
                rep.value = i;
                rep.size = 1;
                rep.head = nodes[i];
                rep.tail = nodes[i];

                nodes[i].representative = rep;
            }
        }

        // Restituisce il valore del rappresentante
        public int find(int x) {
            return nodes[x].representative.value;
        }

        // Unisce due gruppi di nodi attribuendo lo stesso rappresentante
        public void merge(int x, int y) {
            Rep newRep;
            QFNode elem;

            if(nodes[x].representative.size < nodes[y].representative.size) {
                newRep = nodes[y].representative;
                newRep.size += nodes[x].representative.size;
                elem = nodes[x].representative.head; // Da dove inizio a cambiare il rappresentante dei nodi
            } else {
                newRep = nodes[x].representative;
                newRep.size += nodes[y].representative.size;
                elem = nodes[y].representative.head;
            }

            while(elem != null) {
                elem.representative = newRep;
                newRep.tail.next = elem;
                newRep.tail = elem;
                elem = elem.next;
            }
        }

    }

    /**
     * Strutture dati utilizzata dall'algoritmo di Prim
     */
    private static class PriorityQueue {

        PQNode q[];
        int size, max;

        public PriorityQueue(int maxSize) {
            q = new PQNode[maxSize];
            max = maxSize;
            size = 0;
        }

        // Controllo se la lista è vuota
        public boolean isEmpty() {
            return size == 0;
        }

        // Restituisce l'elemento in posizione 0 della lista
        public Node find() {
            if (size == 0) {
                System.err.println("Empty priority queue");
                System.exit(1);
            }
            return (q[0].node);
        }

        // Inserisce un nodo con una certa priorità andandolo a sistemare nella posizione adatta
        public void insert(Node node, double prio) {
            int i = size++, j;
            if (size > max) {
                System.err.println("Full priority queue");
                System.exit(1);
            }
            PQNode n = new PQNode(node, prio);
            q[i] = n;
            j = (i == 0) ? 0 : ((i+1) / 2) - 1;
            while (prio < q[j].prio) {
                q[i] = q[j];
                q[j] = n;
                i = j;
                j = (i == 0) ? 0 : ((i+1) / 2)-1;
            }
        }

        // Tolgo l'elemento in posizione 0
        public void deleteMin() {
            if (size == 0) {
                System.err.println("Empty priority queue");
                System.exit(1);
            }
            int i = 0, j = 0;
            boolean done = false;
            PQNode n = q[--size]; // Ultimo elemento in coda
            q[0] = n;
            while(!done) {
                j = 2*(i+1)-1;
                if(((j < size) && (q[j].prio < n.prio)) || ((j+1 < size) && (q[j+1].prio < n.prio))) {
                    if ((j+1 < size) && (q[j+1].prio < q[j].prio))
                        j++;
                    q[i] = q[j];
                    q[j] = n;
                    i = j;
                } else
                    done = true;
            }
        }

        // Diminuisco la priorità di un certo noto andandolo eventualmente a posizionare nella sua nuova posizione
        public void decreaseKey(Node node, double p) {
            int j = 0, k;
            PQNode n;
            while(j < size && (q[j].node != node))
                j++;
            if (j >= size) {
                System.err.println("Absent element");
                System.exit(1);
            } else {
                if(q[j].prio <= p) {
                    System.err.println("Non decreased priority");
                    System.exit(1);
                }
                n = q[j];
                n.prio = p;
                k = (j == 0) ? 0 : ((j+1) / 2)-1;
                while(p < q[k].prio) {
                    q[j] = q[k];
                    q[k] = n;
                    j = k;
                    k = (j == 0) ? 0 : ((j+1) / 2)-1;
                }
            }
        }

        private static class PQNode {
            Node node;
            double prio;

            PQNode(Node node, double prio){
                this.node = node;
                this.prio = prio;
            }
        }

    }

    private static class Data {
        double val;
        boolean visited;

        Data(double val, boolean visited) {
            this.val = val;
            this.visited = visited;
        }
    }

}
