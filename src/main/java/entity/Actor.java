package entity;

import java.awt.*;
/*A simulation is full of actor, each Actor has a position (2D here), and can update/render them self
Vehicles, Intersections, Roads are all Actors. The Simulation will update all Actors, and pass an elapsedTime
parameter.
* */

public abstract class Actor {
    float x;
    float y;

    public abstract void tick(double elapsedTime);
    public abstract void render(Graphics graphics,double elapsedTime);
}
