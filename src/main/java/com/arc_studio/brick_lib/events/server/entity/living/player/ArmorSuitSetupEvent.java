package com.arc_studio.brick_lib.events.server.entity.living.player;


import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.core.ArmorSuit;

import java.util.HashSet;

public class ArmorSuitSetupEvent extends BaseEvent {
    protected HashSet<ArmorSuit> armorSuits;
    public ArmorSuitSetupEvent(HashSet<ArmorSuit> armorSuits){
        this.armorSuits = armorSuits;
    }

    public void addNewArmorSuit(ArmorSuit armorSuit){
        armorSuits.add(armorSuit);
    }
    public void removeArmorSuit(String id){
        armorSuits.removeIf(armorSuit -> armorSuit.id().equals(id));
    }
    public void removeArmorSuit(ArmorSuit armorSuit){
        armorSuits.removeIf(armorSuit1 -> armorSuit1.id().equals(armorSuit.id()));
    }
}
