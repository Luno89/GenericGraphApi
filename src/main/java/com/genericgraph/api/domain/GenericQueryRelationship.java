package com.genericgraph.api.domain;

import java.util.ArrayList;

public class GenericQueryRelationship {
    String id;
    GenericQueryNode fromNode;
    GenericQueryNode toNode;
    String label;
    ArrayList<QueryParameter> parameters;


    public static class Builder {
        GenericQueryRelationship relationship;
        GenericQueryNode fromNode;
        GenericQueryNode toNode;

        public Builder(GenericQueryRelationship relationship) {
            this.relationship = relationship;
            this.fromNode = relationship.fromNode;
            this.toNode = relationship.toNode;
        }

        public String buildLabel() {
            String fromLabelString = "";
            String toLabelString = "";
            
            fromLabelString = new GenericQueryNode.Builder(fromNode, getFromId()).buildLabels();
            toLabelString = new GenericQueryNode.Builder(toNode, getToId()).buildLabels();

            return "(" + fromLabelString + ")-[" + relationship.id + ":" + relationship.label + "]-(" + toLabelString + ")";
        }

        public String buildWhere() {
            if (relationship.parameters.size() < 1) return "";

            StringBuilder stringBuilder = new StringBuilder();
            String id = relationship.id;
            relationship.parameters.forEach(p -> {
                stringBuilder.append(p.toStringWithId(id) + " AND ");
            });
            return stringBuilder.toString();
        }

        private String getFromId() {
            return relationship.id + "f";
        }

        private String getToId() {
            return relationship.id + "t";
        }
    }
}