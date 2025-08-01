package com.arc_studio.brick_lib.api.core.container;

public record Triple<E1, E2, E3>(E1 element1, E2 element2, E3 element3) {
    @Override
    public String toString() {
        return "(" + element1 + "," + element2 + "," + element3 + ")";
    }
}
