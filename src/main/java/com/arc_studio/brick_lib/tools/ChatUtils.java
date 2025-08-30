package com.arc_studio.brick_lib.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

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
        MinecraftServer server = Constants.currentServer();
        if(server != null){
            server.getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(Component.literal(message)));
        }
    }
    /**
     * 向所有玩家发送Json文本消息
     * @param message 要发送的消息内容
     */
    public static void sendMessageToAllPlayers(Component message) {
        MinecraftServer server = Constants.currentServer();
        if(server != null) {
            server.getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(message));
        }
    }

    public static void clientMessage(Component component,boolean narrate){
        SideExecutor.runOnClient(() -> () -> {
            Minecraft client = Minecraft.getInstance();
            client.gui.getChat().addMessage(component);
            if(narrate){
                client.getNarrator().sayNow(component);
            }
        });
    }
}
