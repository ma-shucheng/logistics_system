package com.shuke.logistics.entity.input;

public class Topo {
    private Link[] links;
    private Node[] nodes;

    public Link[] getLinks() {
        return links;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public Topo(Link[] links, Node[] nodes) {
        this.links = links;
        this.nodes = nodes;
    }
}
