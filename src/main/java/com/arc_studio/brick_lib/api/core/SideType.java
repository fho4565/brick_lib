package com.arc_studio.brick_lib.api.core;

import java.util.Map;

public final class SideType {
    public static final int
            FALSE = 0,
            CLIENT = 1,
            SERVER = 2,
            CAS = 4,
            FORGE = 8,
            FABRIC = 16,
            NEOFORGE = 32,
            FORGELIKE = 64,
            TRUE = 128;
    private static final Map<Integer, String> map = Map.of(
            CLIENT, "CLIENT",
            SERVER, "SERVER",
            FORGE, "FORGE",
            FABRIC, "FABRIC",
            NEOFORGE, "NEOFORGE"
    );
    private int types = 0;

    public SideType setClient() {
        this.types |= CLIENT;
        return this;
    }

    public SideType setOnlyClient() {
        removeServer();
        setClient();
        return this;
    }

    public SideType setServer() {
        this.types |= SERVER;
        return this;
    }

    public SideType setOnlyServer() {
        removeClient();
        setServer();
        return this;
    }

    public SideType setForge() {
        this.types |= FORGE;
        return this;
    }

    public SideType setOnlyForge() {
        removeFabric();
        removeNeoForge();
        setForge();
        return this;
    }

    public SideType setOnlyFabric() {
        removeForge();
        removeNeoForge();
        setFabric();
        return this;
    }

    public SideType setOnlyNeoForge() {
        removeForge();
        removeFabric();
        setNeoForge();
        return this;
    }

    public SideType setFabric() {
        this.types |= FABRIC;
        return this;
    }

    public SideType setNeoForge() {
        this.types |= NEOFORGE;
        return this;
    }

    public boolean isClient() {
        return (types & CLIENT) == CLIENT;
    }
    public boolean isOnlyClient(){
        return isClient() && !isServer();
    }

    public boolean isServer() {
        return (types & SERVER) == SERVER;
    }
    public boolean isOnlyServer(){
        return isServer() && !isClient();
    }

    public boolean isForge() {
        return (types & FORGE) == FORGE;
    }

    public boolean isFabric() {
        return (types & FABRIC) == FABRIC;
    }

    public boolean isNeoForge() {
        return (types & NEOFORGE) == NEOFORGE;
    }

    public SideType removeClient() {
        this.types &= ~CLIENT;
        return this;
    }

    public SideType removeServer() {
        this.types &= ~SERVER;
        return this;
    }

    public SideType removeForge() {
        this.types &= ~FORGE;
        return this;
    }

    public SideType removeFabric() {
        this.types &= ~FABRIC;
        return this;
    }

    public SideType removeNeoForge() {
        this.types &= ~NEOFORGE;
        return this;
    }

    public SideType removeAll() {
        this.types = 0;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Sides[");
        for (int i = 0; i < 5; i++) {
            if ((types & (1 << i)) == (1 << i)) {
                sb.append(map.get(1 << i)).append(",");
            }
        }
        if(sb.lastIndexOf(",") != -1){
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.append("]").toString();
    }

    @Override
    public int hashCode() {
        return types;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SideType sideType) {
            return types == sideType.types;
        }
        return false;
    }

    public int loaderEquals(SideType other){
        if (this.isFabric()) {
            if (other.isFabric()) {
                return FABRIC;
            } else {
                return FALSE;
            }
        } else if (this.isForge()) {
            if (other.isForge()) {
                return FORGE;
            } else {
                return FALSE;
            }
        } else if (this.isNeoForge())
            if (other.isNeoForge()) {
                return NEOFORGE;
            } else {
                return FALSE;
            }
        return FALSE;
    }

    public int sideEquals(SideType other) {
        boolean c = this.isClient() && other.isClient();
        boolean s = this.isServer() && other.isServer();
        if (c && s) {
            return CAS;
        } else if (c) {
            return CLIENT;
        } else if (s) {
            return SERVER;
        } else {
            return FALSE;
        }
    }
}