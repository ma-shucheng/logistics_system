package com.shuke.logistics.entity.output;

import java.util.LinkedList;

public class Result {
    private int itemId;
    private double weight;
    private LinkedList<Integer> linkIds = new LinkedList<>();
    private LinkedList<Integer> carNums = new LinkedList<>();

    public int getItemId() {
        return itemId;
    }

    public Result(int itemId, LinkedList<Integer> linkIds, LinkedList<Integer> carNums) {
        this.itemId = itemId;
        this.linkIds = linkIds;
        this.carNums = carNums;
    }

    public LinkedList<Integer> getCarNums() {
        return carNums;
    }

    public LinkedList<Integer> getLinkIds() {
        return linkIds;
    }

    public Result(int itemId, double weight) {
        this.itemId = itemId;
        this.weight = weight;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("G").append(itemId).append("\n");
        if (linkIds.isEmpty()) {
            stringBuilder.append("null").append("\n");
            stringBuilder.append("null");
            return stringBuilder.toString();
        }
        for (int linkId : linkIds) {
            stringBuilder.append("R").append(linkId).append(",");
        }
        stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.lastIndexOf(",") + 1, "\n");
        for (int carId : carNums) {
            stringBuilder.append(carId).append(",");
        }
        stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.lastIndexOf(",") + 1, "");
        return stringBuilder.toString();
    }



    public void setLinkIds(int linkId) {
        this.linkIds.add(linkId);
    }

    public void setCarNums(int carNum) {
        this.carNums.add(carNum);
    }
}
