package com.shuke.logistics.dao;

import com.shuke.logistics.entity.input.Item;
import com.shuke.logistics.entity.input.Link;
import com.shuke.logistics.entity.input.Node;
/**
 * 
 * <p>
 * 重新排布输入数据，按照各自ID存入数组中
 * </p>
 * 
 * @author Shuke
 * @version v1.0.0
 * @since 2020-04-27 17:59:30
 * @see com.shuke.logistics.dao
 *
 */
public class ReSort {
    public static Node[] reNodes;
    public static Link[] reLinks;
    public static Item[] reItems;

    public static void reSort() {
        Read read = new Read();
        ReSort reSort = new ReSort();
        read.readFile();
        reNodes = reSort.reSortNodes(Read.nodes);
        reLinks = reSort.reSortLinks(Read.links);
        reItems = reSort.reSortItems(Read.items);
    }

    /**
     * 重新排布节点
     * @param nodes
     * @return
     */
    private Node[] reSortNodes(Node[] nodes) {
        int max = 0;
        //取站点序列号的最大值
        for (Node node : nodes) {
            if (max < node.getNodeId()) {
                max = node.getNodeId();
            }
        }
        Node[] nodes1 = new Node[max + 1];
        //将站点放入对应位置数组
        for (Node node : nodes) {
            nodes1[node.getNodeId()] = node;
        }
        return nodes1;
    }

    /**
     * 重新排布链路，并将链路添加到对应连接站点中
     * @param links
     * @return
     */
    private Link[] reSortLinks(Link[] links) {
        int max = 0;
        //取站点序列号的最大值
        for (Link node : links) {
            if (max < node.getLinkId()) {
                max = node.getLinkId();
            }
        }
        Link[] links1 = new Link[max + 1];
        //将站点放入对应位置数组
        for (Link link : links) {
            links1[link.getLinkId()] = link;
            //链路添加到对应连接站点中
            reNodes[Integer.parseInt(link.getSrcNode().substring(1))].setRelatedLink(link.getLinkId());
            reNodes[Integer.parseInt(link.getDstNode().substring(1))].setRelatedLink(link.getLinkId());
        }
        return links1;
    }

    /**
     * 重新排布货物
     * @param items
     * @return
     */
    private Item[] reSortItems(Item[] items) {
        int max = 0;
        //取站点序列号的最大值
        for (Item item : items) {
            if (max < item.getItemId()) {
                max = item.getItemId();
            }
        }
        Item[] items1 = new Item[max + 1];
        //将站点放入对应位置数组
        for (Item item : items) {
            items1[item.getItemId()] = item;
        }
        return items1;
    }
}
