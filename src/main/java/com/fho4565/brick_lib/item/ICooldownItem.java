package com.fho4565.brick_lib.item;

import com.fho4565.brick_lib.registry.DataComponentRegister;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponentPatch;
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

    String COOLDOWN_TAG = "time";
    String COOLDOWN_MAX_TAG = "max_time";
    String TICK_TAG = "tick";
    String AUTO_TAG = "auto";
    String RENDER_BAR_TAG = "render_bar";
    String RENDER_BAR_WHEN_ENDS_TAG = "render_bar_when_ends";
    /**
     * 最大冷却时间
     */
    int cooldownTime();

    private void init(ItemStack stack) {
        if (stack.getComponents().get(DataComponentRegister.CooldownType.get()) == null) {
            stack.applyComponents(DataComponentPatch.builder()
                    .set(DataComponentRegister.CooldownType.get(),
                            new Cooldown(cooldownTime(), cooldownTime(), true, false, true, false))
                    .build());
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
        Cooldown cooldown = stack.get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            return cooldown.currentCd;
        }
        return 0;
    }

    /**
     * 使当前物品栈开始冷却，如果物品栈正处于冷却状态，则重新开始冷却
     */
    default void startCooldown(Player player, ItemStack stack) {
        init(stack);
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            stack.update(DataComponentRegister.CooldownType.get(), cooldown, cooldown1 -> {
                cooldown1.setCurrentCd(cooldown1.maxTime);
                return cooldown1;
            });
        }
        onCooldownStart(player, stack);
    }

    /**
     * 使当前物品栈停止冷却，不会触发{@link #onCooldownEnd(Player, ItemStack)}
     */
    default void stopCooldown(ItemStack stack) {
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            stack.update(DataComponentRegister.CooldownType.get(), cooldown, cooldown1 -> {
                cooldown1.setCurrentCd(0);
                return cooldown1;
            });
        }
    }

    /**
     * 是否应该继续调用{@link #tickCooldown(Player, ItemStack)}来处理物品栈冷却
     */
    default boolean shouldTick(ItemStack stack) {
        init(stack);
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            return cooldown.tick;
        }
        return false;
    }

    default void setShouldTick(ItemStack stack, boolean shouldTick) {
        init(stack);
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            stack.update(DataComponentRegister.CooldownType.get(), cooldown, cooldown1 -> {
                cooldown1.setTick(shouldTick);
                return cooldown1;
            });
        }
    }

    /**
     * 物品栈是否应该自动在冷却结束时调用{@link #onCooldownEnd(Player, ItemStack)}并重新进入冷却
     */
    default boolean autoCooldown(ItemStack stack) {
        init(stack);
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            return cooldown.auto;
        }
        return false;
    }

    default void setAutoCooldown(ItemStack stack, boolean autoCooldown) {
        init(stack);
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            stack.update(DataComponentRegister.CooldownType.get(), cooldown, cooldown1 -> {
                cooldown1.setAuto(autoCooldown);
                return cooldown1;
            });
        }
    }

    /**
     * 处理物品栈的冷却，此函数不应该被其他地方调用，否则会造成冷却计算错误
     */
    default void tickCooldown(Player player, ItemStack stack) {
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (!shouldTick(stack)) {
            return;
        }
        if (isInCooldown(stack)) {
            if (cooldown != null) {
                stack.update(DataComponentRegister.CooldownType.get(), cooldown, cooldown1 -> {
                    int cd = cooldown1.currentCd - 1;  // 改为使用当前组件的cd值
                    cooldown1.setCurrentCd(cd);
                    if (cd == 0) {
                        onCooldownEnd(player, stack);
                    }
                    return cooldown1;
                });
            }
        }
        // 修改判断逻辑，确保仅在冷却自然结束时触发自动重启
        if (getCurrentCooldown(stack) <= 0 && autoCooldown(stack)) {
            startCooldown(player, stack);
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
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            return cooldown.render_bar;
        }
        return true;
    }

    default void setShouldRenderCooldownBar(ItemStack stack, boolean shouldRenderBar) {
        init(stack);
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            stack.update(DataComponentRegister.CooldownType.get(), cooldown, cooldown1 -> {
                cooldown1.setRender_bar(shouldRenderBar);
                return cooldown1;
            });
        }
    }

    /**
     * 当物品不处于冷却状态时，冷却条是否应该继续被渲染
     */
    default boolean renderCooldownBarWhenEnds(ItemStack stack) {
        init(stack);
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            return cooldown.render_bar_when_ends;
        }
        return false;
    }

    default void setRenderCooldownBarWhenEnds(ItemStack stack, boolean shouldRenderBarWhenEnds) {
        init(stack);
        Cooldown cooldown = stack.getComponents().get(DataComponentRegister.CooldownType.get());
        if (cooldown != null) {
            stack.update(DataComponentRegister.CooldownType.get(), cooldown, cooldown1 -> {
                cooldown1.setRender_bar_when_ends(shouldRenderBarWhenEnds);
                return cooldown1;
            });
        }
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
