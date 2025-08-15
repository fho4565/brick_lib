package com.arc_studio.brick_lib.tools;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComponentUtils {
    /**
     * <p>将一个完整的聊天组件组件分成单个字符组成的列表，原先的样式不变</p>
     * <p>当组件是纯文本时，则对纯文本进行操作</p>
     * <p>当组件是翻译文本时，则对翻译结果进行操作</p>
     * <p>当组件是记分板分数时，则返回空</p>
     * <p>当组件是目标选择器时，则对目标选择器文本(@e[distance=..2],@s,等)进行操作</p>
     * <p>当组件是按键绑定时，则对按键绑定对应的键的名字进行操作</p>
     * <p>当组件是NBT标签时，则返回空</p>
     * */
    public static List<Component> splitToChars(Component component){
        Codec<Integer> codec = Codec.of(new Encoder<Integer>() {
            @Override
            public <T> DataResult<T> encode(Integer input, DynamicOps<T> ops, T prefix) {
                return null;
            }
        }, new Decoder<Integer>() {
            @Override
            public <T> DataResult<Pair<Integer, T>> decode(DynamicOps<T> ops, T input) {
                return null;
            }
        });
        RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("man").forGetter(new Function<Object, String>() {
                    @Override
                    public String apply(Object o) {
                        return "";
                    }
                })
        ).apply(instance, s -> null));

        ArrayList<Component> list = new ArrayList<>();
        component.toFlatList().forEach(component1 -> {
            for (char c : component1.getString().toCharArray()) {
                Style style = component1.getStyle();
                list.add(Component.literal(String.valueOf(c)).withStyle(style));
            }
        });
        return list;
    }
    /**
     * <p>将一个完整的聊天组件组件分成多个组件组成的列表，原先的样式不变</p>
     * <p>不同样式的组件会被合并至一行</p>
     * <p>当组件是纯文本时，则对纯文本进行操作</p>
     * <p>当组件是翻译文本时，则对翻译结果进行操作</p>
     * <p>当组件是记分板分数时，则返回空</p>
     * <p>当组件是目标选择器时，则对目标选择器文本(@e[distance=..2],@s,等)进行操作</p>
     * <p>当组件是按键绑定时，则对按键绑定对应的键的名字进行操作</p>
     * <p>当组件是NBT标签时，则返回空</p>
     * @param component 组件
     * @param maxLength 分开的组件内容最大的长度
     * */
    public static List<Component> splitToLines(Component component, int maxLength) {
        if (component == null || maxLength <= 0) {
            return new ArrayList<>();
        }
        List<Component> list = new ArrayList<>();
        List<Component> charsList = splitToChars(component);
        if (charsList.size() <= maxLength) {
            list.add(combineToSingleComponent(charsList));
        } else {
            int index = 0;
            while (index < charsList.size()) {
                List<Component> lineComponents = new ArrayList<>();
                int lineLength = 0;
                while (index < charsList.size() && lineLength < maxLength) {
                    Component charComponent = charsList.get(index);
                    lineComponents.add(charComponent);
                    lineLength++;
                    index++;
                }
                list.add(combineToSingleComponent(lineComponents));
            }
        }
        return list;
    }

    /**
     * <p>将多个聊天组件组件合并成一个组件，原先的样式不变</p>
     * <p>当组件是纯文本时，则对纯文本进行操作</p>
     * <p>当组件是翻译文本时，则对翻译结果进行操作</p>
     * <p>当组件是记分板分数时，则返回空</p>
     * <p>当组件是目标选择器时，则对目标选择器文本(@e[distance=..2],@s,等)进行操作</p>
     * <p>当组件是按键绑定时，则对按键绑定对应的键的名字进行操作</p>
     * <p>当组件是NBT标签时，则返回空</p>
     * */
    public static Component combineToSingleComponent(List<Component> list) {
        MutableComponent components = Component.empty();
        StringBuilder stringBuilder = new StringBuilder(list.get(0).getString());
        Style currentStyle = list.get(0).getStyle();

        for (int i = 1; i < list.size(); i++) {
            Component component = list.get(i);
            if (component.getStyle().equals(currentStyle)) {
                stringBuilder.append(component.getString());
            } else {
                components.append(Component.literal(stringBuilder.toString()).withStyle(currentStyle));
                stringBuilder = new StringBuilder(component.getString());
                currentStyle = component.getStyle();
            }
        }
        components.append(Component.literal(stringBuilder.toString()).withStyle(currentStyle));
        return components;
    }
    /**
     * 创建一个带有渐变色的聊天组件
     * @param content 聊天组件内容
     * @param colorStart 第一个字符的颜色
     * @param colorEnd 最后一个字符的颜色
     * */
    public static Component generateColorGradientComponent(String content,int colorStart,int colorEnd){
        char[] chars = content.toCharArray();
        List<Integer> color = ColorUtils.createGradientColor(colorStart,colorEnd,content.length());
        return component(chars, color);
    }

    @NotNull
    private static Component component(char[] chars, List<Integer> color) {
        MutableComponent components = Component.empty();
        for (int i = 0; i < chars.length; i++) {
            char charComponent = chars[i];
            components.append(Component.literal(String.valueOf(charComponent)).withStyle(Style.EMPTY.withColor(color.get(i))));
        }
        return components;
    }

    /**
     * 用指定渐变颜色创建一个聊天组件
     * @param content 聊天组件内容
     * @param gradientColor 渐变颜色设置
     * */
    public static Component generateColorGradientComponent(String content, ColorUtils.GradientColor gradientColor){
        char[] chars = content.toCharArray();
        List<Integer> color = gradientColor.createGradient().stream().map(Color::getRGB).toList();
        return component(chars, color);
    }

}
