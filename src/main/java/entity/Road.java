package entity;

import data.RoadData;

import java.awt.*;

public class Road extends Actor{
    private int id;
    private int startId;
    private int endId;
    private int startArc;
    private int endArc;
    private float length;
    private float speed;
    private int type;

    private Intersection start;
    private Intersection end;

    public Road(RoadData roadData, Simulation simulation) {
        super(0,0, simulation);
        this.id = roadData.id;
        this.startId = roadData.startId;
        this.endId = roadData.endId;
        this.startArc = roadData.startArc;
        this.endArc = roadData.endArc;
        this.length = roadData.length;
        this.speed = roadData.speed;
        this.type = roadData.type;
        start = sim.getIntersection(startId);
        end = sim.getIntersection(endId);
    }

    @Override
    public void tick(double elapsedTime) {
        //move my x, and y according to speed, and elapsedTime
    }

    @Override
    public void render(Graphics graphics, double elapsedTime) {
        graphics.setColor(Color.black);
        graphics.drawLine(Math.round(start.x), Math.round(start.y), Math.round(end.x), Math.round(end.y));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public int getStartArc() {
        return startArc;
    }

    public void setStartArc(int startArc) {
        this.startArc = startArc;
    }

    public int getEndArc() {
        return endArc;
    }

    public void setEndArc(int endArc) {
        this.endArc = endArc;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Intersection getStart() {
        return start;
    }

    public void setStart(Intersection start) {
        this.start = start;
    }

    public Intersection getEnd() {
        return end;
    }

    public void setEnd(Intersection end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Road{" +
                "id=" + id +
                ", startId=" + startId +
                ", endId=" + endId +
                ", startArc=" + startArc +
                ", endArc=" + endArc +
                ", length=" + length +
                ", type=" + type +
                '}';
    }
}
