package com.company;

public class Vehicle {
    int x;
    int y;

    public Vehicle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void Move(Edge[] route){
        for (int i = 0; i < route.length; i++) {
            this.x = route[i].end.x;
            this.y = route[i].end.y;
            System.out.println(this.x + ", " + this.y);
        }

    }
}
