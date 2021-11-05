package com.company;


import java.awt.*;
import java.awt.image.BufferStrategy;

public class Simulation extends Canvas implements Runnable {
    private boolean running = false;
    private Thread thread;
    Node A = new Node(20,20);
    Node B = new Node(100,100);
    Node C = new Node(250,250);
    Edge AB = new Edge(A,B, 50);
    Edge BC = new Edge(B,C, 50);
    Edge CB = new Edge(C,B, 50);
    Edge BA = new Edge(B,A, 50);
    int i = 0;
    Vehicle car = new Vehicle(20,20);
    Edge[] arr = {AB,BC,CB,BA};

    public synchronized void start(){
        if(running) return;

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private void tick(){

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
        g.setColor(Color.red);
        g.fillRect(car.x, car.y, 15,15);
        car.Move(arr[i]);
        //////////////////////////////////

        g.dispose();
        bs.show();
    }


    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                tick();
                updates++;
                delta--;
            }
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: " + frames + " TICKS: " + updates);
                if(i == arr.length-1){
                    i=0;
                }else {
                    i++;
                }
                frames = 0;
                updates = 0;
            }
        }

    }

    public static void main(String[] args) {
        new Window(800,600,"Traffic Simulator", new Simulation());
    }
}
