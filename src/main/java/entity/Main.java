package entity;

public class Main {
    public static void main(String[] args) {
        boolean GUI = true;
        String configPath = "config.json";
        String intersectionPath = "intersections.json";
        if (args.length != 0) {
            GUI = Boolean.parseBoolean(args[0]);
            configPath = args[1];
            intersectionPath = args[2];
        }
        Simulation sim = new Simulation(true, GUI, configPath, intersectionPath);
        if (GUI) new Window(800, 800, "Traffic Simulator", sim);
        sim.start();
    }
}
