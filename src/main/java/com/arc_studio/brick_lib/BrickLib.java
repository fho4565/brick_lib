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
import com.arc_studio.brick_lib.api.register.BrickRegisterManager;
import com.arc_studio.brick_lib.client.command.ClientCommands;
import com.arc_studio.brick_lib.config.*;
import com.arc_studio.brick_lib.core.global_pack.GlobalPack;
import com.arc_studio.brick_lib.core.global_pack.GlobalPacks;
import com.arc_studio.brick_lib.datagen.BrickDataGenerator;
import com.arc_studio.brick_lib.events.client.ClientTickEvent;
import com.arc_studio.brick_lib.events.client.KeyEvent;
import com.arc_studio.brick_lib.events.client.RenderEvent;
import com.arc_studio.brick_lib.events.game.CommandEvent;
import com.arc_studio.brick_lib.events.game.LogInEvent;
import com.arc_studio.brick_lib.events.server.NetworkMessageEvent;
import com.arc_studio.brick_lib.events.server.entity.EntityEvent;
import com.arc_studio.brick_lib.events.server.entity.living.LivingEntityEvent;
import com.arc_studio.brick_lib.events.server.entity.living.mob.MobEvent;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import com.arc_studio.brick_lib.events.server.server.ServerEvent;
import com.arc_studio.brick_lib.globalpack.file_types.ItemType;
import com.arc_studio.brick_lib.item.ICooldownItem;
import com.arc_studio.brick_lib.network.DemoReplyPacket;
import com.arc_studio.brick_lib.network.LoginPacketDemo;
import com.arc_studio.brick_lib.platform.Platform;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.arc_studio.brick_lib.register.CommandEntitySelector;
import com.arc_studio.brick_lib.register.CommandSelectorOption;
import com.arc_studio.brick_lib.tools.*;
import com.google.gson.*;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author fho4565
 */
public final class BrickLib {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "brick_lib";
    public static final Version BRICK_LIB_VERSION = new Version.Builder(1, 0, 0).preRelease(Version.PreReleaseType.ALPHA,3).build();
    public static final List<Entity> ENTITIES = new ArrayList<>();
    static boolean test01 = false;

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
    static HashMap<String, Block> map = new HashMap<>(
            Map.of(
                    "grass_block", Blocks.GRASS_BLOCK,
                    "stone", Blocks.STONE,
                    "redstone_block", Blocks.REDSTONE_BLOCK,
                    "bedrock", Blocks.BEDROCK
            )
    );

    private static void test() {
        BrickEventBus.registerListener(ServerEvent.AboutToStart.class, event -> System.out.println("Server About to start"));
        BrickEventBus.registerListener(ServerEvent.LoadData.class, event -> System.out.println("Server Load data"));

        BrickEventBus.registerListener(PlayerEvent.RequestItemTooltip.class,event -> {
            ArrayList<Component> lines = event.getToolTipLines();
            lines.clear();
            lines.add(Component.literal("man!"));
        });

        BrickEventBus.registerListener(PlayerEvent.Advancement.Progress.class,event -> {
            if (event.advancement().getDisplay() != null) {
                event.getEntity().sendSystemMessage(Component.literal("progress"));
                event.getEntity().sendSystemMessage(event.advancement().getDisplay().getTitle());
            }
        });
        BrickEventBus.registerListener(PlayerEvent.Advancement.Complete.class,event -> {
            if (event.advancement().getDisplay() != null) {
                event.getEntity().sendSystemMessage(Component.literal("complete"));
                event.getEntity().sendSystemMessage(event.advancement().getDisplay().getTitle());
            }
            event.cancel();
        });
        BrickEventBus.registerListener(PlayerEvent.Advancement.Revoke.class,event -> {
            DisplayInfo display = event.advancement().getDisplay();
            if (display != null) {
                event.getEntity().sendSystemMessage(Component.literal("revoke"));
                event.getEntity().sendSystemMessage(display.getTitle());
                event.getEntity().sendSystemMessage(Component.literal(event.getCriterionName()));
                event.advancement().getCriteria().forEach((s, criterion) -> event.advancementProgress().grantProgress(s));
            }
            event.cancel();
        });
        BrickRegisterManager.register(BrickRegistries.COMMAND,  () -> commandBuildContext -> Commands.literal("check").then(Commands.argument("entity", EntityArgument.entities())
                .executes(commandContext -> {
                    test01 = !test01;
                    commandContext.getSource().sendSuccess(()->Component.literal("Now test01 = "+test01),true);
                    return 1;
                })));
        BrickEventBus.registerListenerClient(PlayerEvent.Gui.Open.class, event -> {
            Screen screen = event.screen();
            if (screen != null) {
                if(test01){
                    event.cancel();
                }
                System.out.println("event.screen().getTitle() = " + screen.getTitle().getString());
            }
        });
/*        BrickEventBus.registerListenerClient(PlayerEvent.Gui.Close.class, event -> {
            Screen screen = event.screen();
            if (screen != null) {
                if(test01){
                    event.cancel();
                }
                System.out.println("event.screen().getTitle() = " + screen.getTitle().getString());
            }
        });*/
        BrickRegisterManager.register(BrickRegistries.CLIENT_COMMAND,
                () ->buildContext -> {
                    LiteralArgumentBuilder<ClientSuggestionProvider> builder = ClientCommands.literal("client_cfg");
                    for (ModConfig.Type type : ConfigTracker.configSets().keySet()) {
                        builder.then(ClientCommands.literal(type.name().toLowerCase())
                                .then(ClientCommands.argument("cfg",StringArgumentType.string())
                                        .suggests((context, builder1) -> {
                                            ConfigTracker.configSets().get(type).forEach(s -> builder1.suggest(s.getFileName()));
                                            return builder1.buildFuture();
                                        })
                                        .executes(context -> {
                                            MutableObject<Integer> r = new MutableObject<>();
                                            final String cfgName = StringArgumentType.getString(context, "cfg");
                                            ConfigTracker.configSets().get(type)
                                                    .stream().filter(c-> cfgName
                                                            .equals(c.getFileName())).findFirst().ifPresentOrElse(config -> {
                                                                System.out.println(config.getFileName()+" comments :");
                                                                config.getConfigData().commentMap().forEach((s, s2) -> {
                                                                    System.out.println("s = "+s+" , s2 = "+s2);
                                                                });

                                                                System.out.println(config.getFileName()+" values :");
                                                                config.getConfigData().valueMap().forEach((k, v) -> {
                                                                    System.out.println("k = "+k+" , v = "+v);
                                                                });
                                                                r.setValue(1);
                                                            }, () -> {
                                                        System.out.println("No config named "+cfgName+" found in "+type.name().toLowerCase());
                                                        r.setValue(0);
                                                    });

                                            return r.getValue();
                                        })
                                )
                        );
                    }
                    return builder;
                });

        BrickRegistries.NETWORK_PACKET.register(BrickLib.createBrickRL("demo_login_packet"),() ->
                new PacketConfig.Login<>(
                        LoginPacketDemo.class,
                        LoginPacketDemo::encoder,
                        LoginPacketDemo::new,
                        LoginPacketDemo::new,
                        LoginPacketDemo::serverHandle,
                        LoginPacketDemo::clientHandle,
                        isLocal -> {
                            ArrayList<Pair<String, LoginPacketDemo>> list = new ArrayList<>();
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
        ConfigManager.registerConfig(ModConfig.Type.SERVER, builder.build(),MOD_ID);
        System.out.println("BrickLib.test");
        SideExecutor.runOnClient(() -> {
            KeyMapping man = new KeyMapping("man", GLFW.GLFW_KEY_J, "man");
            BrickRegisterManager.register(BrickRegistries.KEY_MAPPING, BrickLib.createBrickRL("mankey"), () -> man);
        });
        BrickEventBus.registerListenerClient(KeyEvent.Down.class,event -> {
            if ("man".equals(event.keyMapping().getName())) {
                System.out.println("send");
                BrickNetwork.sendMessageToServer("bl", "k_skill");
            }
        });

        BrickRegisterManager.register(BrickRegistries.COMMAND_ENTITY_SELECTORS,
                ()->new CommandEntitySelector("pig", Component.literal("find pigs"), entity -> entity.getType() == EntityType.PIG)
        );
        BrickRegisterManager.register(BrickRegistries.COMMAND_ENTITY_SELECTOR_OPTIONS,() ->
                new CommandSelectorOption("man", entitySelectorParser -> {
            String key = entitySelectorParser.getReader().readUnquotedString();
            entitySelectorParser.addPredicate(entity -> {
                if (map.containsKey(key)) {
                    return entity.level().getBlockState(entity.blockPosition().below()).is(map.get(key));
                } else {
                    return false;
                }
            });
        }, entitySelectorParser -> true, Component.literal("man option")));
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

        SideExecutor.runOnClient(() -> () -> BrickRegisterManager.register(BrickRegistries.CLIENT_COMMAND,
                () -> buildContext -> ClientCommands.literal("client")
                        .executes(context -> {
                            System.out.println("BrickLib.test");
                            ClientCommands.sendError(Component.literal("Error!"));
                            ClientCommands.sendFeedback(Component.literal("Feedback!"));
                            return 1;
                        })
        ));

        BrickRegisterManager.register(BrickRegistries.COMMAND, () ->
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
        BrickRegisterManager.register(BrickRegistries.COMMAND, () ->
                buildContext -> Commands.literal("test01")
                        .executes(context -> {
                            try {
                                Codec<Pair<Integer,Integer>> codec = Codecs.intervalCodec(Codec.INT, "l", "r",
                                        (l, r) -> DataResult.success(Pair.of(l, r))
                                        , Pair::getLeft, Pair::getRight);
                                System.out.println("codec.encodeStart(JsonOps.INSTANCE,Pair.of(1,8)).get().orThrow() = " + codec.encodeStart(JsonOps.INSTANCE, Pair.of(1, 8))
                                        //? if >= 1.20.6 {
                                        /*.getOrThrow()
                                        *///?} else {
                                        .get().orThrow()
                                        //?}
                                );
                                System.out.println("codec.decode(JsonOps.INSTANCE, JsonParser.parseString(\"[1, 10]\")) = " + codec.decode(JsonOps.INSTANCE, JsonParser.parseString("[1, 10]"))
                                        //? if >= 1.20.6 {
                                        /*.getOrThrow()
                                        *///?} else {
                                        .get().orThrow()
                                        //?}
                                );
                                System.out.println("codec.decode(JsonOps.INSTANCE, JsonParser.parseString(\"5\")) = " + codec.decode(JsonOps.INSTANCE, JsonParser.parseString("5"))
                                                //? if >= 1.20.6 {
                                                /*.getOrThrow()
                                        *///?} else {
                                        .get().orThrow()
                                        //?}
                                );
                                final JsonObject object = new JsonObject();
                                object.addProperty("l",2);
                                object.addProperty("r",5);
                                System.out.println("codec.decode(JsonOps.INSTANCE, JsonParser.parseString(\"{\"l\": 1, \"r\": 10}\")) = " + codec.decode(JsonOps.INSTANCE, JsonParser.parseString(object.toString()))
                                                //? if >= 1.20.6 {
                                                /*.getOrThrow()
                                        *///?} else {
                                        .get().orThrow()
                                        //?}
                                );
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                            return 1;
                        })
        );
        BrickRegisterManager.register(BrickRegistries.COMMAND, () ->
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
        BrickRegisterManager.register(BrickRegistries.COMMAND, () ->
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
            if ("bl".equals(event.getId())) {
                System.out.println(event.getMessage());
            } else {
                System.out.println("kksk");
                System.out.println(event.getMessage());
            }
        });
        BrickEventBus.registerListener(NetworkMessageEvent.ServerReceive.class, event -> {
            System.out.println("event.getId() = " + event.getId());
            System.out.println("event.getMessage() = " + event.getMessage());
            if ("bl".equals(event.getId())) {
                if ("k_skill".equals(event.getMessage())) {
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
                //event.cancel();
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
            ConfigTracker.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(event.server()));
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
            System.out.println("Start breaking blocks");
        });
        BrickEventBus.registerListener(PlayerEvent.BreakBlock.Stop.class, event -> System.out.println("Stop breaking blocks, is client ? = " + event.getEntity().level().isClientSide));
        BrickEventBus.registerListener(PlayerEvent.BreakBlock.Finish.Pre.class, event -> System.out.println("Complete the destruction block.Count"));
        BrickEventBus.registerListener(PlayerEvent.BreakBlock.Finish.Post.class, event -> System.out.println("Complete the destruction block.Post"));
        BrickEventBus.registerListener(PlayerEvent.LeftClick.class, event -> System.out.println("Left-click on the block"));
    }

    private static void preLoad() {
        BrickRegistries.NETWORK_PACKET.register(BrickLib.createBrickRL("config_sync_packet"),() ->
                new PacketConfig.Login<>(
                        ConfigSyncPacket.class,
                        ConfigSyncPacket::encoder,
                        ConfigSyncPacket::new,
                        ConfigSyncPacket::new,
                        ConfigSyncPacket::serverHandle,
                        ConfigSyncPacket::clientHandle,
                        isLocal -> {
                            Map<String, byte[]> configData = ConfigTracker.configSets().get(ModConfig.Type.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, mc -> {
                                try {
                                    if (Platform.isClient() || mc.getConfigData() == null) {
                                        System.out.println("ConfigSyncPacket.generatePackets = NULL");
                                    }else{
                                        System.out.println("ConfigSyncPacket.generatePackets = YES");
                                    }
                                    return Platform.isClient() || mc.getConfigData() == null ? new byte[0] : Files.readAllBytes(mc.getFullPath());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                            return configData.entrySet().stream().map(e-> {
                                System.out.println("ConfigSyncPacket.generatePackets Name = config_" + e.getKey());
                                return Pair.of("config_" + e.getKey(), new ConfigSyncPacket(e.getKey(), e.getValue()));
                            }).collect(Collectors.toList());
                        }
                )
        );
        BrickRegisterManager.register(BrickRegistries.COMMAND, () -> buildContext ->
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
        BrickRegisterManager.register(BrickRegistries.COMMAND, () -> buildContext ->
                Commands.literal("datagen")
                        .requires(stack -> Constants.isInDevelopEnvironment())
                        .executes(context -> genData(context, true, true))
                        .then(Commands.argument("genClient", BoolArgumentType.bool())
                                .executes(context -> genData(context, BoolArgumentType.getBool(context,"genClient"), true))
                                .then(Commands.argument("genServer", BoolArgumentType.bool())
                                        .executes(context -> genData(context,
                                                BoolArgumentType.getBool(context,"genClient"),
                                                BoolArgumentType.getBool(context,"genServer")))
                                )
                        )
        );
        BrickRegisterManager.register(BrickRegistries.NETWORK_PACKET,
                BrickLib.createBrickRL("built_in_packet"),
                () -> new PacketConfig.SAC<>(BuiltInPacket.class,
                        BuiltInPacket::encoder,
                        BuiltInPacket::new,
                        BuiltInPacket::serverHandle,
                        BuiltInPacket::clientHandle,
                        false,
                        false
                )
        );
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
            ConfigTracker.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(event.server()));
        });
        BrickEventBus.registerListenerClient(LogInEvent.ClientSuccess.class,event -> {
            ConfigTracker.loadDefaultServerConfigs();
        });
    }

    private static int genData(CommandContext<CommandSourceStack> context, boolean client, boolean server) {
        try {
            BrickDataGenerator.run(client, server);
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Error when generate data").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,Component.literal(e.getMessage())))));
            return 0;
        }
        return 1;
    }

    private static Path getServerConfigPath(final MinecraftServer server) {
        Path serverConfig = Constants.serverConfigFolder();
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
        return Utils.ofResourceLocation(MOD_ID, path);
    }
}

