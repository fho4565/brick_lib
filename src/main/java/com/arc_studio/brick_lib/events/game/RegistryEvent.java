package com.arc_studio.brick_lib.events.game;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class RegistryEvent extends BaseEvent {
    private final ResourceKey<? extends Registry<?>> key;
    private final Registry<?> registry;

    public RegistryEvent(ResourceKey<? extends Registry<?>> key, Registry<?> registry) {
        this.key = key;
        this.registry = registry;
    }

    public Registry<?> getRegistry() {
        return registry;
    }

    public ResourceKey<? extends Registry<?>> getKey() {
        return key;
    }
}
