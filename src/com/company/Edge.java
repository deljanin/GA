package com.company;

public class Edge {
    Node start;
    Node end;
    double speed;
    int length;

    public Edge(Node start, Node end, double speed) {
        this.start = start;
        this.end = end;
        this.speed = speed;
        int x1 = this.end.x - this.start.x;
        int y1 = this.end.y - this.start.y;
        this.length = (int) Math.round(Math.sqrt((x1 * x1) + (y1 * y1)));
    }
}
