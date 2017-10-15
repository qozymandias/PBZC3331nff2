import java.util.*;

public class Tasker<E> implements Runnable {

    private Thread t;

    private long initialTimeMicro;
    private long delay;
    private long timeToLive;

    private Graph<E> g;
    private E start;
    private E end;

    private String routingScheme;

    private String networkScheme;

    private int hops;

    public Tasker(Long initialTimeMicro, Long delay, Long timeToLive, 
                  Graph<E> g, E start, E end, String routingScheme, String networkScheme) {
        
        this.t = null;

        this.initialTimeMicro = initialTimeMicro;
    
        this.delay = delay;
        this.timeToLive = timeToLive;

        this.g = g;
        this.start = start;
        this.end = end;

        this.routingScheme = routingScheme;

        this.networkScheme = networkScheme;

        this.hops = 0;
    }


    @Override 
    public void run() {
        try {
            boolean done = false;
            while(true) {

                Long timeD = System.nanoTime()/1000 - this.initialTimeMicro; 
                if( timeD >= this.delay) {

                    Thread thread = Thread.currentThread();

                    if (timeD == this.delay) {

                        System.out.println (thread.getId() + " Starting task..." + (double)timeD/1000000 );
                    }

                    //do task

                    if(!done) {

                        if(Objects.equals(this.networkScheme, "CIRCUIT")) {
                            // circuit - same path for packets
                            RouterAlgo<E> r = new RouterAlgo<E>(this.g);
                            if(Objects.equals(this.routingScheme, "SHP")) {
                                done = r.shortestHopPath(this.start, this.end);
                            } else if(Objects.equals(this.routingScheme, "SDP")) {
                                done = r.shortestDelayPath(this.start, this.end);
                            }
                            this.hops = r.getHops();
                        } else if(Objects.equals(this.networkScheme, "PACKET")) {
                            // packet - new path for each packet 
                            //        - evaluate routing protocol N times
                            RouterAlgo<E> r = new RouterAlgo<E>(this.g);
                            if(Objects.equals(this.routingScheme, "SHP")) {
                                done = r.shortestHopPath(this.start, this.end);
                            } else if(Objects.equals(this.routingScheme, "SDP")) {
                                done = r.shortestDelayPath(this.start, this.end);
                            }
                            this.hops = r.getHops();
                        }

                        
                    }


                    timeD = System.nanoTime()/1000 - this.initialTimeMicro - this.delay; 
                    if( timeD >= this.timeToLive) {

                        System.out.println (thread.getId() + " Ending task..." + (double)timeD/1000000 );
                        Thread.sleep(1);

                        break;
                    }   
                }   
            


                

            }
        
        } catch (InterruptedException e) {
             System.out.println("Thread interrupted.");
        }
    }


    public void start() {
        if(this.t == null) {
            this.t = new Thread(this,Long.toString(System.nanoTime()));
            this.t.start();
        }
    }

    public boolean join() {
        try {
            this.t.join();
        } catch (InterruptedException e ) {

        }
        return true;
    }

    public int getHops() {
        return this.hops;
    }

}