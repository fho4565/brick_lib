package com.fho4565.brick_lib.capability;

import com.fho4565.brick_lib.capability.entity.BrickAttribute;
import com.fho4565.brick_lib.network.NetworkUtils;
import com.fho4565.brick_lib.network.packets.ServerAttributeSyncPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class BrickCapabilities {
    public static final Capability<BrickAttribute> BRICK_ATTRIBUTE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static BrickAttribute getBrickAttribute(Player player) {
        return player.getCapability(BRICK_ATTRIBUTE).orElse(new BrickAttribute(player));
    }

    public static void serverSyncAttribute(Player player) {
        NetworkUtils.sendToPlayer(new ServerAttributeSyncPacket(getBrickAttribute(player)), player);
    }

}
