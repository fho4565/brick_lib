package com.fho4565.brick_lib;

import com.fho4565.brick_lib.gen.ModLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
    /**
     * 根据给定的键和参数翻译文本，并返回一个翻译组件
     * 这个键会被添加至内部集合，执行生成命令时会整理所有没有翻译的键
     *
     * @param key  翻译键
     * @param args 翻译使用的参数
     * @return 翻译组件
     */
    public static Component translate(String key, Object... args){
        MutableComponent translatable = Component.translatable(key, args);
        if (translatable.plainCopy().getString().equals(key)) {
            ModLang.languageKeys.add(key);
        }
        return translatable;
    }
}
