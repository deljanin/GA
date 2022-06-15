package entity;


import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Vehicle extends Actor{
    private float speed;
    private Queue<Road> route;
    private boolean isRiding = true;
    private boolean isFinished = false;
    private boolean next = false;
    private int comingFromArc;

    public Vehicle(float speed, List<Road> route, Simulation simulation) {
        super(0,0, simulation);
        this.speed = speed;
        this.route = new LinkedList<>(route);
        this.x = this.route.peek().getStart().x;
        this.y = this.route.peek().getStart().y;
    }

    @Override
    public synchronized void tick(double elapsedTime) throws InterruptedException {
        if (this.route.isEmpty() || this.x < 0 || this.y < 0 || this.x > 800 || this.y > 800) {
            this.isRiding = false;
            this.isFinished = true;
            sim.actors.remove(this);
            if (!route.isEmpty()) System.exit(0);
        }
        if (!this.isRiding || this.isFinished) return;

        Road currentRoad = this.route.peek();
        this.speed = currentRoad.getSpeed();

        float totalTicks = currentRoad.getLength() / this.speed;

        float oneStepX = ((currentRoad.getStart().x - currentRoad.getEnd().x) / totalTicks)*(-1);
        float oneStepY = ((currentRoad.getStart().y - currentRoad.getEnd().y) / totalTicks)*(-1);

        float oneRealStepX = ((float) (oneStepX * elapsedTime));
        float oneRealStepY = ((float) (oneStepY * elapsedTime));


        //--------------CIRCLE AROUND INTERSECTIONS CALCULATION----------------
        float firstParameter = (currentRoad.getEnd().x - this.x)*(currentRoad.getEnd().x - this.x);
        float firstParameterNext = (currentRoad.getEnd().x - this.x+oneRealStepX)*(currentRoad.getEnd().x - this.x+oneRealStepX);
        float drugiParameter = (currentRoad.getEnd().y - this.y)*(currentRoad.getEnd().y - this.y);
        float drugiParameterNext = (currentRoad.getEnd().y - this.y+oneRealStepY)*(currentRoad.getEnd().y - this.y+oneRealStepY);

        float sum = firstParameter + drugiParameter;
        float sumNext = firstParameterNext + drugiParameterNext;


        float d = 10*10 - sum;
        float dNext = 10*10 - sumNext;
        //---------------------------------------------------------------------

        //If radius squared of the intersection is >= 0 then we are at the intersection...
        if (d > 0 || this.next) {
            this.comingFromArc = currentRoad.getEndArc();
            this.nextRoad();
            this.sim.getIntersection(currentRoad.getEndId()).arrived(this.comingFromArc, this);
        } else {
            if (dNext > 0) this.next=true;
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
        if (!this.route.isEmpty()) {
            this.x = route.peek().getEnd().x;
            this.y = route.peek().getEnd().y;
            this.route.remove();
        }
        this.next=false;
        this.isRiding = false;
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

    @Override
    public String toString() {
        return "Vehicle{" +
                "x=" + x +
                ", y=" + y +
                ", routeSize=" + route.size() +
                ", isRiding=" + isRiding +
                ", isFinished=" + isFinished +
                ", comingFromArc=" + comingFromArc +
                ", id=" + this.hashCode() +
                '}';
    }
}
