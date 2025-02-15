package com.fho4565.brick_lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet {
    public abstract void encoder(FriendlyByteBuf buf);

    public abstract void handler(Supplier<NetworkEvent.Context> ctx);
}
