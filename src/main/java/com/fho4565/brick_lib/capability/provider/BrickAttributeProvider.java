package com.fho4565.brick_lib.capability.provider;

import com.fho4565.brick_lib.capability.BrickCapabilities;
import com.fho4565.brick_lib.capability.entity.BrickAttribute;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class BrickAttributeProvider implements ICapabilityProvider, NonNullSupplier<BrickAttribute>, ICapabilitySerializable<CompoundTag> {

    private final BrickAttribute capability;
    public static final HashMap<String,Integer> integers = new HashMap<>();
    public static final HashMap<String,Double> doubles = new HashMap<>();
    public static final HashMap<String,String> strings = new HashMap<>();

    public BrickAttributeProvider(Entity entity) {
        this.capability = new BrickAttribute(entity);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == BrickCapabilities.BRICK_ATTRIBUTE ? LazyOptional.of(this).cast() : LazyOptional.empty();
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
}
