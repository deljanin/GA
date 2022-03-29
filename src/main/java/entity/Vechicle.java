package entity;


import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Vechicle extends Actor{
    private float speed;
    private Queue<Road> route;
    private boolean isRiding = true;
    private boolean isFinished = false;
    private boolean next = false;
    private int comingFromArc;
    private boolean alreadyRemoved = false;
    private boolean inRoundabout = false;

    public Vechicle(float speed, List<Road> route, Simulation simulation) {
        super(0,0, simulation);
        this.speed = speed;
        this.route = new LinkedList<>(route);
        this.x = this.route.peek().getStart().x;
        this.y = this.route.peek().getStart().y;
        /*
        this.x = (float) sim.getXY(this.route.peek().getStartId()).getX();                     //Kako dostopat v routu preko start in end Idjev do koordinat....
        this.y = (float) sim.getXY(this.route.peek().getStartId()).getY();
         */
    }


    /*tick too fast for render*/

    @Override
    public void tick(double elapsedTime) throws InterruptedException {
        if (route.isEmpty() && !inRoundabout) {
            isFinished = true;
            isRiding = false;
        }
        if (!isRiding || isFinished || route.isEmpty()) return;
        //dont be afraid of references
        Road currentRoad = route.peek();

        float totalTicks = currentRoad.getLength() / this.speed; // dobimo vse tick-e tako da delimo dolžino v metrih z hitrostjo v m/s

        float oneStepX = ((currentRoad.getStart().x - currentRoad.getEnd().x) / totalTicks)*(-1);
        float oneStepY = ((currentRoad.getStart().y - currentRoad.getEnd().y) / totalTicks)*(-1);

        float oneRealStepX = ((float) (oneStepX * elapsedTime));
        float oneRealStepY = ((float) (oneStepY * elapsedTime));


        //--------------CIRCLE AROUND INTERSECTIONS CALCULATION----------------
        float prviParameter = (currentRoad.getEnd().x - this.x)*(currentRoad.getEnd().x - this.x);
        float prviParameterNext = (currentRoad.getEnd().x - this.x+oneRealStepX)*(currentRoad.getEnd().x - this.x+oneRealStepX);
        float drugiParameter = (currentRoad.getEnd().y - this.y)*(currentRoad.getEnd().y - this.y);
        float drugiParameterNext = (currentRoad.getEnd().y - this.y+oneRealStepY)*(currentRoad.getEnd().y - this.y+oneRealStepY);

        float sestevek = prviParameter + drugiParameter;
        float sestevekNext = prviParameterNext + drugiParameterNext;


        float d = 10*10 - sestevek;
        float dNext = 10*10 - sestevekNext;
        //---------------------------------------------------------------------

        //If radius squared of the intersection is >= 0 then we are at the intersection...
        if (d > 0 || next) {
            isRiding = false;
            this.comingFromArc = getRoute().peek().getEndArc();
            sim.getIntersection(currentRoad.getEnd().getId()).arrived(this.comingFromArc, this);
        } else {
            if (dNext > 0) next=true;
            // dobimo trenutne koordinate tako, da množimo steps z elapsed time z one Step
            this.x = this.x + oneRealStepX;
            this.y = this.y + oneRealStepY;
        }
    }
    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
        graphics.setColor(Color.BLUE);
        graphics.fillRect((int)this.x,(int)this.y,5,5);
    }

    public synchronized void nextRoad(){
        //System.out.println(route.peek());
        if (route.size() >= 1) {
            this.x = route.peek().getEnd().x;
            this.y = route.peek().getEnd().y;
            route.remove();
            next=false;
        }
        isRiding = true;
    }

    public float getSpeed() {
        return speed;
    }

    public int getComingFromArc() {
        return comingFromArc;
    }

    public void setComingFromArc(int comingFromArc) {
        this.comingFromArc = comingFromArc;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Queue<Road> getRoute() {
        return route;
    }

    public void setRoute(Queue<Road> route) {
        this.route = route;
    }

    public boolean isRiding() {
        return isRiding;
    }

    public void setRiding(boolean riding) {
        isRiding = riding;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void removeRoadWithoutMakingTheCarDrive(){
        nextRoad();
        isRiding = false;
    }

    public boolean isAlreadyRemoved() {
        return alreadyRemoved;
    }

    public void setAlreadyRemoved(boolean alreadyRemoved) {
        this.alreadyRemoved = alreadyRemoved;
    }

    public boolean isInRoundabout() {
        return inRoundabout;
    }

    public void setInRoundabout(boolean inRoundabout) {
        this.inRoundabout = inRoundabout;
    }
}
