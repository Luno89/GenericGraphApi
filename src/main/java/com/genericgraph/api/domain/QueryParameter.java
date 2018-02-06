package com.genericgraph.api.domain;

public class QueryParameter {
    public String paramaterName;
    public Object value;
    public String comparator;

    QueryParameter() {}

    QueryParameter(String paramaterName, String comparator, Object value ) {
        this.paramaterName = paramaterName;
        this.comparator = comparator;
        this.value = value;
    }

    @Override
    public String toString() {
        String valueString = this.value instanceof Integer ? this.value.toString() : "'" + this.value + "'";
        return "n." + this.paramaterName + " " + this.comparator + " " + valueString;
    }
}