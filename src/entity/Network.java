package entity;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class Network {
    private String intersections_file;
    private String roads_file;
    private List<Actor> intersections;
    private List<Actor> roads;

    public Network(String intersections_file, String roads_file){
        this.intersections_file =intersections_file;
        this.roads_file = roads_file;
    }


    public boolean initialize(){
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(this.intersections_file));
            intersections = Arrays.asList(gson.fromJson(reader, Intersection[].class));
            System.out.println("Successfully imported " + intersections.size() +" intersections");

            reader = new JsonReader(new FileReader(this.roads_file));
            roads = Arrays.asList(gson.fromJson(reader, Road[].class));
            System.out.println("Successfully imported " + roads.size() +" roads");
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception, initialization failed.");
            return false;
        }
        return true;
    }
}
