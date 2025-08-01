package com.arc_studio.brick_lib.api.core;

public enum PlatformType {
    FORGE("forge"),
    FABRIC("fabric"),
    NEO_FORGE("neo_forge");
    final String name;

    PlatformType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
