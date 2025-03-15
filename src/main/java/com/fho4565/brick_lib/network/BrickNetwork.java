package com.fho4565.brick_lib.network;

import com.fho4565.brick_lib.BrickLib;
import com.fho4565.brick_lib.network.core.C2SPacket;
import com.fho4565.brick_lib.network.core.S2CPacket;
import com.fho4565.brick_lib.variables.PlayerVariablesSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;


public class BrickNetwork {
    public static BNetworkChannel CHANNEL;

    public static void register() {
        CHANNEL = new BNetworkChannel(new ResourceLocation(BrickLib.MOD_ID, "common"));
        CHANNEL.registerS2CPacket(PlayerVariablesSyncPacket.class,
                PlayerVariablesSyncPacket::encoder,
                PlayerVariablesSyncPacket::new,
                PlayerVariablesSyncPacket::handler);
    }

    public static <MSG extends C2SPacket> void sendToServer(MSG message) {
        CHANNEL.sendToServer(message);
    }

    public static <MSG extends S2CPacket> void sendToPlayer(MSG message, ServerPlayer player) {
        CHANNEL.sendToPlayer(message,player);
    }
    public static <MSG extends S2CPacket> void sendToAllPlayers(MSG message) {
        CHANNEL.sendToAllPlayers(message);
    }
    public static <MSG extends S2CPacket> void sendToPlayers(MSG message) {
        CHANNEL.sendToPlayer(message);
    }

}
