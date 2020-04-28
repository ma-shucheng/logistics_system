package com.shuke.logistics.entity.input;

import java.util.ArrayList;

public class Item implements Comparable<Item>{
    private int itemId;
    private String srcNode;
    private String dstNode;
    private double weight;

    public String getSrcNode() {
        return srcNode;
    }

    public String getDstNode() {
        return dstNode;
    }

    public double getWeight() {
        return weight;
    }

    public ArrayList<String> getIncNode() {
        return incNode;
    }

    /**
     * 必经站点
     */
    private ArrayList<String> incNode;

    public Item(int itemId, String srcNode, String dstNode, double weight, ArrayList<String> incNode) {
        this.itemId = itemId;
        this.srcNode = srcNode;
        this.dstNode = dstNode;
        this.weight = weight;
        this.incNode = incNode;
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

    @Override
    public int compareTo(Item o) {
        if (weight<o.weight) return 1;
        else return -1;
    }
}
