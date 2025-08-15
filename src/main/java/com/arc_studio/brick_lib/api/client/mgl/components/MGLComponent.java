package com.arc_studio.brick_lib.api.client.mgl.components;

import com.arc_studio.brick_lib.api.client.mgl.MGLObject;
import net.minecraft.resources.ResourceLocation;

public abstract class MGLComponent extends MGLObject {
    protected MGLComponent parent;
    protected int x,y;
    protected int width,height;
    public MGLComponent(ResourceLocation id) {
        super(id);
    }
}
