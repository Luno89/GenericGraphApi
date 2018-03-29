package com.genericgraph.api.domain;

public class QueryParameter {
    public String paramaterName;
    public Object value;
    public String comparator;

    public QueryParameter() {}

    public QueryParameter(String paramaterName, String comparator, Object value ) {
        this.paramaterName = paramaterName;
        this.comparator = comparator;
        this.value = value;
    }

    @Override
    public String toString() {
        return toStringWithId("n");
    }

    public String toStringWithId(String id) {
        String valueString = this.value instanceof Integer ? this.value.toString() : "\"" + this.value + "\"";
        return id + "." + this.paramaterName + " " + this.comparator + " " + valueString;
    }
}