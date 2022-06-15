package entity;

import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Simulation extends Canvas implements Runnable {
    private boolean running;
    private long ticks = 0;
    private Network network;
    private BufferedImage cityImage;
    Vector<Actor> actors = new Vector<>(); //objects within the simulation
    Config config;
    boolean GUI;
    int totalTicksWaiting = 0;
    int totalCars = 0;

    private void initialize(String configPath, String intersectionPath){

        try {
            config = new Gson().fromJson(Files.readString(Paths.get(configPath)), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //not needed presently, will be useful to set up data structures, compute paths, etc..
        network = new Network(intersectionPath, "roads.json", this, config);
        network.initialize();
        //populate intersection
        actors.addAll(network.getIntersectionMap().values());
        //populate roads
        actors.addAll(network.getRoadMap().values());

        actors.forEach(actor -> actor.sim = this);//Add this simulation reference to all actors

        try {
            cityImage =  ImageIO.read(new File("Koper.png"));
        } catch (IOException e) {
            System.out.println("Failed to load city image");
        }

    }
    //constructor overloading
    public Simulation() {
    }
    public Simulation(boolean running, boolean GUI, String configPath, String intersectionPath) {
        this.running = running;
        this.GUI = GUI;
        initialize(configPath,intersectionPath);
    }

    public Simulation(boolean running, Vector<Actor> actors) {
        this.running = running;
        this.actors = actors;
    }

    public void run(){
        while (running) {
            network.emit(ticks);
            try {
                tick();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (this.GUI) render(); //I cap render to 60FPS, ce hoces max fps gre render ven
            ticks++;
        }
    }
/*
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
                double slowdown = delta * config.simulationSpeed; //slowdown
                ticks++;
                tick(slowdown);
                if (this.GUI) render(delta); //I cap render to 60FPS, ce hoces max fps gre render ven
                frames++;

                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    frames = 0;
                }
                delta--;
            }
        }
    }
 */

    private void tick() throws InterruptedException {
        //let actors update them self
        Enumeration<Actor> vectorEnums = actors.elements();

        while(vectorEnums.hasMoreElements()) {
            vectorEnums.nextElement().tick(1);
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy(); //this sounds expensive
        if(bs == null){ //ugly
            this.createBufferStrategy(2);
            return;
        }

        Graphics graphics = bs.getDrawGraphics();

        graphics.drawImage(cityImage,0,0,null);
        //i let all actors render them self
        Enumeration<Actor> vectorEnums = actors.elements();

        while(vectorEnums.hasMoreElements()) {
            vectorEnums.nextElement().render(graphics,1);
        }
        graphics.dispose();
        bs.show();
    }

    public Intersection getIntersection(int id){
        return network.getIntersectionMap().get(id);
    }

    public void start(){
        new Thread(this).start();
    }
}
