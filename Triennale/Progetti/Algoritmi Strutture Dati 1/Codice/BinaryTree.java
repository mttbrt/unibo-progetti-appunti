import java.util.ArrayList;

public class BinaryTree {

   private BTreeNode root;
   private String json = ""; // Stringa in formato json per rappresentare i nodi dell'albero

	// Inserisce un nodo nell'albero nella posizione corretta, seguendo i criteri del minHeap
   public void insert(int key) {
      BTreeNode newNode = new BTreeNode(key); // Creo un nodo per la chiave

      if (root == null) { // Se l'albero è vuoto, questo nodo diventa radice
         newNode.parent = newNode; // Essendo il root ha se stesso come padre
         root = newNode;
      } else {
         // Inserisco il nodo nella prima posizione disponibile e shifto i valori finchè non sono tutti in posizione corretta
         shiftUp(addNodeInNextPosition(newNode));
      }

   }

   // Trova la prossima posizione libera, aggiunge il nodo e restituisce il nodo inserito
   private BTreeNode addNodeInNextPosition(BTreeNode addNode) { 
      
      ArrayList<BTreeNode> treeElements = new ArrayList<>(); 
      treeElements.add(root); // Parto dalla radice a ispezionare
      int id = 0; // Parto dall'inizio
      
      while(true) {
         BTreeNode iterNode = treeElements.get(id);
         
         // Quando i nodi non sono nulli creo una lista che ne tiene traccia (mi serve anche per determinare il padre del nuovo nodo)
         if (iterNode.left != null)
            treeElements.add(iterNode.left); 
         else {
            addNode.parent = treeElements.get((int)(treeElements.size()-1)/2); // Imposto il padre del nodo
            return iterNode.left = addNode; // Aggiungo il nodo a sinistra
         } 
         
         if (iterNode.right != null) 
            treeElements.add(iterNode.right);
         else {
            addNode.parent = treeElements.get((int)(treeElements.size()-1)/2); // Imposto il padre del nodo   
            return iterNode.right = addNode; // Aggiungo il nodo a destra
         }
         
         id++;
      }
      
   }
   
   // Ricorsivamente sposta una chiave verso la cima dell'albero fino a che non raggiunge la sua posizione 
   private void shiftUp(BTreeNode shiftNode) {
      if (shiftNode.key < shiftNode.parent.key) { 
         int temp = shiftNode.key;
         shiftNode.key = shiftNode.parent.key;
         shiftNode.parent.key = temp;
         shiftUp(shiftNode.parent);
      }
   }
   
   // Trasforma l'albero in un array tramite una visita level order traversal dell'albero binario
   public ArrayList<Integer> treeInArray() { 
      
      ArrayList<Integer> treeArray = new ArrayList<>(); // Lista di valori chiave (in questo caso Integer)
      ArrayList<BTreeNode> treeElements = new ArrayList<>(); // Lista di puntatori ai nodi
      
      treeElements.add(root); // Parto dalla radice a ispezionare
      int id = 0; // Parto dall'inizio
      
      while(id < treeElements.size()) {
         BTreeNode iterNode = treeElements.get(id);
         treeArray.add(iterNode.key);
         
         if(iterNode.left != null) 
            treeElements.add(iterNode.left);
         
         if(iterNode.right != null) 
            treeElements.add(iterNode.right);
         
         id++;
      }
      
      return treeArray;
   }

   public void printTree() {
      printTree(root);
   }
   
   // Stampa ricorsivamente i nodi del sottoalbero partendo da un nodo particolare
   private void printTree(BTreeNode node) {
      if(node == null)
         return;
      
      printTree(node.left);
      System.out.println(node.key + 
              "\t| Parent:" + (node.parent != null ? node.parent.key : "-") + 
              "\t| Left:" + (node.left != null ? node.left.key : "-") + 
              "\t| Right:" + (node.right != null ? node.right.key : "-"));
      printTree(node.right);
   }
   
   public String jsonTree() {
      json+="[";
      jsonTree(root);
      json+="]";
      
      return json;
   }
   
   // Ricorsivamente costruisce una stringa in formato json per la rappresentazione grafica con JavaScript
   private void jsonTree(BTreeNode node) {
      json+="{\"name\": " + node.key;
      
      if(node.left != null || node.right != null) {
         json+=", \"children\": [";
         
         if(node.left != null)
            jsonTree(node.left);
         if(node.right != null) {
            json+=", ";
            jsonTree(node.right);
         }
            
         json+="]";
      }
      
      json+="}";
   }
   
   // Classe che rappresenta un nodo dell'albero minHeap
   private class BTreeNode {

      int key;

      BTreeNode parent;
      BTreeNode left;
      BTreeNode right;

      BTreeNode(int key) {
         this.key = key;
      }

   }
   
}



