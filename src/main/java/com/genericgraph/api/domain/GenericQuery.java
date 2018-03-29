package com.genericgraph.api.domain;

import java.util.ArrayList;

public class GenericQuery {
    public ArrayList<String> labels;
    public ArrayList<QueryParameter> nodes;
    public ArrayList<QueryParameter> relationships;

    public static class Builder {
        final String queryStart = "MATCH (n) WHERE ";
        final String queryEnd = "RETURN n";
        public String labels = "";
        public String values = "";
        public ArrayList<QueryParameter> relationships;
        GenericQuery genericQuery;

        public Builder(GenericQuery genericQuery) {
            this.genericQuery = genericQuery;
        }

        Builder withLabels(ArrayList<String> labels) {
            if (labels != null && labels.size() > 0) {
                StringBuilder labelStringBuilder = new StringBuilder();
                labels.forEach(s -> {
                    labelStringBuilder.append(":" + s);
                });
                this.labels = "n" + labelStringBuilder.toString() + " ";
            }
            return this;
        }

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

        Builder withRelationships(ArrayList<QueryParameter> parameters) {
            return this;
        }

        public String build() {
            withLabels(this.genericQuery.labels);
            withValues(this.genericQuery.nodes);
            withRelationships(this.genericQuery.relationships);
            String where = values != "" && labels != "" ? values + "AND " + labels : values + labels;
            return queryStart + where + queryEnd;
        }
    }
}