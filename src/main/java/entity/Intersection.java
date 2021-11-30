package entity;

import data.IntersectionData;

import java.awt.*;

public class Intersection extends Actor {
    private int id;
    private int type;
    private int arc1;
    private int arc2;

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
    }

    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
        graphics.setColor(getColor());
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
}

