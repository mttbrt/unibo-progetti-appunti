/**
 * Matteo
 * Berti
 *
 * CONSIDERAZIONI:
 * Ho implementato due versioni entrambe che raggiungono lo stesso scopo utilizzando la programmazione lineare.
 * La prima determina per ogni elemento la somma massima raggiungibile fino ad esso, senza mai scendere di valore. Ha complessità O(n^2).
 * La seconda si basa su confronti rispetto all'array ordinato completamente (non solo per stanza come nella prima implementazione), in questo modo procede determinando il massimo da 0 fino all'elemento in analisi e sommandolo al suo valore.
 * Ha complessità O(n^2), tuttavia ho lasciato questa seconda implementazione in quanto tramite un albero binario indicizzato, modificato in modo che restituisca il valore massimo da 0 a un certo indice, si può risolvere il problema in O(nlogn).
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
import java.util.List;

public class Esercizio1 {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("Type:\n$  java -cp . Esercizio1 <file_input> <file_output>");
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
            String[] rooms = inputStream.readLine().split(" ");

            int nRooms = Integer.parseInt(rooms[0]), lastRoom = 1, lowerBound = 0; // numero stanze, ultima stanza visitata (per la stampa dei #), limite inferiore di ogni stanza
            boolean exit = false; // condizione di uscita dal while che stampa i risultati
            ArrayList<ObjInRoom> objects = new ArrayList<ObjInRoom>();

            // Leggo i valori degli oggetti separando per stanza, una volta terminata una stanza ne ordino gli oggetti al suo interno in modo non decrescente
            for(int i = 1; i <= nRooms; i++) {
                for(int j = 0; j < Integer.parseInt(rooms[i]); j++)
                    objects.add(new ObjInRoom(Integer.parseInt(inputStream.readLine()), i));
                Collections.sort(objects.subList(lowerBound, lowerBound + Integer.parseInt(rooms[i])), new Comparator<ObjInRoom>() {
                    public int compare(ObjInRoom obj1, ObjInRoom obj2) {
                        if(obj1.value < obj2.value)
                            return -1;
                        else if(obj1.value > obj2.value)
                            return 1;
                        return 0;
                    }
                });
                lowerBound += Integer.parseInt(rooms[i]); // La prossima stanza parte da qui
            }

            //METODO 1 di ricerca della combinazione con stampa su file
            List<ObjInRoom> objs = getBestOrder(objects);

            // Stampo i risultati su file distinguendo per stanza
            int k = objs.size()-1;
            while(!exit) {
                if(k >= 0) {
                    if(objs.get(k).room == lastRoom) {
                        outputStream.println(objs.get(k).value);
                        k--;
                    } else {
                        for(int h = 0; h < objs.get(k).room-lastRoom; h++)
                            outputStream.println("#");
                        lastRoom = objs.get(k).room;
                    }
                } else {
                    for(int h = 0; h < nRooms-lastRoom; h++)
                        outputStream.println("#");
                    exit = true;
                }
            }

            //METODO 2 di ricerca della combinazione con stampa a schermo (per confrontare i risultati)

            List<ObjInRoom> objs2 = getBestOrder2(objects);
            System.out.println();

            // Stampo i risultati distinguendo per stanza
            k = objs2.size()-1;
            exit = false;
            lastRoom = 1;
            lowerBound = 0;
            while(!exit) {
                if(k >= 0) {
                    if(objs2.get(k).room == lastRoom) {
                        System.out.println(objs2.get(k).value);
                        k--;
                    } else {
                        for(int h = 0; h < objs2.get(k).room-lastRoom; h++)
                            System.out.println("#");
                        lastRoom = objs2.get(k).room;
                    }
                } else {
                    for(int h = 0; h < nRooms-lastRoom; h++)
                        System.out.println("#");
                    exit = true;
                }
            }


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
     * Questo metodo effettua una doppia iterazione, un certo iteratore i scorre tutto l'array da 1 alla fine, e un altro iteratore j ripercorre l'array da 0 a i-1.
     * Ad ogni iterazione di j si effettua un doppio controllo, innanzitutto si controlla che l'elemento i-esimo sia maggiore uguale del j-esimo, in questo modo si rispetta la regola che possono essere presi solo elementi di valore non inferiore all'ultimo
     * Poi si controlle se la somma raggiunta fino ad ora dall'elemento i-esimo sia inferiore alla somma che si raggiungerebbe se questo elemento i-esimo fosse il successivo del j-esimo.
     * Se entrambe le condizioni sono vere è la scelta fino ad ora ottimale, e salvo il valore (somma massima raggiunta) in un array di supporto, insieme al padre, che sarà il j-esimo elemento.
     * Salvo inoltre la posizione dell'elemento che raggiunge somma massima all'interno dell'array, che sarà colui da cui partirò a risalire i padri in quanto combinazione ottimale di elementi da prelevare.
     *
     * @param objs  Lista di oggetti ObjInRoom già ordinati per stanza, ognuno contiene il valore dell'oggetto e la stanza di appartenenza
     * @return      Lista di oggetti nell'ordine in cui devono essere prelevati per massimizzare la somma totale
     */
    static List<ObjInRoom> getBestOrder(ArrayList<ObjInRoom> objs) {
        int n = objs.size(), maxPos = -1, max = 0;
        Data[] temp = new Data[n]; // Somme massime fino a un nodo e il padre
        List<ObjInRoom> solution = new ArrayList<ObjInRoom>();

        for (int k = 0; k < n; k++) // Inizializzo l' array di supporto
            temp[k] = new Data(objs.get(k).value, k); // La somma minima per ogni oggetto è il valore dell'oggetto stesso e ogni oggetto punta alla propria posizione

        for (int i = 1; i < n; i++)
            for (int j = 0; j < i; j++)
                if (objs.get(i).value >= objs.get(j).value && objs.get(i).value + temp[j].sum > temp[i].sum) {
                    temp[i].sum = temp[j].sum + objs.get(i).value;
                    temp[i].parent = j;
                    if (temp[i].sum > max) {
                        max = temp[i].sum;
                        maxPos = i;
                    }
                }

        // Creo la lista con le soluzioni partendo dall'indice corrispondente al valore massimo risalendo fino all'inizio
        for(int k = maxPos; k >= 0; k = temp[k].parent) {
            solution.add(objs.get(k));
            if(k == temp[k].parent)
                break;
        }

        System.out.println("Tot1: " + max);
        return solution;
    }

    /**
     * Questo metodo per prima cosa necessita di un array con gli elementi ordinati completamente (ordered), senza considerare le stanze.
     * La struttura di supporto è un array con oggetti di 4 elementi: pos (indica la posizione che l'elemento i-esimo ha nell'array ordinato completamente), sum (la somma massima raggiungibile fino all'elemento i-esimo),
     * parent (l'indice dell'array di supporto che contiene l'elemento padre), e original (il valore a cui corrisponde l'i-esimo elemento se fosse nell'array ordinato).
     * L'indice i scorre tutto l'array iniziale da 0 a n, la posizione su cui metterò la somma massima trovata fin ora è ArraySupporto.pos (ovvero la posizione che ha l'elemento i-esimo dell'array normale, nell'array ordinato completamente)
     * In questo modo è sufficiente trovare il valore massimo da 0 a i-1 in quanto sono in ordine crescente (dato che lavoro sull'array ordinato completamente). Vado infatti a inserire nella posizione ArraySupporto.pos la somma che ha valore massimo tra i valori precedenti e il valore dell'attuale elemento in analisi.
     * Tengo anche qui memorizzato l'indice nell'array di supporto del padre, e l'indice del valore massimo in assuluto (in tutto l'array). La ricerca del Max tra 0 e i-1 richiede O(n), se tuttavia si utilizzasse un albero binario indicizzato si avrebbe O(logn).
     *
     * @param objs  Lista di oggetti ObjInRoom già ordinati per stanza, ognuno contiene il valore dell'oggetto e la stanza di appartenenza
     * @return      Lista di oggetti nell'ordine in cui devono essere prelevati per massimizzare la somma totale
     */
    static List<ObjInRoom> getBestOrder2(ArrayList<ObjInRoom> objs) {
        int n = objs.size(), maxPos = -1, max = 0;
        Data2[] temp = new Data2[n];
        List<ObjInRoom> solution = new ArrayList<ObjInRoom>(); // Array ordinato per stanza
        ArrayList<ObjInRoom> ordered = (ArrayList<ObjInRoom>) objs.clone(); // Array ordinato completamente

        Collections.sort(ordered, new Comparator<ObjInRoom>() { // Ordino l'array in modo totale
            @Override
            public int compare(ObjInRoom obj1, ObjInRoom obj2) {
                if(obj1.value < obj2.value)
                    return -1;
                else if(obj1.value > obj2.value)
                    return 1;
                return 0;
            }
        });

        // Inizializzo l'array di supporto
        for (int k = 0; k < n; k++)
            temp[k] = new Data2(ordered.indexOf(objs.get(k)), 0, k, ordered.get(k));

        // Eseguo i confronti per aggiornare le somme migliori fino ad ogni elemento
        for (int i = 0; i < n; i++) {
            int nextPos = temp[i].pos, subMax = 0, subMaxIndex = temp[i].parent;
            for (int j = 0; j < nextPos; j++) // Trovo il massimo da la posizione 0 a quella dell'elemento in analisi (qui andrebbe inserita la ricerca nell'albero)
                if(temp[j].sum > subMax) {
                    subMax = temp[j].sum;
                    subMaxIndex = j;
                }
            temp[nextPos].sum = subMax + objs.get(i).value;
            temp[nextPos].parent = subMaxIndex;
            if (temp[nextPos].sum > max) {
                max = temp[nextPos].sum;
                maxPos = nextPos;
            }
        }

        // Creo la lista con le soluzioni partendo dall'indice corrispondente al valore massimo risalendo fino all'inizio
        for(int k = maxPos; k >= 0; k = temp[k].parent) {
            solution.add(temp[k].original);
            if(temp[k].original.value == temp[k].sum)
                break;
        }

        System.out.println("Tot2: " + max);
        return solution;
    }

    // Oggetto che rappresenta l'elemento da rubare
    private static class ObjInRoom {
        int value;
        int room;

        ObjInRoom(int value, int room) {
            this.value = value;
            this.room = room;
        }
    }

    // Oggetto per la struttura di supporto al metodo 2
    private static class Data {
        int sum; // somma fino a questo elemento dell'array
        int parent; // indice del padre nell'array

        Data(int sum, int parent) {
            this.sum = sum;
            this.parent = parent;
        }
    }

    // Oggetto per la struttura di supporto al metodo 2
    private static class Data2 {
        int pos; // posizione dell'elemento i-esimo se fosse nell'array completamente ordinato
        int sum; // somma fino a questo elemento dell'array
        int parent; // indice del padre nell'array
        ObjInRoom original; // valore dell'i-esimo elemento se fosse nell'array ordinato

        Data2(int pos, int sum, int parent, ObjInRoom original) {
            this.pos = pos;
            this.sum = sum;
            this.parent = parent;
            this.original = original;
        }
    }

}
