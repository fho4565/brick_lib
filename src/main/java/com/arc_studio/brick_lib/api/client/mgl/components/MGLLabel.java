package com.arc_studio.brick_lib.api.client.mgl.components;

import com.arc_studio.brick_lib.api.client.mgl.MGLObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class MGLLabel extends MGLTextComponent {
    protected static final Codec<MGLLabel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(MGLComponent::id),
            ExtraCodecs.COMPONENT.optionalFieldOf("text",Component.empty()).forGetter(mglLabel -> mglLabel.text)
    ).apply(instance, MGLLabel::new));

    public MGLLabel(ResourceLocation id) {
        super(id);
    }

    public MGLLabel(ResourceLocation id, Component text) {
        super(id);
        this.text = text;
    }

    @Override
    public Codec<? extends MGLObject> codec() {
        return CODEC;
    }
}
