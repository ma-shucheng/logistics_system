package com.shuke.logistics.entity.output;

import java.util.LinkedList;

public class OutPut {
    private int totalFailedNum;
    private int totalFailedWeight;
    private LinkedList<Result> results;

    public OutPut(int totalFailedNum, int totalFailedWeight, LinkedList<Result> results) {
        this.totalFailedNum = totalFailedNum;
        this.totalFailedWeight = totalFailedWeight;
        this.results = results;
    }
}
