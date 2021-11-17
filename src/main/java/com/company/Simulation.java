package com.company;


import java.awt.*;
import java.awt.image.BufferStrategy;

public class Simulation extends Canvas implements Runnable {
    private boolean running = false;
    private Thread thread;
    Node A = new Node(20,20);
    Node B = new Node(100,100);
    Node C = new Node(250,250);
    Edge AB = new Edge(A,B, 0.84);
    Edge BC = new Edge(B,C, 0.84);
    Edge CB = new Edge(C,B, 0.84);
    Edge BA = new Edge(B,A, 0.84);
    int i = 0;
    Vehicle car = new Vehicle(20,20);
    Edge[] arr = {AB,BC,CB,BA};

    public synchronized void start(){
        if(running) return;

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private void integrate(double time,double delta){
        double step = time/delta; // one step == 1 min
        System.out.println(step);

        double covered = AB.speed * step;
        //double stepsToTake = AB.length/AB.speed;
        //System.out.println(AB.length);
        double x1 = AB.end.x/covered;
        //System.out.println(x1);
        double y1 = AB.end.y/covered;
        car.x = car.x+(int)Math.round(x1);
        car.y = car.y+(int)Math.round(y1);
        //System.out.println(car.x + ", " + car.y);

    }

    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        //////////////////////////////////
        //Draw Here
        g.setColor(Color.black);
        g.fillRect(0,0, getWidth(), getHeight());
        g.setColor(Color.yellow);
        g.fillRect(A.x,A.y,25,25);
        g.fillRect(B.x,B.y,25,25);
        g.fillRect(C.x,C.y,25,25);
        g.drawLine(A.x,A.y,B.x,B.y);

        g.drawLine(B.x,B.y,C.x,C.y);
        g.setColor(Color.red);
        g.fillRect(car.x, car.y, 15,15);
        //car.Move(arr[i]);
        //////////////////////////////////

        g.dispose();
        bs.show();
    }


    @Override
    public void run() {
        double t = 0.0;
        final double dt = 0.1;

        double currentTime = System.currentTimeMillis();                          //hires_time_in_seconds();
        double accumulator = 0.0;

        while ( true )
        {
            double newTime = System.currentTimeMillis();
            double frameTime = newTime - currentTime;
            currentTime = newTime;

            accumulator += frameTime;

            while ( accumulator >= dt )
            {
                integrate(t, dt );
                accumulator -= dt;
                t += dt;
            }

            render();
        }

    }

    public static void main(String[] args) {
        new Window(800,600,"Traffic Simulator", new Simulation());
    }
}
