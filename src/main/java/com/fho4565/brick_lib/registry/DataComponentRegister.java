package com.fho4565.brick_lib.registry;

import com.fho4565.brick_lib.BrickLib;
import com.fho4565.brick_lib.item.CooldownDataComponent;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class DataComponentRegister {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, BrickLib.MOD_ID);
    public static final RegistryObject<DataComponentType<CooldownDataComponent>> CooldownType = DATA_COMPONENT_TYPES.register("coldown",
            () -> new DataComponentType<>() {
                @Override
                public @Nullable Codec<CooldownDataComponent> codec() {
                    return CooldownDataComponent.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, CooldownDataComponent> streamCodec() {
                    return CooldownDataComponent.STREAM_CODEC;
                }
            });
}
