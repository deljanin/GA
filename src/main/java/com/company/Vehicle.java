package com.company;

public class Vehicle {
    int x;
    int y;

    public Vehicle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void Move(Edge e){
            this.x = e.end.x;
            this.y = e.end.y;
            System.out.println(this.x + ", " + this.y);
        }
}
