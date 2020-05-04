package com.shuke.logistics.entity.middle;

import java.util.*;

public class SortItem {
    private Map<Double, List<Integer>> sortItem = new HashMap<>();

    public SortItem(Set<Double> weights) {
        for (Double weight : weights) {
            sortItem.put(weight, new LinkedList<>());
        }
    }

    public void setSortItem(Double weight, Integer itemId) {
        sortItem.get(weight).add(itemId);
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
