package com.arc_studio.brick_lib.api.client.mgl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public abstract class MGLObject {
    protected final ResourceLocation id;

    public MGLObject(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation id() {
        return id;
    }

    public abstract Codec<? extends MGLObject> codec();
}
