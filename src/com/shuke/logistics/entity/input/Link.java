package com.shuke.logistics.entity.input;

import java.util.LinkedList;

public class Link {
    private int linkId;
    private String srcNode;
    private String dstNode;
    private Car[] availCars;
    private LinkedList<Integer> availCarsId = new LinkedList<>();
    private int cost = 0;

    public Integer getSrcNodeId() {
        return Integer.parseInt(srcNode.substring(1));
    }

    public Integer getDstNodeId() {
        return Integer.parseInt(dstNode.substring(1));
    }

    public void deleteAvailCars(Integer carId) {
        availCarsId.remove(carId);
    }

    public String getSrcNode() {
        return srcNode;
    }

    public String getDstNode() {
        return dstNode;
    }

    public LinkedList<Integer> getAvailCarsId() {
        return availCarsId;
    }

    public int getCost() {
        return cost;
    }

    public Link(int linkId, String srcNode, String dstNode, int carNum) {
        this.linkId = linkId;
        this.srcNode = srcNode;
        this.dstNode = dstNode;
        this.availCars = new Car[carNum+1];
        for (int i = 1; i <= carNum; i++) {
            availCars[i] = new Car(linkId, i);
            this.availCarsId.add(i);
        }
    }

    public int getLinkId() {
        return linkId;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
