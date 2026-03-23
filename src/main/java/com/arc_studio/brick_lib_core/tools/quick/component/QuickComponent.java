package com.arc_studio.brick_lib_core.tools.quick.component;

import com.arc_studio.brick_lib_api.core.data.ResourceID;
import com.arc_studio.brick_lib_core.tools.ComponentUtils;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.util.UUID;

public class QuickComponent {

    private QuickComponent() {
    }

    public static Builder plainText(String content) {
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

    public static Builder blockNBT(String path, boolean interpreting, String pos, @Nullable Builder separator) {
        return new Builder(ComponentUtils.blockNBT(path, interpreting, pos, separator != null ? separator.build() : null));
    }

    public static Builder entityNBT(String path, boolean interpreting, String selector, @Nullable Builder separator) {
        return new Builder(ComponentUtils.entityNBT(path, interpreting, selector, separator != null ? separator.build() : null));
    }

    public static Builder storageNBT(String path, boolean interpreting, ResourceID id, @Nullable Builder separator) {
        return new Builder(ComponentUtils.storageNBT(path, interpreting, id, separator != null ? separator.build() : null));
    }

    public static class Builder {
        protected MutableComponent component = ComponentUtils.empty();
        protected MutableComponent last;

        protected Builder(MutableComponent initial) {
            last = initial;
        }

        public Builder append(Builder text) {
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

        public Builder blockNBT(String path, boolean interpreting, String pos, @Nullable Builder separator) {
            push(ComponentUtils.blockNBT(path, interpreting, pos, separator != null ? separator.build() : null));
            return this;
        }

        public Builder entityNBT(String path, boolean interpreting, String selector, @Nullable Builder separator) {
            push(ComponentUtils.entityNBT(path, interpreting, selector, separator != null ? separator.build() : null));
            return this;
        }

        public Builder storageNBT(String path, boolean interpreting, ResourceID id, @Nullable Builder separator) {
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
            this.last.withStyle(style -> style.withClickEvent(
                    clickEvent(ClickEvent.Action.OPEN_URL, url)
            ));
            return this;
        }

        public Builder openFile(String file) {
            this.last.withStyle(style -> style.withClickEvent(
                    clickEvent(ClickEvent.Action.OPEN_FILE, file)
            ));
            return this;
        }

        public Builder runCommand(String command) {
            this.last.withStyle(style -> style.withClickEvent(
                    clickEvent(ClickEvent.Action.RUN_COMMAND, command)
            ));
            return this;
        }

        public Builder suggestCommand(String command) {
            this.last.withStyle(style -> style.withClickEvent(
                    clickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
            ));
            return this;
        }

        public Builder changePage(int page) {
            this.last.withStyle(style -> style.withClickEvent(
                    clickEvent(ClickEvent.Action.CHANGE_PAGE, String.valueOf(page))
            ));
            return this;
        }

        public Builder copyToClipboard(String text) {
            this.last.withStyle(style -> style.withClickEvent(
                    clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text)
            ));
            return this;
        }

        public Builder showText(Builder text) {
            this.last.withStyle(style -> style.withHoverEvent(
                    hoverEvent(HoverEvent.Action.SHOW_TEXT, text.build())
            ));
            return this;
        }

        public Builder showItem(ItemStack itemStack) {
            this.last.withStyle(style -> style.withHoverEvent(
                    hoverEvent(HoverEvent.Action.SHOW_ITEM, itemStack)
            ));
            return this;
        }

        public Builder showEntity(EntityType<?> entityType, UUID uuid, Builder name) {
            this.last.withStyle(style -> style.withHoverEvent(
                    hoverEvent(HoverEvent.Action.SHOW_ENTITY, entityType, uuid, name.build())
            ));
            return this;
        }

        public MutableComponent build() {
            if (this.last != null) {
                component.append(this.last);
            }
            return component;
        }
    }

    protected static ClickEvent clickEvent(ClickEvent.Action action,Object obj){
        return switch (action) {
            case OPEN_URL -> /*? if > 1.21.4 {*/ /*new ClickEvent.OpenUrl((URI) obj); *//*?} else {*/ new ClickEvent(ClickEvent.Action.OPEN_URL, (String) obj); /*?}*/
            case OPEN_FILE -> /*? if >1.21.4 {*/ /*new ClickEvent.OpenFile((File) obj); *//*?} else {*/ new ClickEvent(ClickEvent.Action.OPEN_FILE, (String) obj); /*?}*/
            case RUN_COMMAND -> /*? if >1.21.4 {*/ /*new ClickEvent.RunCommand((String) obj); *//*?} else {*/ new ClickEvent(ClickEvent.Action.RUN_COMMAND, (String) obj); /*?}*/
            case SUGGEST_COMMAND -> /*? if >1.21.4 {*/ /*new ClickEvent.SuggestCommand((String) obj); *//*?} else {*/ new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,(String) obj); /*?}*/
            case CHANGE_PAGE -> /*? if >1.21.4 {*/ /*new ClickEvent.ChangePage((Integer) obj); *//*?} else {*/ new ClickEvent(ClickEvent.Action.CHANGE_PAGE, (String) obj); /*?}*/
            case COPY_TO_CLIPBOARD -> /*? if >1.21.4 {*/ /*new ClickEvent.CopyToClipboard((String) obj); *//*?} else {*/ new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,(String) obj); /*?}*/
        };
    }
    protected static HoverEvent hoverEvent(HoverEvent.Action action,Object ...obj){
        //? if >1.21.4 {
        /*return switch (action) {
            case HoverEvent.Action.SHOW_TEXT -> new HoverEvent.ShowText(((Builder) obj[0]).build());
            case HoverEvent.Action.SHOW_ITEM -> new HoverEvent.ShowItem((ItemStack) obj[0]);
            case HoverEvent.Action.SHOW_ENTITY ->new HoverEvent.ShowEntity(new HoverEvent.EntityTooltipInfo((EntityType<?>) obj[0], (UUID) obj[1], ((Builder) obj[2]).build()));
        };
        *///? } else {
        //~ if < 1.20.4 'getSerializedName()' -> 'getName()' {
        if (action.getName().equals(HoverEvent.Action.SHOW_TEXT.getName())) {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, ((Builder) obj[0]).build());
        } else if (action.getName().equals(HoverEvent.Action.SHOW_ITEM.getName())) {
            return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo((ItemStack) obj[0]));
        } else if (action.getName().equals(HoverEvent.Action.SHOW_ENTITY.getName())) {
            return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo((EntityType<?>) obj[0], (UUID) obj[1], ((Builder) obj[2]).build()));
        }
        //~}
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentUtils.plainText("[QuickComponent][ERROR] : not a valid hover event action"));
        //? }
    }
}
