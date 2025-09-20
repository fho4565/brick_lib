package com.arc_studio.brick_lib.events.server.entity.living;

import com.arc_studio.brick_lib.api.event.ICancelableEvent;
import com.arc_studio.brick_lib.events.server.entity.EntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author fho4565
 */
public abstract class LivingEntityEvent extends EntityEvent {
    protected LivingEntity livingEntity;

    public LivingEntityEvent(LivingEntity entity) {
        super(entity);
        this.livingEntity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return livingEntity;
    }
/*    @ApiStatus.Experimental
    public static class Move extends LivingEntityEvent {
        public Move(LivingEntity entity) {
            super(entity);
        }
    }
    @ApiStatus.Experimental
    public static class PickupItem extends LivingEntityEvent {
        ItemStack itemStack;

        public PickupItem(LivingEntity entity, ItemStack itemStack) {
            super(entity);
            this.itemStack = itemStack;
        }
    }*/

    public static class Jump extends LivingEntityEvent implements ICancelableEvent {

        public Jump(LivingEntity entity) {
            super(entity);
        }
    }

   /* @ApiStatus.Experimental
    public static abstract class Attack extends LivingEntityEvent {
        private final Entity target;
        private float damage = 0;
        private boolean critical = false;

        public Attack(LivingEntity livingEntity, Entity target) {
            super(livingEntity);
            this.target = target;
        }

        public Attack(LivingEntity livingEntity, Entity target, boolean critical) {
            super(livingEntity);
            this.target = target;
            this.critical = critical;
        }

        public boolean isCritical() {
            return critical;
        }

        public void setCritical(boolean critical) {
            this.critical = critical;
        }

        public float getDamage() {
            return damage;
        }

        public void setDamage(float damage) {
            this.damage = damage;
        }
    }
    @ApiStatus.Experimental
    public static class Sleep extends LivingEntityEvent {
        protected final BlockPos pos;
        public Sleep(LivingEntity entity, BlockPos pos) {
            super(entity);
            this.pos = pos;
        }

        public BlockPos getPos() {
            return pos;
        }

        public static class Check extends Sleep {
            public Check(LivingEntity entity, BlockPos pos) {
                super(entity, pos);
            }
        }

        public static class Start extends Sleep {

            public Start(LivingEntity entity, BlockPos pos) {
                super(entity, pos);
            }
        }
        public static class Stop extends Sleep {
            public Stop(LivingEntity entity, BlockPos pos) {
                super(entity, pos);
            }
        }
        public static class Tick extends Sleep {
            public Tick(LivingEntity entity, BlockPos pos) {
                super(entity, pos);
            }
        }
        public static class Finish extends Sleep {
            public Finish(LivingEntity entity, BlockPos pos) {
                super(entity, pos);
            }
        }
    }
    @ApiStatus.Experimental
    public static class Tick extends LivingEntityEvent {
        public Tick(LivingEntity entity) {
            super(entity);
        }
    }
*/
}
