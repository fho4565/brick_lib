package com.fho4565.brick_lib.network.packets;


import com.fho4565.brick_lib.capability.BrickCapabilities;
import com.fho4565.brick_lib.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

import static com.fho4565.brick_lib.capability.provider.BrickAttributeProvider.*;

public class ServerAttributeSyncPacket extends Packet {
    private final CompoundTag nbt;

    public ServerAttributeSyncPacket(INBTSerializable<CompoundTag> cap) {
        this.nbt = cap.serializeNBT();
    }

    public ServerAttributeSyncPacket(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    @Override
    public void encoder(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        integers.clear();
        doubles.clear();
        strings.clear();
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
        ctx.get().enqueueWork(() -> {
            BrickCapabilities.getBrickAttribute(Objects.requireNonNull(ctx.get().getSender())).deserializeNBT(nbt);
        });
        ctx.get().setPacketHandled(true);
    }
}
