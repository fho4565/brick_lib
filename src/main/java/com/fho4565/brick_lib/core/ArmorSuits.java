package com.fho4565.brick_lib.core;

import net.minecraft.world.entity.player.Player;

import java.util.concurrent.ConcurrentLinkedDeque;

public class ArmorSuits {
    private static final ConcurrentLinkedDeque<ArmorSuit> armorSuits = new ConcurrentLinkedDeque<>();

    public static void tick(Player player) {
        for (ArmorSuit armorSuit : armorSuits) {
            armorSuit.tick(player);
        }
    }

    public static void init() {
        armorSuits.clear();
    }
}
