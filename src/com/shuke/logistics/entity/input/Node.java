package com.shuke.logistics.entity.input;

import com.shuke.logistics.entity.middle.Path;

import java.util.HashSet;
import java.util.Set;

public class Node {
    private int nodeId;
    private HashSet<Integer> relatedLink = new HashSet<>();

    @Override
    public String toString() {
        return "Node{" +
                "nodeId=" + nodeId +
                ", totalWorkerNumber=" + totalWorkerNumber +
                ", availWorkerNumber=" + availWorkerNumber +
                ", needWorCarIdStr=" + needWorCarIdStr.size() +
                '}';
    }

    private int totalWorkerNumber;
    private int availWorkerNumber;


    private Set<String> needWorCarIdStr = new HashSet<>();

    public void setNeedWorCar(Integer carId,Integer linkId) {
        this.needWorCarIdStr.add(carId + "," + linkId);
    }

    public boolean exitNeedWorCar(Integer carId,Integer linkId) {
        String str = carId + "," + linkId;
        return needWorCarIdStr.contains(str);
    }

    public void deleteNeedWorCar(Integer carId,Integer linkId) {
        String str = carId + "," + linkId;
        needWorCarIdStr.remove(str);
    }

    private Path[] minPath;

    public Path[] getMinPath() {
        return minPath;
    }

    public void setMinPath(Path[] minPath) {
        this.minPath = minPath;
    }

    public HashSet<Integer> getRelatedLink() {
        return relatedLink;
    }

    public int getTotalWorkerNumber() {
        return totalWorkerNumber;
    }

    public int getAvailWorkerNumber() {
        return availWorkerNumber;
    }


    public Node(int nodeId, int totalWorkerNumber) {
        this.nodeId = nodeId;
        this.totalWorkerNumber = totalWorkerNumber;
        availWorkerNumber = totalWorkerNumber;
    }

    public void setAvailWorkerNumber() {
        this.availWorkerNumber--;
    }

    public void setRelatedLink(Integer integer) {
        this.relatedLink.add(integer);
    }


    public int getNodeId() {
        return nodeId;
    }

}
