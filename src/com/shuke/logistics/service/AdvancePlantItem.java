package com.shuke.logistics.service;

import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;
import com.shuke.logistics.entity.middle.ItemGroup;
import com.shuke.logistics.entity.middle.LastItem;
import com.shuke.logistics.entity.middle.RemainItem;
import com.shuke.logistics.entity.middle.SortItem;
import com.shuke.logistics.entity.output.OutPut;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AdvancePlantItem {
    public static OutPut outPut;
    public static Item[] items;
    public static Link[] links;
    public static Node[] nodes;
    public static Map<String, SortItem> sortItemMap;
    public static List<LastItem> sortPathByWe;
    public static void main(String[] args) {
        MiddlePlanItem.arrange100AndAb50();
        init();
        for (LastItem lastItem : sortPathByWe) {
            List<ItemGroup> itemGroups = new LinkedList<>();
            itemGroups.add(arrangedItem(lastItem));
            for (ItemGroup itemGroup : itemGroups) {
                if (itemGroup.getAvilWeight() != 0) {
                    //分情况安排货物
                    CarpoolPlantItem.arrangeItemGroup(itemGroup, itemGroup.getAvilWeight() == 100);
                }
            }
        }
        SimplePlanItem.writeFile(outPut);
    }

    private static ItemGroup arrangedItem(LastItem lastItem) {
        Double avWeight = 0.0;
        ItemGroup itemGroup = new ItemGroup();
        SortItem sortItem = sortItemMap.get(lastItem.getPassNodes());
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
        lastItem.deleteSumWeight(avWeight);
        return itemGroup;
    }

    public static void init() {
        outPut = MiddlePlanItem.outPut;
        items = MiddlePlanItem.items;
        links = MiddlePlanItem.links;
        nodes = MiddlePlanItem.nodes;
        sortItemMap = MiddlePlanItem.sortItemMap;
        sortPathByWe = new LinkedList<>();
        for (String path : sortItemMap.keySet()) {
            SortItem sortItem = sortItemMap.get(path);
            sortPathByWe.add(new LastItem(path, sortItem.getAllWeight()));
        }
        Collections.sort(sortPathByWe);
    }
}
