package com.shuke.logistics.dao;

import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * <p>
 * Description:读入数据
 * </p>
 * 
 * @author Shuke
 * @version v1.0.0
 * @since 2020-04-27 18:00:11
 * @see com.shuke.logistics.dao
 *
 */
public class Read {

    public static Node[] nodes;
    public static Link[] links;
    public static Item[] items;
    public static int nodeNum;

    public void readFile() {
        Read read = new Read();
        try {
//            BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\Shuke\\Desktop\\中兴大赛\\测试用例集\\case1\\demo.txt"));
//            BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\Shuke\\Desktop\\中兴大赛\\Dijkstra2020验证程序\\test.txt"));
            BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\Shuke\\Desktop\\中兴大赛\\测试用例集\\case1\\topoAndRequest1.txt"));
//            BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\Shuke\\Desktop\\中兴大赛\\测试用例集\\case8\\topoAndRequest8.txt"));
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            //站点数，轨道数，列车数，列车容量
            String s = in.readLine();
            String[] dataSize = s.split(",");
            nodeNum = Integer.parseInt(dataSize[0]);
            int linkNum = Integer.parseInt(dataSize[1]);
            int carNum = Integer.parseInt(dataSize[2]);
            nodes = new Node[nodeNum];
            links = new Link[linkNum];
            for (int i = 0; i < nodeNum; i++) {
                //每个站点的拣货员数据
                s = in.readLine();
                nodes[i] = readNode(s);
            }
            for (int i = 0; i < linkNum; i++) {
                //每条轨道的起止点
                s = in.readLine();
                links[i] = readLink(s, carNum);
            }
            //货物数量S
            s = in.readLine();
            int requestNum = Integer.parseInt(s);
            items = new Item[requestNum];
            for (int i = 0; i < requestNum; i++) {
                //每个货物相关信息
                s = in.readLine();
                items[i] = readItem(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node readNode(String s) {
        int nodeId = Integer.parseInt(s.substring(1, s.indexOf(",")));
        int totalWorNum = Integer.parseInt(s.substring(s.indexOf(",") + 1));
        Node node = new Node(nodeId, totalWorNum);
        return node;
    }
    private Link readLink(String s,int carNum) {
        int linkId = Integer.parseInt(s.substring(1, s.indexOf(",")));
        String src = s.substring(s.indexOf(",") + 1, s.lastIndexOf(","));
        String dest = s.substring(s.lastIndexOf(",") + 1);
        Link link = new Link(linkId, src, dest, carNum);
        return link;
    }
    private Item readItem(String s) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        String[] strings = s.split(",");
        int itemId = Integer.parseInt(strings[0].substring(1));
        String src = strings[1];
        String dst = strings[2];
        Double weight = Double.parseDouble(strings[3]);
        for (int i = 4; i < strings.length; i++) {
            if (strings[i].equals("null")) {
                break;
            }
            arrayList.add(Integer.parseInt(strings[i].substring(1)));
        }
        Item item = new Item(itemId, Integer.parseInt(src.substring(1)), Integer.parseInt(dst.substring(1)), weight, arrayList);
        return item;
    }

}
