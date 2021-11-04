package com.company;

public class Edge {
    Node start;
    Node end;
    int speed;

    public Edge(Node start, Node end, int speed) {
        this.start = start;
        this.end = end;
        this.speed = speed;
    }
}
