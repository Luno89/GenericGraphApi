package com.genericgraph.api.domain;

import java.util.ArrayList;

public class GenericQueryNode {
    String id;
    ArrayList<String> labels;
    ArrayList<QueryParameter> parameters;

    public static class Builder {
        GenericQueryNode node;
        String id;

        public Builder(GenericQueryNode node, String id) {
            this.node = node;
            this.id = id;
        }
        
        public String buildLabels() {
            if (node == null || node.labels == null || (node.labels.size() < 1)) return "";

            StringBuilder stringBuilderL = new StringBuilder();
            node.labels.forEach( l -> {
                stringBuilderL.append(":" + l);
            });
            id += stringBuilderL.toString();
            return id;
        }

        public String buildWhere() {
            if (node == null || node.parameters == null || (node.parameters.size() < 1)) return "";

            StringBuilder stringBuilder = new StringBuilder();
            node.parameters.forEach(p -> {
                stringBuilder.append(p.toStringWithId(id) + " AND ");
            });
            return stringBuilder.toString();
        }
    }
}