package com.genericgraph.api;

import java.util.HashMap;
import java.util.Map;

public class Relationship {
    String name;
    HashMap<String, Object> values;

    public Relationship() {}

    Relationship(Map<String,Object> map) {
        this.name = (String)map.get("name");
        this.values = new HashMap<String, Object>();
        map.entrySet().stream().filter(it -> it.getKey() != "name").forEach(e -> {
            this.values.put(e.getKey(), e.getValue());
        });
    }
}