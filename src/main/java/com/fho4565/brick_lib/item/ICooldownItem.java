package com.fho4565.brick_lib.item;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

/**
 * <p>此接口被用来处理物品栈冷却，冷却的数据保存在物品栈的标签中。</p>
 * <p>当你把正在冷却的物品栈分开时，冷却依旧保留，之后两个物品栈分别计算各自的冷却时间</p>
 * <p>默认没有物品装饰器，需自行注册</p>
 *
 * @author fho4565
 */
public interface ICooldownItem {
    /**
     * 物品装饰器，注册到物品上来显示冷却条
     * */
    IItemDecorator DEFAULT_DECORATOR = (guiGraphics, font, stack, x, y) -> {
        if(stack.getItem() instanceof ICooldownItem cooldownItem) {
            if (cooldownItem.shouldRenderCooldownBar(stack)) {
                guiGraphics.pose().pushPose();
                if (cooldownItem.isInCooldown(stack) || cooldownItem.renderCooldownBarWhenEnds(stack)) {
                    int barWidth = cooldownItem.cooldownBarWidth(stack);
                    int barColor = cooldownItem.cooldownBarColor(stack);
                    guiGraphics.fill(RenderType.guiOverlay(), x, y, x + 1, y + 15, -16777216);
                    guiGraphics.fill(RenderType.guiOverlay(), x, y + 15, x + 1, y + 15 - barWidth, barColor | -16777216);
                }
                guiGraphics.pose().popPose();
            }
            return true;
        }
        return false;
    };

    String COOLDOWN_ALL_TAG = "cooldown";
    String COOLDOWN_TAG = "time";
    String TICK_TAG = "tick";
    String AUTO_TAG = "auto";
    String RENDER_BAR_TAG = "render_bar";
    String RENDER_BAR_WHEN_ENDS_TAG = "render_bar_when_ends";
    /**
     * 最大冷却时间
     */
    int cooldownTime();

    private void init(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(COOLDOWN_ALL_TAG)) {
            tag.put(COOLDOWN_ALL_TAG, new CompoundTag());
        }
        if (!tag.getCompound(COOLDOWN_ALL_TAG).contains(COOLDOWN_TAG)) {
            tag.getCompound(COOLDOWN_ALL_TAG).putInt(COOLDOWN_TAG, 0);
        }
        if (!tag.getCompound(COOLDOWN_ALL_TAG).contains(TICK_TAG)) {
            tag.getCompound(COOLDOWN_ALL_TAG).putBoolean(TICK_TAG, true);
        }
        if (!tag.getCompound(COOLDOWN_ALL_TAG).contains(AUTO_TAG)) {
            tag.getCompound(COOLDOWN_ALL_TAG).putBoolean(AUTO_TAG, false);
        }
    }

    /**
     * 判断当前物品栈是否处于冷却状态
     */
    default boolean isInCooldown(ItemStack stack) {
        return getCurrentCooldown(stack) > 0;
    }

    /**
     * 获取当前物品栈的冷却时间
     */
    default int getCurrentCooldown(ItemStack stack) {
        return stack.getOrCreateTag().getCompound(COOLDOWN_ALL_TAG).getInt(COOLDOWN_TAG);
    }

    /**
     * 使当前物品栈开始冷却，如果物品栈正处于冷却状态，则重新开始冷却
     */
    default void startCooldown(Player player, ItemStack stack) {
        init(stack);
        stack.getOrCreateTag().getCompound(COOLDOWN_ALL_TAG).putInt(COOLDOWN_TAG, cooldownTime());
        onCooldownStart(player, stack);
    }

    /**
     * 使当前物品栈停止冷却，不会触发{@link #onCooldownEnd(Player, ItemStack)}
     */
    default void stopCooldown(ItemStack stack) {
        stack.getOrCreateTag().getCompound(COOLDOWN_ALL_TAG).putInt(COOLDOWN_TAG, 0);
    }

    /**
     * 是否应该继续调用{@link #tickCooldown(Player, ItemStack)}来处理物品栈冷却
     */
    default boolean shouldTick(ItemStack stack) {
        init(stack);
        return stack.getOrCreateTag().getCompound(COOLDOWN_ALL_TAG).getBoolean(TICK_TAG);
    }

    default void setShouldTick(ItemStack stack, boolean shouldTick) {
        init(stack);
        stack.getOrCreateTag().getCompound(COOLDOWN_ALL_TAG).putBoolean(TICK_TAG, shouldTick);
    }

    /**
     * 物品栈是否应该自动在冷却结束时调用{@link #onCooldownEnd(Player, ItemStack)}并重新进入冷却
     */
    default boolean autoCooldown(ItemStack stack) {
        init(stack);
        return stack.getOrCreateTag().getCompound(COOLDOWN_ALL_TAG).getBoolean(AUTO_TAG);
    }

    default void setAutoCooldown(ItemStack stack, boolean autoCooldown) {
        init(stack);
        stack.getOrCreateTag().getCompound(COOLDOWN_ALL_TAG).putBoolean(AUTO_TAG, autoCooldown);
    }

    /**
     * 处理物品栈的冷却，此函数不应该被其他地方调用，否则会造成冷却计算错误
     */
    default void tickCooldown(Player player, ItemStack stack) {
        if (!shouldTick(stack)) {
            return;
        }
        if (isInCooldown(stack)) {
            CompoundTag tag = stack.getOrCreateTag().getCompound(COOLDOWN_ALL_TAG);
            int value = tag.getInt(COOLDOWN_TAG) - 1;
            if (value <= 0) {
                onCooldownEnd(player, stack);
            }
            tag.putInt(COOLDOWN_TAG, value);
        }
        if (!isInCooldown(stack)) {
            if (autoCooldown(stack)) {
                startCooldown(player, stack);
            }
        }
    }

    /**
     * 当物品栈开始冷却时调用
     */
    default void onCooldownStart(Player player, ItemStack itemStack) {

    }

    /**
     * 当物品栈冷却结束时调用
     */
    default void onCooldownEnd(Player player, ItemStack itemStack) {

    }

    default boolean shouldRenderCooldownBar(ItemStack stack) {
        init(stack);
        return stack.getOrCreateTag().getBoolean(RENDER_BAR_TAG);
    }

    default void setShouldRenderCooldownBar(ItemStack stack, boolean shouldRenderBar) {
        init(stack);
        stack.getOrCreateTag().putBoolean(RENDER_BAR_TAG, shouldRenderBar);
    }

    /**
     * 当物品不处于冷却状态时，冷却条是否应该继续被渲染
     */
    default boolean renderCooldownBarWhenEnds(ItemStack stack) {
        init(stack);
        return stack.getOrCreateTag().getBoolean(RENDER_BAR_WHEN_ENDS_TAG);
    }

    default void setRenderCooldownBarWhenEnds(ItemStack stack, boolean shouldRenderBarWhenEnds) {
        init(stack);
        stack.getOrCreateTag().putBoolean(RENDER_BAR_WHEN_ENDS_TAG, shouldRenderBarWhenEnds);
    }
    /**
     * 冷却条的颜色，默认像原版耐久条一样变化
     */
    default int cooldownBarColor(ItemStack stack) {
        return Mth.hsvToRgb(Math.max(0.0F, 1 - (float) getCurrentCooldown(stack) / cooldownTime()) / 3.0F, 1.0F, 1.0F);
    }

    /**
     * 冷却条的长度，默认以15像素为基准变化
     */
    default int cooldownBarWidth(ItemStack stack) {
        return Math.round(15 - 15 * (float) getCurrentCooldown(stack) / cooldownTime());
    }

}
