package com.shuke.logistics.entity.middle;

import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.service.MiddlePlanItem;

import java.util.*;

public class SortItem {
    private Map<Double, List<Integer>> sortItem = new HashMap<>();
    private int allItemIdsNum = 0;

    public int getAllItemIdsNum() {
        return allItemIdsNum;
    }

    public void failClearThisMap() {
        sortItem.clear();
        this.allItemIdsNum = 0;
    }

    public SortItem(Set<Double> weights) {
        for (Double weight : weights) {
            sortItem.put(weight, new LinkedList<>());
        }
    }

    public Set<Double> getWeights () {
        return sortItem.keySet();
    }

    public boolean getItemZeroByWeight(Double weight) {
        return sortItem.get(weight).size() == 0;
    }

    public void getAllItemID (Set<Integer> allItemID) {
        if (getWeights().size() != 0) {
            for (Double weight : getWeights()) {
                for (Integer itemId : sortItem.get(weight)) {
                    allItemID.add(itemId);
                }
            }
        }
    }

    public Double getAllWeight () {
        Double allWeight = 0.0;
        if (getWeights().size() != 0) {
            for (Double weight : getWeights()) {
                int size = sortItem.get(weight).size();
                allWeight += weight * size;
            }
        }
        return allWeight;
    }

    public void getAllItemID (List<Item> allItemID) {
        if (getWeights().size() != 0) {
            for (Double weight : getWeights()) {
                for (Integer itemId : sortItem.get(weight)) {
                    allItemID.add(MiddlePlanItem.items[itemId]);
                }
            }
        }
    }

    public Integer getItemId(Double weight) {
        return sortItem.get(weight).get(0);
    }

    public void removeItemId(Double weight,Integer itemId) {
        sortItem.get(weight).remove(itemId);
        allItemIdsNum--;
    }

    public void setSortItem(Double weight, Integer itemId) {
        sortItem.get(weight).add(itemId);
        allItemIdsNum++;
    }

    public Map<Double, List<Integer>> getSortItem() {
        return sortItem;
    }

    public void removeZeroEn() {
        Set<Double> set = new HashSet<>();
        for (Map.Entry<Double, List<Integer>> entry : sortItem.entrySet()) {
            if (entry.getValue().size() == 0) {
                set.add(entry.getKey());
            }
        }
        for (Double weight : set) {
            sortItem.remove(weight);
        }
    }

}
