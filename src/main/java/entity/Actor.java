package entity;

import java.awt.*;
/*A simulation is full of actor, each Actor has a position (2D here), and can update/render them self
Vehicles, Intersections, Roads are all Actors. The Simulation will update all Actors, and pass an elapsedTime
parameter.
* */

public abstract class Actor {
    float x;
    float y;
    Simulation sim;

    public Actor(float x, float y, Simulation sim) {
        this.x = x;
        this.y = y;
        this.sim = sim;
    }

    public abstract void tick(double elapsedTime) throws InterruptedException;
    public abstract void render(Graphics graphics,double elapsedTime);
}
