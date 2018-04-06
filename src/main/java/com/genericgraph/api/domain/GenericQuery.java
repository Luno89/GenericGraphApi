package com.genericgraph.api.domain;

import java.util.ArrayList;

public class GenericQuery {
    public ArrayList<String> labels;
    public ArrayList<QueryParameter> nodes;
    public ArrayList<GenericQueryRelationship> relationships;

    public static class Builder {
        String queryStart = "MATCH (n) WHERE ";
        final String queryEnd = "RETURN n";
        public String labels = "";
        public String values = "";
        public ArrayList<QueryParameter> relationships;
        GenericQuery genericQuery;

        public Builder(GenericQuery genericQuery) {
            this.genericQuery = genericQuery;
        }

        // TO-DO Move to GenericQueryNode
        Builder withLabels(ArrayList<String> labels) {
            if (labels != null && labels.size() > 0) {
                this.labels = "n" + buildLabelList(labels) + " ";
            }
            return this;
        }

        // TO-DO Move to GenericQueryNode
        private String buildLabelList(ArrayList<String> labels) {
            StringBuilder labelStringBuilder = new StringBuilder();
            labels.forEach(s -> {
                labelStringBuilder.append(":" + s);
            });
            return labelStringBuilder.toString();
        }

        // TO-DO Move to GenericQueryNode
        Builder withValues(ArrayList<QueryParameter> parameters) {
            if (parameters != null) {
                StringBuilder valuesString = new StringBuilder();
                parameters.forEach((QueryParameter p) -> {
                    valuesString.append(p.toString() + " AND ");
                });
                if (valuesString.length() > 4) {
                    values = valuesString.toString().substring(0, valuesString.length()-4);
                }
            }

            return this;
        }

        Builder withRelationships(ArrayList<GenericQueryRelationship> parameters) {
            if (parameters != null) {
                String id = "";
                StringBuilder l = new StringBuilder();
                parameters.forEach(p -> {
                    if (p.id == null) p.id = getId(id);
                    GenericQueryRelationship.Builder builder = new GenericQueryRelationship.Builder(p);
                    l.append(builder.buildLabel());
                    values += builder.buildWhere();
                });
                queryStart = "MATCH " + l.toString() + " WHERE ";
                values = values.substring(0,values.length() - 4);
            }
            return this;
        }
        
        private String getId(String id) {
            return id += "n";
        }

        public String build() {
            withLabels(this.genericQuery.labels); // TO-DO Move to GenericQueryNode
            withValues(this.genericQuery.nodes); // TO-DO Move to GenericQueryNode
            withRelationships(this.genericQuery.relationships);
            String where = values != "" && labels != "" ? values + "AND " + labels : values + labels;
            return queryStart + where + queryEnd;
        }
    }
}