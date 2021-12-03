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

    @Override
    public void tick(double elapsedTime) {
        //System.out.println(elapsedTime);
        if (!isRiding) return;
        if (isFinished) return;

        //move my x, and y according to speed, and elapsedTime

        //kako točno računat premik avta v updated funkciji z elapsedTime?


        // dobimo en step tako da delimo razdaljo med zacetno x coordinato od koncne in delimo s totalTicks
        //float oneStepX = (float) ((sim.getXY(route.peek().getStartId()).getX()-sim.getXY(route.peek().getEndId()).getX())/totalTicks);
        //float oneStepY = (float) ((sim.getXY(route.peek().getStartId()).getY()-sim.getXY(route.peek().getEndId()).getY())/totalTicks);

        //dont be afraid of references
        Road currentRoad = route.peek();

        //System.out.println("Coordinates of the car: ( " + this.x + ", " + this.y + ")");
        //System.out.println("Coordinates of the starting intersection: ( " + currentRoad.getStart().x + ", " + currentRoad.getStart().y + ")");

        //--------------CIRCLE AROUND INTERSECTIONS CALCULATION----------------
        float prviParameter = (currentRoad.getEnd().x - this.x)*(currentRoad.getEnd().x - this.x);
        float drugiParameter = (currentRoad.getEnd().y - this.y)*(currentRoad.getEnd().y - this.y);

        float sestevek = prviParameter + drugiParameter;

        System.out.println("Calculation test: " + sestevek);

        float d = 10*10 - sestevek;
        //---------------------------------------------------------------------

        System.out.println("D je: " + d);
        //System.out.println("Road Length: " + currentRoad.getLength());

        //If radius squared of the intersection is >= 0 then we are at the intersection...
        if (d >= 0) {
            System.out.println("Done");
            isRiding = false;
            if (sim.getIntersection(currentRoad.getEnd().getId()).canIGo()) nextRoad();
            //route.remove();
        } else {
            System.out.println("Not done!!!");
            float totalTicks = currentRoad.getLength() / this.speed; // dobimo vse tick-e tako da delimo dolžino v metrih z hitrostjo v m/s
            System.out.println("Total ticks: " + totalTicks);
            float oneStepX = ((currentRoad.getStart().x - currentRoad.getEnd().x) / totalTicks)*(-1);
            float oneStepY = ((currentRoad.getStart().y - currentRoad.getEnd().y) / totalTicks)*(-1);
            System.out.println("One step of X: " + oneStepX);
            System.out.println("One step of Y: " + oneStepY);

            System.out.println("Acctual step of X: " + (float) (oneStepX * elapsedTime));
            System.out.println("Acctual step of Y: " + (float) (oneStepY * elapsedTime));

            // dobimo trenutne koordinate tako, da množimo steps z elapsed time z one Step
            this.x = this.x + ((float) (oneStepX * elapsedTime));
            this.y = this.y + ((float) (oneStepY * elapsedTime));
            System.out.println(this.x);
            System.out.println(this.y);

        }
    }
    @Override
    public void render(Graphics graphics, double elapsedTime) {
        //render at x,y. use elapsedTime where animations are needed (likely never)
        graphics.setColor(Color.BLUE);
        graphics.fillRect((int)this.x,(int)this.y,8,8);
    }

    public void nextRoad(){
        //System.out.println(route.peek());
        route.remove();
        isRiding = true;
        if (route.isEmpty()) isFinished=true;
    }
}
