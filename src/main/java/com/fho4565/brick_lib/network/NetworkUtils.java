package com.fho4565.brick_lib.network;

import com.fho4565.brick_lib.BrickLib;
import com.fho4565.brick_lib.network.packets.ServerAttributeSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


public class NetworkUtils {
    public static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;
    private static int id;

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(BrickLib.MOD_ID, "common"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        INSTANCE.messageBuilder(ServerAttributeSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ServerAttributeSyncPacket::new)
                .encoder(ServerAttributeSyncPacket::encoder)
                .consumerMainThread(ServerAttributeSyncPacket::handler)
                .add();
/*        INSTANCE.messageBuilder(ScreenShow.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ScreenShow::new)
                .encoder(ScreenShow::encoder)
                .consumerMainThread(ScreenShow::handler)
                .add();*/
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, Player player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
    }
    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.PLAYER.noArg(), message);
    }
    public static <MSG> void sendToPlayers(MSG message, ServerPlayer... player) {
        for (ServerPlayer p : player) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), message);
        }
    }

}
