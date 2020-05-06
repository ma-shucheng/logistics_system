package com.shuke.logistics.entity.middle;

public class RemainItem implements Comparable<RemainItem> {
    private String passNodes;
    private Double sumWeights;

    @Override
    public String toString() {
        return "RemainItem{" +
                "passNodes='" + passNodes + '\'' +
                ", sumWeights=" + sumWeights +
                '}';
    }

    public void setPassNodes(String passNodes) {
        this.passNodes = passNodes;
    }

    public void setSumWeights(Double sumWeights) {
        this.sumWeights = sumWeights;
    }

    public void deleteSumWeight(Double useWeight) {
        this.sumWeights -= useWeight;
    }

    public String getPassNodes() {
        return passNodes;
    }

    public Double getSumWeights() {
        return sumWeights;
    }

    public RemainItem(String passNodes, Double sumWeights) {
        this.passNodes = passNodes;
        this.sumWeights = sumWeights;
    }

    @Override
    public int compareTo(RemainItem o) {
        if (sumWeights<o.sumWeights) {
            return 1;
        } else if (sumWeights == o.sumWeights) {
            return 0;
        } else {
            return -1;
        }
    }
}
