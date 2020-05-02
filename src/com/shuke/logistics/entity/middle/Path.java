package com.shuke.logistics.entity.middle;

import java.util.LinkedList;
import java.util.List;

public class Path {
    private int src;
    private int dst;
    private int weight = -1;
    private List<Integer> links = new LinkedList<>();
    private List<Integer> nodes = new LinkedList<>();

    public void setSrc(int src) {
        this.src = src;
    }

    public List<Integer> getNodes() {
        return nodes;
    }

    public void setNodes(List<Integer> nodes) {
        this.nodes = nodes;
    }

    public String nodesSeq() {
        StringBuilder stringBuilder = new StringBuilder();
        if (nodes.size() == 0) return "";
        for (int i : nodes) {
            stringBuilder.append("Z").append(i).append(",");
        }
        String result = stringBuilder.toString();
        return result.substring(0,result.lastIndexOf(","));
    }

    public void setLinks(List<Integer> links) {
        this.links = links;
    }

    public void setDst(int dst) {
        this.dst = dst;
    }

    public Path(int src) {
        this.src = src;
    }

    public Path(int weight, int src) {
        this.weight = weight;
        this.src = src;
    }

    public int getSrc() {
        return src;
    }

    public int getDst() {
        return dst;
    }

    public int getWeight() {
        return weight;
    }

    public List<Integer> getLinks() {
        return links;
    }

    public void setLinks(Integer linkId) {
        this.links.add(linkId);
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (links.size() == 0) return "";
        for (int i : links) {
            stringBuilder.append("R").append(i).append(",");
        }
        String result = stringBuilder.toString();
        return result.substring(0,result.lastIndexOf(","));
    }

}
