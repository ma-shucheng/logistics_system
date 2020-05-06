package com.shuke.logistics.service;

import com.shuke.logistics.entity.output.OutPut;

public class AdvancePlantItem {
    public static OutPut outPut;
    public static void main(String[] args) {
        MiddlePlanItem.arrange100AndAb50();
        outPut = MiddlePlanItem.outPut;
        MiddlePlanItem.failedRemainSortItemMap();
        SimplePlanItem.writeFile(outPut);
    }
}
