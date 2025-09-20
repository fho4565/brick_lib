package com.arc_studio.brick_lib.api.register;

import com.arc_studio.brick_lib.BrickLib;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Brick Lib注册管理器，使用这个类来注册
 *
 * @author fho4565*/
public class BrickRegisterManager {
    private static final HashSet<VanillaRegistryType<?>> VANILLA_REGISTRY_TYPES = new HashSet<>();

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
            VANILLA_REGISTRY_TYPES.add(new VanillaRegistryType<>(value.getClass(), key.getVanillaRegistry(), hashMap));
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

    public static<T> void register(BrickRegistry<T> key, Supplier<T> value) {
        ResourceLocation regLocation = BrickLib.createBrickRL(key.key().location().toLanguageKey() + key.count());
        System.out.println("regLocation = " + regLocation);
        register(key, regLocation,value);
    }

    public static Set<VanillaRegistryType<?>> vanillaEntries() {
        return VANILLA_REGISTRY_TYPES;
    }
}
