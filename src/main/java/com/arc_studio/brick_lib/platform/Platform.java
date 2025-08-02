package com.arc_studio.brick_lib.platform;

import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.api.core.Version;
import com.arc_studio.brick_lib.api.network.PacketContent;
import com.arc_studio.brick_lib.api.tools.Constants;
import com.arc_studio.brick_lib.api.core.PlatformType;
import com.arc_studio.brick_lib.api.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib.api.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib.api.network.type.*;
import com.arc_studio.brick_lib.api.register.BrickRegisterManager;
//? if fabric {
import com.arc_studio.brick_lib.register.BrickRegistries;
import net.fabricmc.api.EnvType;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.ModContainer;
//? if >= 1.20.6 {
/*import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
*///?} else {
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
//?}

//?}


import com.arc_studio.brick_lib.api.tools.SideExecutor;
import io.netty.util.AttributeKey;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
//? if >=1.20.6 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.arc_studio.brick_lib.compatibility.V1201;
*///?}
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
//? if forge {
/*import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.network.*;
import net.minecraftforge.registries.RegisterEvent;
*///?}
//? if neoforge {
/*import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;
//? if <1.20.4 {
/^import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.ModList;
^///?} else if <1.20.6 {

//?} else {
/^import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModContainer;
^///?}
*///?}


import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Brick Lib使用的平台类，Mod作者不应使用这个类
 */
@ApiStatus.Internal
@SuppressWarnings("unchecked")
//? if forge {
/*@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
*///?}
//? if neoforge {
/*//? if < 1.20.6 {
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
 //?} else {
/^@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
^///?}
*///?}
public class Platform {
    //? if !fabric {
    /*private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");
    *///?}
    public static String[] args;

    public static Path getConfigDirectory() {
        //? if fabric {
        return FabricLoader.getInstance().getConfigDir();
         //?} else if forge {
        /*return FMLPaths.CONFIGDIR.get();
        *///?} else if neoforge {
        /*return FMLPaths.CONFIGDIR.get();
         *///?}
    }

    public static Path getServerConfigDirectory() {
        //? if fabric {
        final Path serverConfig = Constants.currentServer().getWorldPath(LevelResource.ROOT).resolve("serverconfig");
        if (!Files.isDirectory(serverConfig)) {
            try {
                Files.createDirectories(serverConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return serverConfig;
        //?} else {
        /*return Constants.currentServer().getWorldPath(SERVERCONFIG);
        *///?}
    }

    public static void scanAllModClasses(Consumer<Class<?>> consumer) {
        //? if fabric {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            try {
                for (Path path : mod.getOrigin().getPaths()) {
                    try {
                        if (Files.isDirectory(path)) {
                            try (Stream<Path> stream = Files.walk(path)) {
                                stream.forEach(path1 -> {
                                    String p = path1.toString();
                                    if (!p.endsWith("package-info.class") && p.endsWith(".class")) {
                                        String className = p.substring(1)
                                                .replace('/', '.')
                                                .replace(".class", "");
                                        try {
                                            consumer.accept(ClassUtils.getClass(className, false));
                                        } catch (Exception | Error ignored) {

                                        }
                                    }
                                });
                            }
                        } else {
                            try (FileSystem fs = FileSystems.newFileSystem(path, (ClassLoader) null)) {
                                Path root = fs.getRootDirectories().iterator().next();
                                try (Stream<Path> stream = Files.walk(root)) {
                                    stream.forEach(path1 -> {
                                        String p = path1.toString();
                                        if (!p.endsWith("package-info.class") && p.endsWith(".class")) {
                                            String className = p.substring(1)
                                                    .replace('/', '.')
                                                    .replace(".class", "");
                                            try {
                                                consumer.accept(ClassUtils.getClass(className, false));
                                            } catch (Exception | Error ignored) {

                                            }
                                        }
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        BrickLib.LOGGER.error(e.toString());
                    }
                }
            } catch (Exception e) {
                BrickLib.LOGGER.error(e.toString());
            }
        }
        //?} else {
        /*ModList.get().getAllScanData().forEach(modFileScanData -> {
            for (ModFileScanData.ClassData classData : modFileScanData.getClasses()) {
                String className = classData.clazz().getClassName();
                try {
                    consumer.accept(ClassUtils.getClass(className, false));
                } catch (Exception | Error ignored) {
                }
            }
        });
        *///?}
    }

    public static boolean itemEqual(ItemStack first, ItemStack second, boolean compareDamageValue) {
        //? if fabric {
        if (first.isEmpty()) {
            return second.isEmpty();
        } else {
            if (!compareDamageValue) {
                ItemStack i1 = first.copy();
                ItemStack i2 = second.copy();
                i1.setDamageValue(0);
                i2.setDamageValue(0);
                return ItemStack.matches(i1, i2);
            } else {
                return ItemStack.matches(first, second);
            }
        }
        //?} else {
        /*if (first.isEmpty()) {
            return second.isEmpty();
        } else {
            ItemStack i1 = first.copy();
            ItemStack i2 = second.copy();
            if (!compareDamageValue) {
                i1.setDamageValue(0);
                i2.setDamageValue(0);
            }
            //? if >= 1.20.6 {
            /^return V1201.equals(i1, i2);
            ^///?} else {
            return ItemStack.isSameItem(i1,i2);
            //?}
        }
        *///?}
    }

    public static Path versionPath() {
        //? if fabric {
        return FabricLoader.getInstance().getGameDir();
         //?} else {
        /*return FMLPaths.GAMEDIR.get();
        *///?}
    }

    public static boolean isDev() {
        //? if fabric {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
         //?} else {
        /*return !FMLEnvironment.production;
        *///?}
    }

    public static String[] launchArgs() {
        //? if fabric {
        return FabricLoader.getInstance().getLaunchArguments(true);
         //?} else {
        /*return args;
        *///?}
    }

    public static PlatformType platform() {
        //? if fabric {
        return PlatformType.FABRIC;
        //?} else if forge {
        /*return PlatformType.FORGE;
        *///?} else if neoforge {
        /*return PlatformType.NEO_FORGE;
        *///?}
    }

    public static Version gameVersion(){
        MutableObject<Version> version = new MutableObject<>();
        SideExecutor.runSeparately(() -> version.setValue(Version.parse(Minecraft.getInstance().getLaunchedVersion())),
                () -> version.setValue(Version.parse(Constants.currentServer().getServerVersion())));
        return version.getValue();
    }
    public static String gameVersionStr(){
        MutableObject<String> version = new MutableObject<>();
        SideExecutor.runSeparately(() -> version.setValue(Minecraft.getInstance().getLaunchedVersion()),
                () -> version.setValue(Constants.currentServer().getServerVersion()));
        return version.getValue();
    }


    public static boolean isClient() {
        //? if fabric {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
         //?} else {
        /*return FMLEnvironment.dist.isClient();
        *///?}
    }


    public static boolean isServer() {
        //? if fabric {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
         //?} else {
        /*return FMLEnvironment.dist.isDedicatedServer();
        *///?}
    }


    public static <T extends S2CNetworkContext> void enqueueWork(T context, Runnable runnable) {
        //? if fabric {
        Minecraft.getInstance().execute(runnable);
         //?} else if forge {
        /*BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(NetworkDirection.PLAY_TO_CLIENT.getReceptionSide());
        if (!executor.isSameThread()) {
            executor.submitAsync(runnable);
        } else {
            runnable.run();
            CompletableFuture.completedFuture(null);
        }
        *///?} else if neoforge {
        /*Minecraft instance = Minecraft.getInstance();
        if (instance.isSameThread()) {
            CompletableFuture.completedFuture(null);
        }
        instance.submit(runnable).exceptionally(
                ex -> {
                    BrickLib.LOGGER.error("Failed to process a synchronized task of the payload: %s".formatted(context.direction()), ex);
                    return null;
                }
        );
        *///?}
    }

    public static <T extends C2SNetworkContext> void enqueueWork(T context, Runnable runnable) {
        //? if fabric {
        MinecraftServer server = context.getSender().getServer();
        if (server != null) {
            server.execute(runnable);
        }
        //?} else if forge {
        /*BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(NetworkDirection.PLAY_TO_SERVER.getReceptionSide());
        if (!executor.isSameThread()) {
            executor.submitAsync(runnable);
        } else {
            runnable.run();
            CompletableFuture.completedFuture(null);
        }
        *///?} else if neoforge {
        /*MinecraftServer instance = Constants.currentServer();
        if (instance.isSameThread()) {
            CompletableFuture.completedFuture(null);
        }
        instance.submit(runnable).exceptionally(
                ex -> {
                    BrickLib.LOGGER.error("Failed to process a synchronized task of the payload: %s".formatted(context.direction()), ex);
                    return null;
                }
        );
        *///?}
    }


    public static void sendToPlayer(ICHandlePacket packet, Iterable<ServerPlayer> serverPlayers) {
        //? if fabric {
        
        ResourceLocation id = Optional.ofNullable(packet.id()).orElseGet(() -> new ResourceLocation(BrickLib.MOD_ID, packet.getClass().getName().replace(".", "_").toLowerCase() + "_s2c"));
        
        //?}
        for (ServerPlayer serverPlayer : serverPlayers) {
            //? if fabric {
            //? if < 1.20.6 {
            ServerPlayNetworking.send(serverPlayer, id, packet.getEncodedPacketContent(new PacketContent()).friendlyByteBuf());
            //?} else {
            /*ServerPlayNetworking.send(serverPlayer, packet);
            *///?}
            //?} else if forge {
            /*ForgePlatform.s2cChannel.send(/^? <1.20.4 {^/ /^PacketDistributor.PLAYER.with(() -> serverPlayer), packet ^//^?} else {^/packet,PacketDistributor.PLAYER.with(serverPlayer)/^?}^/);
            *///?} else if neoforge {
            /*//? if <=1.20.4 {
            PacketDistributor.PLAYER.with(serverPlayer).send(packet);
            //?} else {
            /^PacketDistributor.sendToPlayer(serverPlayer,packet);
            ^///?}
            *///?}
        }
    }

    public static void sendToAllPlayers(ICHandlePacket packet) {
        sendToPlayer(packet,Constants.currentServer().getPlayerList().getPlayers());
    }


    public static void sendToServer(ISHandlePacket packet) {
        //? if fabric {
        //? if < 1.20.6 {
        ResourceLocation id = Optional.ofNullable(packet.id()).orElseGet(() -> new ResourceLocation(BrickLib.MOD_ID, packet.getClass().getName().replace(".", "_").toLowerCase() + "_c2s"));
        ClientPlayNetworking.send(id, packet.getEncodedPacketContent(new PacketContent()).friendlyByteBuf());
        //?} else {
        /*ClientPlayNetworking.send(packet);
        *///?}
        //?} else if forge {
        
        /*ForgePlatform.c2sChannel/^? <1.20.4 {^/ /^.sendToServer(packet) ^//^?} else {^/.send(packet,PacketDistributor.SERVER.noArg())/^?}^/;
        *///?} else if neoforge {
        /*//? if <=1.20.4 {
        PacketDistributor.SERVER.noArg().send(packet);
        //?} else {
        /^System.out.println("About to send "+packet.id());
        PacketDistributor.sendToServer(packet);
        ^///?}
        *///?}
    }


    public static <T> void brickFinalizeRegistry() {
        //? if fabric {
        //? if >= 1.20.6 {
        /*BrickRegistries.NETWORK_PACKET.foreachValueAndClear(packetConfig -> {
            if (packetConfig instanceof PacketConfig.C2S c2S) {
                c2s(c2S);
            } else if (packetConfig instanceof PacketConfig.S2C s2C) {
                s2c(s2C);
            } else if (packetConfig instanceof PacketConfig.SAC sac) {
                System.out.println("=====================================================");
                System.out.println(sac.c2sID());
                System.out.println(sac.s2cID());
                sac(sac);
            }
        });
        *///?} else {
        BrickRegistries.NETWORK_PACKET.foreachValueAndClear(packetConfig -> {
            if (packetConfig instanceof PacketConfig.C2S c2S) {
                ServerPlayNetworking.registerGlobalReceiver(c2S.id(), (server, player, handler, buf, responseSender) -> {
                    Object applied = c2S.decoder().apply(new PacketContent(buf));
                    if (c2S.netHandle()) {
                        c2S.packetHandler().accept(applied, new C2SNetworkContext(player));
                    } else {
                        server.execute(() -> c2S.packetHandler().accept(applied, new C2SNetworkContext(player)));
                    }
                });
            } else if (packetConfig instanceof PacketConfig.S2C s2C) {
                ClientPlayNetworking.registerGlobalReceiver(s2C.id(), (render, handler, buf, responseSender) -> {
                    Object applied = s2C.decoder().apply(new PacketContent(buf));
                    if (s2C.netHandle()) {
                        s2C.packetHandler().accept(applied, new S2CNetworkContext());
                    } else {
                        render.execute(() -> s2C.packetHandler().accept(applied, new S2CNetworkContext()));
                    }
                });
            } else if (packetConfig instanceof PacketConfig.SAC sac) {
                ClientPlayNetworking.registerGlobalReceiver(sac.s2cID(), (render, handler, buf, responseSender) -> {
                    Object applied = sac.decoder().apply(new PacketContent(buf));
                    if (sac.netHandle()) {
                        sac.clientHandler().accept(applied, new S2CNetworkContext());
                    } else {
                        render.execute(() -> sac.clientHandler().accept(applied, new S2CNetworkContext()));
                    }
                });
                ServerPlayNetworking.registerGlobalReceiver(sac.c2sID(), (server, player, handler, buf, responseSender) -> {
                    try {
                        Object applied = sac.decoder().apply(new PacketContent(buf));
                        if (sac.netHandle()) {
                            sac.serverHandler().accept(applied, new C2SNetworkContext(player));
                        } else {
                            server.execute(() -> sac.serverHandler().accept(applied, new C2SNetworkContext(player)));
                        }
                    } catch (Exception e) {
                        BrickLib.LOGGER.error(e.toString());
                    }
                });
            }
        });
        //?}
        BrickRegisterManager.vanillaEntries().forEach(vanillaEntry -> vanillaEntry.value().forEach((resourceLocation, supplier) -> {
            if (!vanillaEntry.key().containsKey(resourceLocation)) {
                Registry.register((Registry<T>) vanillaEntry.key(), resourceLocation, (T) supplier);
            }
        }));
        BrickRegistries.KEY_MAPPING.foreachValueAndClear(KeyBindingHelper::registerKeyBinding);
        //?} else {

        //?}
    }
    //? if fabric && >= 1.20.6 {
    /*private static <T extends C2SPacket> void c2s(PacketConfig.C2S<T> c2S) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(c2S.id());
        StreamCodec<RegistryFriendlyByteBuf, T> codec = new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return c2S.decoder().apply(new PacketContent(buf));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T packet) {
                c2S.encoder().accept(packet, new PacketContent(buf));
            }
        };
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            if (c2S.netHandle()) {
                c2S.packetHandler().accept(payload, new C2SNetworkContext(context.player()));
            } else {
                context.player().getServer().execute(() -> c2S.packetHandler().accept(payload, new C2SNetworkContext(context.player())));
            }
        });
    }

    private static <T extends S2CPacket> void s2c(PacketConfig.S2C<T> s2C) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(s2C.id());
        StreamCodec<RegistryFriendlyByteBuf, T> codec = new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return s2C.decoder().apply(new PacketContent(buf));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T packet) {
                s2C.encoder().accept(packet, new PacketContent(buf));
            }
        };
        PayloadTypeRegistry.playS2C().register(type, codec);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            if (s2C.netHandle()) {
                s2C.packetHandler().accept(payload, new S2CNetworkContext());
            } else {
                context.client().execute(() -> s2C.packetHandler().accept(payload, new S2CNetworkContext()));
            }
        });
    }

    private static <T extends SACPacket> void sac(PacketConfig.SAC<T> sAC) {
        CustomPacketPayload.Type<T> s2cT = new CustomPacketPayload.Type<>(sAC.s2cID());
        CustomPacketPayload.Type<T> c2sT = new CustomPacketPayload.Type<>(sAC.c2sID());
        StreamCodec<RegistryFriendlyByteBuf, T> codec = new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return sAC.decoder().apply(new PacketContent(buf));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T packet) {
                sAC.encoder().accept(packet, new PacketContent(buf));
            }
        };
        PayloadTypeRegistry.playC2S().register(c2sT, codec);
        ServerPlayNetworking.registerGlobalReceiver(c2sT, (payload, context) -> {
            if (sAC.serverNetHandle()) {
                sAC.serverHandler().accept(payload, new C2SNetworkContext(context.player()));
            } else {
                context.player().getServer().execute(() -> sAC.serverHandler().accept(payload, new C2SNetworkContext(context.player())));
            }
        });
        PayloadTypeRegistry.playS2C().register(s2cT, codec);
        ClientPlayNetworking.registerGlobalReceiver(s2cT, (payload, context) -> {
            if (sAC.clientNetHandle()) {
                sAC.clientHandler().accept(payload, new S2CNetworkContext());
            } else {
                context.client().execute(() -> sAC.clientHandler().accept(payload, new S2CNetworkContext()));
            }
        });
    }
    *///?}


    public static Set<ResourceLocation> networkChannels(Connection connection, ConnectionProtocol protocol) {
        //? if fabric {
        return Set.of();
         //?} else if forge {
        /*//? if >= 1.20.4 {
        return Set.of();
        //?} else {
        /^MCRegisterPacketHandler.ChannelList list = NetworkHooks.getChannelList(connection);
        if (list != null) {
            return list.getRemoteLocations();
        }
        return Set.of();
        ^///?}
        *///?} else if neoforge {
        /*NetworkPayloadSetup payloadSetup = (NetworkPayloadSetup) connection.channel().attr(AttributeKey.valueOf("neoforge:payload_setup")).get();
        if (payloadSetup == null) {
            return getKnownAdHocChannelsOfOtherEnd(connection);
        }
        if (protocol != null) {
            //? if =1.20.4 {
            HashSet<ResourceLocation> set = new HashSet<>();
            set.addAll(payloadSetup.play().keySet());
            set.addAll(payloadSetup.configuration().keySet());
            return set;
            //?} else {
            /^return payloadSetup.getChannels(protocol).keySet();
            ^///?}
        } else{
            HashSet<ResourceLocation> set = new HashSet<>();
            //? if =1.20.4 {
            set.addAll(payloadSetup.play().keySet());
            set.addAll(payloadSetup.configuration().keySet());
            //?} else {
            /^payloadSetup.channels().values().stream().map(Map::keySet).forEach(set::addAll);
            ^///?}
            return set;
        }
        *///?} else {
        /*return Set.of();
        *///?}
    }

    //? if forge || neoforge {
    /*@SubscribeEvent
    public static <T> void onRegister(RegisterEvent event) {
        ResourceKey<? extends Registry<T>> registeringKey = (ResourceKey<? extends Registry<T>>) event.getRegistryKey();
        for (BrickRegisterManager.VanillaRegistryType<?> vanillaRegistryType : BrickRegisterManager.vanillaEntries()) {
            if (vanillaRegistryType.key().key().equals(registeringKey)) {
                vanillaRegistryType.value().forEach((resourceLocation, supplier) -> {
                    T o = (T) supplier.get();
                    event.register(registeringKey, resourceLocation, () -> o);
                });
                return;
            }
        }
    }


    *///?}
    //? if neoforge {
    /*private static Set<ResourceLocation> getKnownAdHocChannelsOfOtherEnd(Connection connection) {
        var map = connection.channel().attr(AttributeKey.valueOf("neoforge:adhoc_channels")).get();

        if (map == null) {
            map = new HashSet<>();
            connection.channel().attr(AttributeKey.valueOf("neoforge:adhoc_channels")).set(map);
        }

        return (Set<ResourceLocation>) map;
    }  
    *///?}
}
