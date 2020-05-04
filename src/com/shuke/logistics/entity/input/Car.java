package com.shuke.logistics.entity.input;


public class Car {
    private int linkId;
    private int carNum;
    private final double maxWeight = 100;
    private double availWeight;

    public int getLinkId() {
        return linkId;
    }

    public int getCarNum() {
        return carNum;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public double getAvailWeight() {
        return availWeight;
    }

    public Car(int linkId, int carNum) {
        this.linkId = linkId;
        this.carNum = carNum;
        availWeight = this.maxWeight;
    }

    public void setAvailWeight(double useWeight) {
        //保留3位小数
        this.availWeight = Double.parseDouble(String.format("%.4f", this.availWeight - useWeight));
    }
}
