package com.arc_studio.brick_lib.api.core;

import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.api.tools.ItemUtils;
import com.arc_studio.brick_lib.api.core.packs.MaterialPack;
import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.ArmorSuitEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * 盔甲套装效果
 */
public class ArmorSuit {
    final HashSet<UUID> players = new HashSet<>();
    private final ItemStack helmet, chestplate, leggings, boots;
    private final String id;
    private boolean enabled = true;

    public ArmorSuit(String id, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.id = id;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }


    public static ArmorSuit fromPack(String id, MaterialPack materialPack) {
        Item helmet = materialPack.get(MaterialPack.ItemVariant.HELMET);
        Item chestplate = materialPack.get(MaterialPack.ItemVariant.CHESTPLATE);
        Item leggings = materialPack.get(MaterialPack.ItemVariant.LEGGINGS);
        Item boots = materialPack.get(MaterialPack.ItemVariant.BOOTS);
        if (helmet == null || chestplate == null || leggings == null || boots == null) {
            BrickLib.LOGGER.error("ArmorSuit {} is not complete", id);
            return null;
        }
        return new ArmorSuit(id, helmet.getDefaultInstance(), chestplate.getDefaultInstance(), leggings.getDefaultInstance(), boots.getDefaultInstance());
    }

    public final boolean isComplete(Player player) {
        ItemStack item1 = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack item2 = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack item3 = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack item4 = player.getItemBySlot(EquipmentSlot.FEET);
        return ItemUtils.equal(item1, helmet) &&
                ItemUtils.equal(item2, chestplate) &&
                ItemUtils.equal(item3, leggings) &&
                ItemUtils.equal(item4, boots);
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private boolean contains(Player player) {
        return players.contains(player.getUUID());
    }

    public void onArmorSuitCompleted(Player player) {
    }

    public void onArmorSuitTick(Player player) {

    }

    public void onArmorSuitUnset(Player player) {
    }

    public final void tick(Player player) {
        if (!enabled) return;
        if (isComplete(player)) {
            if (!contains(player)) {
                if (!BrickEventBus.postEvent(new ArmorSuitEvent.Complete(player, this))) {
                    onArmorSuitCompleted(player);
                }
                players.add(player.getUUID());
            }
            if (!BrickEventBus.postEvent(new ArmorSuitEvent.Tick(player, this))) {
                onArmorSuitTick(player);
            }
        } else {
            if (contains(player)) {
                if (!BrickEventBus.postEvent(new ArmorSuitEvent.Unset(player, this))) {
                    onArmorSuitUnset(player);

                }
                players.remove(player.getUUID());
            }
        }
    }

    public ItemStack leggings() {
        return leggings;
    }

    public String id() {
        return id;
    }

    public ItemStack helmet() {
        return helmet;
    }

    public ItemStack chestplate() {
        return chestplate;
    }

    public ItemStack boots() {
        return boots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArmorSuit armorSuit)) return false;
        return this.id.equals(armorSuit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(helmet, chestplate, leggings, boots);
    }
}
