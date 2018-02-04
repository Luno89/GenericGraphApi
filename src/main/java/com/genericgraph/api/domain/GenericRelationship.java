package com.genericgraph.api.domain;

import java.util.HashMap;
import java.util.Map;

public class GenericRelationship {
    public String name;
    public HashMap<String, Object> values;

    public GenericRelationship() {}

    GenericRelationship(Map<String,Object> map) {
        this.name = (String)map.get("name");
        this.values = new HashMap<String, Object>();
        map.entrySet().stream().filter(it -> it.getKey() != "name").forEach(e -> {
            this.values.put(e.getKey(), e.getValue());
        });
    }
}