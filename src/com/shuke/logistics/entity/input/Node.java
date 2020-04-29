package com.shuke.logistics.entity.input;

import com.shuke.logistics.entity.middle.Path;

import java.util.HashSet;
import java.util.LinkedList;

public class Node {
    private int nodeId;
    private HashSet<Integer> relatedLink = new HashSet<>();
    private int totalWorkerNumber;
    private int availWorkerNumber;
    private class NeedWorCarId {
        private int needWorkCarId;
        private int needWorkLinkId;

        public NeedWorCarId(int needWorkCarId, int needWorkLinkId) {
            this.needWorkCarId = needWorkCarId;
            this.needWorkLinkId = needWorkLinkId;
        }
    }
    private LinkedList<NeedWorCarId> needWorCarId = new LinkedList<>();

    public void setNeedWorCar(Integer carId,Integer linkId) {
        this.needWorCarId.add(new NeedWorCarId(carId,linkId));
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
