package com.shuke.logistics.service;

import com.shuke.logistics.dao.ReSort;
import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;
import com.shuke.logistics.entity.middle.CompareItem;
import com.shuke.logistics.entity.middle.SortItem;
import com.shuke.logistics.entity.output.OutPut;
import com.shuke.logistics.entity.output.Result;

import java.util.*;

public class MiddlePlanItem {

    public static OutPut outPut;
    public static Item[] items;
    public static Link[] links;
    public static Node[] nodes;
    public static Map<String, SortItem> sortItemMap;
    public static List<Item> sortItemByWe;

    public static void arrange100AndAb50() {
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
                arrangeAb50Item(item);
            }
        }
    }


    public static void failedRemainSortItemMap() {
        for (String string : sortItemMap.keySet()) {
            Set<Integer> itemIds = new HashSet<>();
            sortItemMap.get(string).getAllItemID(itemIds);
            for (Integer itemId : itemIds) {
                Item item1 = items[itemId];
                failedItemDeal(new Result(item1.getItemId(), item1.getWeight()), item1);
            }
        }
    }


    /**
     * 查看sortItemMap中还剩多少元素
     * @return
     */
    public static int remainItemNum() {
        int num = 0;
        for (String path : sortItemMap.keySet()) {
            num += sortItemMap.get(path).getAllItemIdsNum();
        }
        return num;
    }


    /**
     * 安排大于50小于100的货物，与100的货物
     * 尽量将同路径的货物进行拼车
     * @param waitItem
     */
    private static void arrangeAb50Item(Item waitItem) {
        //新建拼车货物集合，如果不为空就进行如下操作
        Set<Integer> carpItemIds = new HashSet<>();
        Set<Integer> allCarpItemIds = new HashSet<>();
        Double avWeight = carpoolWithAb50(carpItemIds, allCarpItemIds, waitItem);
        //有效重量刚好等于100
        if (avWeight == 100) {
            arrange100ItemCarp(waitItem, carpItemIds, allCarpItemIds);
            return;
        }
        Result result = new Result(waitItem.getItemId(),waitItem.getWeight());
        //初始化必经站点
        List<Integer> passNodes = waitItem.getPlanedPath().getNodes();
        //先判断起点是否有空余的工作人员，如果没有直接失败
        if (nodeWorkNumNotEnoughCarp(waitItem, waitItem.getSrcNode(), 1, allCarpItemIds)) return;
        //经过的第一个轨道ID
        int initLinkId = waitItem.getPlanedPath().getLinks().get(0);
        //再判定起始路径是否还有可用车辆，如果没有直接失败
        if (linkCarNotEnoughCarp(waitItem, links[initLinkId], allCarpItemIds)) return;
        //初始化起点出发路径轨道的可用车辆，提取可用车辆ID，默认安排可用的第一辆车
        Integer useCarId = links[initLinkId].getAvailCarsId().get(0);
        //有效重量刚好小于100
        //开始遍历规划路径
        int passNodeNum = 0;
        //存贮上一次的轨道ID
        for (Integer linkId : waitItem.getPlanedPath().getLinks()) {
            Link link = links[linkId];
            //实时记录经过的第几个站点
            int curNodeId = passNodes.get(passNodeNum);
            passNodeNum++;
            //如果轨道有相同编号的可用车辆，安排车辆，将其存入结果，并将这个车辆信息缓存在轨道中，注明现有载重
            if (link.getAvailCarsId().contains(useCarId)) {
                //存入结果中
                storageResult(result, linkId, useCarId);
                //将还可用的车辆信息缓存
            }
            //如果轨道含有的可用轨道，不含目前的轨道，则要进行换乘
            else {
                //先判断站点是否还有可用的两个拣货员，如果没有直接失败
                if (nodeWorkNumNotEnoughCarp(waitItem, curNodeId,2, allCarpItemIds)) return;
                //判定这一轨道是否还有可用车辆，如果没有了，也失败处理
                if (linkCarNotEnoughCarp(waitItem, link, allCarpItemIds)) return;
                //新的可用车ID
                useCarId = link.getAvailCarsId().get(0);
                //存入结果中
                storageResult(result, linkId, useCarId);
            }
        }
        //判断终点是否有可用拣货员，如果没有判定失败
        if (nodeWorkNumNotEnoughCarp(waitItem, waitItem.getDstNode(), 1, allCarpItemIds)) return;
        //存低于100大于50的货物
        arrangeBl100CarAndWor(result, passNodes, avWeight);
        storageResultCarp(result, carpItemIds);
    }

    /**
     * 拼车结果输出
     * @param result
     * @param carpItemIds
     */
    private static void storageResultCarp(Result result, Set<Integer> carpItemIds) {
        //将结果存入输出中
        outPut.setResults(result);
        //将拼车货物也放入输出中
        if (carpItemIds.size() != 0) {
            for (Integer itemId : carpItemIds) {
                outPut.setResults(new Result(itemId, result.getLinkIds(), result.getCarNums()));
            }
        }
    }


    /**
     * 拼大于50货物的车
     * @param item
     * @return
     */
    private static Double carpoolWithAb50(Set<Integer> carpoolItemIds,Set<Integer> allCarpoolItemIds, Item item) {
        Double avWeight = item.getWeight();
        String pathStr = item.getPlanedPath().nodesSeq();
        String pathReStr = pathReverse(pathStr);
        SortItem sortItem = sortItemMap.get(pathStr) != null ? sortItemMap.get(pathStr) : sortItemMap.get(pathReStr);
        if (sortItem.getWeights().size() == 0){ return avWeight;}
        //先将对应位置下的所有货物ID存放
        sortItem.getAllItemID(allCarpoolItemIds);
        for (Double weight : sortItem.getWeights()) {
            while (!sortItem.getItemZeroByWeight(weight)) {
                //重量与有效重量相加大于100下一次循环
                if (weight + avWeight > 100) {
                    break;
                }
                //没有跳过有效重量增加
                avWeight += weight;
                //将其放入搭车货物中
                carpoolItemIds.add(sortItem.getItemId(weight));
                //删除列表中的货物
                sortItem.removeItemId(weight, sortItem.getItemId(weight));
            }
        }
        //清空重量对应货物数量已经为0的键值
        sortItem.removeZeroEn();
        return avWeight;
    }

    /**
     * 将列车进行缓存，并刷新可用重量
     * @param linkId
     * @param useCarId
     */
    public static void cacheNotMaxCar(int linkId, int useCarId, Double avWeight) {
        links[linkId].setNotMaxCarsId(useCarId, avWeight);
    }

    /**
     * 安排重量为100的货物
     * @param waitItem
     */
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
        //开始遍历规划路径
        int passNodeNum = 0;
        for (Integer linkId : waitItem.getPlanedPath().getLinks()) {
            Link link = links[linkId];
            //实时记录经过的第几个站点
            int curNodeId = passNodes.get(passNodeNum);
            passNodeNum++;
            //如果轨道有相同编号的可用车辆，安排车辆，将其存入结果
            if (link.getAvailCarsId().contains(useCarId)) {
                //存入结果中
                storageResult(result, linkId, useCarId);
            }
            //如果轨道含有的可用轨道，不含目前的轨道，则要进行换乘
            else {
                //先判断站点是否还有可用的两个拣货员，如果没有直接失败
                if (nodeWorkNumNotEnough(waitItem,curNodeId,2)) return;
                //判定这一轨道是否还有可用车辆，如果没有了，也失败处理
                if (linkCarNotEnough(waitItem, link)) return;
                //新的可用车ID
                useCarId = link.getAvailCarsId().get(0);
                //存入结果中
                storageResult(result, linkId, useCarId);
            }
        }
        //判断终点是否有可用拣货员，如果没有判定失败
        if (nodeWorkNumNotEnough(waitItem, waitItem.getDstNode(), 1)) return;
        //安排路径中的工作人员和列车
        arrange100CarAndWor(result, passNodes);
        //将结果存入输出中
        outPut.setResults(result);
    }

    /**
     * 安排满100货物路径中的拣货员和列车
     * @param result
     */
    public static void arrange100CarAndWor(Result result, List<Integer> passNodes) {
        //开始遍历规划路径
        int passNodeNum = 0;
        int carNum = 0;
        //安排起点拣货员
        arrange100NodeWorker(passNodes.get(0));
        //记录起始列车
        Integer curCarId = result.getCarNums().get(0);
        for (Integer linkId : result.getLinkIds()) {
            Integer useCarId = result.getCarNums().get(carNum);
            //实时记录经过的第几个站点
            int curNodeId = passNodes.get(passNodeNum);
            //如果下一个轨道列车不同说明有换乘，安排换乘拣货员
            if (!curCarId.equals(useCarId)) {
                arrange100NodeWorker(curNodeId);
                arrange100NodeWorker(curNodeId);
            }
            curCarId = useCarId;
            arrangeCar(linkId, useCarId);
            passNodeNum++;
            carNum++;
        }
        //安排终点拣货员
        int dstNodeId = passNodes.get(passNodeNum);
        arrange100NodeWorker(dstNodeId);
    }

    /**
     * 安排低于100货物路径中的拣货员和列车
     * @param result
     */
    public static void arrangeBl100CarAndWor(Result result, List<Integer> passNodes,Double avWeight) {
        //开始遍历规划路径
        int passNodeNum = 0;
        int carNum = 0;
        //安排起点拣货员
        arrange100NodeWorker(passNodes.get(0));
        //记录起始轨道
        Integer befCarId = result.getCarNums().get(0);
        Integer befLinkId = result.getLinkIds().get(0);
        for (Integer linkId : result.getLinkIds()) {
            Integer useCarId = result.getCarNums().get(carNum);
            //实时记录经过的第几个站点
            int curNodeId = passNodes.get(passNodeNum);
            //如果下一个轨道列车不同说明有换乘，安排换乘拣货员
            if (!befCarId.equals(useCarId)) {
                arrangeBl100NodeWorker(curNodeId, befLinkId, befCarId);
                arrangeBl100NodeWorker(curNodeId, linkId, useCarId);
            }
            befCarId = useCarId;
            befLinkId = linkId;
            arrangeCar(linkId, useCarId);
            cacheNotMaxCar(linkId, useCarId, avWeight);
            passNodeNum++;
            carNum++;
        }
        //终点人员安排
        int dstNodeId = passNodes.get(passNodeNum);
        carNum--;
        arrangeBl100NodeWorker(dstNodeId, result.getLinkIds().get(carNum), result.getCarNums().get(carNum));
    }

    /**
     * 安排拼车为100的货物
     * @param waitItem
     * @param carpItemIds
     * @param allCarpItemIds
     */
    private static void arrange100ItemCarp(Item waitItem, Set<Integer> carpItemIds,Set<Integer> allCarpItemIds) {
        Result result = new Result(waitItem.getItemId(),waitItem.getWeight());
        //初始化必经站点
        List<Integer> passNodes = waitItem.getPlanedPath().getNodes();
        //先判断起点是否有空余的工作人员，如果没有直接失败
        if (nodeWorkNumNotEnoughCarp(waitItem, waitItem.getSrcNode(), 1, allCarpItemIds)) return;
        //经过的第一个轨道ID
        int initLinkId = waitItem.getPlanedPath().getLinks().get(0);
        //再判定起始路径是否还有可用车辆，如果没有直接失败
        if (linkCarNotEnoughCarp(waitItem, links[initLinkId], allCarpItemIds)) return;
        //初始化起点出发路径轨道的可用车辆，提取可用车辆ID，默认安排可用的第一辆车
        Integer useCarId = links[initLinkId].getAvailCarsId().get(0);
        //开始遍历规划路径
        int passNodeNum = 0;
        for (Integer linkId : waitItem.getPlanedPath().getLinks()) {
            Link link = links[linkId];
            //实时记录经过的第几个站点
            int curNodeId = passNodes.get(passNodeNum);
            passNodeNum++;
            //如果轨道有相同编号的可用车辆，安排车辆，将其存入结果
            if (link.getAvailCarsId().contains(useCarId)) {
                //存入结果中
                storageResult(result, linkId, useCarId);
            }
            //如果轨道含有的可用轨道，不含目前的轨道，则要进行换乘
            else {
                //先判断站点是否还有可用的两个拣货员，如果没有直接失败
                if (nodeWorkNumNotEnoughCarp(waitItem,curNodeId,2, allCarpItemIds)) return;
                //判定这一轨道是否还有可用车辆，如果没有了，也失败处理
                if (linkCarNotEnoughCarp(waitItem, link, allCarpItemIds)) return;
                useCarId = link.getAvailCarsId().get(0);
                //存入结果中
                storageResult(result, linkId, useCarId);
            }
        }
        //判断终点是否有可用拣货员，如果没有判定失败
        if (nodeWorkNumNotEnoughCarp(waitItem, waitItem.getDstNode(), 1, allCarpItemIds)) return;
        //安排拼车为100的货物
        arrange100CarAndWor(result, passNodes);
        storageResultCarp(result, carpItemIds);
    }

    /**
     * 安排可用车
     * @param linkId
     * @param useCarId
     */
    private static void arrangeCar(int linkId, int useCarId) {
        //删除已使用的列车
        links[linkId].deleteAvailCars(useCarId);
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
     * 拼车路径列车不足
     * @return
     */
    private static boolean linkCarNotEnoughCarp(Item item,Link link, Set<Integer> itemIds) {
        if (link.getAvailCarsId().size() == 0) {
            failedItemDeal(new Result(item.getItemId(), item.getWeight()), item);
            if (itemIds.size() != 0) {
                for (Integer itemId : itemIds) {
                    Item item1 = items[itemId];
                    failedItemDeal(new Result(item1.getItemId(), item1.getWeight()), item1);
                }
            }
            String pathStr = item.getPlanedPath().nodesSeq();
            String pathReStr = pathReverse(pathStr);
            SortItem sortItem = sortItemMap.get(pathStr) != null ? sortItemMap.get(pathStr) : sortItemMap.get(pathReStr);
            sortItem.failClearThisMap();
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
     * 拼车节点拣货员不充足，失败处理
     * @param nodeId
     * @param needWorNum
     * @return
     */
    private static boolean nodeWorkNumNotEnoughCarp(Item item, int nodeId, int needWorNum, Set<Integer> itemIds) {
        if (nodes[nodeId].getAvailWorkerNumber() < needWorNum) {
            failedItemDeal(new Result(item.getItemId(), item.getWeight()), item);
            if (itemIds.size() != 0) {
                for (Integer itemId : itemIds) {
                    Item item1 = items[itemId];
                    failedItemDeal(new Result(item1.getItemId(), item1.getWeight()), item1);
                }
            }
            String pathStr = item.getPlanedPath().nodesSeq();
            String pathReStr = pathReverse(pathStr);
            SortItem sortItem = sortItemMap.get(pathStr) != null ? sortItemMap.get(pathStr) : sortItemMap.get(pathReStr);
            sortItem.failClearThisMap();
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
    public static void storageResult(Result result, int linkId, int useCarId) {
        result.setLinkIds(linkId);
        result.setCarNums(useCarId);
    }

    /**
     * 安排100货物的站点工作人员
     * @param nodeId
     */
    private static void arrange100NodeWorker(int nodeId) {
        Node node = nodes[nodeId];
        //站点有效工作人员减少
        node.setAvailWorkerNumber();
    }

    /**
     * 安排低于100货物的站点工作人员
     * @param nodeId
     * @param linkId
     * @param useCarId
     */
    private static void arrangeBl100NodeWorker(int nodeId, int linkId, int useCarId) {
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
    public static void failedItemDeal(Result result, Item waitItem) {
        outPut.setTotalFailedNum();
        outPut.setTotalFailedWeight(waitItem.getWeight());
        outPut.setResults(result);
    }


    /**
     * 货物的初始化分类
     */
    private static void init() {
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
                if (items[itemId].getWeight() > 50) continue;
                sortItem.setSortItem(items[itemId].getWeight(), itemId);
            }
            sortItem.removeZeroEn();
            sortItemMap.put(entry.getKey(), sortItem);
        }

        sortItemByWe = new LinkedList<>();
        for (Item item : items) {
            if (item.getPlanedPath() != null) {
                String pathStr = item.getPlanedPath().nodesSeq();
                String pathReStr = pathReverse(pathStr);
                SortItem sortItem = sortItemMap.get(pathStr) != null ? sortItemMap.get(pathStr) : sortItemMap.get(pathReStr);
                item.setItemNumInPath(sortItem.getAllItemIdsNum());
            }
            sortItemByWe.add(item);
        }
        //货物按重量和路径货物数降序排序
        CompareItem compareItem = new CompareItem(sortItemByWe);

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

    /**
     * 相反路径
     * @param string
     * @return
     */
    public static String pathReverse(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] nodes = string.split(",");
        for (int i = nodes.length-1; i >= 0; i--) {
            stringBuilder.append(nodes[i]).append(",");
        }
        String result = stringBuilder.toString();
        return result.substring(0,result.lastIndexOf(","));
    }
}
