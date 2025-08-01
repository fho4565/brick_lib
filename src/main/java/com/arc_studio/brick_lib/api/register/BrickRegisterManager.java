package com.arc_studio.brick_lib.api.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Brick Lib注册管理器，使用这个类来注册
 * */
public class BrickRegisterManager {
    private static final HashSet<VanillaRegistryType<?>> vanillaRegistryTypes = new HashSet<>();

    private BrickRegisterManager() {
    }

    public record VanillaRegistryType<T>(Class<?> type, Registry<T> key, HashMap<ResourceLocation, Supplier<T>> value) {

    }

    /**
     * 注册到原版注册表中
     * */
    public static <T> void register(VanillaRegistry<T> key, ResourceLocation id, Supplier<T> value) {
        if(!key.getVanillaRegistry().containsKey(id)) {
            HashMap<ResourceLocation, Supplier<T>> hashMap = new HashMap<>();
            hashMap.putIfAbsent(id, value);
            vanillaRegistryTypes.add(new VanillaRegistryType<>(value.getClass(), key.getVanillaRegistry(), hashMap));
        }
    }

    /**
     * 注册到非原版注册表中
     * */
    public static<T> void register(BrickRegistry<T> key, ResourceLocation id, Supplier<T> value) {
        if (key.get(id) == null) {
            key.register(id,value.get());
        }
    }

    public static Set<VanillaRegistryType<?>> vanillaEntries() {
        return vanillaRegistryTypes;
    }
}
