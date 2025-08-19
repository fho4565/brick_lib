package com.arc_studio.brick_lib.globalpack.file_types;

import com.arc_studio.brick_lib.core.global_pack.files.GlobalPackFileType;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ItemType extends GlobalPackFileType {
    public static final Codec<ItemType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("stackSize", 64).forGetter(ItemType::stackSize)
        ).apply(instance, integer -> {
        ItemType itemType = new ItemType();
        itemType.stackSize = integer;
        return itemType;
    }));
    private int stackSize = 64;

    public ItemType() {
        super("item", ".item");
    }

    public int stackSize() {
        return stackSize;
    }

    public ItemType setStackSize(int stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    @Override
    public JsonObject createEmpty() {
        return CODEC.encodeStart(JsonOps.INSTANCE,new ItemType().setStackSize(1)).result().orElseThrow().getAsJsonObject();
    }
}
