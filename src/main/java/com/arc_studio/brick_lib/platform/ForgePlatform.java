package com.arc_studio.brick_lib.platform;



//? if (forge) || oldnf {
/*import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.api.network.PacketContent;
import com.arc_studio.brick_lib.api.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib.api.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib.api.network.type.C2SPacket;
import com.arc_studio.brick_lib.api.network.type.PacketConfig;
import com.arc_studio.brick_lib.api.network.type.S2CPacket;
import com.arc_studio.brick_lib.api.network.type.SACPacket;
import com.arc_studio.brick_lib.register.BrickRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;
/^//? if forge && < 1.20.4 {
/^¹import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
¹^///?}

//? if forge && >=1.20.4 {
/^¹import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.*;
¹^///?}

//? if neoforge {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.events.lifecycle.FMLCommonSetupEvent;
//?}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

^/*///?}
@SuppressWarnings({"unchecked", "rawtypes"})
public class ForgePlatform {
    //? if (forge) || oldnf {
    /*protected static final String PROTOCOL_VERSION = "0";
    public static final SimpleChannel c2sChannel = //? if < 1.20.4 {
    /^NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BrickLib.MOD_ID, "c2s"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    ^///?} else {
    ChannelBuilder.named(BrickLib.createBrickRL("c2s"))
            .networkProtocolVersion(1)
            .serverAcceptedVersions(Channel.VersionTest.exact(1))
            .clientAcceptedVersions(Channel.VersionTest.exact(1))
            .simpleChannel();
    //?}
    public static final SimpleChannel s2cChannel = //? if <1.20.4 {
    /^NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BrickLib.MOD_ID, "s2c"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    ^///?} else {
            ChannelBuilder.named(BrickLib.createBrickRL("s2c"))
                    .networkProtocolVersion(1)
                    .serverAcceptedVersions(Channel.VersionTest.exact(1))
                    .clientAcceptedVersions(Channel.VersionTest.exact(1))
                    .simpleChannel();
    //?}
    protected static final AtomicInteger c2sID = new AtomicInteger(0);
    protected static final AtomicInteger s2cID = new AtomicInteger(0);

    @SubscribeEvent
    public static void onNetwork(FMLCommonSetupEvent event) {
        BrickRegistries.NETWORK_PACKET.foreachValueAndClear(packetConfig -> {
            if (packetConfig instanceof PacketConfig.C2S c2S) {
                SimpleChannel.MessageBuilder<? extends C2SPacket> builder = c2sChannel.messageBuilder(c2S.type(), c2sID.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER)
                        .encoder((msg, buf) -> c2S.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                        .decoder(buf -> c2S.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (c2S.netHandle()) {
                    builder.consumerNetworkThread((msg, contextSupplier) -> {
                        c2S.packetHandler().accept(msg, new C2SNetworkContext(/^? <1.20.4 {^/ /^contextSupplier.get().getSender() ^//^?} else {^/contextSupplier.getSender()/^?}^/));
                    });
                } else {
                    builder.consumerMainThread((msg, contextSupplier) -> c2S.packetHandler().accept(msg, new C2SNetworkContext(/^? <1.20.4 {^/ /^contextSupplier.get().getSender() ^//^?} else {^/contextSupplier.getSender()/^?}^/)));
                }
                builder.add();
            } else if (packetConfig instanceof PacketConfig.S2C s2C) {
                SimpleChannel.MessageBuilder<? extends S2CPacket> builder = s2cChannel.messageBuilder(s2C.type(), s2cID.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT)
                        .encoder((msg, buf) -> s2C.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                        .decoder(buf -> s2C.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (s2C.netHandle()) {
                    builder.consumerNetworkThread((msg, contextSupplier) -> {
                        s2C.packetHandler().accept(msg, new S2CNetworkContext());
                    });
                } else {
                    builder.consumerMainThread((msg, contextSupplier) -> {
                        s2C.packetHandler().accept(msg, new S2CNetworkContext());
                    });
                }
                builder.add();
            } else if (packetConfig instanceof PacketConfig.SAC sac) {
                SimpleChannel.MessageBuilder<? extends SACPacket> s2cBuilder = s2cChannel
                        .messageBuilder(sac.type(), s2cID.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT)
                        .encoder((msg, buf) -> sac.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                        .decoder(buf -> sac.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (sac.netHandle()) {
                    s2cBuilder.consumerNetworkThread((msg, contextSupplier) -> {
                        sac.clientHandler().accept(msg, new S2CNetworkContext());
                    });
                } else {
                    s2cBuilder.consumerMainThread((msg, contextSupplier) -> {
                        sac.clientHandler().accept(msg, new S2CNetworkContext());
                    });
                }
                s2cBuilder.add();
                SimpleChannel.MessageBuilder<? extends SACPacket> c2sBuilder = c2sChannel
                        .messageBuilder(sac.type(), c2sID.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER)
                        .encoder((msg, buf) -> sac.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                        .decoder(buf -> sac.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (sac.netHandle()) {
                    c2sBuilder.consumerNetworkThread((msg, contextSupplier) -> {
                        sac.serverHandler().accept(msg, new C2SNetworkContext(/^? <1.20.4 {^/ /^contextSupplier.get().getSender() ^//^?} else {^/contextSupplier.getSender()/^?}^/));
                    });
                } else {
                    c2sBuilder.consumerMainThread((msg, contextSupplier) -> sac.serverHandler().accept(msg, new C2SNetworkContext(/^? <1.20.4 {^/ /^contextSupplier.get().getSender() ^//^?} else {^/contextSupplier.getSender()/^?}^/)));
                }
                c2sBuilder.add();
            }

        });
    }
    *///?}
}
