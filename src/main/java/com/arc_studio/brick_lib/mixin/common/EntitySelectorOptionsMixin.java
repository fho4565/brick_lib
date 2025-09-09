package com.arc_studio.brick_lib.mixin.common;

import com.arc_studio.brick_lib.register.BrickRegistries;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EntitySelectorOptions.class)
public abstract class EntitySelectorOptionsMixin {

    @Shadow
    private static void register(String id, EntitySelectorOptions.Modifier handler, Predicate<EntitySelectorParser> predicate, Component tooltip) {
    }

    @Inject(method = "get", at = @At("HEAD"), remap = false)
    private static void getMixin(EntitySelectorParser parser, String id, int cursor, CallbackInfoReturnable<EntitySelectorOptions.Modifier> cir) {
        BrickRegistries.COMMAND_ENTITY_SELECTOR_OPTIONS.foreachValueAndClear(option -> {
            register(option.name(), option.handler(), option.canUse(), option.description());
        });
    }

    @Unique
    private EntitySelectorOptions getThis() {
        return (EntitySelectorOptions) (Object) this;
    }
}