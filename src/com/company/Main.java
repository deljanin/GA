package com.company;

import java.util.Timer;

public class Main {

    public static void main(String[] args) {
	    Node A = new Node(0,0);
	    Node B = new Node(0,1);
	    Node C = new Node(0,2);
        Edge AB = new Edge(A,B, 50);
        Edge BC = new Edge(B,C, 50);
        Vehicle car = new Vehicle(0,0);
        Edge[] arr = {AB,BC};
        Timer t = new Timer();

        car.Move(arr);


    }


}
