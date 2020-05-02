package com.shuke.logistics.service;

import com.shuke.logistics.dao.ReSort;
import com.shuke.logistics.dao.Read;
import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;
import com.shuke.logistics.entity.middle.Path;
import com.shuke.logistics.entity.output.OutPut;
import com.shuke.logistics.entity.output.Result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimplePlanItem {
    public static OutPut outPut;
    private static Item[] oldItems;
    private static Node[] oldNodes;
    private static Link[] oldLinks;
    private static Item[] items;
    private static Node[] nodes;
    private static Link[] links;
    private static List<Item> list;
    private static boolean success = true;
    private static void init() {
        Read read = new Read();
        read.readFile();
        oldItems = Read.items;
        oldNodes = Read.nodes;
        oldLinks = Read.links;
        list = new LinkedList<>();
        for (Item item : oldItems) {
            list.add(item);
        }
        Collections.sort(list);
        ReSort.reSort();
        items = ReSort.reItems;
        nodes = ReSort.reNodes;
        links = ReSort.reLinks;
    }

    public static void main(String[] args) {
        init();
        List<Result> results = new LinkedList<>();
        outPut = new OutPut();
        int exceptionId = 0;
        try {
            for (Item item : list) {
                exceptionId = item.getItemId();
//                if (checkNoWorkerInStation(item, results)) continue;
                //将动态规划的路径规划保存到结果中
                results.add(dijkstraArrange(item));

            }
        } catch (RuntimeException e) {
            System.out.println(exceptionId);
            System.out.println(e);
        }
        nodesSeqInItem();
        outPut.setResults(results);
//        System.out.println(outPut.getTotalFailedNum()+","+outPut.getTotalFailedWeight());
//        for (Result result : outPut.getResults()) {
//            System.out.println(result);
//        }
        writeFile(outPut);
    }

    public static void arrangeAllItemPath() {
        init();
        List<Result> results = new LinkedList<>();
        outPut = new OutPut();
        int exceptionId = 0;
        try {
            for (Item item : list) {
                exceptionId = item.getItemId();
                //将动态规划的路径规划保存到结果中
                results.add(dijkstraArrange(item));
            }
        } catch (RuntimeException e) {
            System.out.println(exceptionId);
            System.out.println(e);
        }
        nodesSeqInItem();
    }


    /**
     * 将所有路径翻译为站点顺序存入货物Item中
     */
    private static void nodesSeqInItem() {
        Item[] items = ReSort.reItems;
        for (Item item : items) {
            List<Integer> nodes = new LinkedList<>();
            if (item != null && item.getPlanedPath() != null) {
                Path path = item.getPlanedPath();
                int curSrc = path.getSrc();
                for (Integer linkId : path.getLinks()) {
                    Link link = links[linkId];
                    int srcId = link.getSrcNodeId();
                    int dstId = link.getDstNodeId();
                    nodes.add(curSrc);
                    //curSrc重新赋值，变为第一个路径的终点
                    curSrc = curSrc != srcId ? srcId : dstId;
                }
                nodes.add(curSrc);
                path.setNodes(nodes);
                //item的path重新赋值
                item.setPlanedPath(path);
            }
        }
    }

    /**
     * 判定站点的拣货员是否够用，不够用直接判定规划失败
     * @param item
     * @param results
     * @return
     */
    public static boolean checkNoWorkerInStation(Item item,List<Result> results) {
        if (nodes[item.getSrcNode()].getAvailWorkerNumber() == 0 || nodes[item.getDstNode()].getAvailWorkerNumber() == 0) {
            outPut.setTotalFailedNum();
            outPut.setTotalFailedWeight(item.getWeight());
            results.add(new Result(item.getItemId(), item.getWeight()));
            return true;
        }
        return false;
    }

    /**
     * 结果写文件
     * @param outPut
     */
    private static void writeFile(OutPut outPut) {
        try {
            File file = new File("C:\\Users\\Shuke\\Desktop\\中兴大赛\\测试用例集\\case1\\result.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(outPut.getTotalFailedNum()+","+outPut.getTotalFailedWeight()+"\n");
            for (Result result : outPut.getResults()) {
                stringBuilder.append(result+"\n");
            }
            //使用true，即进行append file
            FileWriter fileWritter = new FileWriter(file,false);
            fileWritter.write(stringBuilder.toString());
            fileWritter.flush();
            fileWritter.close();
            System.out.println("finish");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Result dijkstraArrange(Item waitItem) {
        Result result;
        Path path = Dijkstra.dynamicDijkstra(waitItem.getIncNode(), waitItem.getSrcNode(), waitItem.getDstNode());
        //如果path为空动态规划失败
        if (path == null) {
            outPut.setTotalFailedNum();
            outPut.setTotalFailedWeight(waitItem.getWeight());
            result = new Result(waitItem.getItemId(),waitItem.getWeight());
            return result;
        }
        items[waitItem.getItemId()].setPlanedPath(path);
        //初始化可用的列车，确定轨道可用的车
        LinkedList<Integer> availCarsId = links[path.getLinks().get(0)].getAvailCarsId();
        for (int rloc : path.getLinks()) {
            availCarsId.retainAll(links[rloc].getAvailCarsId());
        }
        //如果可用的车为空集，则规划失败
        if (availCarsId.size() == 0) {
            outPut.setTotalFailedNum();
            outPut.setTotalFailedWeight(waitItem.getWeight());
            result = new Result(waitItem.getItemId(),waitItem.getWeight());
            return result;
        }
        //取第一个公共的车
        Integer usedCarId = availCarsId.get(0);
        result = arrangeCar(usedCarId, waitItem, path);
        int srcLinkId = result.getLinkIds().get(0);
        int dstLinkId = result.getLinkIds().get(result.getLinkIds().size()-1);
        arrangePeo(usedCarId, path.getSrc(),path.getDst(),srcLinkId,dstLinkId);
        return result;
    }

    /**
     * 安排起始终点的拣货员
     * @param usedCarId{这里可用序号必须是Integer类型，否则不能正确的将集合对应位置元素删除}
     */
    private static void arrangePeo(Integer usedCarId, int src,int dst,int srcLinkId,int dstLinkId) {
        //安排起始和终止端的拣货员
        nodes[src].setAvailWorkerNumber();
        nodes[dst].setAvailWorkerNumber();
        nodes[src].setNeedWorCar(usedCarId, srcLinkId);
        nodes[dst].setNeedWorCar(usedCarId, dstLinkId);
    }

    /**
     * 安排路径列车
     * @param usedCarId
     * @param waitItem
     * @param path
     * @return
     */
    private static Result arrangeCar(Integer usedCarId, Item waitItem, Path path) {
        Result result = new Result(waitItem.getItemId(), waitItem.getWeight());
        for (int linkId : path.getLinks()) {
            Link link = links[linkId];
            link.deleteAvailCars(usedCarId);
            result.setLinkIds(linkId);
            result.setCarNums(usedCarId);
        }
        return result;
    }
}
