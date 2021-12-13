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
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

public class Config {
    private String config_file;
    private Map<?,?> configMap;

    private void loadConfig(){
        try {
            JsonReader reader = new JsonReader(new FileReader(this.config_file));
            configMap = new Gson().fromJson(reader, Map.class);
            reader.close();
            System.out.println("Config loaded!");
            //configMap.forEach((key, value) -> System.out.println(key + " " + value));
            //System.out.println(configMap.get("simulationSpeed"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Config(String config_file) {
        this.config_file = config_file;
        loadConfig();
    }

    public Map<?, ?> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<?, ?> configMap) {
        this.configMap = configMap;
    }
}
