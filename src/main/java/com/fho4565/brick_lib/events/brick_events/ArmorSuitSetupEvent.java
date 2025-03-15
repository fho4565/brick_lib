package com.fho4565.brick_lib.events.brick_events;

import com.fho4565.brick_lib.core.ArmorSuit;
import net.minecraftforge.eventbus.api.Event;

import java.util.concurrent.ConcurrentLinkedDeque;

public class ArmorSuitSetupEvent extends Event {
    protected ConcurrentLinkedDeque<ArmorSuit> armorSuits;
    public ArmorSuitSetupEvent(ConcurrentLinkedDeque<ArmorSuit> armorSuits){
        this.armorSuits = armorSuits;
    }

    public ConcurrentLinkedDeque<ArmorSuit> armorSuits() {
        return armorSuits;
    }
}
