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
        this.x = (float) sim.getXY(route.peek().getStartId()).getX();                     //Kako dostopat v routu preko start in end Idjev do koordinat....
        this.y = (float) sim.getXY(route.peek().getStartId()).getY();
    }

    @Override
    public void tick(double elapsedTime) {
        //move my x, and y according to speed, and elapsedTime

        //kako točno računat premik avta v updated funkciji z elapsedTime?

        float totalTicks = route.peek().getLength()/this.speed; // dobimo vse tick-e tako da delimo dolžino v metrih z hitrostjo v m/s

        // dobimo en step tako da delimo razdaljo med zacetno x coordinato od koncne in delimo s totalTicks
        float oneStepX = (float) ((sim.getXY(route.peek().getStartId()).getX()-sim.getXY(route.peek().getEndId()).getX())/totalTicks);
        float oneStepY = (float) ((sim.getXY(route.peek().getStartId()).getY()-sim.getXY(route.peek().getEndId()).getY())/totalTicks);

        // dobimo trenutne koordinate tako, da množimo steps z elapsed time z one Step
        this.x = (float) (oneStepX*elapsedTime);
        this.y = (float) (oneStepY*elapsedTime);
    }

    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
        graphics.setColor(Color.BLUE);
        graphics.fillRect((int)this.x,(int)this.y,8,8);
    }
}
