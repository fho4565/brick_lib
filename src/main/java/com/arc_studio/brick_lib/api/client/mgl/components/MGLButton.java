package com.arc_studio.brick_lib.api.client.mgl.components;


import com.arc_studio.brick_lib.api.json_function.InstructionExecutor;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public class MGLButton extends MGLComponent {
    protected static final Codec<MGLButton> CODEC = RecordCodecBuilder.create(instance->instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(MGLComponent::id),
            Codec.list(ExtraCodecs.JSON).fieldOf("onPress").forGetter(mglButton -> mglButton.onPress)
    ).apply(instance, MGLButton::new));
    protected final List<JsonElement> onPress;
    public MGLButton(ResourceLocation id,List<JsonElement> onPress) {
        super(id);
        this.onPress = onPress;
    }

    @Override
    public Codec<MGLButton> codec() {
        return CODEC;
    }

    public void onPress(){
        onPress.forEach(InstructionExecutor::execute);
    }
}
