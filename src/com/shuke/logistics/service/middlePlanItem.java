package com.shuke.logistics.service;

import com.shuke.logistics.dao.ReSort;
import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;
import com.shuke.logistics.entity.middle.SortItem;
import com.shuke.logistics.entity.output.OutPut;
import com.shuke.logistics.entity.output.Result;

import java.util.*;

public class middlePlanItem {

    public static OutPut outPut;
    private static Item[] items;
    private static Link[] links;
    private static Node[] nodes;
    private static Map<String, SortItem> sortItemMap;
    private static List<Item> sortItemByWe;
    public static void main(String[] args) {
        init();
        for (Item item : sortItemByWe) {
            //没有安排到路径的
            if (item.getPlanedPath() == null) {
                failedItemDeal(new Result(item.getItemId(), item.getWeight()), item);
            }
            //重量为100的货物安排
            else if (item.getWeight() == 100) {
                arrange100Item(item);
            }
            //重量为50到100的货物
            else if (item.getWeight() > 50) {
                arrrngeAb50Item(item);
            } else {
                failedItemDeal(new Result(item.getItemId(), item.getWeight()), item);
            }
        }
        SimplePlanItem.writeFile(outPut);
    }

    /**
     * 安排大于50小于100的货物，与100的货物类似不过先放到可用列车中，保证可以拼车
     * @param waitItem
     */
    private static void arrrngeAb50Item(Item waitItem) {
        Result result = new Result(waitItem.getItemId(),waitItem.getWeight());
        //初始化必经站点
        List<Integer> passNodes = waitItem.getPlanedPath().getNodes();
        //先判断起点是否有空余的工作人员，如果没有直接失败
        if (nodeWorkNumNotEnough(waitItem, waitItem.getSrcNode(), 1)) return;
        //经过的第一个轨道ID
        int initLinkId = waitItem.getPlanedPath().getLinks().get(0);
        //再判定起始路径是否还有可用车辆，如果没有直接失败
        if (linkCarNotEnough(waitItem, links[initLinkId])) return;
        //初始化起点出发路径轨道的可用车辆，提取可用车辆ID，默认安排可用的第一辆车
        Integer useCarId = links[initLinkId].getAvailCarsId().get(0);
        //安排起点工作人员
        arrangeNodeWorker(waitItem.getSrcNode(), initLinkId, useCarId);
        //开始遍历规划路径
        int passNodeNum = 0;
        //存贮上一次的轨道ID
        int befLinkId = 0;
        for (Integer linkId : waitItem.getPlanedPath().getLinks()) {
            Link link = links[linkId];
            //实时记录经过的第几个站点
            int curNodeId = passNodes.get(passNodeNum);
            passNodeNum++;
            //如果轨道有相同编号的可用车辆，安排车辆，将其存入结果，并将这个车辆信息缓存在轨道中，注明现有载重
            if (link.getAvailCarsId().contains(useCarId)) {
                arrangeCar(result, linkId, useCarId);
                //将还可用的车辆信息缓存
                cacheNotMaxCar(linkId, useCarId, waitItem);
            }
            //如果轨道含有的可用轨道，不含目前的轨道，则要进行换乘
            else {
                //先判断站点是否还有可用的两个拣货员，如果没有直接失败
                if (nodeWorkNumNotEnough(waitItem,curNodeId,2)) return;
                //判定这一轨道是否还有可用车辆，如果没有了，也失败处理
                if (linkCarNotEnough(waitItem, link)) return;
                //安排站点拣货员和初始化新车
                arrangeNodeWorker(curNodeId, befLinkId, useCarId);
                //新的可用车ID
                useCarId = link.getAvailCarsId().get(0);
                arrangeNodeWorker(curNodeId, linkId, useCarId);
                arrangeCar(result, linkId, useCarId);
                //将还可用的车辆信息缓存
                cacheNotMaxCar(linkId, useCarId, waitItem);
            }
            befLinkId = linkId;
        }
        //判断终点是否有可用拣货员，如果没有判定失败
        if (nodeWorkNumNotEnough(waitItem, waitItem.getDstNode(), 1)) return;
        //安排终点拣货员
        arrangeNodeWorker(waitItem.getDstNode(), befLinkId, useCarId);
        //将结果存入输出中
        outPut.setResults(result);
    }

    /**
     * 将列车进行缓存，并刷新可用重量
     * @param linkId
     * @param useCarId
     * @param item
     */
    private static void cacheNotMaxCar(int linkId, int useCarId, Item item) {
        links[linkId].setNotMaxCarsId(useCarId, item);
    }

    private static void arrange100Item(Item waitItem) {
        Result result = new Result(waitItem.getItemId(),waitItem.getWeight());
        //初始化必经站点
        List<Integer> passNodes = waitItem.getPlanedPath().getNodes();
        //先判断起点是否有空余的工作人员，如果没有直接失败
        if (nodeWorkNumNotEnough(waitItem, waitItem.getSrcNode(), 1)) return;
        //经过的第一个轨道ID
        int initLinkId = waitItem.getPlanedPath().getLinks().get(0);
        //再判定起始路径是否还有可用车辆，如果没有直接失败
        if (linkCarNotEnough(waitItem, links[initLinkId])) return;
        //初始化起点出发路径轨道的可用车辆，提取可用车辆ID，默认安排可用的第一辆车
        Integer useCarId = links[initLinkId].getAvailCarsId().get(0);
        //安排起点工作人员
        arrangeNodeWorker(waitItem.getSrcNode(), initLinkId, useCarId);
        //开始遍历规划路径
        int passNodeNum = 0;
        //存贮上一次的轨道ID
        int befLinkId = 0;
        for (Integer linkId : waitItem.getPlanedPath().getLinks()) {
            Link link = links[linkId];
            //实时记录经过的第几个站点
            int curNodeId = passNodes.get(passNodeNum);
            passNodeNum++;
            //如果轨道有相同编号的可用车辆，安排车辆，将其存入结果
            if (link.getAvailCarsId().contains(useCarId)) {
                arrangeCar(result, linkId, useCarId);
            }
            //如果轨道含有的可用轨道，不含目前的轨道，则要进行换乘
            else {
                //先判断站点是否还有可用的两个拣货员，如果没有直接失败
                if (nodeWorkNumNotEnough(waitItem,curNodeId,2)) return;
                //判定这一轨道是否还有可用车辆，如果没有了，也失败处理
                if (linkCarNotEnough(waitItem, link)) return;
                //安排站点拣货员和初始化新车
                arrangeNodeWorker(curNodeId, befLinkId, useCarId);
                //新的可用车ID
                useCarId = link.getAvailCarsId().get(0);
                arrangeNodeWorker(curNodeId, linkId, useCarId);
                arrangeCar(result, linkId, useCarId);
            }
            befLinkId = linkId;
        }
        //判断终点是否有可用拣货员，如果没有判定失败
        if (nodeWorkNumNotEnough(waitItem, waitItem.getDstNode(), 1)) return;
        //安排终点拣货员
        arrangeNodeWorker(waitItem.getDstNode(), befLinkId, useCarId);
        //将结果存入输出中
        outPut.setResults(result);
    }

    /**
     * 安排可用车
     * @param result
     * @param linkId
     * @param useCarId
     */
    private static void arrangeCar(Result result, int linkId, int useCarId) {
        //删除已使用的列车
        links[linkId].deleteAvailCars(useCarId);
        //存入结果中
        storageResult(result, linkId, useCarId);
    }


    /**
     * 路径列车不足
     * @return
     */
    private static boolean linkCarNotEnough(Item item,Link link) {
        if (link.getAvailCarsId().size() == 0) {
            failedItemDeal(new Result(item.getItemId(), item.getWeight()), item);
            return true;
        }
        return false;
    }

    /**
     * 节点拣货员不充足
     * @param nodeId
     * @param needWorNum
     * @return
     */
    private static boolean nodeWorkNumNotEnough(Item item, int nodeId, int needWorNum) {
        if (nodes[nodeId].getAvailWorkerNumber() < needWorNum) {
            failedItemDeal(new Result(item.getItemId(), item.getWeight()), item);
            return true;
        }
        return false;
    }

    /**
     * 将结果存贮进结果中
     * @param result
     * @param linkId
     * @param useCarId
     */
    private static void storageResult(Result result, int linkId, int useCarId) {
        result.setLinkIds(linkId);
        result.setCarNums(useCarId);
    }

    /**
     * 安排站点工作人员
     * @param nodeId
     * @param linkId
     * @param useCarId
     */
    private static void arrangeNodeWorker(int nodeId,int linkId,int useCarId) {
        Node node = nodes[nodeId];
        //站点有效工作人员减少
        node.setAvailWorkerNumber();
        //将减少的工作人员安排至对应工位
        node.setNeedWorCar(useCarId,linkId);
    }

    /**
     * 将失败货物存入outPut中
     * @param result
     * @param waitItem
     */
    private static void failedItemDeal(Result result,Item waitItem) {
        outPut.setTotalFailedNum();
        outPut.setTotalFailedWeight(waitItem.getWeight());
        outPut.setResults(result);
    }


    /**
     * 货物的初始化分类
     */
    public static void init() {
        outPut = new OutPut();
        Map<String, List<Integer>> itemMap;
        SimplePlanItem.arrangeAllItemPath();
        items = ReSort.reItems;
        itemMap = new HashMap<>();
        for (Item item : items) {
            if (item.getPlanedPath() != null) {
                String path = item.getPlanedPath().nodesSeq();
                String pathReverse = pathReverse(item.getPlanedPath().nodesSeq());
                //正序反序的路径都不存在再添加
                if (itemMap.get(path) == null && itemMap.get(pathReverse) == null) {
                    itemMap.put(path, new LinkedList<>());
                }
            }
        }

        for (Item item : items) {
            if (item.getPlanedPath() != null) {
                String path = item.getPlanedPath().nodesSeq();
                String pathReverse = pathReverse(item.getPlanedPath().nodesSeq());
                //正序不为空添加正序
                if (itemMap.get(path) != null  ) {
                    itemMap.get(path).add(item.getItemId());
                }
                //反序不为空添加反序
                else if (itemMap.get(pathReverse) != null) {
                    itemMap.get(pathReverse).add(item.getItemId());
                }
            }
        }

        Set<Double> weights = new HashSet<>();
        for (Item item : items) {
            if (item.getPlanedPath() != null) {
                weights.add(item.getWeight());
            }
        }

        sortItemMap = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : itemMap.entrySet()) {
            List<Integer> itemIds = entry.getValue();
            SortItem sortItem = new SortItem(weights);
            for (Integer itemId : itemIds) {
                sortItem.setSortItem(items[itemId].getWeight(), itemId);
            }
            sortItem.removeZeroEn();
            sortItemMap.put(entry.getKey(), sortItem);
        }

        sortItemByWe = new LinkedList<>();
        for (Item item : items) {
            sortItemByWe.add(item);
        }
        //货物按重量降序
        Collections.sort(sortItemByWe);

        //节点初始化
        nodes = new Node[ReSort.reNodes.length];
        for (Node node : ReSort.reNodes) {
            nodes[node.getNodeId()] = new Node(node.getNodeId(), node.getTotalWorkerNumber());
        }
        //链路初始化
        links = new Link[ReSort.reLinks.length];
        for (Link link : ReSort.reLinks) {
            links[link.getLinkId()] = new Link(link.getLinkId(), link.getSrcNode(), link.getDstNode(), link.getTotalCarNum());
        }
    }

    private static String pathReverse(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] nodes = string.split(",");
        for (int i = nodes.length-1; i >= 0; i--) {
            stringBuilder.append(nodes[i]).append(",");
        }
        String result = stringBuilder.toString();
        return result.substring(0,result.lastIndexOf(","));
    }
}
