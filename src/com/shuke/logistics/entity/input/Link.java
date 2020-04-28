package com.shuke.logistics.entity.input;

import java.util.ArrayList;
import java.util.LinkedList;

public class Link {
    private int linkId;
    private String srcNode;
    private String dstNode;
    private LinkedList<Car> availCars = new LinkedList<>();
    private int cost = 0;

    public String getSrcNode() {
        return srcNode;
    }

    public String getDstNode() {
        return dstNode;
    }

    public LinkedList<Car> getAvailCars() {
        return availCars;
    }

    public int getCost() {
        return cost;
    }

    public Link(int linkId, String srcNode, String dstNode, int carNum) {
        this.linkId = linkId;
        this.srcNode = srcNode;
        this.dstNode = dstNode;
        for (int i = 0; i < carNum; i++) {
            this.availCars.add(new Car(linkId, i + 1));
        }
    }

    public int getLinkId() {
        return linkId;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
