package entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import data.IntersectionData;
import data.RoadData;
import org.jgrapht.Graph;
import org.jgrapht.*;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DirectedWeightedPseudograph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public class Network {
    private String intersections_file;
    private String roads_file;

    private HashMap<Integer, Intersection> intersectionMap;
    private HashMap<Integer, Road> roadMap;
    private Graph<Intersection, Road> graph;
    private List<Vechicle> cars = new ArrayList<>();
    private Simulation simulation;

    public Network(String intersections_file, String roads_file, Simulation simulation) {
        this.intersections_file = intersections_file;
        this.roads_file = roads_file;
        this.simulation = simulation;
    }


    public boolean initialize() {
        Gson gson = new Gson();
        try {
            //prase from json to dataclass (IntersrctionData)
            Type listType = new TypeToken<ArrayList<IntersectionData>>() {}.getType();
            JsonReader reader = new JsonReader(new FileReader(this.intersections_file));
            List<IntersectionData> intersectionsData = new Gson().fromJson(reader, listType);
            //now construct actual Intersection objects from the data class
            List<Intersection> intersections = new ArrayList<Intersection>();
            intersectionsData.stream().forEach(I -> intersections.add(new Intersection(I, simulation)));
            intersectionMap = new HashMap<Integer, Intersection>(intersections.size());
            intersections.forEach(intersection -> {
                intersectionMap.put(intersection.getId(), intersection);
            });
            System.out.println("Successfully imported " + intersections.size() + " intersections");
            //parse from json to dataclass (RoadData)
            listType = new TypeToken<ArrayList<RoadData>>() {}.getType();
            reader = new JsonReader(new FileReader(this.roads_file));
            List<RoadData> roadsData = new Gson().fromJson(reader, listType);
            List<Road> roads = new ArrayList<Road>();
            roadsData.stream().forEach(R-> roads.add(new Road(R,simulation)));
            roadMap = new HashMap<Integer, Road>(roads.size());
            roads.forEach(road -> {
                roadMap.put(road.getId(), road);
            });
            System.out.println("Successfully imported " + roads.size() + " roads");
            graph = new DirectedWeightedPseudograph<Intersection,Road>(Road.class);

            //add intersections as vertexes
            intersections.forEach(intersection -> graph.addVertex(intersection));
            //add edges by mapping them to vertices
            roads.forEach(road -> {
                graph.addEdge(
                        intersectionMap.get(road.getStartId()), //start
                        intersectionMap.get(road.getEndId()), //end
                        road
                );
                graph.setEdgeWeight(road,road.getLength());
            });


            System.out.println("Constructed graph with "
                    + graph.edgeSet().size() + " edges and "
                    + graph.vertexSet().size() + " vertices"
            );

            //fixed?: System.out.println(roads.get(1).getLength());   //  problem da graf ne mappa length --> weight


            DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);   // test z enim avtkom, ali je pravilno da tukaj definiramo poti in naredimo vse avte?

            List<Road> test = dijkstraShortestPath.getPath(intersections.get(1), intersections.get(2)).getEdgeList();
            //debug print of roads
            test.stream().forEach(road -> System.out.println(road));

            Vechicle car1 = new Vechicle(14,test,simulation);

            cars.add(car1);             // list avtkov, ki jih vrnemo simulaciji


        } catch (FileNotFoundException e) {
            System.out.println("File not found exception, initialization failed.");
            return false;
        }
        return true;
    }
    public Coordinates getXY(int intersectionId){
        return new Coordinates(intersectionMap.get(intersectionId).x, intersectionMap.get(intersectionId).y);
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

    public List<Vechicle> getCars() {return cars;}

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
