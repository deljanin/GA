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
    }

}
