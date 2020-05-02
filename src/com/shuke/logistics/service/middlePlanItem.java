package com.shuke.logistics.service;

import com.shuke.logistics.dao.ReSort;
import com.shuke.logistics.dao.Read;
import com.shuke.logistics.entity.input.Item;

public class middlePlanItem {

    public static void main(String[] args) {
        SimplePlanItem.arrangeAllItemPath();
        int havePathItemNum = 0;
        for (Item item : ReSort.reItems) {
            if (item != null) {
                if (item.getPlanedPath() != null) {
                    havePathItemNum++;
                }
            }
        }
        System.out.println("共有货物"+(Read.items.length));
        System.out.println(havePathItemNum);
    }

}
