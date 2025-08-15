package com.arc_studio.brick_lib.core.global_pack.files;

import com.arc_studio.brick_lib.BrickLib;

import com.arc_studio.brick_lib.api.core.interfaces.data.ISerializable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;

public final class PackMcMetaFile implements ISerializable<JsonObject> {
    private MutableComponent description = Component.empty();

    public PackMcMetaFile() {
    }

    public PackMcMetaFile(MutableComponent description) {
        this.description = description;
    }

    public static String fileName() {
        return "pack.gpmeta";
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.add("description", Component.Serializer.toJsonTree(description));
        return object;
    }

    @Override
    public boolean deserialize(JsonObject object) {
        try {
            this.description = Component.Serializer.fromJson(object.get("description"));
            return true;
        } catch (JsonSyntaxException e) {
            BrickLib.LOGGER.error(e.toString());
            return false;
        }
    }

    public Component description() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PackMcMetaFile) obj;
        return Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(serialize());
    }

}
