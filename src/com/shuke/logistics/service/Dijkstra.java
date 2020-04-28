package com.shuke.logistics.service;

import com.shuke.logistics.dao.ReSort;
import com.shuke.logistics.dao.Read;
import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;
import com.shuke.logistics.entity.middle.Path;

import java.util.*;

public class Dijkstra {
    public static void main(String[] args) {
        Dijkstra dijkstra = new Dijkstra();
        ReSort.reSort();
        Node[] nodes = ReSort.reNodes;
        Link[] links = ReSort.reLinks;
        Item[] items = ReSort.reItems;
        List<Item> list = new LinkedList<>();
        for (Item item : Read.items) {
            list.add(item);
        }
        Collections.sort(list);
        Path[] paths = new Path[nodes.length];
        Node src = Read.nodes[0];
        dijkstra.singleDijkstra(nodes, links, paths, src);
        System.out.println(paths[3]);
    }

    private void singleDijkstra(Node[] nodes,Link[] links, Path[] paths, Node intiNode) {
        //初始化单源规划
        List<Node> middleNodes = new LinkedList<>();
        Set<Integer> set = new HashSet<>();
        set.add(intiNode.getNodeId());
        middleNodes.add(intiNode);
        paths[intiNode.getNodeId()] = new Path(0,intiNode.getNodeId());

        int weight = 1;
        while (set.size() != Read.nodeNum) {
            HashSet<Integer> newSet = new HashSet<>();
            HashSet<Integer> newLinks = new HashSet<>();
            for (Node node : middleNodes) {
                HashSet<Integer> relatedLink = node.getRelatedLink();
                for (Integer i : relatedLink) {
                    int src = Integer.parseInt(links[i].getSrcNode().substring(1));
                    int dst = Integer.parseInt(links[i].getDstNode().substring(1));
                    if (!middleNodes.contains(nodes[src])) {
                        newSet.add(src);
                    }
                    if (!middleNodes.contains(nodes[dst])) {
                        newSet.add(dst);
                    }
                    newLinks.add(i);
                }
            }
            //将新节点对应的路径初始化
            for (int nodePathId : newSet) {
                if (set.contains(nodePathId)) continue;
                paths[nodePathId] = new Path(intiNode.getNodeId());
            }
            for (Integer linkId : newLinks) {
                int src = Integer.parseInt(links[linkId].getSrcNode().substring(1));
                int dst = Integer.parseInt(links[linkId].getDstNode().substring(1));
                //如果src有权重，dst无权重
                if (paths[src].getWeight() != -1 && paths[dst].getWeight() == -1) {
                    //如果可达点的路径集合为空，则直接在不可达集添加路径
                    if (paths[src].getWeight() == 0) {
                        paths[dst].setLinks(linkId);
                        paths[dst].setWeight(weight);
                    }
                    //如果可达点的路径集合不为空，先将路径集合搬运过来再添加新路径
                    else {
                        for (Integer linkIdd : paths[src].getLinks()) {
                            paths[dst].setLinks(linkIdd);
                        }
                        paths[dst].setLinks(linkId);
                        paths[dst].setWeight(weight);
                    }
                }
                //如果src无权重，dst有权重
                else if ((paths[dst].getWeight() != -1 && paths[src].getWeight() == -1)){
                    //如果可达点的路径集合为空，则直接在不可达集添加路径
                    if (paths[dst].getWeight() == 0) {
                        paths[src].setLinks(linkId);
                        paths[src].setWeight(weight);
                    }
                    //如果可达点的路径集合不为空，先将路径集合搬运过来再添加新路径
                    else {
                        for (Integer linkIdd : paths[dst].getLinks()) {
                            paths[src].setLinks(linkIdd);
                        }
                        paths[src].setLinks(linkId);
                        paths[src].setWeight(weight);
                    }
                }
            }
            middleNodes.clear();
            for (Integer nodeId : newSet) {
                if (set.contains(nodeId)) continue;
                middleNodes.add(nodes[nodeId]);
                set.add(nodeId);
            }
            weight++;
        }
    }
}
