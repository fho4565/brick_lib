package com.fho4565.brick_lib.capability.entity;

import com.fho4565.brick_lib.BrickLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.concurrent.ConcurrentHashMap;

public class BrickAttribute implements INBTSerializable<CompoundTag> {
    public final ConcurrentHashMap<String,Integer> integers = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String,Double> doubles = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String,String> strings = new ConcurrentHashMap<>();

    public BrickAttribute(Entity entity) {
        if (!(entity instanceof Player)) {
            BrickLib.LOGGER.error("Cannot create attribute for {}", entity.getType().getDescriptionId());
        }
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        CompoundTag integerTag = new CompoundTag();
        integers.forEach(integerTag::putInt);
        nbt.put("integers",integerTag);
        CompoundTag doubleTag = new CompoundTag();
        doubles.forEach(doubleTag::putDouble);
        nbt.put("doubles",doubleTag);
        CompoundTag stringTag = new CompoundTag();
        strings.forEach(stringTag::putString);
        nbt.put("strings",stringTag);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag intList = (CompoundTag) nbt.get("integers");
        if (intList != null) {
            intList.getAllKeys().forEach(string -> integers.put(string,intList.getInt(string)));
        }
        CompoundTag doubleList = (CompoundTag) nbt.get("doubles");
        if (doubleList != null) {
            doubleList.getAllKeys().forEach(string -> doubles.put(string,doubleList.getDouble(string)));
        }

        CompoundTag stringList = (CompoundTag) nbt.get("strings");
        if (stringList != null) {
            stringList.getAllKeys().forEach(string -> strings.put(string,stringList.getString(string)));
        }
    }
}
