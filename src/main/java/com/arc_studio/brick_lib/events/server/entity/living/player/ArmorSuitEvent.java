package com.arc_studio.brick_lib.events.server.entity.living.player;

import com.arc_studio.brick_lib.api.core.ArmorSuit;
import com.arc_studio.brick_lib.api.event.BaseEvent;
import net.minecraft.world.entity.player.Player;

public class ArmorSuitEvent extends BaseEvent {
    protected ArmorSuit armorSuit;
    protected Player player;

    public ArmorSuit armorSuit() {
        return armorSuit;
    }

    public Player player() {
        return player;
    }

    public ArmorSuitEvent(Player player, ArmorSuit armorSuit) {
        this.player = player;
        this.armorSuit = armorSuit;
    }

    public static class Complete extends ArmorSuitEvent {
        public Complete(Player player, ArmorSuit armorSuit) {
            super(player, armorSuit);
        }
    }

    public static class Tick extends ArmorSuitEvent {
        public Tick(Player player, ArmorSuit armorSuit) {
            super(player, armorSuit);
        }
    }

    public static class Unset extends ArmorSuitEvent {
        public Unset(Player player, ArmorSuit armorSuit) {
            super(player, armorSuit);
        }
    }
}
