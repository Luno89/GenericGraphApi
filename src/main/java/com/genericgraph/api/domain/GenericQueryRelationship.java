package com.genericgraph.api.domain;

import java.util.ArrayList;

public class GenericQueryRelationship {
    String id;
    // String toId;
    // String fromId;
    String label;
    ArrayList<QueryParameter> parameters;


    public static class Builder {
        GenericQueryRelationship relationship;

        public Builder(GenericQueryRelationship relationship) {
            this.relationship = relationship;
        }

        public String buildLabel() {
            return "[" + relationship.id + ":" + relationship.label + "]";
        }

        public String buildWhere() {
            if (relationship.parameters.size() < 1) return "";

            StringBuilder stringBuilder = new StringBuilder();
            String id = relationship.id;
            relationship.parameters.forEach(p -> {
                stringBuilder.append(p.toStringWithId(id) + " AND ");
            });
            return stringBuilder.toString().substring(0, stringBuilder.length() - 4);
        }
    }
}