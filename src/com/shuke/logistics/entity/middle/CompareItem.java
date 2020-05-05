package com.shuke.logistics.entity.middle;

import com.shuke.logistics.entity.input.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompareItem {

    public List<Item> mList;
    public List<Comparator<Item>> mCmpList = new ArrayList<Comparator<Item>>();
    public CompareItem(List<Item> list){
        mList = list;
        mCmpList.add(compareAgeASC);
        mCmpList.add(comparePointDESC);
        sort(mList, mCmpList);
    }
    public void sort(List<Item> list, final List<Comparator<Item>> comList) {
        if (comList == null)
            return;
        Comparator<Item> cmp = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                for (Comparator<Item> comparator : comList) {
                    if (comparator.compare(o1, o2) > 0) {
                        return 1;
                    } else if (comparator.compare(o1, o2) < 0) {
                        return -1;
                    }
                }
                return 0;
            }
        };
        Collections.sort(list, cmp);
    }

    private Comparator<Item> compareAgeASC = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            if (o1.getWeight()<o2.getWeight()) {
                return 1;
            } else if (o1.getWeight() == o2.getWeight()) {
                return 0;
            } else {
                return -1;
            }
        }
    };

    private Comparator<Item> comparePointDESC = new Comparator<Item>() {

        @Override
        public int compare(Item o1, Item o2) {
            if (o1.getItemNumInPath()<o2.getItemNumInPath()) {
                return 1;
            } else if (o1.getItemNumInPath() == o2.getItemNumInPath()) {
                return 0;
            } else {
                return -1;
            }
        }
    };


}
