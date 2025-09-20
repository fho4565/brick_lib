package com.arc_studio.brick_lib.events.server.entity;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.ICancelableEvent;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author fho4565
 */
public abstract class EntityEvent extends BaseEvent {
    protected final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
/*    @ApiStatus.Experimental
    public static class ChangedDimension extends EntityEvent {
        private final ResourceKey<Level> from;
        private ResourceKey<Level> to;
        private boolean toChanged = false;
        public ChangedDimension(Entity entity, ResourceKey<Level> from, ResourceKey<Level> levelResourceKey) {
            super(entity);
            this.from = from;
            to = levelResourceKey;
        }

        public ResourceKey<Level> getFrom() {
            return from;
        }

        public ResourceKey<Level> getTo() {
            return to;
        }
        public void setTo(ResourceKey<Level> to) {
            this.to = to;
            toChanged = true;
        }

        public boolean isToChanged() {
            return toChanged;
        }
    }*/

}
