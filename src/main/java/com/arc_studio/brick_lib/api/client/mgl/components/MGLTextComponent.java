package com.arc_studio.brick_lib.api.client.mgl.components;

import com.arc_studio.brick_lib.api.client.mgl.MGLObject;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MGLTextComponent extends MGLComponent{
    protected Component placeHolderText;
    protected String echoText;
    protected Component text;
    public MGLTextComponent(ResourceLocation id) {
        super(id);
    }

    @Override
    public Codec<? extends MGLObject> codec() {
        return null;
    }
}
