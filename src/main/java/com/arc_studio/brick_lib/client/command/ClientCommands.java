package com.arc_studio.brick_lib.client.command;

import com.arc_studio.brick_lib.tools.ChatUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class ClientCommands {
	private ClientCommands() {
	}

	public static @Nullable CommandDispatcher<ClientSuggestionProvider> getActiveDispatcher() {
		return ClientCommandInternals.getActiveDispatcher();
	}

	public static LiteralArgumentBuilder<ClientSuggestionProvider> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}

	public static <T> RequiredArgumentBuilder<ClientSuggestionProvider, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}

	public static void sendFeedback(Component message){
		ChatUtils.clientMessage(message,true);
	}

	public static void sendError(Component message){
		ChatUtils.clientMessage(Component.literal("").append(message).withStyle(ChatFormatting.RED),true);
	}
}
