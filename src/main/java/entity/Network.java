package entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import data.IntersectionData;
import data.RoadData;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Network {
    private String intersections_file;
    private String roads_file;

    private HashMap<Integer, Intersection> intersectionMap;
    private HashMap<Integer, Road> roadMap;
    private Graph<Intersection, Road> graph;
    private Simulation simulation;
    private Config config;
    private Distribution distribution;
    private LinkedList<Intersection> parking;
    private DijkstraShortestPath dijkstraShortestPath;
    AllDirectedPaths<Intersection, Road> allPaths;
    private int k=-1;

    public Network(String intersections_file, String roads_file, Simulation simulation, Config config, Distribution distribution) {
        this.intersections_file = intersections_file;
        this.roads_file = roads_file;
        this.simulation = simulation;
        this.config = config;
        this.distribution = distribution;
    }


    public void initialize() {
        Gson gson = new Gson();
        try {
            //parse from json to dataclass (IntersrctionData)
            Type listType = new TypeToken<ArrayList<IntersectionData>>() {}.getType();
            JsonReader reader = new JsonReader(new FileReader(this.intersections_file));
            List<IntersectionData> intersectionsData = new Gson().fromJson(reader, listType);
            //now construct actual Intersection objects from the data class
            List<Intersection> intersections = new ArrayList<Intersection>();
            intersectionsData.forEach(I -> intersections.add(new Intersection(I, simulation)));
            intersectionMap = new HashMap<>(intersections.size());
            intersections.forEach(intersection -> {
                intersectionMap.put(intersection.getId(), intersection);
            });
            listType = new TypeToken<ArrayList<RoadData>>() {}.getType();
            reader = new JsonReader(new FileReader(this.roads_file));
            List<RoadData> roadsData = new Gson().fromJson(reader, listType);
            List<Road> roads = new ArrayList<Road>();
            roadsData.forEach(R-> roads.add(new Road(R,simulation)));
            roadMap = new HashMap<>(roads.size());
            roads.forEach(road -> {
                roadMap.put(road.getId(), road);
            });
            graph = new DirectedWeightedPseudograph<>(Road.class);

            //add intersections as vertexes
            intersections.forEach(intersection -> graph.addVertex(intersection));
            //add edges by mapping them to vertices
            roads.forEach(road -> {
                if(road != null) {
                    graph.addEdge(

                    intersectionMap.get(road.getStartId()), //start
                    intersectionMap.get(road.getEndId()), //end
                    road
                    );
                    graph.setEdgeWeight(road, road.getLength());

                    //Adds incoming and outgoing roads of an intersections
                    intersectionMap.get(road.getEndId()).addIn(road);
                    intersectionMap.get(road.getStartId()).addOut(road);

                    //initializing intersection queues for every incoming road
                    intersectionMap.values().forEach(Intersection::initialize);
                }
            });

            parking = intersectionMap.values().stream().filter(intersection -> intersection.getType() == 0).collect(Collectors.toCollection(LinkedList::new));
            dijkstraShortestPath = new DijkstraShortestPath(graph);
            allPaths = new AllDirectedPaths<Intersection, Road>(graph);




        } catch (FileNotFoundException e) {
            System.out.println("File not found exception, initialization failed.");
        }
    }

    public void emit(long ticks) {
        Random rnd = new Random(config.seed);
        if (ticks >= 86400) {//86400 seconds in a day
            System.out.println(simulation.totalTicksWaiting/ simulation.totalCars);
            System.exit(0);
        }
        if (ticks % 10800 == 0) k++;
        Vector<Vehicle> cars = new Vector<>();

        for (Emitter emitter: distribution.emitters) {
            if (emitter.spaceDrivingIn[k%8] != 0 && ticks % emitter.spaceDrivingIn[k%8] == 0) {
                Collections.shuffle(parking, rnd);
                List<Road> route = dijkstraShortestPath.getPath(intersectionMap.get(emitter.id), parking.getLast()).getEdgeList();
                Vehicle car = new Vehicle(14, route, simulation);
                cars.add(car);
            } else if (emitter.spaceOvertakingIn[k%8] != 0 && ticks % emitter.spaceOvertakingIn[k%8] == 0) {
                Collections.shuffle(parking, rnd);
                List<Road> route = dijkstraShortestPath.getPath(intersectionMap.get(emitter.id), parking.getLast()).getEdgeList();
                Vehicle car = new Vehicle(14, route, simulation);
                cars.add(car);
            } else if (emitter.spaceDrivingOut[k%8] != 0 && ticks % emitter.spaceDrivingOut[k%8] == 0) {
                Collections.shuffle(parking, rnd);
                List<Road> route = dijkstraShortestPath.getPath(parking.getFirst(), intersectionMap.get(emitter.id)).getEdgeList();
                Vehicle car = new Vehicle(14, route, simulation);
                cars.add(car);
            } else if (emitter.spaceOvertakingOut[k%8] != 0 && ticks % emitter.spaceOvertakingOut[k%8] == 0) {
                Collections.shuffle(parking, rnd);
                List<Road> route = dijkstraShortestPath.getPath(parking.getFirst(), intersectionMap.get(emitter.id)).getEdgeList();
                Vehicle car = new Vehicle(14, route, simulation);
                cars.add(car);
            }
        }
        for (int i = 0; i < Math.round((cars.size()/0.7)*0.3); i++) {
            Collections.shuffle(parking, rnd);
            List<Road> route;
            while ((route = dijkstraShortestPath.getPath(parking.getFirst(), parking.getLast()).getEdgeList()).isEmpty()) {
                Collections.shuffle(parking, rnd);
            }
            Vehicle car = new Vehicle(14, route, simulation);
            cars.add(car);
        }
        simulation.totalCars += cars.size();
        simulation.actors.addAll(cars);
    }

    public String getIntersections_file() {
        return intersections_file;
    }

    public void setIntersections_file(String intersections_file) {
        this.intersections_file = intersections_file;
    }

    public String getRoads_file() {
        return roads_file;
    }

    public void setRoads_file(String roads_file) {
        this.roads_file = roads_file;
    }

    public HashMap<Integer, Intersection> getIntersectionMap() {
        return intersectionMap;
    }

    public void setIntersectionMap(HashMap<Integer, Intersection> intersectionMap) {
        this.intersectionMap = intersectionMap;
    }

    public HashMap<Integer, Road> getRoadMap() {
        return roadMap;
    }

    public void setRoadMap(HashMap<Integer, Road> roadMap) {
        this.roadMap = roadMap;
    }

    public Graph<Intersection, Road> getGraph() {
        return graph;
    }

    public void setGraph(Graph<Intersection, Road> graph) {
        this.graph = graph;
    }
}
