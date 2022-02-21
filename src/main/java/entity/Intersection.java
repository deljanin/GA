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
    private Vechicle[] roundabout;
    private int semaphoreTimer = 5;
    private boolean semaphore = true;

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
        canIGo();
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
            vehicleQueue.put(road.getEndArc(), new LinkedBlockingQueue<>());
            //System.out.println(road.getId());
        });
        roundabout = new Vechicle[roadsIn.size()*3];
        for (int i = 0; i < roundabout.length-1; i++) {
            roundabout[i] = null;
        }
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

    public void canIGo(){
        List<Vechicle> onTheIntersection;
        switch (type) {
            //Basic intersection
            case 1:
                //System.out.println("Number of roads on the intersection: " + this.id + " is " + vehicleQueue.size());
                if (vehicleQueue.values().stream().filter(Collection::isEmpty).count() == vehicleQueue.size()) return;
                onTheIntersection = new ArrayList<>();
                vehicleQueue.values().forEach(q -> {
                    if (!q.isEmpty()) {
                        Vechicle v = q.peek();
                        onTheIntersection.add(v);
                        //if(v.getRoute().peek().getEndArc() == arc1) v.setRiding(true);
                    }
                });
                if (onTheIntersection.size() == 1) {
                    Vechicle tmp = onTheIntersection.get(0);
                    //System.out.println("tukaj: " + tmp.getRoute().peek().getId());
                    vehicleQueue.get(tmp.getComingFromArc()).remove();
                    tmp.nextRoad();
                    return;
                }
                if (!vehicleQueue.get(this.arc1).isEmpty() || !vehicleQueue.get(this.arc2).isEmpty()) {
                    Vechicle arc1Car = vehicleQueue.get(this.arc1).peek();
                    Vechicle arc2Car = vehicleQueue.get(this.arc2).peek();
                    if (arc1Car != null) {
                        vehicleQueue.get(arc1Car.getRoute().peek().getEndArc()).remove(arc1Car);
                        arc1Car.nextRoad();
                    }
                    if (arc2Car != null) {
                        vehicleQueue.get(arc2Car.getRoute().peek().getEndArc()).remove(arc2Car);
                        arc2Car.nextRoad();
                    }
                    return;
                }
                //<<<<< NO CAR ON PRIORITY ROAD AND MORE THEN ONE CAR ON THE INTERSECTION>>>>>
                onTheIntersection.forEach(x -> {
                    if (!x.isAlreadyRemoved()) x.removeRoadWithoutMakingTheCarDrive();
                });
                int endArc = onTheIntersection.get(0).getRoute().peek().getStartArc();
                if (onTheIntersection.stream().allMatch(x -> x.getRoute().peek().getStartArc() == endArc)){
                    onTheIntersection.forEach(x ->{
                        if (x.getComingFromArc() > endArc && endArc > x.getComingFromArc()/2){
                            x.setRiding(true);
                            vehicleQueue.get(x.getComingFromArc()).remove();
                        }else x.setAlreadyRemoved(true);
                    });
                    return;
                }
                // If we come to here all the cars on the intersection can go
                vehicleQueue.values().forEach(q ->{
                    if (!q.isEmpty()) {
                        q.peek().nextRoad();
                        q.remove();
                    }
                });
                break;
            //Roundabout
            case 2:
                if (vehicleQueue.values().stream().filter(Collection::isEmpty).count() == vehicleQueue.size()) return;
                onTheIntersection = new ArrayList<>();
                vehicleQueue.values().forEach(q -> {
                    if (!q.isEmpty()) {
                        Vechicle v = q.peek();
                        onTheIntersection.add(v);
                        //if(v.getRoute().peek().getEndArc() == arc1) v.setRiding(true);
                    }
                });
                onTheIntersection.forEach(x -> {
                    if (!x.isAlreadyRemoved()) x.removeRoadWithoutMakingTheCarDrive();
                });
                if (!(Arrays.stream(roundabout).allMatch(Objects::isNull))) shift();
                for (int i = 0; i < roundabout.length; i++) {
                    if (roundabout[i] == null) continue;
                    if (i % 3 == 0) {
                        if (roundabout[i].getRoute().isEmpty()) continue;
                        if (i / 3 == roundabout[i].getRoute().peek().getStartArc()) {
                            //vehicleQueue.get(roundabout[i].getComingFromArc()).remove();
                            roundabout[i].setInRoundabout(false);
                            roundabout[i].setRiding(true);
                        } else {
                            if (i == 0) {
                                roundabout[roundabout.length - 1] = roundabout[i];
                            } else {
                                roundabout[i - 1] = roundabout[i];
                            }
                        }
                        roundabout[i] = null;
                    }
                }
                onTheIntersection.forEach(x -> {
                    int arc = x.getComingFromArc();
                    int going = arc*3-1;
                    int coming = arc*3+1;
                    if (arc*3 == 0){
                        going = roundabout.length-1;
                    }
                    System.out.println("arc is: " + arc + " coming is: " + coming + " roundabout size is: " + roundabout.length + " intersection id is: " + this.id);
                    if (roundabout[coming] == null && roundabout[going] == null) {
                        roundabout[going] = x;
                        vehicleQueue.get(arc).remove();
                        x.setInRoundabout(true);
                    }
                });
                break;
            //Semaphore
            case 3:
                if (vehicleQueue.values().stream().filter(Collection::isEmpty).count() == vehicleQueue.size()) return;
                onTheIntersection = new ArrayList<>();
                vehicleQueue.values().forEach(q -> {
                    if (!q.isEmpty()) {
                        Vechicle v = q.peek();
                        onTheIntersection.add(v);
                        //if(v.getRoute().peek().getEndArc() == arc1) v.setRiding(true);
                    }
                });
                onTheIntersection.forEach(x -> {
                    if (x != null) {
                        if (semaphore) {
                            if (x.getComingFromArc() % 2 == 0) {
                                System.out.println("Even arcs can go! " + semaphore);
                                System.out.println("Coming from arc is: " + x.getComingFromArc());
                                x.setRiding(true);
                                System.out.println("Is the car finished? " + vehicleQueue.get(x.getComingFromArc()).peek().isFinished());
                                vehicleQueue.get(x.getComingFromArc()).remove();
                            }
                        } else {
                            if (x.getComingFromArc() % 2 == 1) {
                                System.out.println("Odd arcs can go! " + semaphore);
                                x.setRiding(true);
                                vehicleQueue.get(x.getComingFromArc()).remove();
                            }
                        }
                    }
                });
                semaphoreTimer--;
                if (semaphoreTimer == 0) {
                    System.out.println("SemaphoreTimer was 0!" + semaphoreTimer);
                    semaphoreTimer = 5;
                    semaphore = !semaphore;
                }
                break;
            case 0:
                vehicleQueue.values().forEach(q -> {
                    if (!q.isEmpty()) {
                        q.peek().nextRoad();
                        q.remove();
                        //if(v.getRoute().peek().getEndArc() == arc1) v.setRiding(true);
                    }
                });
                break;
            default:
                System.out.println("this shouldn't happen");
        }
    }

    public void arrived(int roadEndArc, Vechicle car) throws InterruptedException {
        //System.out.println(car);
        vehicleQueue.get(roadEndArc).put(car);
        //System.out.println(vehicleQueue.get(roadEndArc).peek().isRiding());
    }

    private void shift(){
        System.out.println("SHIFTING");
        Vechicle temp=roundabout[0];
        for(int i=0;i<roundabout.length-1;i++)
        {
            roundabout[i]=roundabout[i+1];
        }
        roundabout[roundabout.length-1]=temp;
    }
}

