package com.shuke.logistics.entity.input;

import com.shuke.logistics.entity.middle.Path;

import java.util.ArrayList;

public class Item implements Comparable<Item>{
    private int itemId;
    private int srcNode;
    private int dstNode;
    private double weight;
    private int weightInt;
    private Path planedPath;


    public String outSrcAndDst() {
        return srcNode+","+dstNode;
    }

    public void setPlanedPath(Path planedPath) {
        this.planedPath = planedPath;
    }

    public Path getPlanedPath() {
        return planedPath;
    }

    public int getSrcNode() {
        return srcNode;
    }

    public int getDstNode() {
        return dstNode;
    }

    public double getWeight() {
        return weight;
    }

    public ArrayList<Integer> getIncNode() {
        return incNode;
    }

    /**
     * 必经站点
     */
    private ArrayList<Integer> incNode;

    public Item(int itemId, int srcNode, int dstNode, double weight, ArrayList<Integer> incNode) {
        this.itemId = itemId;
        this.srcNode = srcNode;
        this.dstNode = dstNode;
        this.weight = weight;
        this.incNode = incNode;
        this.weightInt = (int) weight;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", srcNode='" + srcNode + '\'' +
                ", dstNode='" + dstNode + '\'' +
                ", weight=" + weight +
                ", incNode=" + incNode +
                '}';
    }

    public int getItemId() {
        return itemId;
    }

    /**
     * 这里容易出错，相等必须返回0
     * @param o
     * @return
     */
    @Override
    public int compareTo(Item o) {
        if (weightInt<o.weightInt) {
            return 1;
        } else if (weightInt == o.weightInt) {
            return 0;
        } else {
            return -1;
        }
    }
}
