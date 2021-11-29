package entity;


import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class Vechicle extends Actor{
    private float speed;
    private Queue<Road> route;

    public Vechicle(float speed, LinkedList<Road> route) {
        this.speed = speed;
        this.route = route;
        //this.x = route.peek().getStartId().x                          Kako dostopat v routu preko start in end Idjev do koordinat....
        //this.y = route.peek().getStartId().y
    }

    @Override
    public void tick(double elapsedTime) {
        //move my x, and y according to speed, and elapsedTime

        //kako točno računat premik avta v updated funkciji z elapsedTime?
    }

    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
        graphics.setColor(Color.yellow);
        graphics.fillOval((int)this.x,(int)this.y,15,15);
    }
}
