package com.shuke.logistics.entity.output;

import java.util.LinkedList;

public class Result {
    private int itemId;
    private int weight;
    class Path{
        LinkedList<Integer> linkIds;
        LinkedList<Integer> carNums;
    }
}
