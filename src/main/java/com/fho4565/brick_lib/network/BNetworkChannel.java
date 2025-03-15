package com.fho4565.brick_lib.network;

import com.fho4565.brick_lib.network.core.C2SNetworkContext;
import com.fho4565.brick_lib.network.core.C2SPacket;
import com.fho4565.brick_lib.network.core.S2CNetworkContext;
import com.fho4565.brick_lib.network.core.S2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 一个网络通道类，包装了SimpleChannel并将不同端位的包分开
 */
public class BNetworkChannel {
    private final SimpleChannel channel;
    private final String PROTOCOL_VERSION = "0";
    private final ResourceLocation resourceLocation;
    private int id = 0;

    public BNetworkChannel(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        channel = NetworkRegistry.newSimpleChannel(
                resourceLocation,
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
    }

    public <MSG extends C2SPacket> void registerC2SPacket(Class<MSG> type, BiConsumer<MSG, PacketContent> encoder, Function<PacketContent, MSG> decoder, BiConsumer<MSG, C2SNetworkContext> handler) {
        channel.messageBuilder(type, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder((msg, buf) -> encoder.accept(msg, new PacketContent(buf)))
                .decoder(buf -> decoder.apply(new PacketContent(buf)))
                .consumerMainThread((msg, contextSupplier) -> handler.accept(msg, new C2SNetworkContext(contextSupplier.get())))
                .add();
    }

    public <MSG extends S2CPacket> void registerS2CPacket(Class<MSG> type, BiConsumer<MSG, PacketContent> encoder, Function<PacketContent, MSG> decoder, BiConsumer<MSG, S2CNetworkContext> handler) {
        channel.messageBuilder(type, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder((msg, buf) -> encoder.accept(msg, new PacketContent(buf)))
                .decoder(buf -> decoder.apply(new PacketContent(buf)))
                .consumerMainThread((msg, contextSupplier) -> handler.accept(msg, new S2CNetworkContext(contextSupplier.get())))
                .add();
    }

    public <MSG extends C2SPacket> void sendToServer(MSG packet) {
        channel.sendToServer(packet);
    }

    public <MSG extends S2CPacket> void sendToPlayer(MSG packet, ServerPlayer... serverPlayers) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
        }
    }

    public <MSG extends S2CPacket> void sendToAllPlayers(MSG packet) {
        channel.send(PacketDistributor.PLAYER.noArg(), packet);
    }

    public ResourceLocation resourceLocation() {
        return resourceLocation;
    }
}
