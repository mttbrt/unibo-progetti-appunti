public class RedBlackTree {

   // Nodi principali
   private final TreeNode nil = new TreeNode("nil"); // Il nodo nil ha ha una chiave non disponibile tra quelle che possono essere immesse)
   private TreeNode root = nil;
   
   // Dati Albero
   private static final int RED = 0;
   private static final int BLACK = 1;
   private static int size = 0;
   
   // Operazioni Elementari
   private static int search = 0;
   private static int insert = 0;
   private static int delete = 0;
   
   private static int linkedListElements = 0; // Numero di elementi nella lista (per valutare complessità)
   private static int linkedListOp = 0; // Operazioni effettuate
   
   // Dimensione albero
   public static int dimension() {
      return size;
   }
   
   // Azzera elementi di conteggio delle op elementari
   public static void resetElemOp() {
      search = 0; insert = 0; delete = 0;
   }
   
   // Stampa le operazioni elementari divise per tipologia
   public static void printElemOp() {
      System.out.println("[S:" + search + "|I:" + insert + "|D:" + delete + "] tot:" + getElemOp());
   }
   
   // Restituisce la somma delle operazioni elmentari effettuate
   public static int getElemOp() {
      return search + insert + delete;
   }
   
   // Restituisce la somma delle operazioni elmentari effettuate sulla lista di un nodo
   public static int getLinkedListOp() {
      return linkedListOp;
   }
   
   // Restituisce il numero di elementi presenti nella lista di un nodo
   public static int getLinkedListElements() {
      return linkedListElements;
   }
   
   // Cerca un luogo all'interno di un nodo e restituisce -1 se il nodo non esiste, 0 se non presente o il numero di attrezzature nel luogo se c'è
   public int search(String key, String place) { // Complessità teorica O(logn)
      TreeNode keyNode = search(key, root); search++;
      
      search++; // if sotto
      if(keyNode == null) // Nodo non presente
         return -1;
      else {
         search++; // if sotto
         LinkedList.resetElemOp();
         if(keyNode.places.searchNode(place) == null) { // Luogo non presente in lista
            linkedListOp = LinkedList.getElemOp(); // Operazioni elementari effettuate sulla lista
            linkedListElements = keyNode.places.dimension(); // Lunghezza della lista
            
            return 0;
         }
         else {
            linkedListOp = LinkedList.getElemOp(); // Operazioni elementari effettuate sulla lista
            linkedListElements = keyNode.places.dimension(); // Lunghezza della lista
            
            return keyNode.places.searchNum(place); // Luogo presente nel nodo specificato
         }
      }
      
   }

   // Metodo ricorsivo di ricerca di una chiave all'interno di un albero, ritorna il nodo cercato se presente o null se non presente
   private TreeNode search(String key, TreeNode startingNode) {
      search++; // if sotto
      if (root == nil) 
         return null;

      // Se la chiave è < di quella del nodo attuale, allora ricerca nell'albero sinistro, se > ricerca nell'albero destro (sempre se != da nil)
      search++; // if/else if sotto
      if (key.compareTo(startingNode.key) < 0) {
         search++; // if sotto
         if (startingNode.left != nil) 
            return search(key, startingNode.left);
      } 
      search++;
      if (key.compareTo(startingNode.key) > 0) {
         search++; // if sotto
         if (startingNode.right != nil) 
            return search(key, startingNode.right);
      } 
      search++;
      if (key.equals(startingNode.key)) { // Chiave trovata, ritorna il nodo
         return startingNode;
      }

      return null;
   }
   
   // Inserimento di un luogo in un nodo con relativo ribilanciamento (se il nodo non esiste lo creo)
   public void insert(String key, String place) { // Complessità teorica O(logn)
      TreeNode node = search(key, root);
      
      insert++; // if sotto
      if (node != null) { // Se il nodo esiste già, aggiungo il luogo
         LinkedList.resetElemOp();
         
         node.places.insert(place);
         
         linkedListOp = LinkedList.getElemOp(); // Operazioni elementari effettuate sulla lista
         linkedListElements = node.places.dimension(); // Lunghezza della lista
      } else {
         node = new TreeNode(key, place); // Se il nodo non esiste, creo un nodo con luogo da inserire per questa chiave
         TreeNode temp = root;
         
         insert++; // if sotto
         if (root == nil) { // Albero vuoto
            root = node; // Il nodo lo metto come root
            node.color = BLACK; // Il root è sempre Black
            node.parent = nil; // Il root non ha padre
         } else { // Albero non vuoto
            node.color = RED;
            while (true) { insert++; // if true==true
               insert++; // if sotto
               if (node.key.compareTo(temp.key) < 0) { // Lato sinistro 
                  insert++;
                  if (temp.left == nil) { // E' un leaf-node
                     temp.left = node; 
                     node.parent = temp; 
                     break;
                  } else {
                     temp = temp.left;
                  }
               }
               insert++;
               if (node.key.compareTo(temp.key) > 0) { // Lato destro
                  insert++;
                  if (temp.right == nil) { // E' un leaf-node
                     temp.right = node;
                     node.parent = temp;
                     break;
                  } else {
                     temp = temp.right;
                  }
               }
            }
            balanceInsert(node); // Ribilancia l'albero
         }
         size++; // Ho aggiunto un nodo all'albero
      }
   }

   // Riorienta l'albero a partire dal nodo dato (I casi si riferiscono a quelli delle slide)
   private void balanceInsert(TreeNode node) {
      while (node.parent.color == RED) { // Finchè il padre del nodo è rosso (dato che il nodo inserito è rosso c'è un conflitto)
         TreeNode uncle = nil;
         
         if (node.parent == node.parent.parent.left) { // Se il padre del nodo è il figlio sinistro del nonno
            uncle = node.parent.parent.right;
            
            if (uncle != nil && uncle.color == RED) { // Caso 3: lo zio del nodo è rosso
               node.parent.color = BLACK;
               uncle.color = BLACK;
               node.parent.parent.color = RED;
               node = node.parent.parent; // Il ciclo si ripete (a partire dal nonno ora diventato rosso)
               continue;
            }
            if (node == node.parent.right) { // Caso 4a: il nodo è figlio destro del padre e il padre figlio sinistro del nonno e zio nero
               // Necessaria una doppia rotazione
               node = node.parent;
               rotateLeft(node); // Doppia rotazione, una quì e una dopo (dopo questa mi trovo nel Caso 5a)
            } 
            
            // Caso 5a (se il secondo if non è stato eseguito questa è una singola rotazione)
            node.parent.color = BLACK; // Il padre diventa nero
            node.parent.parent.color = RED; // Il nonno rosso (corretto perchè ora entrambi i figli sono neri)
            rotateRight(node.parent.parent); // Ribilancio il nonno
            
         } else { // Se il padre del nodo è il figlio destro del nonno
            uncle = node.parent.parent.left;
            
            if (uncle != nil && uncle.color == RED) { // Caso 3: lo zio del nodo è rosso
               node.parent.color = BLACK; 
               uncle.color = BLACK;
               node.parent.parent.color = RED;
               node = node.parent.parent;
               continue;
            }
            if (node == node.parent.left) { // Caso 4b: il nodo è figlio sinistro del padre e il padre figlio destro del nonno e zio nero
               node = node.parent;
               rotateRight(node); 
            }
            
            // Caso 5b 
            node.parent.color = BLACK;
            node.parent.parent.color = RED;
            rotateLeft(node.parent.parent); // Ribilancio il nonno
            
         }
      }
      root.color = BLACK; // Caso 1/Caso 2 (o il nodo non ha padre, o il padre è nero)
   }

   // Ruota a sinistra partendo dal nodo dato
   private void rotateLeft(TreeNode node) {
      
      if (node.parent != nil) { // Non mi trovo dalla radice
         if (node == node.parent.left) { // Il nodo è figlio sinistro del padre
            node.parent.left = node.right;
         }
         else {
            node.parent.right = node.right;
         }
         
         // Effettuo il nuovo allacciamento
         node.right.parent = node.parent;
         node.parent = node.right;
         
         if (node.right.left != nil) {
            node.right.left.parent = node;
         }
         
         node.right = node.right.left;
         node.parent.left = node;
      } else { // Devo ruotare la radice
         // Ruoto la radice con il suo elemento destro
         TreeNode right = root.right;
         root.right = right.left;
         right.left.parent = root;
         root.parent = right;
         right.left = root;
         right.parent = nil;
         root = right;
      }

   }

   // Ruota a destra partendo dal nodo dato
   private void rotateRight(TreeNode node) {
      
      if (node.parent != nil) { // Non mi trovo dalla radice
         if (node == node.parent.left) {
            node.parent.left = node.left;
         }
         else { 
            node.parent.right = node.left;
         }

         // Effettuo il nuovo allacciamento
         node.left.parent = node.parent;
         node.parent = node.left;
         
         if (node.left.right != nil) {
            node.left.right.parent = node;
         }
         
         node.left = node.left.right;
         node.parent.right = node;
      } else { // Devo ruotare la radice (più facile)
         // Ruoto la radice con il suo elemento sinistro
         TreeNode left = root.left;
         root.left = root.left.right;
         left.right.parent = root;
         root.parent = left;
         left.right = root;
         left.parent = nil;
         root = left;
      }
      
   }
   
   // Cancella un luogo dalla lista di una chiave, se era l'ultimo luogo presente in lista cancella il nodo e ribilancia l'albero
   public boolean delete(String key, String place) { // Complessità teorica O(logn)
      TreeNode a = search(key, root); // Nodo corrispondente alla chiave
      
      delete++;
      if (a == null) // Se l'attrezzatura non è presente
         return false;
      
      // Attrezzatura presente
      LinkedList.resetElemOp();
      
      boolean resultDeletion = a.places.delete(place);
      
      linkedListOp = LinkedList.getElemOp(); // Operazioni elementari effettuate sulla lista
      linkedListElements = a.places.dimension(); // Lunghezza della lista
      
      delete++;
      if(resultDeletion) { // Elemento cancellato correttamente dalla lista
         delete++;
         if(a.places.getHead() == null) { // Se non ci sono più elementi in lista cancello totalmente il nodo dell'albero perchè non ha più luoghi
            TreeNode b; 
            TreeNode c = a; // Referenza temporanea
            int yFirstColor = c.color; // Colore originario del nodo da eliminare

            delete+=2;
            if (a.left == nil) { // Ha il figlio sinistro leaf-node, trapianto col destro
               b = a.right;
               replace(a, a.right); 
            } else if (a.right == nil) { // Ha il figlio destro leaf-node, trapianto col sinistro
               b = a.left;
               replace(a, a.left);
            } else { // Ha entrambi i figli (posiziono il nodo più piccolo tra tutti i nodi maggiori di a [il suo successivo] al posto di a)
               c = subTreeMin(a.right);
               yFirstColor = c.color;
               b = c.right;

               delete++;
               if (c.parent == a) {
                  b.parent = c;
               }
               else {
                  replace(c, c.right);
                  c.right = a.right;
                  c.right.parent = c;
               }

               replace(a, c);
               c.left = a.left;
               c.left.parent = c;
               c.color = a.color;
            }

            // Se il nodo da eliminare era nero rioriento partendo da b (ovvero il valore subito maggiore al più piccolo dopo a) a>c>b
            delete++;
            if(yFirstColor == BLACK)
               balanceDelete(b); 
         
            size--; // Ho cancellato un nodo dalla lista
         }
         return true; // Elemento cancellatto correttamente dalla lista (e se era l'ultimo cancellato anche il nodo)
      }
      
      return false; // Elemento non presente in lista
      
   }
    
   // Trapianta un nodo transplant con un nodo with (non si preoccupa delle connessioni del nuovo nodo con il precedente)
   private void replace(TreeNode transplant, TreeNode with) {
      if (transplant.parent == nil) {  // Trapianto di radice
         root = with;
      }
      else if (transplant == transplant.parent.left) {  // Target è un figlio sinistro
         transplant.parent.left = with;
      }
      else {   // Target è un figlio destro
         transplant.parent.right = with;
      }
      
      with.parent = transplant.parent;
   }
   
   // Ribilancia l'albero dopo la cancellazione
   private void balanceDelete(TreeNode x) {
      while(x != root && x.color == BLACK) {
         
         if (x == x.parent.left) { // Se b è figlio sinistro
            TreeNode w = x.parent.right; // w è fratello di b
            
            if (w.color == RED) {  // w rosso e b nero
               w.color = BLACK; // Lo imposto nero
               x.parent.color = RED; // Il padre di b (e w) lo imposto rosso
               rotateLeft(x.parent); 
               w = x.parent.right; // w diventa il nuovo fratello di b dopo la rotazione (che è l'ex figlio sinistro del w precedente)
            }
            
            if (w.left.color == BLACK && w.right.color == BLACK) { // Se w ha entrambi i figli neri
               w.color = RED; // w diventa rosso
               x = x.parent; // Questo è sistemato, ricomincia analizzando il padre di b (risali l'albero)
               continue;
            } else if (w.right.color == BLACK) { // Se w ha il figlio destro nero e sinistro rosso
               w.left.color = BLACK; // Il sinistro lo imposto nero
               w.color = RED; // w lo metto rosso
               rotateRight(w);
               w = x.parent.right; // w diventa il padre del vecchio w dopo la rotazione
            }
            if (w.right.color == RED) { // Se w ha figlio destro rosso e sinistro nero o rosso
               w.color = x.parent.color; // Sistemo i colori secondo i criteri
               x.parent.color = BLACK;
               w.right.color = BLACK;
               rotateLeft(x.parent); // Con questa rotazione porto w centrale e grazie ai cambiamenti sopra i colori sono corretti
               x = root; // b diventa root (prima condizione per uscire dal ciclo)
            }
             
         } else { // Se b è figlio destro
            TreeNode w = x.parent.left; // w è fratello di b
            
            if (w.color == RED) { // w rosso e b nero
               w.color = BLACK;  // w nero
               x.parent.color = RED;  // il padre di b (e w) lo imposto rosso
               rotateRight(x.parent);
               w = x.parent.left; // w diventa il nuovo fratello di b dopo la rotazione (che è l'ex figlio destro del w precedente)
            }
            
            if (w.right.color == BLACK && w.left.color == BLACK) { // Se w ha entrambi i figli neri
               w.color = RED; // w diventa rosso
               x = x.parent; // Questo è sistemato, ricomincia analizzando il padre di b (risali l'albero)
               continue;
            }
            else if (w.left.color == BLACK) {  // Se w ha il figlio sinistro nero e destro rosso
               w.right.color = BLACK; // Il destro lo imposto nero
               w.color = RED; // w lo metto rosso
               rotateLeft(w);
               w = x.parent.left; // w diventa il padre del vecchio w dopo la rotazione
            }
            if (w.left.color == RED) { // Se w ha figlio sinistro rosso e destro nero o rosso
               w.color = x.parent.color; // Sistemo i colori secondo i criteri 
               x.parent.color = BLACK; 
               w.left.color = BLACK; 
               rotateRight(x.parent); // Con questa rotazione porto w centrale e grazie ai cambiamenti sopra i colori sono corretti
               x = root; // b diventa root (prima condizione per uscire dal ciclo)
            }
         }
         
      }
      x.color = BLACK; // b lo imposto nero
   }
    
   // Restituisce il minimo di un sottoalbero
   private TreeNode subTreeMin(TreeNode subTreeRoot){
      while(subTreeRoot.left != nil) { delete++;
         subTreeRoot = subTreeRoot.left; 
      }

      return subTreeRoot;
   }
   
   // Stampa tutto l'albero
   public void printTree() {
      printTree(root);
   }
   
   // Stampa da un nodo assegnato fino alla fine nella forma: ColoreNodo | ID | {lista luoghi} [NodoLeft / Nodo Right]
   private void printTree(TreeNode node) {
      if (node == nil)
         return;
      
      printTree(node.left);
      System.out.print((node.color == 0 ? "R" : "B") + " | " + node.key + " | \n    ");
      node.places.printList(); // Lista luoghi
      System.out.println("\n    [L:" + node.left.key + " / R:" + node.right.key + "]\n"); // Nodi Left e Right
      printTree(node.right);
   }

   private class TreeNode {

      // Elementi contenuti in un nodo red and black
      String key;
      int color = RedBlackTree.BLACK;
      LinkedList places = new LinkedList();
      
      // Riferimenti
      TreeNode left = nil, right = nil, parent = nil;
      
      // Costruttore che verrà utilizzato per un nodo nil che prevede una chiave non utilizzabile (tipicamente 0) e nessuna lista luoghi
      TreeNode(String key) { 
         this.key = key;
         places = null;
      }
      
      // Un TreeNode per come è strutturato l'esercizio verrà creato sempre insieme ad un luogo
      TreeNode(String key, String place) { 
         this.key = key;
         
         LinkedList.resetElemOp();
         
         places.insert(place);
         
         linkedListOp = LinkedList.getElemOp(); // Operazioni elementari effettuate sulla lista
         linkedListElements = 1; // Lunghezza della lista (solo il nodo appena inserito
      }
      
   }
    
}
