package entity;

import java.awt.*;

public class Road extends Actor{
    private int id;
    private int startId;
    private int endId;
    private int startArc;
    private int endArc;
    private float length;
    private int type;

    @Override
    public void tick(double elapsedTime) {
        //move my x, and y according to speed, and elapsedTime
    }

    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
        //TODO: render road as line
        Coordinates startCoordinates = sim.getXY(startId);
        Coordinates endCoordinates = sim.getXY(endId);
        graphics.setColor(Color.black);
        graphics.drawLine((int)startCoordinates.getX(),(int)startCoordinates.getY(),(int)endCoordinates.getX(),(int)endCoordinates.getY()); //Fix with floor or whatever, think about it....
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
