package com.fho4565.brick_lib.events.brick_evemts;

import com.fho4565.brick_lib.core.ArmorSuit;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class ArmorSuitEvent extends Event {
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
    public static class Complete extends ArmorSuitEvent{


        public Complete(Player player, ArmorSuit armorSuit) {
            super(player, armorSuit);
        }
    }
    public static class Tick extends ArmorSuitEvent{


        public Tick(Player player, ArmorSuit armorSuit) {
            super(player, armorSuit);
        }
    }
    public static class Unset extends ArmorSuitEvent{


        public Unset(Player player, ArmorSuit armorSuit) {
            super(player, armorSuit);
        }
    }
}
