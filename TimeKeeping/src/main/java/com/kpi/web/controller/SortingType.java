package com.kpi.web.controller;

public enum SortingType {
    ASCENDING(true),
    DESCENDING(false);

    private final boolean ascending;

    SortingType(boolean ascending) {
        this.ascending = ascending;
    }

    public boolean isAscending() {
        return ascending;
    }
}
