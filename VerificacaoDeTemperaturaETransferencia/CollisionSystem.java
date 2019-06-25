/*************************************************************************
 *  Compilation:  javac CollisionSystem.java
 *  Execution:    java CollisionSystem N               (N random particles)
 *                java CollisionSystem < input.txt     (from a file)
 *
 *  Creates N random particles and simulates their motion according
 *  to the laws of elastic collisions.
 *
 *************************************************************************/

/*************************************************************************
 *  Fernando Tocantins, Raul Rocha, Victor Martins
 *************************************************************************/
import java.awt.Color;

public class CollisionSystem {
    private MinPQ<Event> pq;        // the priority queue
    private double t = 0.0;        // simulation clock time
    private double hz = 0.5;        // number of redraw events per clock tick
    private Particle[] particles;   // the array of particles
    
    // create a new collision system with the given set of particles
    public CollisionSystem (Particle[] particles) {
        this.particles = particles;
    }
    
    // updates priority queue with all new events for particle a
    private void predict (Particle a, double limit) {
        if (a == null) return;
        
        // particle-particle collisions
        for (int i = 0; i < particles.length; i++) {
            double dt = a.timeToHit (particles[i]);
            if (t + dt <= limit)
                pq.insert (new Event (t + dt, a, particles[i]));
        }
        
        // particle-wall collisions
        double dtX = a.timeToHitVerticalWall ();
        double dtY = a.timeToHitHorizontalWall ();
        //double dtE = a.timeToHitEmbulo();
        //if (t + dtE <= limit) pq.insert(new Event(t + dtX, a, E));
        if (t + dtX <= limit) pq.insert (new Event (t + dtX, a, null));
        if (t + dtY <= limit) pq.insert (new Event (t + dtY, null, a));
    }
    
    // redraw all particles
    private void redraw (double limit) {
        StdDraw.clear ();
	particles[0].drawpiston ();
        for (int i = 1; i < particles.length; i++) {
            particles[i].draw ();
        }
        StdDraw.show (20);
        if (t < limit) {
            pq.insert (new Event (t + 1.0 / hz, null, null));
        }
    }
    
    
    /********************************************************************************
     *  Event based simulation for limit seconds
     ********************************************************************************/
    public void simulate (double limit, int left, int right) {
        
        // initialize PQ with collision events and redraw event
        pq = new MinPQ<Event> ();
        for (int i = 0; i < particles.length; i++) {
            predict (particles[i], limit);
        }
        pq.insert (new Event (0, null, null));        // redraw event
        
        int j = 1;
        
        // the main event-driven simulation loop
        while (!pq.isEmpty ()) {
            
            // get impending event, discard if invalidated
            Event e = pq.delMin ();
            if (!e.isValid ()) continue;
            Particle a = e.a;
            Particle b = e.b;
            
            // physical collision, so update positions, and then simulation clock
            for (int i = 0; i < particles.length; i++)
                particles[i].move (e.time - t);
            t = e.time;
            
            // process event
            if (a != null && b != null) a.bounceOff (b);              // particle-particle collision
            else if (a != null && b == null) a.bounceOffVerticalWall ();   // particle-wall collision
                //else if (a != null && b.equalsTo(E)) a.bounceOffEmbulo();         // particle-wall collision
            else if (a == null && b != null) b.bounceOffHorizontalWall (); // particle-wall collision
            else if (a == null && b == null) redraw (limit);               // redraw event
            
            // update the priority queue with new collisions involving a or b
            predict (a, limit);
            predict (b, limit);
            
            int conttempo = (int) t;
            
            if (conttempo > j) {
                double kmedio = 0;
                StdOut.print (j);
                // determina a energia cinetica media das particulas do lado esquerdo
                for (int i = 1; i <= left; i++) {
                    kmedio += particles[i].kineticEnergy () / left;
                }
                StdOut.print ("\t" + kmedio);
		          kmedio = 0.0;
                // determina a energia cinetica media das particulas do lado direito
                for (int i = left + 1; i <= right + left; i++) {
                    kmedio += particles[i].kineticEnergy () / right;
                }
                StdOut.println ("\t" + kmedio);
                j++;
            }
        }
    }
    
    
    /*************************************************************************
     *  An event during a particle collision simulation. Each event contains
     *  the time at which it will occur (assuming no supervening actions)
     *  and the particles a and b involved.
     *
     *    -  a and b both null:      redraw event
     *    -  a null, b not null:     collision with vertical wall
     *    -  a not null, b null:     collision with horizontal wall
     *    -  a and b both not null:  binary collision between a and b
     *
     *************************************************************************/
    private static class Event implements Comparable<Event> {
        private final double time;         // time that event is scheduled to occur
        private final Particle a, b;       // particles involved in event, possibly null
        private final int countA, countB;  // collision counts at event creation
        
        
        // create a new event to occur at time t involving a and b
        public Event (double t, Particle a, Particle b) {
            this.time = t;
            this.a = a;
            this.b = b;
            if (a != null) countA = a.count ();
            else countA = -1;
            if (b != null) countB = b.count ();
            else countB = -1;
        }
        
        // compare times when two events will occur
        public int compareTo (Event that) {
            if (this.time < that.time) return -1;
            else if (this.time > that.time) return +1;
            else return 0;
        }
        
        // has any collision occurred between when event was created and now?
        public boolean isValid () {
            if (a != null && a.count () != countA) return false;
            if (b != null && b.count () != countB) return false;
            return true;
        }
        
    }
    
    
    /********************************************************************************
     *  Sample client
     ********************************************************************************/
    public static void main (String[] args) {
        
        // remove the border
        StdDraw.setXscale (1.0 / 22.0, 21.0 / 22.0);
        StdDraw.setYscale (1.0 / 22.0, 21.0 / 22.0);
        
        
        // StdDraw.setCanvasSize(1024, 768);
        // StdDraw.setXscale(1.0/22.0 +.016, +0.013 + 27.6666667/22.0);
        // StdDraw.setYscale(1.0/22.0, 21.0/22.0);
        
        // turn on animation mode
        StdDraw.show (0);
        
        // the array of particles
        Particle[] particles;
        
        int left = 0;
        int right = 0;
        
        // create N random particles
        if (args.length == 1) {
            int N = Integer.parseInt (args[0]);
            particles = new Particle[N];
            for (int i = 0; i < N; i++) particles[i] = new Particle ();
        }
        
        // or read from standard input
        else {
            left = StdIn.readInt ();
            right = StdIn.readInt ();
            particles = new Particle[left + right + 1];
            for (int i = 0; i < left + right + 1; i++) {
                double rx = StdIn.readDouble ();
                double ry = StdIn.readDouble ();
                double vx = StdIn.readDouble ();
                double vy = StdIn.readDouble ();
                double radius = StdIn.readDouble ();
                double mass = StdIn.readDouble ();
                int r = StdIn.readInt ();
                int g = StdIn.readInt ();
                int b = StdIn.readInt ();
                int index = StdIn.readInt ();
                Color color = new Color (r, g, b);
                particles[i] = new Particle (rx, ry, vx, vy, radius, mass, color, index);
            }
        }
        
        double kmedio = 0;
        // determina a energia cinetica media das particulas do lado esquerdo
        for (int i = 0; i <= left; i++) {
            kmedio += particles[i].kineticEnergy () / left;
        }
        StdOut.print (0 + "\t" + kmedio);
        kmedio = 0.0;
        
        // determina a energia cinetica media das particulas do lado direito
        for (int i = left + 1; i <= right+left; i++) {
            kmedio += particles[i].kineticEnergy () / right;
        }
        StdOut.println ("\t" + kmedio);
        
        // create collision system and simulate
        CollisionSystem system = new CollisionSystem (particles);
        system.simulate (10000, left, right);
    }
    
}

