package com.fho4565.brick_lib.events;

import com.fho4565.brick_lib.BrickLib;
import com.fho4565.brick_lib.ChatUtils;
import com.fho4565.brick_lib.Constants;
import com.fho4565.brick_lib.core.ArmorSuits;
import com.fho4565.brick_lib.item.ICooldownItem;
import com.fho4565.brick_lib.tools.placer.Placer;

import com.fho4565.brick_lib.variables.BrickAttributeProvider;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;


@Mod.EventBusSubscriber(modid = BrickLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrickForgeCommonEvents {
    private static CompletableFuture<Suggestions> getCommandSuggestions(SuggestionsBuilder builder, String[] string) {
        for (String s : string) {
            builder.suggest(s);
        }
        return builder.buildFuture();
    }

    @SubscribeEvent
    public static void onRegisterBrickCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("placer").requires(stack -> Constants.isInDevelopEnvironment() && stack.hasPermission(2))
                        .then(Commands.literal("place")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("fileName", StringArgumentType.greedyString())
                                                .suggests((context, builder) -> {
                                                    File file = new File(Constants.brickLibPlacersFolder());
                                                    File[] files = file.listFiles();
                                                    if (files != null) {
                                                        return getCommandSuggestions(builder, Arrays.stream(files).map(File::getName).toArray(String[]::new));
                                                    } else {
                                                        return getCommandSuggestions(builder, new String[0]);
                                                    }
                                                })
                                                .executes(context -> {
                                                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                                    ServerPlayer player = context.getSource().getPlayer();
                                                    CompletableFuture<Boolean> task = CompletableFuture.supplyAsync(() -> {
                                                                try {
                                                                    return Placer.placeIO(context.getSource().getLevel(),
                                                                            pos, StringArgumentType.getString(context, "fileName"),
                                                                            new Placer()
                                                                    );
                                                                } catch (IOException | CommandSyntaxException e) {
                                                                    BrickLib.LOGGER.error(e.getCause().getLocalizedMessage());
                                                                    return false;
                                                                }
                                                            }, BrickLib.THREAD_POOL
                                                    );
                                                    task.thenAccept(bool -> {
                                                        if (player != null) {
                                                            ChatUtils.sendMessageToPlayer(player, "placed : " + bool);
                                                        }

                                                    });
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("create")
                                .then(Commands.argument("pos1", BlockPosArgument.blockPos())
                                        .then(Commands.argument("pos2", BlockPosArgument.blockPos())
                                                .then(Commands.argument("air", BoolArgumentType.bool())
                                                        .executes(context -> {
                                                            BlockPos pos1 = BlockPosArgument.getLoadedBlockPos(context, "pos1");
                                                            BlockPos pos2 = BlockPosArgument.getLoadedBlockPos(context, "pos2");
                                                            ServerPlayer player = context.getSource().getPlayer();
                                                            Placer placer = new Placer();
                                                            CompletableFuture.supplyAsync(() -> {
                                                                        if (player != null) {
                                                                            context.getSource().sendSystemMessage(Component.literal("start generate placer io named : " + placer.name()));
                                                                        }
                                                                        try {
                                                                            if (BoolArgumentType.getBool(context, "air")) {
                                                                                Placer.generateIO(context.getSource().getLevel(), pos1, pos2, placer);
                                                                            } else {
                                                                                Placer.generateIOWithoutAir(context.getSource().getLevel(), pos1, pos2, placer);
                                                                            }
                                                                        } catch (IOException e) {
                                                                            BrickLib.LOGGER.error(e.getLocalizedMessage());
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                        return null;
                                                                    }, BrickLib.THREAD_POOL
                                                            );
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
        );

    }


    @SubscribeEvent
    public static void onAttachBrickCaps(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player player) {
            event.addCapability(new ResourceLocation(BrickLib.MOD_ID, "brick_attribute"), new BrickAttributeProvider(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerClonedBrick(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        LazyOptional<BrickAttributeProvider.BrickAttribute> oldCap = event.getOriginal().getCapability(BrickAttributeProvider.BrickCapability.BRICK_ATTRIBUTE);
        LazyOptional<BrickAttributeProvider.BrickAttribute> newCap = event.getEntity().getCapability(BrickAttributeProvider.BrickCapability.BRICK_ATTRIBUTE);
        if (oldCap.isPresent() && newCap.isPresent()) {
            newCap.ifPresent((newCap1) -> oldCap.ifPresent((oldCap1) -> newCap1.deserializeNBT(oldCap1.serializeNBT())));
        }
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerTickBrick(TickEvent.PlayerTickEvent event) {
        ArmorSuits.tick(event.player);
        event.player.getInventory().items.forEach(itemStack -> {
            if (itemStack.getItem() instanceof ICooldownItem item) {
                item.tickCooldown(event.player, itemStack);
            }
        });
        event.player.getInventory().armor.forEach(itemStack -> {
            if (itemStack.getItem() instanceof ICooldownItem item) {
                item.tickCooldown(event.player, itemStack);
            }
        });
        event.player.getInventory().offhand.forEach(itemStack -> {
            if (itemStack.getItem() instanceof ICooldownItem item) {
                item.tickCooldown(event.player, itemStack);
            }
        });
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        ArmorSuits.init();
    }


}
