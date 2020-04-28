package com.shuke.logistics.entity.input;

import java.util.HashSet;
import java.util.LinkedList;

public class Node {
    private int nodeId;
    private HashSet<Integer> relatedLink = new HashSet<>();
    private int totalWorkerNumber;
    private int availWorkerNumber;
    private LinkedList<Car> needWorCar;

    public HashSet<Integer> getRelatedLink() {
        return relatedLink;
    }

    public int getTotalWorkerNumber() {
        return totalWorkerNumber;
    }

    public int getAvailWorkerNumber() {
        return availWorkerNumber;
    }

    public LinkedList<Car> getNeedWorCar() {
        return needWorCar;
    }

    public Node(int nodeId, int totalWorkerNumber) {
        this.nodeId = nodeId;
        this.totalWorkerNumber = totalWorkerNumber;
        availWorkerNumber = totalWorkerNumber;
    }

    public void setAvailWorkerNumber(int availWorkerNumber) {
        this.availWorkerNumber = availWorkerNumber;
    }

    public void setRelatedLink(Integer integer) {
        this.relatedLink.add(integer);
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeId=" + nodeId +
                ", relatedLink=" + relatedLink +
                ", totalWorkerNumber=" + totalWorkerNumber +
                ", availWorkerNumber=" + availWorkerNumber +
                ", needWorCar=" + needWorCar +
                '}';
    }

    public int getNodeId() {
        return nodeId;
    }

}
