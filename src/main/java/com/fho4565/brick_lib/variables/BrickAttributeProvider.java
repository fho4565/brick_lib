package com.fho4565.brick_lib.variables;

import com.fho4565.brick_lib.BrickLib;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class BrickAttributeProvider implements ICapabilityProvider, NonNullSupplier<BrickAttributeProvider.BrickAttribute>, ICapabilitySerializable<CompoundTag> {

    private final BrickAttribute capability;
    protected static final HashMap<String,Integer> integers = new HashMap<>();
    protected static final HashMap<String,Double> doubles = new HashMap<>();
    protected static final HashMap<String,String> strings = new HashMap<>();

    public BrickAttributeProvider(Entity entity) {
        this.capability = new BrickAttribute(entity);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == BrickCapability.BRICK_ATTRIBUTE ? LazyOptional.of(this).cast() : LazyOptional.empty();
    }

    @Override
    public @NotNull BrickAttribute get() {
        return this.capability;
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.capability.deserializeNBT(nbt);
    }

    public static class BrickCapability {
        public static final Capability<BrickAttribute> BRICK_ATTRIBUTE = CapabilityManager.get(new CapabilityToken<>() {
        });

    }

    public static class BrickAttribute implements INBTSerializable<CompoundTag> {
        protected final ConcurrentHashMap<String,Integer> integers = new ConcurrentHashMap<>();
        protected final ConcurrentHashMap<String,Double> doubles = new ConcurrentHashMap<>();
        protected final ConcurrentHashMap<String,String> strings = new ConcurrentHashMap<>();

        protected BrickAttribute(Entity entity) {
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
}
