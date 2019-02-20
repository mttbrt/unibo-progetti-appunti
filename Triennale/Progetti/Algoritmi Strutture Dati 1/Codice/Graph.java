import java.util.ArrayList;
import java.util.Stack;


class Graph {

   private ArrayList<Edge> edges; // Lista di archi
   private ArrayList<Vertex> vertices; // Lista di nodi
   
   // Per l'attraversamento DFS
   private Stack<Integer> path  = new Stack<>(); // Stack che rappresenta il percorso attualmente analizzato
   private ArrayList<Vertex> viewing = new ArrayList<>(); // Lista che rappresenta tutti i vertici del percorso
   private ArrayList<ArrayList<Integer>> pathsMatrix; // Matrice ArrayList con tutti i percorsi trovati
   
   public Graph() {
      edges = new ArrayList<>();
      vertices = new ArrayList<>();
   }

   // Crea un arco tra due nodi
   public void addEdge(int vert1, int vert2) { // Complessità O(1)
      Vertex vertex1 = addVertex(vert1);
      Vertex vertex2 = addVertex(vert2);

      Edge edge = new Edge(vertex1, vertex2); // Creo un arco tra i due vertici
      edges.add(edge); // Aggiungo l'arco alla lista generale di archi del grafo

      vertex1.addAdjacentVertex(edge, vertex2); // Imposto che il vertex1 punta al vertex2
   }
   
   // Crea e aggiunge un nodo al grafo (è public in quanto in un grafo può esistere un nodo che non ha archi)
   public Vertex addVertex(int vertData) { // Complessità O(v) per il controllo di esistenza
      // Se il vertice è già nella lista di vertici del grafo ritorno il vertice esistente
      for(Vertex v : vertices)
         if (v.data == vertData) 
            return v;
      
      
      Vertex vertex = new Vertex(vertData); // Altrimenti creo il vertice
      vertices.add(vertex); // E lo aggiungo alla lista
      
      return vertex;
   }

   //  Ritorna una lista con gli elementi dei nodi che non hanno vertici che puntano a loro ma almeno un vertice a cui puntano (entrate)
   public ArrayList<Integer> getEntrances() { // Complessità O(e*v)
      ArrayList<Integer> entrances = new ArrayList<>(); // Lista con nodi senza entrate ma con almeno un'uscita
      
      // Nodi che non sono raggiunti da nessun altro nodo
      for(int i=0; i<vertices.size(); i++) {
         boolean flag = false;
         for(int j=0; j<edges.size(); j++) {
            if(vertices.get(i) == edges.get(j).vertex2) {
               flag = true;
               break;
            }
         }
         // Se non viene puntato da nessun nodo ma punta verso almeno un altro nodo aggiungo alla lista
         if(!flag && !vertices.get(i).getAdjacentVertices().isEmpty()) 
            entrances.add(vertices.get(i).data);
      }
      
      return entrances;
   }
   
   //  Ritorna una lista con gli elementi dei nodi che non  puntano a nodi ma puntano ad almeno un vertice (uscite)
   public ArrayList<Integer> getExits() { // Complessità O(e*v)
      ArrayList<Integer> exits = new ArrayList<>(); // Lista con nodi senza archi uscenti ma con almeno uno entrante
      
      // Nodi che sono raggiunti da almeno un altro nodo
      for(int i=0; i<vertices.size(); i++) {
         boolean flag = false;
         for(int j=0; j<edges.size(); j++) {
            if(vertices.get(i) == edges.get(j).vertex2) {
               flag = true;
               break;
            }
         }
         // Se viene puntato da almeno un nodo e non punta verso nessun nodo aggiungo alla lista
         if(flag && vertices.get(i).getAdjacentVertices().isEmpty()) 
            exits.add(vertices.get(i).data);
      }
      
      return exits;
   }
   
   // Tramite un metodo ricorsivo restituisce una matrice di percordi da un nodo source a uno destiantion
   public ArrayList<ArrayList<Integer>> paths(int source, int destination) {
      pathsMatrix = new ArrayList<>();
      Vertex sVert = null;
      Vertex dVert = null;
      
      for(Vertex v : vertices) { 
         if (v.data == source) 
            sVert = v;
         else if (v.data == destination) 
            dVert = v;
      }
      
      if(sVert == null || dVert == null) 
         return null; // Uno dei due vertici non è presente nel grafo (o i 2 vertici sono uguali)
      else {
         pathRecursive(sVert, dVert);
         return pathsMatrix;
      }
      
   }
   
   // Metodo ricorsivo che utilizza il concetto di DFS
   private void pathRecursive(Vertex iter, Vertex dest) { // Complessità O(e+v)
      path.add(iter.data);
      viewing.add(iter);

      // Se il nodo iterativo combacia con quello di destinazione aggiungo il percorso
      if (iter == dest) 
         pathsMatrix.add(new ArrayList(path));
      // Altrimenti ripeto su tutti i nodi adiacenti al vertice che non siano nel gruppo di nodi nel percorso (evita ricorsione infinita in caso di cicli)
      else 
         for (Vertex v : iter.getAdjacentVertices()) 
            if (!viewing.contains(v)) 
               pathRecursive(v, dest);
      
      // Finita l'esplorazione del nodo iter lo rimuovo dal percorso
      path.pop(); 
      viewing.remove(iter);
   }
   
   // Resituisce true se il grafo non ha cicli, restituisce false se ne ha
   public boolean isAcyclic() { 

      ArrayList<Vertex> whiteSet = new ArrayList<>(); // Insieme dei vertici ancora da visitare
      ArrayList<Vertex> graySet = new ArrayList<>(); // Insieme dei vertici in fase di esaminazione
      ArrayList<Vertex> blackSet = new ArrayList<>(); // Insieme dei vertici esaminati completamente (ogni loro arco)

      // Riempio inizialmente l'insieme white con tutti i vertici del grafo
      for (Vertex vertex : vertices)
         whiteSet.add(vertex);
      

      while (whiteSet.size() > 0) { // Finchè ci sono elementi ancora non visitati
         Vertex current = whiteSet.iterator().next();

         if(depthFirstSearch(current, whiteSet, graySet, blackSet)) 
            return false;
         
      }

      return true;
   }

   // Resituisce la presenza di un ciclo (true) o meno (false) tramite DFS
   private boolean depthFirstSearch(Vertex current, ArrayList<Vertex> whiteSet, ArrayList<Vertex> graySet, ArrayList<Vertex> blackSet) { // Complessità O(e+v)
      
      moveVertex(current, whiteSet, graySet); // Sposto il vertice dall'insieme white al gray e poi lo esamino

      for(Vertex neighbor : current.getAdjacentVertices()) {
         // Se il vertice vicino è già nell'insieme black, vuol dire che è già stato analizzato, passo oltre
         if (blackSet.contains(neighbor)) 
            continue;

         // Se il vertice vicino è nell'insieme gray, c'è un ciclo
         if (graySet.contains(neighbor)) 
            return true;
         
         // Il vertice vicino è ancora da esplorare, ripeto ricorsivamente
         if(depthFirstSearch(neighbor, whiteSet, graySet, blackSet))
            return true; // Se l'esito è la presenza di un ciclo, fermo la ricorsione
      }

      // Finita l'esplorazione sposto da gray a black il vertice analizzato
      moveVertex(current, graySet, blackSet);

      return false; // Nessun ciclo
   }

   // Sposta un vertice da un insieme ad un altro
   private void moveVertex(Vertex vertex, ArrayList<Vertex> sourceSet, ArrayList<Vertex> destinationSet) {
      sourceSet.remove(vertex);
      destinationSet.add(vertex);
   }
   
   @Override
   public String toString() {
      String string = "";

      for(Vertex vert : vertices) {
         string += vert.data + " [";
         for(int i=0; i<vert.getAdjacentVertices().size(); i++) 
            string += vert.getAdjacentVertices().get(i).data + ", ";
         string += "]\n";
      }

      return string;

   }
   
   /* Classe che rappresenta un nodo del grafo
         Nel nostro caso la consegna richiede che i nodi siano rappresentati da interi 0-n 
         quindi l'ho impostato già con un tipo di dato int e non generico
   */
   private class Vertex {

      int data;

      ArrayList<Edge> edges = new ArrayList<>();
      ArrayList<Vertex> adjacentVertices = new ArrayList<>();
      ArrayList<Vertex> linkedVertices = new ArrayList<>();

      Vertex(int data){
         this.data = data;
      }

      // Setters
      
      // Aggiunge un vertice a cui questo punta
      void addAdjacentVertex(Edge e, Vertex v){
         edges.add(e);
         adjacentVertices.add(v);
      }

      // Getter
      ArrayList<Vertex> getAdjacentVertices() {
         return adjacentVertices;
      }

   }
   
   /* Classe che rappresenta un arco del grafo
         Un arco non è altro che una "struttura" che comprende due vertici
   */
   private class Edge {

      Vertex vertex1;
      Vertex vertex2;

      Edge(Vertex vertex1, Vertex vertex2){
         this.vertex1 = vertex1;
         this.vertex2 = vertex2;
      }

      @Override
      public String toString() {
         return "Edge [vertex1=" + vertex1 + ", vertex2=" + vertex2 + "]";
      }

   }
   
}

