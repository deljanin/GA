package entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Network {
    private String intersections_file;
    private String roads_file;
    private List<Actor> intersections;
    private List<Actor> roads;

    public Network(String intersections_file, String roads_file) {
        this.intersections_file = intersections_file;
        this.roads_file = roads_file;
    }


    public boolean initialize() {
        Gson gson = new Gson();
        try {
            //prase intersections
            Type listType = new TypeToken<ArrayList<Intersection>>() {}.getType();
            JsonReader reader = new JsonReader(new FileReader(this.intersections_file));
            intersections = new Gson().fromJson(reader, listType);
            System.out.println("Successfully imported " + intersections.size() + " intersections");
            //parse roads
            listType = new TypeToken<ArrayList<Intersection>>() {}.getType();
            reader = new JsonReader(new FileReader(this.roads_file));
            roads = new Gson().fromJson(reader, listType);
            System.out.println("Successfully imported " + roads.size() + " roads");

        } catch (FileNotFoundException e) {
            System.out.println("File not found exception, initialization failed.");
            return false;
        }
        return true;
    }
}
