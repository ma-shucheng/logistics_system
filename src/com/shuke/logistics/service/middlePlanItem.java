package com.shuke.logistics.service;

import com.shuke.logistics.dao.ReSort;
import com.shuke.logistics.dao.Read;
import com.shuke.logistics.entity.input.Item;

import java.lang.reflect.Method;

public class middlePlanItem {

    public static void main(String[] args) {
        try {
            Method method = SimplePlanItem.class.getMethod("main", String[].class);
            method.invoke(SimplePlanItem.class.newInstance(), new Object[]{ new String[0]});
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(SimplePlanItem.failedItemId.size());
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
