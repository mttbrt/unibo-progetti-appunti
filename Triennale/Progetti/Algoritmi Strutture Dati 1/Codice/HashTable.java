import java.util.LinkedList;

public class HashTable {
   
   private static int m;
   private final static double C = 0.15;
   private LinkedList<String>[] table;

   HashTable() {
      this(16);
   }
   
   // Costruttore con scelta della dimensione della tabella hash
   HashTable(int m) {
      this.m = m;
      table = new LinkedList[m];
   }
   
   // Inserimento di una chiave nella tabella hash e restituisce la dimensione della lista di trabocco
   public int insert(String key) {
      int pos = hashFunction(key);
      
      // Nessuna elemento per ora in questa posizione, creo la lista e aggiungo l'elemento
      if(table[pos] == null) { 
         table[pos] = new LinkedList<>();
         table[pos].add(key);
      }
      else // Elemento/i già presente/i, aggiungo un altro
         table[pos].add(key);
      
      return table[pos].size(); // Dimensione lista in questa posizione
   }
   
   // Stampa la tabella hash
   public void print() {
      for(int i=0; i<m; i++) {
         System.out.print(i + " {");
         if (table[i] != null) 
            for(int j=0; j<table[i].size(); j++) 
               System.out.print(table[i].get(j) + " - ");
         System.out.println("}");
      }
   }
   
   // Restituisce il valore hash secondo il metodo della moltiplicazione
   public int hashFunction(String key) {
      int i = hash(key);
      
      return (int)(m*(i*C % 1)); // E' uguale a (int)(m*(i*C - (int)(i*C))) ma ho un'operazione e un cast in meno da fare
   }
   
   // Restituisce un valore numerico per una stringa, utilizza la regola di Horner ed effettua mod a ogni iterazione per evitare b troppo grande e quindi overflow
   private int hash(String key) {
      char[] charArray = key.toCharArray();
      int b = (int)charArray[0];
      
      // Moltiplico per 64 in quanto il nostro alfabeto è su una base di 64
      for(int i=1; i<charArray.length; i++) 
         b = ((b*64) + (int)charArray[i]) % m; 
      
      return b;
   }
   
   // Conta il numero di collisioni nella tabella hash
   public int countCollisions() { // Complessità O(m)
      int collisions = 0;

      for(LinkedList<String> list : table)
         if(list!=null)
            if(list.size() > 1) 
               collisions += list.size() - 1; // Lunghezza lista meno l'elemento che dovrebbe starci di base
      
      return collisions;
   }
   
   // Numero di collisioni aspettate dato un numero di inserimenti (probabilità)
   public int collisionsExpected(int insertions) { 
      double n = insertions; // Numero Inserimenti
      double m = table.length; // Numero di celle nella tabella
      
      int prob = (int)(n-m*(1-Math.pow(((m-1)/m), n)));
      
      return prob;
   }
   
}

