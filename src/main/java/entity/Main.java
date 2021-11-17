package entity;

public class Main {
    public static void main(String[] args) {
        Simulation sim = new Simulation(true);
        new Window(800, 600, "Traffic Simulator", sim);
        sim.start();
    }
}
