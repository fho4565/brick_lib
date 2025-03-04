package com.fho4565.brick_lib.network;

import com.fho4565.brick_lib.network.core.C2SNetworkContext;
import com.fho4565.brick_lib.network.core.C2SPacket;
import com.fho4565.brick_lib.network.core.S2CNetworkContext;
import com.fho4565.brick_lib.network.core.S2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
import net.minecraftforge.network.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
/**
 * 一个网络通道类，包装了SimpleChannel并将不同端位的包分开
 * */
public class BNetworkChannel {
    private final SimpleChannel channel;
    private int id = 0;
    private final ResourceLocation resourceLocation;
    public BNetworkChannel(ResourceLocation resourceLocation){
        this.resourceLocation = resourceLocation;
        channel = ChannelBuilder.named(resourceLocation)
                .networkProtocolVersion(1)
                .serverAcceptedVersions(Channel.VersionTest.exact(1))
                .clientAcceptedVersions(Channel.VersionTest.exact(1))
                .simpleChannel()
        ;
    }
    public<MSG extends C2SPacket> void registerC2SPacket(Class<MSG> type, BiConsumer<MSG,PacketContent> encoder, Function<PacketContent,MSG> decoder, BiConsumer<MSG, C2SNetworkContext> handler){
        channel.messageBuilder(type, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder((msg, buf) -> encoder.accept(msg,new PacketContent(buf)))
                .decoder(buf -> decoder.apply(new PacketContent(buf)))
                .consumerMainThread((msg, contextSupplier) -> handler.accept(msg,new C2SNetworkContext(contextSupplier)))
                .add();
    }
    public<MSG extends S2CPacket> void registerS2CPacket(Class<MSG> type, BiConsumer<MSG,PacketContent> encoder, Function<PacketContent,MSG> decoder, BiConsumer<MSG, S2CNetworkContext> handler){
        channel.messageBuilder(type, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder((msg, buf) -> encoder.accept(msg,new PacketContent(buf)))
                .decoder(buf -> decoder.apply(new PacketContent(buf)))
                .consumerMainThread((msg, contextSupplier) -> handler.accept(msg,new S2CNetworkContext(contextSupplier)))
                .add();
    }
    public<MSG extends C2SPacket> void sendToServer(MSG packet){
        channel.send(packet,PacketDistributor.SERVER.noArg());
    }
    public<MSG extends S2CPacket> void sendToPlayer(MSG packet,ServerPlayer ...serverPlayers){
        for (ServerPlayer serverPlayer : serverPlayers) {
            channel.send(packet,PacketDistributor.PLAYER.with(serverPlayer));
        }
    }
    public<MSG extends S2CPacket> void sendToAllPlayers(MSG packet){
        channel.send(packet,PacketDistributor.PLAYER.noArg());
    }

    public ResourceLocation resourceLocation() {
        return resourceLocation;
    }
}
