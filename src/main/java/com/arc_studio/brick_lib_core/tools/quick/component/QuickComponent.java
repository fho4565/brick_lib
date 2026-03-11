package com.arc_studio.brick_lib_core.tools.quick.component;

import com.arc_studio.brick_lib_api.core.data.ResourceID;
import com.arc_studio.brick_lib_core.tools.ComponentUtils;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class QuickComponent {

    private QuickComponent() {
    }

    public static Builder plaintext(String content) {
        return new Builder(ComponentUtils.plainText(content));
    }

    public static Builder translate(String key, Object... value) {
        return new Builder(ComponentUtils.translate(key, value));
    }

    public static Builder translateAuto(String content, Object... value) {
        return new Builder(ComponentUtils.plainTextOrTranslate(content, value));
    }

    public static Builder keybind(String key) {
        return new Builder(ComponentUtils.keybind(key));
    }

    public static Builder score(String objective, String selector) {
        return new Builder(ComponentUtils.scoreboard(objective, selector));
    }

    public static Builder blockNBT(String path, boolean interpreting, String pos, @Nullable TextBase separator) {
        return new Builder(ComponentUtils.blockNBT(path, interpreting, pos, separator != null ? separator.build() : null));
    }

    public static Builder entityNBT(String path, boolean interpreting, String selector, @Nullable TextBase separator) {
        return new Builder(ComponentUtils.entityNBT(path, interpreting, selector, separator != null ? separator.build() : null));
    }

    public static Builder storageNBT(String path, boolean interpreting, ResourceID id, @Nullable TextBase separator) {
        return new Builder(ComponentUtils.storageNBT(path, interpreting, id, separator != null ? separator.build() : null));
    }

    public static class Builder {
        protected MutableComponent component;
        protected MutableComponent last;

        protected Builder(MutableComponent initial) {
            component = initial;
        }

        public Builder append(TextBase text) {
            push(text.build());
            return this;
        }

        public Builder append(Component component) {
            push(component.copy());
            return this;
        }

        private void push(MutableComponent textBase) {
            if (this.last != null) {
                component.append(this.last);
            }
            this.last = textBase;
        }

        public Builder plaintext(String content) {
            push(ComponentUtils.plainText(content));
            return this;
        }

        public Builder translate(String key, Object... value) {
            push(ComponentUtils.translate(key, value));
            return this;
        }

        public Builder translateAuto(String content, Object... value) {
            push(ComponentUtils.plainTextOrTranslate(content, value));
            return this;
        }

        public Builder keybind(String key) {
            push(ComponentUtils.keybind(key));
            return this;
        }

        public Builder score(String objective, String selector) {
            push(ComponentUtils.scoreboard(objective, selector));
            return this;
        }

        public Builder blockNBT(String path, boolean interpreting, String pos, @Nullable TextBase separator) {
            push(ComponentUtils.blockNBT(path, interpreting, pos, separator != null ? separator.build() : null));
            return this;
        }

        public Builder entityNBT(String path, boolean interpreting, String selector, @Nullable TextBase separator) {
            push(ComponentUtils.entityNBT(path, interpreting, selector, separator != null ? separator.build() : null));
            return this;
        }

        public Builder storageNBT(String path, boolean interpreting, ResourceID id, @Nullable TextBase separator) {
            push(ComponentUtils.storageNBT(path, interpreting, id, separator != null ? separator.build() : null));
            return this;
        }

        public Builder color(@Nullable Integer rgb) {
            if (rgb != null) {
                this.last.withStyle(style -> style.withColor(rgb));
            }
            return this;
        }

        public Builder bold(boolean bold) {
            this.last.withStyle(style -> style.withBold(bold));
            return this;
        }

        public Builder italic(boolean italic) {
            this.last.withStyle(style -> style.withItalic(italic));
            return this;
        }

        public Builder underlined(boolean underlined) {
            this.last.withStyle(style -> style.withUnderlined(underlined));
            return this;
        }

        public Builder strikethrough(boolean strikethrough) {
            this.last.withStyle(style -> style.withStrikethrough(strikethrough));
            return this;
        }

        public Builder obfuscated(boolean obfuscated) {
            this.last.withStyle(style -> style.withObfuscated(obfuscated));
            return this;
        }

        public Builder insertion(String insertion) {
            this.last.withStyle(style -> style.withInsertion(insertion));
            return this;
        }

        public Builder font(ResourceID font) {
            this.last.withStyle(style -> style.withFont(font));
            return this;
        }

        public Builder openUrl(String url) {
            this.last.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
            return this;
        }

        public Builder openFile(String file) {
            this.last.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file)));
            return this;
        }

        public Builder runCommand(String command) {
            this.last.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
            return this;
        }

        public Builder suggestCommand(String command) {
            this.last.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
            return this;
        }

        public Builder changePage(int page) {
            this.last.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE,
                    String.valueOf(page))));
            return this;
        }

        public Builder copyToClipboard(String text) {
            this.last.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text)));
            return this;
        }

        public Builder showText(TextBase text) {
            this.last.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.build())));
            return this;
        }

        public Builder showItem(ItemStack itemStack) {
            this.last.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                    new HoverEvent.ItemStackInfo(itemStack))));
            return this;
        }

        public Builder showEntity(EntityType<?> entityType, UUID uuid, TextBase name) {
            this.last.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY,
                    new HoverEvent.EntityTooltipInfo(entityType, uuid, name.build()))));
            return this;
        }

        public MutableComponent build() {
            if (this.last != null) {
                component.append(this.last);
            }
            return component;
        }
    }

    public static abstract class TextBase {
        protected String content;
        @Nullable
        protected TextColor color = null;
        protected boolean bold = false;
        protected boolean italic = false;
        protected boolean underlined = false;
        protected boolean strikethrough = false;
        protected boolean obfuscated = false;
        @Nullable
        protected ClickEvent clickEvent = null;
        @Nullable
        protected HoverEvent hoverEvent = null;
        @Nullable
        protected String insertion = null;
        @Nullable
        protected ResourceID font = null;


        public TextBase(String content) {
            this.content = content;
        }

        protected abstract MutableComponent componentType();

        protected MutableComponent build() {
            MutableComponent componentedType = componentType();
            componentedType.withStyle(style -> {
                if (color != null) {
                    style.withColor(color);
                }
                style.withBold(bold);
                style.withItalic(italic);
                style.withUnderlined(underlined);
                style.withStrikethrough(strikethrough);
                style.withObfuscated(obfuscated);
                style.withFont(font);
                style.withClickEvent(clickEvent);
                style.withHoverEvent(hoverEvent);
                style.withInsertion(insertion);
                return style;
            });
            return componentedType;
        }

        public TextBase color(TextColor color) {
            this.color = color;
            return this;
        }

        public TextBase bold(boolean bold) {
            this.bold = bold;
            return this;
        }

        public TextBase italic(boolean italic) {
            this.italic = italic;
            return this;
        }

        public TextBase underlined(boolean underlined) {
            this.underlined = underlined;
            return this;
        }

        public TextBase strikethrough(boolean strikethrough) {
            this.strikethrough = strikethrough;
            return this;
        }

        public TextBase obfuscated(boolean obfuscated) {
            this.obfuscated = obfuscated;
            return this;
        }

        public TextBase insertion(String insertion) {
            this.insertion = insertion;
            return this;
        }

        public TextBase font(ResourceID font) {
            this.font = font;
            return this;
        }

        public TextBase openUrl(String url) {
            clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            return this;
        }

        public TextBase openFile(String file) {
            clickEvent = new ClickEvent(ClickEvent.Action.OPEN_FILE, file);
            return this;
        }

        public TextBase runCommand(String command) {
            clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            return this;
        }

        public TextBase suggestCommand(String command) {
            clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            return this;
        }

        public TextBase changePage(int page) {
            clickEvent = new ClickEvent(ClickEvent.Action.CHANGE_PAGE, String.valueOf(page));
            return this;
        }

        public TextBase copyToClipboard(String text) {
            clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text);
            return this;
        }

        public TextBase showText(TextBase text) {
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.build());
            return this;
        }

        public TextBase showItem(ItemStack itemStack) {
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(itemStack));
            return this;
        }

        public TextBase showEntity(EntityType<?> entityType, UUID uuid, TextBase name) {
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ENTITY,
                    new HoverEvent.EntityTooltipInfo(entityType, uuid, name.build()));
            return this;
        }

    }

    public static class Plain extends TextBase {

        public Plain(String content) {
            super(content);
        }

        @Override
        protected MutableComponent componentType() {
            return ComponentUtils.plainText(content);
        }
    }

    public static class Translate extends TextBase {

        private final Object[] value;

        public Translate(String key, Object... value) {
            super(key);
            this.value = value;
        }

        @Override
        protected MutableComponent componentType() {
            return ComponentUtils.translate(content, value);
        }
    }

    public static class Keybind extends TextBase {

        public Keybind(String id) {
            super(id);
        }

        @Override
        protected MutableComponent componentType() {
            return ComponentUtils.keybind(content);
        }
    }

    public static class Scoreboard extends TextBase {

        private final String selector;

        public Scoreboard(String objective, String selector) {
            super(objective);
            this.selector = selector;
        }

        @Override
        protected MutableComponent componentType() {
            return ComponentUtils.scoreboard(content, selector);
        }
    }

    public abstract static class NBTBase extends TextBase {
        boolean interpret = false;
        TextBase separator = new Plain(",");

        public NBTBase(String path) {
            super(path);
        }

        public NBTBase interpret(boolean interpret) {
            this.interpret = interpret;
            return this;
        }

        public NBTBase separator(TextBase text) {
            this.separator = text;
            return this;
        }
    }

    public static class BlockNBT extends NBTBase {

        private final String pos;

        public BlockNBT(String path, String pos) {
            super(path);
            this.pos = pos;
        }

        @Override
        protected MutableComponent componentType() {
            return ComponentUtils.blockNBT(content, interpret, pos, separator.componentType());
        }
    }

    public static class EntityNBT extends NBTBase {

        private final String selector;

        public EntityNBT(String path, String selector) {
            super(path);
            this.selector = selector;
        }

        @Override
        protected MutableComponent componentType() {
            return ComponentUtils.entityNBT(content, interpret, selector, separator.componentType());
        }
    }

    public static class StorageNBT extends NBTBase {

        private final ResourceID resourceID;

        public StorageNBT(String path, ResourceID resourceID) {
            super(path);
            this.resourceID = resourceID;
        }

        @Override
        protected MutableComponent componentType() {
            return ComponentUtils.storageNBT(content, interpret, resourceID, separator.componentType());
        }
    }

}
