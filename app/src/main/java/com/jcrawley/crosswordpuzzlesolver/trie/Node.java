package com.jcrawley.crosswordpuzzlesolver.trie;

import java.util.HashMap;
import java.util.Map;

public class Node {

    private final Map<String, Node> nodesMap;

    public Node(){
        nodesMap = new HashMap<>(50);
    }

    public Node addNode(String name){
        Node childNode = new Node();
        nodesMap.put(name, childNode);
        return childNode;
    }

    public boolean doesChildExist(String name){
        return nodesMap.containsKey(name);
    }

    public Node getNode(String name){
        return nodesMap.get(name);
    }
}
