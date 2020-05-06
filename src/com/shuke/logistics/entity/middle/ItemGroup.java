package com.shuke.logistics.entity.middle;

import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.service.MiddlePlanItem;

import java.util.LinkedList;
import java.util.List;

public class ItemGroup {
    private Double avilWeight = 0.0;
    private List<Integer> itemIds = new LinkedList<>();
    private Item firstItem;


    public Item getFirstItem() {
        firstItem = MiddlePlanItem.items[itemIds.get(0)];
        return firstItem;
    }


    public Double getAvilWeight() {
        return avilWeight;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setAvilWeight(Double avilWeight) {
        this.avilWeight = avilWeight;
    }

    public void setItemIds(Integer itemId) {
        this.itemIds.add(itemId);
    }

    @Override
    public String toString() {
        return "ItemGroup{" +
                "avilWeight=" + avilWeight +
                ", itemIds=" + itemIds +
                '}';
    }
}
