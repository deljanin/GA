package entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Simulation extends Canvas implements Runnable {
    private boolean running;
    private long ticks = 0;
    private Network network;
    private BufferedImage cityImage;
    ArrayList<Actor> actors = new ArrayList<>();; //objects within the simulation

    private void initialize(){
        //not needed presently, will be useful to set up data structures, compute paths, etc..
        network = new Network("intersections.json", "roads.json");
        network.initialize();
        //populate intersection
        network.getIntersectionMap().values().forEach(intersection -> actors.add(intersection));
        //populate roads
        network.getRoadMap().values().forEach(road -> actors.add(road));
        network.getCars().forEach(car -> actors.add(car));// Add all cars to simulation

        actors.forEach(actor -> actor.sim = this);//Add this simulation reference to all actors

        try {
            cityImage =  ImageIO.read(new File("Koper.png"));
        } catch (IOException e) {
            System.out.println("Failed to load city image");
        }

    }
    //constructor overloading
    public Simulation() {
        initialize();
    }
    public Simulation(boolean running) {
        this.running = running;
        initialize();
    }

    public Simulation(boolean running, ArrayList<Actor> actors) {
        this.running = running;
        this.actors = actors;
        initialize();
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0; //i want 60fps for update
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns; //accumulator
            lastTime = now;
            while (delta >= 1) {
                tick(delta);
                render(delta); //I cap render to 6FPS, ce hoces max fps gre render ven
                frames++;

                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }
                delta--;
            }
        }
    }

    private void tick(double elapsedTime) {
        this.ticks++;
        //let actors update them self
        actors.stream().forEach(actor -> actor.tick(elapsedTime));
    }

    private void render(double elapsedTime) {
        BufferStrategy bs = this.getBufferStrategy(); //this sounds expensive
        if(bs == null){ //ugly
            this.createBufferStrategy(2);
            return;
        }

        Graphics graphics = bs.getDrawGraphics();

        graphics.drawImage(cityImage,0,0,null);
        //i let all actors render them self
        actors.stream().forEach(actor -> actor.render(graphics, elapsedTime));
        graphics.dispose();
        bs.show();
    }

    public Coordinates getXY(int intersectionId){
        return network.getXY(intersectionId);
    }

    public void start(){
        new Thread(this).start();
    }
}
