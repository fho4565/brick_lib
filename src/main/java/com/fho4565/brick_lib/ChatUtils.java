package com.fho4565.brick_lib;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ChatUtils {
    /**
     * 向指定玩家发送纯文本消息
     *
     * @param player 接收消息的玩家
     * @param message 要发送的消息内容
     */
    public static void sendMessageToPlayer(Player player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }
    /**
     * 向指定玩家发送Json文本消息
     *
     * @param player 接收消息的玩家
     * @param message 要发送的消息内容
     */
    public static void sendMessageToPlayer(Player player, Component message) {
        player.sendSystemMessage(message);
    }
    /**
     * 向所有玩家发送纯文本消息
     * @param message 要发送的消息内容
     */
    public static void sendMessageToAllPlayers(String message) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if(server != null){
            server.getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(Component.literal(message)));
        }
    }
    /**
     * 向所有玩家发送Json文本消息
     * @param message 要发送的消息内容
     */
    public static void sendMessageToAllPlayers(Component message) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if(server != null) {
            server.getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(message));
        }
    }
}
