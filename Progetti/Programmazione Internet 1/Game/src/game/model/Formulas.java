package game.model;

/**
 * Classe che racchiude tutte le formule necessarie per determinare il moto dei proiettili.
 */

public class Formulas {
    /**
     * 
     * Risolve un'equazione di secondo grado restituendone le soluzioni.
     * 
     * @param a
     * @param b
     * @param c
     * @return soluzioni dell'equazione di secondo grado
     */
    private static double[] equationSolver(double a, double b, double c) {
        double d;
        double s1, s2;
        double[] solutions = new double[2];

        d = (b*b) - (4*a*c); // Calcolo il delta

        if (d==0) { // Delta = 0: un unica soluzione
          s1 = (-b-Math.sqrt( b*b - 4*a*c ))/(2*a);
          s2 = s1;

          solutions[0] = s1;
          solutions[1] = s2;
          return solutions;
        } else if( d>0 ) { // Delta > 0: due soluzioni distinte
          s1=(-b - Math.sqrt( b*b -4*a*c ))/(2*a);
          s2=(-b + Math.sqrt( b*b -4*a*c ))/(2*a);

          solutions[0] = s1;
          solutions[1] = s2;
          return solutions;
        } else  { // Delta < 0: soluzioni complesse
          System.out.println("Soluzioni complesse");
          return solutions;
        }
    }

    /**
     * 
     * Trasforma il grado di un angolo in radianti.
     * 
     * @param degree grado di un angolo.
     * @return radianti del grado dato
     */
    private static double toRadian(double degree) {
       return (degree * (Math.PI/180));
    }

    /**
     * 
     * Mi calcola la coordinata X nel piano cartesiano del punto di partenza di un proiettile, conoscendo l'angolo del cannone.
     * 
     * @param cannonWidth   larghezza del cannone
     * @param angle   angolo del cannone
     * @return coordinata X di partenza del proiettile
     */
    public static double startingPointBulletX(double cannonWidth, double angle) {
       return ((cannonWidth/2) * Math.sin(-toRadian(angle+cannonWidth/2)));
    }

    /**
     * 
     * Mi calcola la coordinata Y nel piano cartesiano del punto di partenza di un proiettile, conoscendo l'angolo del cannone.
     * 
     * @param cannonWidth   larghezza del cannone
     * @param angle   angolo del cannone
     * @return coordinata X di partenza del proiettile
     */
    public static double startingPointBulletY(double cannonWidth, double angle) {
       return ((cannonWidth/2) * Math.cos(-toRadian(angle+cannonWidth/2)));
    }

    /**
     * 
     * Mi determina i valori a, b, c della parabola del moto, fornita nel testo del progetto.
     * 
     * Ciò in relazione all'angolo di partenza e alla velocità del proiettile.
     * 
     * @param cannonWidth   larghezza del cannone
     * @param angle   angolo del cannone
     * @return coordinata X di partenza del proiettile
     */
    private static double[] gravityParabolaABC(double angle, double bulletSpeed) {
       double[] valuesABC = new double[3];
       valuesABC[0] = -(10/(2*Math.pow(bulletSpeed, 2.0)*Math.pow(Math.cos(toRadian(angle)), 2.0)));
       valuesABC[1] = Math.tan(toRadian(angle));
       valuesABC[2] = 0;

       return valuesABC;
    }

    /**
     * 
     * Mi determina l'intersezione con l'asse X della parabola del moto data nel testo del progetto.
     * 
     * In relazione all'angolo iniziale e alla velocità.
     * NOTA: ciò mi serve per determinare poi il percorso che seguirà il proiettile lungo la sua traiettoria: 
     * più l'intersezione con l'asse X sarà lontana dal punto di partenza, più l'angolo sarà vicino al suolo e ciò indica che il percorso del proiettile si concluderà più velocemente rispetto a uno sparo con angolazione vicino ai 90 gradi.
     * 
     * @param angle   angolo di sparo
     * @param bulletSpeed velocità del proiettile
     * @return intersezione con l'asse X
     */
    public static double gravityParabolaXIntersection(double angle, double bulletSpeed) {
        double[] solutions = new double[2]; // Essendo un eq. di 2° grado avrà 2 soluzioni
        double[] parabolaValuesABC = new double[3]; // I valori a, b, c dell'equazione

        parabolaValuesABC = gravityParabolaABC(angle, bulletSpeed);
        solutions = equationSolver(parabolaValuesABC[0], parabolaValuesABC[1], parabolaValuesABC[2]);

        double launch = 0;
        if (solutions[0]>solutions[1]) 
           launch = solutions[0];
        else
           launch = solutions[1];

        return launch;
    }

    /**
     * 
     * Restituisce il valore y della parabola del moto fornita nel testo dell'esercizio, in base ad un valore x passato come parametro.
     * Ciò verrà utilizzato per realizzare l'effettivo movimento dei proiettili sullo schermo restituendo ad ogni frame le coordinate x,y del proiettile all'aumentare di x.
     * 
     * @param angle   angolo di sparo
     * @param bulletSpeed velocità dei proiettili
     * @param x coordinata x da cui ricaveremo la y
     * @return coordinata y in relazione alla x data come parametro
     */
    public static double gravityParabolaYValue(double angle, double bulletSpeed, double x) {
       return (Math.tan(toRadian(angle))*x - (10 / (2*Math.pow(bulletSpeed, 2.0)*Math.pow(Math.cos(toRadian(angle)), 2.0)))*x*x);
    }
   
}
