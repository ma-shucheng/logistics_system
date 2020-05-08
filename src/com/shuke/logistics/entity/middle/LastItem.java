package com.shuke.logistics.entity.middle;

public class LastItem implements Comparable<LastItem>{
    private String passNodes;
    private int sumWeights;

    public String getPassNodes() {
        return passNodes;
    }

    public void deleteSumWeight(Double useWeight) {
        this.sumWeights -= useWeight;
    }

    public int getSumWeights() {
        return sumWeights;
    }

    public void setPassNodes(String passNodes) {
        this.passNodes = passNodes;
    }

    public void setSumWeights(int sumWeights) {
        this.sumWeights = sumWeights;
    }

    @Override
    public String toString() {
        return "RemainItem{" +
                "passNodes='" + passNodes + '\'' +
                ", sumWeights=" + sumWeights +
                '}';
    }

    public LastItem(String passNodes, Double sumWeights) {
        this.passNodes = passNodes;
        this.sumWeights = Integer.parseInt(String.format("%.0f", sumWeights));
    }

    @Override
    public int compareTo(LastItem o) {
        if (sumWeights<o.sumWeights) {
            return 1;
        } else if (sumWeights == o.sumWeights) {
            return 0;
        } else {
            return -1;
        }
    }
}
