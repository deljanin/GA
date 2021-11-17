package entity;

import java.awt.*;

public class Vechicle extends Actor{
    private float speed;

    @Override
    public void tick(double elapsedTime) {
        //move my x, and y according to speed, and elapsedTime
    }

    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
    }
}
