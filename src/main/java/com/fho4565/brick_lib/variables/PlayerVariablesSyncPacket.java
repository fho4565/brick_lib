package com.fho4565.brick_lib.variables;

import com.fho4565.brick_lib.network.PacketContent;
import com.fho4565.brick_lib.network.core.S2CNetworkContext;
import com.fho4565.brick_lib.network.core.S2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import static com.fho4565.brick_lib.variables.BrickAttributeProvider.*;

public class PlayerVariablesSyncPacket extends S2CPacket {
    private final CompoundTag nbt;

    public PlayerVariablesSyncPacket(INBTSerializable<CompoundTag> cap) {
        this.nbt = cap.serializeNBT();
    }

    public PlayerVariablesSyncPacket(PacketContent buf) {
        this.nbt = buf.readNBT();
    }

    @Override
    public void encoder(PacketContent buf) {
        buf.writeNBT(nbt);
    }

    /**
     * @param context
     */
    @Override
    public void clientHandle(S2CNetworkContext context) {
        integers.clear();
        doubles.clear();
        strings.clear();
        CompoundTag intList = nbt.getCompound("integers");
        intList.getAllKeys().forEach(string -> integers.put(string, intList.getInt(string)));

        CompoundTag doubleList = nbt.getCompound("doubles");
        doubleList.getAllKeys().forEach(string -> doubles.put(string, doubleList.getDouble(string)));

        CompoundTag stringList = nbt.getCompound("strings");
        stringList.getAllKeys().forEach(string -> strings.put(string, stringList.getString(string)));
    }
}
