package entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import data.IntersectionData;
import data.RoadData;
import org.jgrapht.Graph;
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
    private List<Vehicle> cars = new ArrayList<>();
    private Simulation simulation;
    private Config config;

    public Network(String intersections_file, String roads_file, Simulation simulation, Config config) {
        this.intersections_file = intersections_file;
        this.roads_file = roads_file;
        this.simulation = simulation;
        this.config = config;
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

            LinkedList<Intersection> parking = intersectionMap.values().stream().filter(intersection -> intersection.getType() == 0).collect(Collectors.toCollection(LinkedList::new));

            DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

            Random rnd = new Random(config.seed);
            for (int i = 0; i < config.numberOfVehicles; i++) {
                Collections.shuffle(parking);
                List<Road> route;
                while((route = dijkstraShortestPath.getPath(parking.getFirst(), parking.getLast()).getEdgeList()).isEmpty()){
                    Collections.shuffle(parking);
                }
                Vehicle car = new Vehicle(14,route,simulation);
                cars.add(car);
            }



        } catch (FileNotFoundException e) {
            System.out.println("File not found exception, initialization failed.");
        }
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

    public List<Vehicle> getCars() {return cars;}

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
