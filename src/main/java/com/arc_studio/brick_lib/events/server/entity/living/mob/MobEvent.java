package com.arc_studio.brick_lib.events.server.entity.living.mob;

import com.arc_studio.brick_lib.events.server.entity.living.LivingEntityEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public abstract class MobEvent extends LivingEntityEvent {
    public MobEvent(Mob entity) {
        super(entity);
    }
    public static class LookAtUpdate extends MobEvent {
        LookControl lookControl;
        public LookAtUpdate(Mob entity) {
            super(entity);
            lookControl = entity.getLookControl();
        }
    }
}
