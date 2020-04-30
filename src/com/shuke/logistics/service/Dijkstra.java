package com.shuke.logistics.service;

import com.shuke.logistics.dao.ReSort;
import com.shuke.logistics.dao.Read;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;
import com.shuke.logistics.entity.middle.Path;

import java.util.*;

/**
 * <p>
 * Description:迪杰斯特拉对所有结节最短路径的规划
 * </p>
 *
 * @author Shuke
 * @version v1.0.0
 * @see com.shuke.logistics.service
 * @since 2020-04-28 16:52:10
 */
public class Dijkstra {
    public static List<Integer> failedNodesId = new LinkedList<>();
    /**
     * 对所有节点进行路径规划
     *
     * @param nodes
     * @param links
     */
    public void allDijkstra(Node[] nodes, Link[] links) {
        for (Node src : Read.nodes) {
            Path[] paths = new Path[nodes.length];
            boolean success = singleDijkstra(nodes, links, paths, src);
            nodes[src.getNodeId()].setMinPath(paths);
            //设置终点
            int loc = 0;
            for (Path path : src.getMinPath()) {
                if (path != null) {
                    path.setDst(loc);
                }
                loc++;
            }
            if (!success) {
                failedNodesId.add(src.getNodeId());
            }
        }
    }

    /**
     * 单源点的迪杰斯特拉路径规划
     *
     * @param nodes
     * @param links
     * @param paths
     * @param intiNode
     */
    private boolean singleDijkstra(Node[] nodes, Link[] links, Path[] paths, Node intiNode) {
        //初始化单源规划
        List<Node> middleNodes = new LinkedList<>();
        Set<Integer> set = new HashSet<>();
        set.add(intiNode.getNodeId());
        middleNodes.add(intiNode);
        paths[intiNode.getNodeId()] = new Path(0, intiNode.getNodeId());

        int weight = 1;
        while (set.size() != Read.nodeNum) {
            HashSet<Integer> newSet = new HashSet<>();
            HashSet<Integer> newLinks = new HashSet<>();
            for (Node node : middleNodes) {
                HashSet<Integer> relatedLink = node.getRelatedLink();
                for (Integer i : relatedLink) {
                    int src = Integer.parseInt(links[i].getSrcNode().substring(1));
                    int dst = Integer.parseInt(links[i].getDstNode().substring(1));
                    if (!set.contains(src)) {
                        newSet.add(src);
                    }
                    if (!set.contains(dst)) {
                        newSet.add(dst);
                    }
                    if (!set.contains(src) || !set.contains(dst)) {
                        newLinks.add(i);
                    }
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
                    // ，则直接在不可达集添加路径
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
                else if ((paths[dst].getWeight() != -1 && paths[src].getWeight() == -1)) {
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
            //如果新增集合大小为0则跳出
            if (newSet.size() == 0) {
                return false;
            }
        }
        return true;
    }


    public static Path dynamicDijkstra(List<Integer> incNodesIds, int initSrc, int initDst) {
        Node[] nodes = ReSort.reNodes;
        Link[] links = ReSort.reLinks;
        int curSrc = initSrc;
        Set<Integer> set = new HashSet<>();
        set.add(initSrc);
        List<Integer> stoPath = new LinkedList<>();
        boolean havaInc = incNodesIds.size() != 0;
        //如果有必经站点再进行以下循环，否则跳过循环
        if (havaInc) {
            for (Integer curDst : incNodesIds) {
                Path path = singleDynaDijkstra(nodes[curSrc], set, curDst,havaInc);
                //路径为空返回路径
                if (path == null) return null;
                set.clear();
                //将路径中所有点重新赋值给set，使得搜索路径不会重复，并将路径结果存储
                for (Integer linkId : path.getLinks()) {
                    set.add(links[linkId].getSrcNodeId());
                    set.add(links[linkId].getDstNodeId());
                    stoPath.add(linkId);
                }
                curSrc = curDst;
            }
        }
        Path path = singleDynaDijkstra(nodes[curSrc], set, initDst,havaInc);
        if (path == null) return null;
        for (Integer linkId : path.getLinks()) {
            stoPath.add(linkId);
        }
        path.setLinks(stoPath);
        path.setDst(initDst);
        return path;
    }

    private static Path singleDynaDijkstra(Node intiNode, Set<Integer> set, int curDst, boolean haveInc) {
        Node[] nodes = ReSort.reNodes;
        Link[] links = ReSort.reLinks;
        Path[] paths = new Path[nodes.length];
        //初始化单源规划
        List<Node> middleNodes = new LinkedList<>();
        middleNodes.add(intiNode);
        paths[intiNode.getNodeId()] = new Path(0, intiNode.getNodeId());

        int weight = 1;
        while (set.size() != Read.nodeNum) {
            HashSet<Integer> newSet = new HashSet<>();
            HashSet<Integer> newLinks = new HashSet<>();
            for (Node node : middleNodes) {
                if (haveInc && set.contains(node.getNodeId())) continue;
                HashSet<Integer> relatedLink = node.getRelatedLink();
                for (Integer i : relatedLink) {
                    int src = Integer.parseInt(links[i].getSrcNode().substring(1));
                    int dst = Integer.parseInt(links[i].getDstNode().substring(1));
                    if (!set.contains(src)) {
                        newSet.add(src);
                    }
                    if (!set.contains(dst)) {
                        newSet.add(dst);
                    }
                    if (!set.contains(src) || !set.contains(dst)) {
                        newLinks.add(i);
                    }
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
                else if ((paths[dst].getWeight() != -1 && paths[src].getWeight() == -1)) {
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
            //如果新增集合大小为0则跳出
            if (newSet.size() == 0) {
                break;
            }
        }
        //路径为空返回空
        if (paths[curDst] == null) return null;
        return paths[curDst];
    }

}
