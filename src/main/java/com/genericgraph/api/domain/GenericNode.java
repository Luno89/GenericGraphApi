package com.genericgraph.api.domain;

import java.util.*;

import org.neo4j.graphdb.Node;

public class GenericNode {
    public ArrayList<String> label;
    public HashMap<String,Object> values;

    public GenericNode() {
        this.label = new ArrayList<>();
        this.values = new HashMap<>();
    }

    public GenericNode(Node node) {
        this.label = new ArrayList<>();
        this.values = new HashMap<>();
        
        node.getAllProperties().forEach((k,v) -> {
            values.put(k, v);
        });

        node.getLabels().forEach(l -> {
            label.add(l.toString());
        });
    }
}