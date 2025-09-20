package com.arc_studio.brick_lib.events.server.entity.living.mob;

import com.arc_studio.brick_lib.events.server.entity.living.LivingEntityEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public abstract class MobEvent extends LivingEntityEvent {
    public MobEvent(Mob entity) {
        super(entity);
    }
    /**
     * 当实体尝试更改视角（非转身时的更改）时触发
     * */
    public static class LookAtUpdate extends MobEvent {
        protected double x;
        protected double y;
        protected double z;
        protected float xMaxRotAngle;
        protected float yMaxRotSpeed;

        public LookAtUpdate(Mob entity, double x, double y, double z, float xMaxRotAngle, float yMaxRotSpeed) {
            super(entity);
            this.x = x;
            this.y = y;
            this.z = z;
            this.xMaxRotAngle = xMaxRotAngle;
            this.yMaxRotSpeed = yMaxRotSpeed;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public float getxMaxRotAngle() {
            return xMaxRotAngle;
        }

        public void setxMaxRotAngle(float xMaxRotAngle) {
            this.xMaxRotAngle = xMaxRotAngle;
        }

        public float getyMaxRotSpeed() {
            return yMaxRotSpeed;
        }

        public void setyMaxRotSpeed(float yMaxRotSpeed) {
            this.yMaxRotSpeed = yMaxRotSpeed;
        }
    }
}
