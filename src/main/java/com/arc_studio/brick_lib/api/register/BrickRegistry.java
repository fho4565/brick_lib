package com.arc_studio.brick_lib.api.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BrickRegistry<T> extends RegistryType<T> implements Iterable<T> {
    HashMap<ResourceLocation, Supplier<T>> map = new HashMap<>();
    HashMap<ResourceLocation, Supplier<T>> toReg = new HashMap<>();

    private final ResourceKey<? extends Registry<T>> key;

    public BrickRegistry(ResourceKey<? extends Registry<T>> key) {
        this.key = key;
    }

    /**
     * 创建一个自定义的注册表
     * */
    public static <T extends Registry<T>> BrickRegistry<T> create(ResourceKey<T> key) {
        return new BrickRegistry<>(key);
    }

    @Nullable
    public ResourceLocation getKey(T value) {
        for (Map.Entry<ResourceLocation, Supplier<T>> entry : map.entrySet()) {
            if (entry.getValue().get().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Optional<ResourceKey<T>> getResourceKey(T value) {
        ResourceLocation resourceLocation = getKey(value);
        if (resourceLocation != null) {
            return Optional.of(ResourceKey.create(key, resourceLocation));
        }
        return Optional.empty();
    }

    @Nullable
    public T get(@Nullable ResourceKey<T> key) {
        if (key != null) {
            Supplier<T> supplier = map.get(key.location());
            if (supplier == null) {
                return null;
            }
            return supplier.get();
        }
        return null;
    }

    @Nullable
    public T get(@Nullable ResourceLocation name) {
        if (name != null) {
            Supplier<T> supplier = map.get(name);
            if (supplier == null) {
                return null;
            }
            return supplier.get();
        }
        return null;
    }

    public Optional<T> getOptional(@Nullable ResourceLocation name) {
        return Optional.ofNullable(this.get(name));
    }

    public Optional<T> getOptional(@Nullable ResourceKey<T> registryKey) {
        return Optional.ofNullable(this.get(registryKey));
    }

    public T getOrThrow(ResourceKey<T> key) {
        T object = this.get(key);
        if (object == null) {
            throw new IllegalStateException("Missing key in " + this.key() + ": " + key);
        } else {
            return object;
        }
    }

    public void remove(ResourceKey<T> key) {
        map.remove(key.location());
    }

    public void remove(ResourceLocation key) {
        map.remove(key);
    }

    public void remove(T value) {
        map.remove(getKey(value));
    }

    public ResourceKey<? extends Registry<T>> key() {
        return this.key;
    }

    public Set<ResourceLocation> keySet() {
        return this.map.keySet();
    }

    public Set<T> values() {
        return map.values().stream().map(Supplier::get).collect(Collectors.toSet());
    }

    public Set<T> toRegValues() {
        return toReg.values().stream().map(Supplier::get).collect(Collectors.toSet());
    }

    public void foreachAndClear(BiConsumer<ResourceLocation, T> consumer) {
        toReg.forEach((rl, supplier) -> consumer.accept(rl, supplier.get()));
        toReg.clear();
    }

    public void foreachValueAndClear(Consumer<T> consumer) {
        toReg.forEach((rl, supplier) -> consumer.accept(supplier.get()));
        toReg.clear();
    }

    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return map.entrySet().stream().map((Function<Map.Entry<ResourceLocation, Supplier<T>>, Map.Entry<ResourceKey<T>, T>>) t -> new AbstractMap.SimpleEntry<>(ResourceKey.create(key, t.getKey()), t.getValue().get())).collect(Collectors.toSet());
    }

    public Set<ResourceKey<T>> registryKeySet() {
        return map.keySet().stream().map(r -> ResourceKey.create(key, r)).collect(Collectors.toSet());
    }

    public T register(String name, Supplier<T> value) {
        return register(new ResourceLocation(name), value.get());
    }

    public T register(String name, T value) {
        return register(new ResourceLocation(name), value);
    }

    public T register(ResourceLocation name, T value) {
        return register(ResourceKey.create(key, name), value);
    }

    public T register(ResourceKey<T> key, T value) {
        map.put(key.location(), () -> value);
        toReg.put(key.location(), () -> value);
        return value;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.values().forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return values().spliterator();
    }

    public void clear() {
        toReg.clear();
    }

    @Override
    public ResourceKey<? extends Registry<T>> getKey() {
        return key;
    }
}
