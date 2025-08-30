package com.arc_studio.brick_lib.tools;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
//? if >= 1.20.6 {
/*import net.minecraft.network.chat.ComponentSerialization;
*///?}

import static net.minecraft.util.ExtraCodecs.JSON;

public class Codecs {
    public static final Codec<Component> COMPONENT = JSON.flatXmap(jsonElement -> {
        try {
            //? if >= 1.20.6 {
            /*return DataResult.success(ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new));
            *///?} else {
            return DataResult.success(Component.Serializer.fromJson(jsonElement));
            //?}
        } catch (JsonParseException var2) {
            return DataResult.error(var2::getMessage);
        }
    },component -> {
        try {
            //? if >= 1.20.6 {
            /*return DataResult.success(ComponentSerialization.CODEC.encodeStart(Constants.currentServer().registryAccess().createSerializationContext(JsonOps.INSTANCE), component).getOrThrow(JsonParseException::new));
            *///?} else {
            return DataResult.success(Component.Serializer.toJsonTree(component));
            //?}
        } catch (IllegalArgumentException var2) {
            return DataResult.error(var2::getMessage);
        }
	});
}
