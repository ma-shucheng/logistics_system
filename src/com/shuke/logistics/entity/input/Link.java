package com.shuke.logistics.entity.input;

import java.util.LinkedList;

public class Link {
    private int linkId;
    private String srcNode;
    private String dstNode;
    private Car[] availCars;
    private int totalCarNum;
    private LinkedList<Integer> availCarsId = new LinkedList<>();
    private LinkedList<Integer> notMaxCarsId = new LinkedList<>();



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
        this.totalCarNum = carNum;
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

    public int getTotalCarNum() {
        return totalCarNum;
    }

    @Override
    public String toString() {
        return "Link{" +
                "linkId=" + linkId +
                ", availCarsId=" + availCarsId.size() +
                ", notMaxCarsId=" + notMaxCarsId.size() +
                '}';
    }

    public void setNotMaxCarsId(Integer useCarId, Double avWeight) {
        this.notMaxCarsId.add(useCarId);
        //刷新可用重量
        this.availCars[useCarId].setAvailWeight(avWeight);
    }

    public Car secletCarById(Integer useCarId) {
        return this.availCars[useCarId];
    }

    public boolean carInNotMaxCar(Integer useCarId) {
        return this.notMaxCarsId.contains(useCarId);
    }

}
