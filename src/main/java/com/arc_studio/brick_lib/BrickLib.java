package com.arc_studio.brick_lib;

import com.arc_studio.brick_lib.api.core.Version;
import com.arc_studio.brick_lib.api.core.task.TaskExecutor;
import com.arc_studio.brick_lib.api.data.BlockAdditionalData;
import com.arc_studio.brick_lib.api.data.EntityAdditionalData;
import com.arc_studio.brick_lib.api.data.LevelAdditionalData;
import com.arc_studio.brick_lib.api.data.WorldAdditionalData;
import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.api.network.BrickNetwork;
import com.arc_studio.brick_lib.api.network.BuiltInPacket;
import com.arc_studio.brick_lib.api.network.type.PacketConfig;
import com.arc_studio.brick_lib.api.network.type.LoginPacket;
import com.arc_studio.brick_lib.api.register.BrickRegisterManager;
import com.arc_studio.brick_lib.client.command.ClientCommands;
import com.arc_studio.brick_lib.config.BrickConfigSpec;
import com.arc_studio.brick_lib.config.ConfigManager;
import com.arc_studio.brick_lib.config.ConfigTracker;
import com.arc_studio.brick_lib.config.ModConfig;
import com.arc_studio.brick_lib.core.global_pack.GlobalPack;
import com.arc_studio.brick_lib.core.global_pack.GlobalPacks;
import com.arc_studio.brick_lib.events.client.ClientTickEvent;
import com.arc_studio.brick_lib.events.client.RenderEvent;
import com.arc_studio.brick_lib.events.game.CommandEvent;
import com.arc_studio.brick_lib.events.game.LogInEvent;
import com.arc_studio.brick_lib.events.server.NetworkMessageEvent;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import com.arc_studio.brick_lib.events.server.server.ServerEvent;
import com.arc_studio.brick_lib.globalpack.file_types.ItemType;
import com.arc_studio.brick_lib.item.ICooldownItem;
import com.arc_studio.brick_lib.network.DemoReplyPacket;
import com.arc_studio.brick_lib.network.LoginPacketDemo;
import com.arc_studio.brick_lib.platform.Platform;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.arc_studio.brick_lib.tools.*;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public final class BrickLib {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "brick_lib";
    public static final Version BRICK_LIB_VERSION = new Version.Builder(1, 0, 0).preRelease(Version.PreReleaseType.ALPHA, 2).build();

    private static final BiConsumer<Player, ItemStack> COOLDOWN_ITEM_CONSUMER = (player, itemStack) -> {
        if (itemStack.getItem() instanceof ICooldownItem item) {
            item.tickCooldown(player, itemStack);
        }
    };

    public static void init() {
        MixinExtrasBootstrap.init();
        Constants.initGeneral();
        BrickLib.LOGGER.info("You are using Brick Lib version {}", BRICK_LIB_VERSION);
        preLoad();
        test();
        Utils.brickFinalize();
    }

    private static void test() {
        BrickRegistries.NETWORK_PACKET.register(BrickLib.createBrickRL("demo_login_packet"),() ->
                new PacketConfig.Login<>(
                        LoginPacketDemo.class,
                        LoginPacketDemo::encoder,
                        LoginPacketDemo::new,
                        LoginPacketDemo::new,
                        LoginPacketDemo::serverHandle,
                        LoginPacketDemo::clientHandle,
                        isLocal -> {
                            ArrayList<Pair<String, ? extends LoginPacket>> list = new ArrayList<>();
                            for (int i = 0; i < 10; i++) {
                                list.add(Pair.of(String.valueOf(i),new LoginPacketDemo(String.valueOf(i))));
                            }
                            return list;
                        }
                )
        );
        BrickRegistries.NETWORK_PACKET.register(BrickLib.createBrickRL("demo_reply_packet"),()->
                new PacketConfig.Login<>(
                        DemoReplyPacket.class,
                        DemoReplyPacket::encoder,
                        DemoReplyPacket::new,
                        DemoReplyPacket::new,
                        DemoReplyPacket::serverHandle,
                        DemoReplyPacket::clientHandle,
                        isLocal -> List.of()
                )
        );


        BrickConfigSpec.Builder builder = new BrickConfigSpec.Builder();
        builder.comment("Brick Lib Config");
        builder.comment("very man string");
        builder.define("str","man");
        ConfigManager.registerConfig(ModConfig.Type.COMMON, builder.build(),MOD_ID);
        System.out.println("BrickLib.test");
        SideExecutor.runOnClient(() -> {
            KeyMapping man = new KeyMapping("man", GLFW.GLFW_KEY_J, "man");
            BrickRegisterManager.register(BrickRegistries.KEY_MAPPING, BrickLib.createBrickRL("mankey"), () -> man);
            BrickEventBus.registerListener(ClientTickEvent.class, event -> {
                if (man.isDown()) {
                    BrickNetwork.sendMessageToServer("bl", "k_skill");
                }
            });
        });
        BrickRegisterManager.register(BrickRegistries.COMMAND, BrickLib.createBrickRL("gp"), () -> buildContext ->
                Commands.literal("globalpack")
                        .then(Commands.literal("create")
                                .executes(context -> {
                                    if (GlobalPacks.createExample()) {
                                        context.getSource().sendSuccess(() -> Component.literal("Generated"), false);
                                        return 1;
                                    }
                                    context.getSource().sendFailure(Component.literal("Failed"));
                                    return 0;
                                })
                        )
                        .then(Commands.literal("list")
                                .executes(context -> {
                                    for (GlobalPack globalPack : BrickRegistries.GLOBAL_PACK.values()) {
                                        context.getSource().sendSuccess(() -> Component.literal(globalPack.name()), false);
                                    }
                                    return 1;
                                })
                        )
        );
        BrickEventBus.registerListener(PlayerEvent.PlayerJoin.Post.class, event -> {
            SideExecutor.runOnClient(() -> () ->
                    System.out.println("Platform.gameVersion() = " +
                                       Minecraft.getInstance().getLaunchedVersion()));
            //ChatUtils.sendMessageToPlayer(event.getEntity(), "You are playing %s %s-%s".formatted(Platform.isServer() ? "server":"client",Platform.platform().getName(),Platform.gameVersion()));
        });

        BrickEventBus.registerListener(RenderEvent.HudEvent.VanillaRender.ExperienceBar.class, event -> {
            event.cancelVanillaRender();
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                int currentExpPoint = Mth.floor(player.experienceProgress * player.getXpNeededForNextLevel());
                event.guiGraphics().drawString(Minecraft.getInstance().font,
                        Component.literal("Exp Level:").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(String.valueOf(player.experienceLevel))
                                        .withStyle(ChatFormatting.GOLD))
                                .append(Component.literal("   Exp Point:")
                                        .withStyle(ChatFormatting.AQUA))
                                .append(Component.literal(String.valueOf(currentExpPoint))
                                        .withStyle(ChatFormatting.GOLD))
                                .append(Component.literal("   Needed for next level:")
                                        .withStyle(ChatFormatting.AQUA))
                                .append(Component.literal(String.valueOf(player.getXpNeededForNextLevel() - currentExpPoint))
                                        .withStyle(ChatFormatting.GOLD))
                        ,
                        event.barX(),
                        event.barY() - 1,
                        Color.RED.getRGB()
                );
            }
        });

        SideExecutor.runOnClient(() -> () -> BrickRegisterManager.register(BrickRegistries.CLIENT_COMMAND, BrickLib.createBrickRL("client_command_demo"),
                () -> buildContext -> ClientCommands.literal("client")
                        .executes(context -> {
                            System.out.println("BrickLib.test");
                            ClientCommands.sendError(Component.literal("Error!"));
                            ClientCommands.sendFeedback(Component.literal("Feedback!"));
                            return 1;
                        })
        ));

        BrickRegisterManager.register(BrickRegistries.COMMAND, BrickLib.createBrickRL("network_list_command"), () ->
                buildContext -> Commands.literal("net_list")
                        .executes(context -> {
                            Constants.currentServer().getConnection().getConnections().forEach(connection -> {
                                for (ConnectionProtocol value : ConnectionProtocol.values()) {
                                    System.out.println("========= Network : " + value.name() + " =========");
                                    Platform.networkChannels(connection, value)
                                            .forEach(System.out::println);
                                }
                            });
                            return 1;
                        })
        );
        BrickRegisterManager.register(BrickRegistries.COMMAND, BrickLib.createBrickRL("test01"), () ->
                buildContext -> Commands.literal("test01")
                        .executes(context -> {
                            final ItemType type = new ItemType();
                            type.setStackSize(16);
                            System.out.println("Tag = " + ItemType.CODEC.encodeStart(NbtOps.INSTANCE, type).result().orElseThrow());
                            final DataResult<JsonElement> result = ItemType.CODEC.encodeStart(JsonOps.INSTANCE, type);
                            result.error().ifPresent(jsonElementPartialResult -> System.out.println("message() = " + jsonElementPartialResult.message()));
                            System.out.println("Json.false = " + result.result().orElseThrow());
                            System.out.println("Json.true = " + ItemType.CODEC.encodeStart(JsonOps.COMPRESSED, type).result().orElseThrow());
                            return 1;
                        })
        );
        BrickRegisterManager.register(BrickRegistries.COMMAND, BrickLib.createBrickRL("network_test_command"), () ->
                buildContext -> Commands.literal("net_test")
                        .then(Commands.literal("s2c")
                                .executes(context -> {
                                    System.out.println("BrickLib.test.net_test.s2c");
                                    System.out.println("Platform.isClient() = " + Platform.isClient());
                                    System.out.println("Platform.isServer() = " + Platform.isServer());
                                    BrickNetwork.sendMessageToAllPlayers("network_test", "bl");
                                    return 1;
                                })
                        )
                        .then(Commands.literal("c2s")
                                .executes(context -> {
                                    System.out.println("BrickLib.test.net_test.c2s");
                                    System.out.println("Platform.isClient() = " + Platform.isClient());
                                    System.out.println("Platform.isServer() = " + Platform.isServer());
                                    // is server
                                    SideExecutor.runOnClient(() -> {
                                        BrickNetwork.sendMessageToServer("bl", "man");
                                        System.out.println("BrickLib.test.net_test.c2s.lambda");
                                    });
                                    return 1;
                                })
                        )
        );
        BrickRegisterManager.register(BrickRegistries.COMMAND, createBrickRL("color"), () ->
                buildContext -> Commands.literal("color")
                        .then(Commands.literal("int2rgb")
                                .then(Commands.argument("int", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int i = IntegerArgumentType.getInteger(context, "int");
                                            Color color = new Color(i);
                                            int r = color.getRed();
                                            int g = color.getGreen();
                                            int b = color.getBlue();
                                            int a = color.getAlpha();
                                            context.getSource().sendSuccess(() ->
                                                    Component.literal("""
                                                            ARGB of %d :\s
                                                             r= %d
                                                             g= %d
                                                             b= %d
                                                             a= %d""".formatted(i, r, g, b, a)), false);
                                            context.getSource().sendSuccess(() ->
                                                    Component.literal("Example Text").withStyle(Style.EMPTY.withColor(i)), false);
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("rgb2int")
                                .then(Commands.argument("r", IntegerArgumentType.integer())
                                        .then(Commands.argument("g", IntegerArgumentType.integer())
                                                .then(Commands.argument("b", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            int r = IntegerArgumentType.getInteger(context, "r");
                                                            int g = IntegerArgumentType.getInteger(context, "g");
                                                            int b = IntegerArgumentType.getInteger(context, "b");
                                                            Color color = new Color(r, g, b);
                                                            int colorInt = color.getRGB();
                                                            context.getSource().sendSuccess(() ->
                                                                    Component.literal("Integer of (%d,%d,%d,255) : %d".formatted(r, g, b, colorInt)), false);
                                                            context.getSource().sendSuccess(() ->
                                                                    Component.literal("Example Text").withStyle(Style.EMPTY.withColor(colorInt)), false);
                                                            return colorInt;
                                                        })
                                                        .then(Commands.argument("a", IntegerArgumentType.integer())
                                                                .executes(context -> {
                                                                    int r = IntegerArgumentType.getInteger(context, "r");
                                                                    int g = IntegerArgumentType.getInteger(context, "g");
                                                                    int b = IntegerArgumentType.getInteger(context, "b");
                                                                    int a = IntegerArgumentType.getInteger(context, "a");
                                                                    Color color = new Color(r, g, b, a);
                                                                    int colorInt = color.getRGB();
                                                                    context.getSource().sendSuccess(() ->
                                                                            Component.literal("Integer of (%d,%d,%d,%d) : %d".formatted(r, g, b, a, colorInt)), false);
                                                                    context.getSource().sendSuccess(() ->
                                                                            Component.literal("Example Text").withStyle(Style.EMPTY.withColor(colorInt)), false);
                                                                    return colorInt;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        ));
        BrickEventBus.registerListener(ServerEvent.LogIn.class, event -> {
            System.out.println("event.getHostName() = " + event.getHostName());
            Connection connection = event.getConnection();
            if (connection != null) {
                InetSocketAddress address = (InetSocketAddress) connection.getRemoteAddress();
                String ipAddress = address.getAddress().getHostAddress();
                int port = address.getPort();
                System.out.println("Remote IP: " + ipAddress + ", Port: " + port);
            }
        });
        BrickEventBus.registerListener(NetworkMessageEvent.ClientReceive.class, event -> {
            if (event.getId().equals("bl")) {
                System.out.println(event.getMessage());
            } else {
                System.out.println("kksk");
                System.out.println(event.getMessage());
            }
        });
        BrickEventBus.registerListener(NetworkMessageEvent.ServerReceive.class, event -> {
            if (event.getId().equals("bl")) {
                if (event.getMessage().equals("k_skill")) {
                    ItemUtils.giveItem(Items.DIAMOND.getDefaultInstance(), List.of(event.getSender()));
                }
            } else {
                System.out.println(event.getMessage());
            }
        });
        BrickEventBus.registerListener(CommandEvent.PreExecute.class, event -> {
            String command = event.getResults().getReader().getString();
            System.out.println("command = " + command);
            if (command.contains("man")) {
                event.cancel();
                System.out.println("Manba out!");
            }
        });
        BrickEventBus.registerListener(PlayerEvent.Chat.Send.Pre.class, event -> {
            Player player = event.getEntity();
            String message = event.getMessage();
            message = message.replace("${pos}", player.position().toString());
            message = message.replace("${name}", player.getScoreboardName());
            message = message.replace("${hp}", String.valueOf(player.getHealth()));
            message = message.replace("${mhp}", String.valueOf(player.getMaxHealth()));
            message = message.replace("${xp}", String.valueOf(player.totalExperience));
            message = message.replace("${xpl}", String.valueOf(player.experienceLevel));
            message = message.replace("${time.y}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            message = message.replace("${time.m}", String.valueOf(Calendar.getInstance().get(Calendar.MONTH)));
            message = message.replace("${time.d}", String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
            message = message.replace("${time.w}", Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            event.setMessage(message);
        });
        BrickEventBus.registerListener(ServerEvent.Stopping.class, event -> System.out.println("BrickLib server stopping"));
        BrickEventBus.registerListener(ServerEvent.Stopped.class, event -> {
            System.out.println("BrickLib server stopped");
            ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(event.server()));
        });
        BrickEventBus.registerListener(PlayerEvent.RightClick.class, event -> {
            System.out.println("Right Click is Client : " + event.getEntity().level().isClientSide);
            System.out.println("Right Click hand : " + event.getClickContext().interactionHand());
            event.getClickContext().ifClicked(player -> System.out.println("Right click on air"),
                    (player, result) -> System.out.println("Right click on the block : " +
                                                           event.getClickContext().blockHitResult().getBlockPos()),
                    (player, entityHitResult) -> System.out.println("Right click on the entity : " +
                                                                    event.getClickContext().entityHitResult().getEntity().getType().getDescriptionId()),
                    (player, stack) -> System.out.println("Right click on item : "
                        + event.getClickContext().itemStack()
                    ));
        });
        BrickEventBus.registerListener(PlayerEvent.UseItem.Start.class, event -> System.out.println("Start use item"));
        BrickEventBus.registerListener(PlayerEvent.UseItem.Using.class, event -> System.out.println("Using item"));
        BrickEventBus.registerListener(PlayerEvent.UseItem.Stop.class, event -> System.out.println("Stop use item"));
        BrickEventBus.registerListener(PlayerEvent.UseItem.Finish.class, event -> System.out.println("Finish use item"));
        BrickEventBus.registerListener(PlayerEvent.BreakBlock.Start.class, event -> {
            event.cancel();
            System.out.println("Start breaking blocks");
        });
        BrickEventBus.registerListener(PlayerEvent.BreakBlock.Stop.class, event -> System.out.println("Stop breaking blocks, is client ? = " + event.getEntity().level().isClientSide));
        BrickEventBus.registerListener(PlayerEvent.BreakBlock.Finish.Pre.class, event -> System.out.println("Complete the destruction block.Count"));
        BrickEventBus.registerListener(PlayerEvent.BreakBlock.Finish.Post.class, event -> System.out.println("Complete the destruction block.Post"));
        BrickEventBus.registerListener(PlayerEvent.LeftClick.class, event -> System.out.println("Left-click on the block"));
    }

    private static void preLoad() {
        BrickRegisterManager.register(BrickRegistries.NETWORK_PACKET, BrickLib.createBrickRL("built_in_packet"), () -> new PacketConfig.SAC<>(BuiltInPacket.class, BuiltInPacket::encoder, BuiltInPacket::new, BuiltInPacket::serverHandle, BuiltInPacket::clientHandle, false, false));
        BrickRegisterManager.register(BrickRegistries.GLOBAL_PACK_FILE_TYPE, BrickLib.createBrickRL("item"), ItemType::new);
        BrickEventBus.registerListener(PlayerEvent.Tick.Pre.class, event -> {
            Player player = event.getEntity();
            BrickRegistries.ARMOR_SUIT.forEach(armorSuit -> armorSuit.tick(player));
            player.getInventory().items.forEach(itemStack -> COOLDOWN_ITEM_CONSUMER.accept(player, itemStack));
            player.getInventory().armor.forEach(itemStack -> COOLDOWN_ITEM_CONSUMER.accept(player, itemStack));
            player.getInventory().offhand.forEach(itemStack -> COOLDOWN_ITEM_CONSUMER.accept(player, itemStack));
        });
        BrickEventBus.registerListener(ServerEvent.LoadData.class, event -> {
            try {
                BrickLib.LOGGER.debug("Loading Brick Lib additional data");
                BlockAdditionalData.load();
                EntityAdditionalData.load();
                LevelAdditionalData.load();
                WorldAdditionalData.load();
            } catch (IOException e) {
                LOGGER.error("Error when loading Brick Lib additional data");
                LOGGER.error(e.toString());
            }
        });
        BrickEventBus.registerListener(ServerEvent.Tick.class, event -> {
            BlockAdditionalData.tick();
            EntityAdditionalData.tick();
            LevelAdditionalData.tick();
            TaskExecutor.tick();
        });
        BrickEventBus.registerListener(ServerEvent.SaveData.class, event -> {
            try {
                BrickLib.LOGGER.debug("Saving Brick Lib additional data");
                BlockAdditionalData.save();
                EntityAdditionalData.save();
                LevelAdditionalData.save();
                WorldAdditionalData.save();
            } catch (IOException e) {
                LOGGER.error("Error when saving Brick Lib additional data");
                LOGGER.error(e.toString());
            }
        });
        BrickEventBus.registerListener(ServerEvent.AboutToStart.class, event -> {
            System.out.println("BrickLib server about to start");
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(event.server()));
        });
        BrickEventBus.registerListenerClient(LogInEvent.ClientSuccess.class,event -> {
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        });
    }

    private static Path getServerConfigPath(final MinecraftServer server)
    {
        final Path serverConfig = Constants.serverConfigFolder();
        if (!Files.isDirectory(serverConfig)) {
            try {
                Files.createDirectories(serverConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return serverConfig;
    }

    public static ResourceLocation createBrickRL(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

