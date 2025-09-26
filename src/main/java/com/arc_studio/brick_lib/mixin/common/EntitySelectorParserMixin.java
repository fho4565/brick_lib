package com.arc_studio.brick_lib.mixin.common;

import com.arc_studio.brick_lib.core.CommandEntitySelector;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.commands.arguments.selector.EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE;

@Mixin(EntitySelectorParser.class)
public abstract class EntitySelectorParserMixin {
    @Shadow private boolean usesSelectors;

    @Shadow private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;

    @Shadow protected abstract CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer);

    @Shadow @Final private StringReader reader;

    @Shadow private int maxResults;

    @Shadow private boolean includesEntities;

    @Shadow private BiConsumer<Vec3, List<? extends Entity>> order;

    @Shadow public abstract void limitToType(EntityType<?> type);

    @Shadow private boolean currentEntity;

    @Shadow @Final public static DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE;

    @Shadow @Final public static BiConsumer<Vec3, List<? extends Entity>> ORDER_RANDOM;

    @Shadow @Final public static BiConsumer<Vec3, List<? extends Entity>> ORDER_NEAREST;

    @Shadow private Predicate<Entity> predicate;

    @Shadow protected abstract CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer);

    @Shadow protected abstract CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer);

    @Shadow protected abstract void parseOptions() throws CommandSyntaxException;

    @Shadow protected abstract CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer);

    @Shadow @Final public static SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS;

    @Shadow protected abstract CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer);

    @Shadow @Final public static DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE;

    @WrapWithCondition(method = "parse", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/EntitySelectorParser;parseSelector()V"))
    public boolean parseSelectorMixin(EntitySelectorParser instance) throws CommandSyntaxException {
        this.usesSelectors = true;
        this.suggestions = this::suggestSelector;
        if (!this.reader.canRead()) {
            throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
        } else {
            int i = this.reader.getCursor();
            StringBuilder stringBuilder = new StringBuilder();
            while (this.reader.canRead()) {
                char nextChar = this.reader.peek();
                if (nextChar == '[' || Character.isWhitespace(nextChar) || nextChar == '\0') {
                    break;
                }
                if (Character.isLetterOrDigit(nextChar) || nextChar == '_') {
                    stringBuilder.append(this.reader.read());
                } else {
                    break;
                }
            }
            String key = stringBuilder.toString();
            boolean find = false;
            for (CommandEntitySelector selector : BrickRegistries.COMMAND_ENTITY_SELECTORS) {
                if(selector.sign().equals(key)){
                    this.maxResults = selector.maxCount();
                    this.includesEntities = selector.includeEntities();
                    this.order = switch (selector.sort()) {
                        case NEAREST -> EntitySelectorParser.ORDER_NEAREST;
                        case FURTHEST -> EntitySelectorParser.ORDER_FURTHEST;
                        case RANDOM -> EntitySelectorParser.ORDER_RANDOM;
                        case ARBITRARY -> EntitySelector.ORDER_ARBITRARY;
                    };
                    if(selector.entityType() != null){
                        this.limitToType(selector.entityType());
                    }
                    this.currentEntity = selector.selfSelector();
                    this.predicate = selector.entityPredicate();
                    find = true;
                    break;
                }
            }
            if (find) {
                this.suggestions = this::suggestOpenOptions;
                if (this.reader.canRead() && this.reader.peek() == '[') {
                    this.reader.skip();
                    this.suggestions = this::suggestOptionsKeyOrClose;
                    this.parseOptions();
                }
                return false;
            } else {
                this.reader.setCursor(i);
                return true;
            }
        }
    }

/*    @WrapWithCondition(method = "parseSelector", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/EntitySelectorParser;parseOptions()V"))
    public boolean parseMixin(EntitySelectorParser instance) throws CommandSyntaxException {
        this.suggestions = this::suggestOptionsKey;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String string = this.reader.readString();
            EntitySelectorOptions.Modifier modifier = get(getThis(), string, i);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor(i);
                throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, string);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = SUGGEST_NOTHING;
            modifier.handle(getThis());
            this.reader.skipWhitespace();
            this.suggestions = this::suggestOptionsNextOrClose;
            if (this.reader.canRead()) {
                if (this.reader.peek() != ',') {
                    if (this.reader.peek() != ']') {
                        throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
                    }
                    break;
                }

                this.reader.skip();
                this.suggestions = this::suggestOptionsKey;
            }
        }

        if (this.reader.canRead()) {
            this.reader.skip();
            this.suggestions = SUGGEST_NOTHING;
        } else {
            throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
        }
        return false;
    }
    private static EntitySelectorOptions.Modifier get(EntitySelectorParser parser, String id, int cursor) throws CommandSyntaxException {
        for (CommandSelectorOption option : BrickRegistries.COMMAND_SELECTOR_OPTIONS) {
            if(option.name().equals(id)){
                if (option.canUse().test(parser)) {
                    System.out.println("EntitySelectorParserMixin.get0");
                    return option.handler();
                } else {
                    throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), id);
                }
            }
        }
        System.out.println("EntitySelectorParserMixin.get1");
        return EntitySelectorOptions.get(parser,id,cursor);
    }*/

    @Inject(method = "fillSelectorSuggestions", at = @At("TAIL"))
    private static void fillSelectorSuggestionsMixin(SuggestionsBuilder builder, CallbackInfo ci) {
        for (CommandEntitySelector selector : BrickRegistries.COMMAND_ENTITY_SELECTORS) {
            builder.suggest("@"+selector.sign(), selector.description());
        }
    }

    @Unique
    private EntitySelectorParser getThis() {
        return (EntitySelectorParser) (Object) this;
    }
}