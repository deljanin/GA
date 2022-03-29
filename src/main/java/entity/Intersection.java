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
    private int semaphoreTimer = 40;
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
    public void addIn(Road road) {
        this.roadsIn.put(road.getId(), road);
    }

    public void addOut(Road road) {
        this.roadsOut.put(road.getId(), road);
    }

    @Override
    public void tick(double elapsedTime) {
        canIGo();
        //move my x, and y according to speed, and drawOval elapsedTime
    }

    public Intersection(IntersectionData intersectionData, Simulation simulation) {
        super(intersectionData.x, intersectionData.y, simulation);
        this.id = intersectionData.id;
        this.type = intersectionData.type;
        this.arc1 = intersectionData.arc1;
        this.arc2 = intersectionData.arc2;
        this.roadsIn = new HashMap<Integer, Road>();
        this.roadsOut = new HashMap<Integer, Road>();
    }

    public void initialize() {
        vehicleQueue = new HashMap<Integer, BlockingQueue<Vechicle>>();
        roadsIn.values().forEach(road -> {
            vehicleQueue.put(road.getEndArc(), new LinkedBlockingQueue<>());
        });

        int max = 0;
        Road[] roadsInArray = roadsIn.values().toArray(new Road[]{});
        Road[] roadsOutArray = roadsOut.values().toArray(new Road[]{});

        for (Road road : roadsInArray) {
            if (road.getEndArc() > max) max = road.getEndArc();
        }
        for (Road road : roadsOutArray) {
            if (road.getStartArc() > max) max = road.getStartArc();
        }
        roundabout = new Vechicle[(max + 1) * 3];
    }

    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
        graphics.setColor(getColor());
        /*if(this.id == 20){
            graphics.setColor(new Color(0x00FF32));
         }*/
        graphics.fillOval((int) this.x, (int) this.y, 10, 10);
    }

    public Color getColor() {
        switch (this.type) {
            case 0:
                return new Color(13131231);
            case 1:
                return new Color(833177);
            case 2:
                return new Color(1311213033);
            case 3:
                return new Color(933112130);
            default:
                return Color.BLACK;
        }
    }

    synchronized public void canIGo() {
        List<Vechicle> onTheIntersection;
        if (vehicleQueue.values().stream().filter(Collection::isEmpty).count() == vehicleQueue.size() && type != 3)
            return;
        onTheIntersection = new ArrayList<Vechicle>();
        vehicleQueue.values().forEach(q -> {
            if (!q.isEmpty()) onTheIntersection.add(q.peek());
        });

        switch (type) {
            //Basic intersection
            case 1:
                if (onTheIntersection.size() == 1) {
                    vehicleQueue.get(onTheIntersection.get(0).getComingFromArc()).remove();
                    onTheIntersection.get(0).setRiding(true);
                    return;
                }
                if (!vehicleQueue.get(this.arc1).isEmpty() ) {
                    Vechicle arc1Car = vehicleQueue.get(this.arc1).peek();
                    if (arc1Car != null) {
                        vehicleQueue.get(arc1Car.getComingFromArc()).remove();
                        arc1Car.setRiding(true);
                    }
                    return;
                }
                //<<<<< NO CAR ON PRIORITY ROAD AND MORE THAN ONE CAR ON THE INTERSECTION>>>>>
                int endArc = onTheIntersection.get(0).getRoute().peek().getStartArc();
                if (onTheIntersection.stream().allMatch(x -> x.getRoute().peek().getStartArc() == endArc)) {
                    onTheIntersection.forEach(x -> {
                        if (x.getComingFromArc() == 0 && endArc == 3) {
                            vehicleQueue.get(x.getComingFromArc()).remove();
                            x.setRiding(true);
                        } else if (endArc == x.getComingFromArc() - 1) {
                            vehicleQueue.get(x.getComingFromArc()).remove();
                            x.setRiding(true);
                        }
                    });
                    return;
                }
                // If we come to here all the cars on the intersection can go
                onTheIntersection.forEach(v -> {
                    vehicleQueue.get(v.getComingFromArc()).remove();
                    v.setRiding(true);
                });
                break;
            //Semaphore
            case 2:
                if (semaphore) {
                    onTheIntersection.forEach(x -> {
                        if (x != null) {
                            if (x.getComingFromArc() % 2 == 0) {
                                vehicleQueue.get(x.getComingFromArc()).remove();
                                x.setRiding(true);
                            }
                        }
                    });
                } else {
                    onTheIntersection.forEach(x -> {
                        if (x != null) {
                            if (x.getComingFromArc() % 2 == 1) {
                                vehicleQueue.get(x.getComingFromArc()).remove();
                                x.setRiding(true);
                            }
                        }
                    });
                }
                semaphoreTimer--;
                if (semaphoreTimer == 0) {
                    semaphoreTimer = 40;
                    semaphore = !semaphore;
                }
                break;
            //Roundabout
            case 3:
                if (!(Arrays.stream(roundabout).allMatch(Objects::isNull))) shift();
                for (int i = 0; i < roundabout.length; i++) {
                    if (roundabout[i] == null) continue;
                    if (i % 3 == 0) {
                        if (i / 3 == roundabout[i].getRoute().peek().getStartArc()) {
                            roundabout[i].setRiding(true);
                            roundabout[i] = null;
                        }
                    }
                }
                onTheIntersection.forEach(x -> {
                    int arc = x.getComingFromArc();
                    int entrance = arc * 3;
                    int going = arc * 3 - 1;
                    int coming = arc * 3 + 1;
                    if (arc * 3 == 0) {
                        going = roundabout.length - 1;
                    }
                    if (roundabout[coming] == null && roundabout[going] == null && roundabout[entrance] == null) {
                        roundabout[going] = x;
                        vehicleQueue.get(arc).remove();
                    }
                });
                break;
            case 0:
                vehicleQueue.values().forEach(q -> {
                    if (!q.isEmpty()) {
                        q.peek().setRiding(true);
                        if (q.peek().getRoute().isEmpty()) {
                            q.peek().setRiding(false);
                            q.peek().setFinished(true);
                        }
                        q.remove();
                    }
                });
                break;
            default:
                System.out.println("this shouldn't happen");

        }
    }

    public void arrived(int roadEndArc, Vechicle car) throws InterruptedException {
        if (!car.isFinished()) vehicleQueue.get(roadEndArc).put(car);
    }

    private void shift() {
        Vechicle temp = roundabout[0];
        System.arraycopy(roundabout, 1, roundabout, 0, roundabout.length - 1);
        roundabout[roundabout.length - 1] = temp;
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
}

