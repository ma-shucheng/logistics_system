package com.shuke.logistics.service;

import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;
import com.shuke.logistics.entity.middle.ItemGroup;
import com.shuke.logistics.entity.middle.RemainItem;
import com.shuke.logistics.entity.middle.SortItem;
import com.shuke.logistics.entity.output.OutPut;
import com.shuke.logistics.entity.output.Result;

import java.util.*;

public class CarpoolPlantItem {

    public static OutPut outPut;
    public static Item[] items;
    public static Link[] links;
    public static Node[] nodes;
    public static Map<String, SortItem> sortItemMap;
    public static List<RemainItem> sortPathByWe;
    private static Set<Integer> failedItemIds;


    public static void arrangeCarpool() {
        init();
        for (RemainItem remainItem : sortPathByWe) {
            List<ItemGroup> itemGroups = new LinkedList<>();
            while (!(remainItem.getSumWeights() < 50)) {
                itemGroups.add(arrangedItem(remainItem));
            }
            for (ItemGroup itemGroup : itemGroups) {
                //分情况安排货物
                arrangeItemGroup(itemGroup, itemGroup.getAvilWeight() == 100);
            }
        }
    }


    /**
     * 安排拼车重量为100的货物
     * @param itemGroup
     */
    private static void arrangeItemGroup(ItemGroup itemGroup, boolean max) {
        //以组内第一个货物作为规划对象
        Item waitItem = itemGroup.getFirstItem();
        Result result = new Result(waitItem.getItemId(),waitItem.getWeight());
        //初始化必经站点
        List<Integer> passNodes = waitItem.getPlanedPath().getNodes();
        //先判断起点是否有空余的工作人员，如果没有直接失败
        if (nodeWorkNumNotEnoughCarp(waitItem.getSrcNode(), 1, itemGroup.getItemIds())) return;
        //经过的第一个轨道ID
        int initLinkId = waitItem.getPlanedPath().getLinks().get(0);
        //再判定起始路径是否还有可用车辆，如果没有直接失败
        if (linkCarNotEnoughCarp(links[initLinkId], itemGroup.getItemIds())) return;
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
                //安排站点拣货员和初始化新车
                MiddlePlanItem.storageResult(result, linkId, useCarId);
            }
            //如果轨道含有的可用轨道，不含目前的轨道，则要进行换乘
            else {
                //先判断站点是否还有可用的两个拣货员，如果没有直接失败
                if (nodeWorkNumNotEnoughCarp(curNodeId,2, itemGroup.getItemIds())) return;
                //判定这一轨道是否还有可用车辆，如果没有了，也失败处理
                if (linkCarNotEnoughCarp(link, itemGroup.getItemIds())) return;
                //新的可用车ID
                useCarId = link.getAvailCarsId().get(0);
                //安排站点拣货员和初始化新车
                MiddlePlanItem.storageResult(result, linkId, useCarId);
            }
        }
        //判断终点是否有可用拣货员，如果没有判定失败
        if (nodeWorkNumNotEnoughCarp(waitItem.getDstNode(), 1, itemGroup.getItemIds())) return;
        if (max) {
            MiddlePlanItem.arrange100CarAndWor(result, passNodes);
        } else {
            MiddlePlanItem.arrangeBl100CarAndWor(result, passNodes, itemGroup.getAvilWeight());
        }
        storageResultCarp(result, itemGroup);
    }

    /**
     * 将结果存贮进output中
     * @param result
     * @param itemGroup
     */
    private static void storageResultCarp(Result result, ItemGroup itemGroup) {
        for (Integer itemId : itemGroup.getItemIds()) {
            outPut.setResults(new Result(itemId, result.getLinkIds(), result.getCarNums()));
        }
    }

    /**
     * 拼车节点拣货员不够
     * @param nodeId
     * @param needWorNum
     * @param itemIds
     * @return
     */
    private static boolean nodeWorkNumNotEnoughCarp(int nodeId, int needWorNum, List<Integer> itemIds) {
        if (nodes[nodeId].getAvailWorkerNumber() < needWorNum) {
            for (Integer itemId : itemIds) {
                Item item1 = items[itemId];
                MiddlePlanItem.failedItemDeal(new Result(item1.getItemId(), item1.getWeight()), item1);
                failedItemIds.add(itemId);
            }
            return true;
        }
        return false;
    }

    /**
     * 拼车路径车辆不够
     * @param link
     * @param itemIds
     * @return
     */
    private static boolean linkCarNotEnoughCarp(Link link, List<Integer> itemIds) {
        if (link.getAvailCarsId().size() == 0) {
            for (Integer itemId : itemIds) {
                Item item1 = items[itemId];
                MiddlePlanItem.failedItemDeal(new Result(item1.getItemId(), item1.getWeight()), item1);
                failedItemIds.add(itemId);
            }
            return true;
        }
        return false;
    }

    /**
     * 剩余货物分组
     * @param remainItem
     * @return
     */
    private static ItemGroup arrangedItem(RemainItem remainItem) {
        Double avWeight = 0.0;
        ItemGroup itemGroup = new ItemGroup();
        SortItem sortItem = sortItemMap.get(remainItem.getPassNodes());
        if (sortItem.getWeights().size() == 0) {
            return itemGroup;
        }
        for (Double weight : sortItem.getWeights()) {
            while (!sortItem.getItemZeroByWeight(weight)) {
                //重量与有效重量相加大于100下一次循环
                if (weight + avWeight > 100) {
                    break;
                }
                //没有跳过有效重量增加
                avWeight += weight;
                //将其放入搭车货物中
                itemGroup.setItemIds(sortItem.getItemId(weight));
                //删除列表对应货物
                sortItem.removeItemId(weight, sortItem.getItemId(weight));
            }
        }
        //清空重量对应货物数量已经为0的键值
        sortItem.removeZeroEn();
        itemGroup.setAvilWeight(avWeight);
        remainItem.deleteSumWeight(avWeight);
        return itemGroup;
    }


    private static void init() {
        outPut = MiddlePlanItem.outPut;
        items = MiddlePlanItem.items;
        links = MiddlePlanItem.links;
        nodes = MiddlePlanItem.nodes;
        sortItemMap = MiddlePlanItem.sortItemMap;
        sortPathByWe = new LinkedList<>();
        for (String path : sortItemMap.keySet()) {
            SortItem sortItem = sortItemMap.get(path);
            sortPathByWe.add(new RemainItem(path, sortItem.getAllWeight()));
        }
        Collections.sort(sortPathByWe);
        failedItemIds = new HashSet<>();
    }
}
