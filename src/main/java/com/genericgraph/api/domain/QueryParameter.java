package com.genericgraph.api.domain;

public class QueryParameter {
    String paramaterName;
    String value;
    String comparator;

    QueryParameter() {}

    QueryParameter(String paramaterName, String comparator, String value ) {
        this.paramaterName = paramaterName;
        this.comparator = comparator;
        this.value = value;
    }
}