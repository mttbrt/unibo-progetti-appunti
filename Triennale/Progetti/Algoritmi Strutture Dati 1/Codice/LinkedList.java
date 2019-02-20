public class LinkedList {
 
   // Elementi principali
   private ListNode head;
   private int size;
   
   // Operazioni Elementari
   private static int search = 0;
   private static int insert = 0;
   private static int delete = 0;
   
   LinkedList() {
      head = null;
      size = 0;
   }
   
   // Dimensione Lista
   public int dimension() {
      return size;
   }
   
   // Azzera elementi di conteggio delle op elementari
   public static void resetElemOp() {
      search = 0;
      insert = 0;
      delete = 0;
   }
   
   // Restituisce la somma delle operazioni elmentari effettuate
   public static int getElemOp() {
      return search + insert + delete;
   }
   
   // Nodo head della lista
   public ListNode getHead() {
      return head;
   }
   
   // Inserisce un elemento nella prima posizione libera della lista se non c'è già, altrimenti incrementa il numero
   public void insert(String value) { // Complessità O(n)
      
      ListNode node = searchNode(value);
      
      // Caso in cui il luogo sia già presente, allora incremento il numero di quell'attrezzatura in quel luogo
      insert++;
      if (node != null) {
         node.num++; // Incremento il numero attrezzature per quel luogo 
      } 
      else { // Caso in cui il luogo non sia presente, allora creo un nuovo nodo
         node = new ListNode(value);
      
         insert+=2; // if/ else if
         if (head == null) { // Caso in cui la lista sia vuota 
            head = node; // Il nodo inserito diventa testa
            head.next = null; // Il successivo è vuoto
         }
         else if (head.next == null) { // Caso in cui sia presente solo la testa
            head.next = node; // Imposto il nodo inserito come il succesivo alla testa
         }
         else { // I primi due nodi sono già impostati, raggiungo la fine della lista
            ListNode iterator = new ListNode(); // Nodo che percorrerà tutta la lista e si fermerà in fondo
            for(iterator = head.next; iterator.next != null; iterator = iterator.next) {
               insert++;// Non fa niente, serve solo a posizionarsi sull'ultimo nodo della lista
            }
            iterator.next = node; // Il successivo elemento dell'ultimo nodo lo imposto a newNode, che diventerà l'ultimo
         }

         size++; 
      }
      
   }
   
   // Cerca un elemento nella lista e ritorna il nodo se c'è o null se non c'è
   public ListNode searchNode(String value) { // Complessità O(n)
      
      for(ListNode temp = head; temp != null; temp = temp.next) {
         search++;
         if(temp.place.equals(value))
            return temp;
      }
      
      // Il valore non è presente
      return null;
   }
   
   // Cerca un elemento nella lista e ritorna il num del nodo
   public int searchNum(String value) {
      ListNode myNode = searchNode(value);
      
      search++; // if sotto
      return myNode != null ? myNode.num : 0;
   }
   
   // Sottrae un num al nodo specificato, se si arriva a 0 elimina il nodo dalla lista
   public boolean delete(String value) { // Complessità O(n)
      delete++;
      if (head.place.equals(value)) { // Caso in cui il luogo da eliminare sia la testa
         delete++;
         if(--head.num == 0) // Decremento il numero, se arriva a 0 elimino il nodo dalla lista
            head = head.next; // La testa la traslo di uno
         return true;
      }
      else { // Caso in cui il nodo da eliminare sia un nodo successivo alla testa
         for(ListNode iterator = head; iterator.next != null; iterator = iterator.next) {
            delete++;
            if(iterator.next.place.equals(value)) {
               delete++;
               if(--iterator.next.num == 0) 
                  iterator.next = iterator.next.next; // Escludo il valore trovato dalla lista saltando la sua referenza
               return true;
            }
         }
         
         // Il valore da cancellare non è presente
         return false;
      }
   }
   
   // Stampa la lista nel formato {luogo:num, luogo:num, luogo:num}
   public void printList() {
      System.out.print("{");
      if (head != null) {
         ListNode temp = head;
         while(temp != null) {
            System.out.print(temp.place + ":" + temp.num + " - ");
            temp = temp.next;
         }
      }
      else {
         System.out.print("Lista Vuota");
      }
      System.out.print("}");
   }
   
   private class ListNode {
      
      // Elementi contenuti in un nodo della lista
      String place;
      int num = 0;
      ListNode next;

      // Costruttore di default
      ListNode() { 
      }
      
      ListNode(String place) {
         this(place, 1, null);
      }
      
      ListNode(String place, int num, ListNode next) {
         this.place = place;
         this.num = num;
         this.next = next;
      }
      
   }
   
}
