package com.arc_studio.brick_lib.core;

import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public record CommandSelectorOption(String name, EntitySelectorOptions.Modifier handler, Predicate<EntitySelectorParser> canUse, Component description) {
}
