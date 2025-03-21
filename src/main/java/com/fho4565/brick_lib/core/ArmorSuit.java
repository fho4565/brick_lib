package com.fho4565.brick_lib.core;

import com.fho4565.brick_lib.ItemUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * 盔甲套装效果
 * */
public abstract class ArmorSuit {
    private final ItemStack helmet, chestplate, leggings, boots;
    private final String id;
    final HashSet<UUID> players = new HashSet<>();

    public ArmorSuit(String id, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.id = id;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public final boolean isComplete(Player player) {
        ItemStack item1 = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack item2 = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack item3 = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack item4 = player.getItemBySlot(EquipmentSlot.FEET);
        return ItemUtils.itemStackEqualsWithoutDamageValue(item1,helmet) &&
                ItemUtils.itemStackEqualsWithoutDamageValue(item2,chestplate) &&
                ItemUtils.itemStackEqualsWithoutDamageValue(item3,leggings) &&
                ItemUtils.itemStackEqualsWithoutDamageValue(item4,boots);
    }


    private boolean contains(Player player) {
        return players.contains(player.getUUID());
    }

    public void onArmorSuitCompleted(Player player) {
    }

    public void onArmorSuitTick(Player player){

    }

    public void onArmorSuitUnset(Player player) {
    }

    public void tick(Player player){
        if (isComplete(player)) {
            if (!contains(player)) {
                onArmorSuitCompleted(player);
                players.add(player.getUUID());
            }
            onArmorSuitTick(player);
        } else {
            if (contains(player)) {
                onArmorSuitUnset(player);
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
