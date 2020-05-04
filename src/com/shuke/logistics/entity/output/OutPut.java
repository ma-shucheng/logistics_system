package com.shuke.logistics.entity.output;

import java.util.LinkedList;
import java.util.List;

public class OutPut {
    private int totalFailedNum = 0;
    private double totalFailedWeight = 0;
    private List<Result> results = new LinkedList<>();

    public int getTotalFailedNum() {
        return totalFailedNum;
    }

    public double getTotalFailedWeight() {
        return totalFailedWeight;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setTotalFailedNum() {
        this.totalFailedNum++;
    }

    /**
     * 保留3位小数的总失败重量
     * @param totalFailedWeight
     */
    public void setTotalFailedWeight(double totalFailedWeight) {
        this.totalFailedWeight +=  Double.parseDouble(String.format("%.3f", totalFailedWeight));
    }

    public void setResults(Result result) {
        this.results.add(result);
    }
}
