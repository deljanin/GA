package entity;

import data.IntersectionData;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Intersection extends Actor {
    private int id;
    private int type;
    private int arc1;
    private int arc2;
    private HashMap<Integer, Road> roadsIn;
    private HashMap<Integer, Road> roadsOut;
    private HashMap<Integer, BlockingQueue<Vechicle>> vehicleQueue;

    public HashMap<Integer, Road> getRoadsIn() {
        return roadsIn;
    }

    public void setRoadsIn(HashMap<Integer, Road> roadsIn) {
        this.roadsIn = roadsIn;
    }

    public HashMap<Integer, Road> getRoadsOut() {
        return roadsOut;
    }

    public void setRoadsOut(HashMap<Integer, Road> roadsOut) {
        this.roadsOut = roadsOut;
    }

    //Add incoming and outgoing roads of an intersection
    public void addIn(Road road){
        this.roadsIn.put(road.getId(), road);
    }
    public void addOut(Road road){
        this.roadsOut.put(road.getId(), road);
    }

    @Override
    public void tick(double elapsedTime) {
        //move my x, and y according to speed, and drawOvalelapsedTime
    }

    public Intersection(IntersectionData intersectionData, Simulation simulation) {
        super(intersectionData.x,intersectionData.y, simulation);
        this.id = intersectionData.id;
        this.type = intersectionData.type;
        this.arc1 = intersectionData.arc1;
        this.arc2 = intersectionData.arc2;
        this.roadsIn = new HashMap<Integer, Road>();
        this.roadsOut = new HashMap<Integer, Road>();
    }

    public void initialize(){
        vehicleQueue = new HashMap<Integer, BlockingQueue<Vechicle>>();
        roadsIn.values().forEach(road ->{
            vehicleQueue.put(road.getId(), new LinkedBlockingQueue<>());
        });
    }

    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
        graphics.setColor(getColor());
//        if(this.id == 36){
//            graphics.setColor(new Color(0x00FF32));
//        }
        graphics.fillOval((int)this.x,(int)this.y,10,10);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getArc1() {
        return arc1;
    }

    public void setArc1(int arc1) {
        this.arc1 = arc1;
    }

    public int getArc2() {
        return arc2;
    }

    public void setArc2(int arc2) {
        this.arc2 = arc2;
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "id=" + id +
                ", type=" + type +
                ", arc1=" + arc1 +
                ", arc2=" + arc2 +
                '}';
    }

    public Color getColor(){
        switch (this.type){
            case 0 : return new Color(13131231);
            case 1 : return new Color(03133231);
            case 2 : return new Color(1311213033);
            case 3 : return new Color(933112130);
            default: return Color.BLACK;
        }
    }

    public boolean canIGo(){
        if (vehicleQueue.values().stream().filter(Collection::isEmpty).count() >= vehicleQueue.size()-1) return true;
        return false;
    }

    public void arrived(int roadID, Vechicle car) throws InterruptedException {
        //System.out.println(car);
        vehicleQueue.get(roadID).put(car);
    }
}

