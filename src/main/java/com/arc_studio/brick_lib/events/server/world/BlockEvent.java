package com.arc_studio.brick_lib.events.server.world;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.ICancelableEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BlockEvent extends BaseEvent {
    BlockState blockState;
    LevelAccessor level;
    BlockPos blockPos;

    public BlockEvent(LevelAccessor level, BlockPos blockPos, BlockState blockState) {
        this.level = level;
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public LevelAccessor getLevel() {
        return level;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    /**
     * 当一个方块即将被破坏时触发的事件，取消这个事件会阻止方块被破坏
     */
    public static class Break extends BlockEvent implements ICancelableEvent {
        /**
         * 破坏此方块的玩家，有可能为空
         */
        protected Player player;

        public Break(LevelAccessor level, BlockPos blockPos, BlockState blockState) {
            super(level, blockPos, blockState);
        }

        public Break(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
            super(level, pos, state);
            this.player = player;
        }

        public Entity getPlayer() {
            return player;
        }
    }

    public static class Place extends BlockEvent {
        protected Entity entity;

        public Place(LevelAccessor level, BlockPos pos, BlockState state) {
            super(level, pos, state);
        }

        public Place(LevelAccessor level, BlockPos pos, BlockState state, Entity entity) {
            super(level, pos, state);
            this.entity = entity;
        }
    }

    public static class NeighborChange extends BlockEvent {
        private final EnumSet<Direction> notifiedSides;
        private final boolean forceRedStoneUpdate;

        public NeighborChange(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedStoneUpdate) {
            super(level, pos, state);
            this.notifiedSides = notifiedSides;
            this.forceRedStoneUpdate = forceRedStoneUpdate;
        }

        /**
         * Gets save list of directions from the base block that updates will occur upon.
         *
         * @return list of notified directions
         */
        public EnumSet<Direction> getNotifiedSides() {
            return notifiedSides;
        }

        /**
         * Get if redstone update was forced during setBlock call (0x16 to flags)
         *
         * @return if the flag was set
         */
        public boolean getForceRedStoneUpdate() {
            return forceRedStoneUpdate;
        }
    }

    public static class CreateFluidSource extends BlockEvent {
        public CreateFluidSource(Level level, BlockPos pos, BlockState state) {
            super(level, pos, state);
        }
    }

    public static class FluidPlaceBlockEvent extends BlockEvent {
        private final BlockPos liquidPos;
        private final BlockState origState;
        private BlockState newState;

        public FluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
            super(level, pos, state);
            this.liquidPos = liquidPos;
            this.newState = state;
            this.origState = level.getBlockState(pos);
        }

        /**
         * @return The position of the liquid this events originated from. This may be the same as {@link #getBlockPos()} ()}.
         */
        public BlockPos getLiquidPos() {
            return liquidPos;
        }

        /**
         * @return The block state that will be placed after this events resolves.
         */
        public BlockState getNewState() {
            return newState;
        }

        public void setNewState(BlockState state) {
            this.newState = state;
        }

        /**
         * @return The state of the block to be changed before the events was fired.
         */
        public BlockState getOriginalState() {
            return origState;
        }
    }

    public static class CropGrowEvent extends BlockEvent {
        public CropGrowEvent(Level level, BlockPos pos, BlockState state) {
            super(level, pos, state);
        }

        public static class Pre extends CropGrowEvent {
            public Pre(Level level, BlockPos pos, BlockState state) {
                super(level, pos, state);
            }
        }

        public static class Post extends CropGrowEvent {
            private final BlockState originalState;

            public Post(Level level, BlockPos pos, BlockState original, BlockState state) {
                super(level, pos, state);
                originalState = original;
            }

            public BlockState getOriginalState() {
                return originalState;
            }
        }
    }

    public static class FarmlandTrampleEvent extends BlockEvent {

        private final Entity entity;
        private final float fallDistance;

        public FarmlandTrampleEvent(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
            super(level, pos, state);
            this.entity = entity;
            this.fallDistance = fallDistance;
        }

        public Entity getEntity() {
            return entity;
        }

        public float getFallDistance() {
            return fallDistance;
        }

    }

    public static class PortalSpawnEvent extends BlockEvent {
        private final PortalShape size;

        public PortalSpawnEvent(LevelAccessor level, BlockPos pos, BlockState state, PortalShape size) {
            super(level, pos, state);
            this.size = size;
        }

        public PortalShape getPortalSize() {
            return size;
        }
    }

    public static class BlockToolModificationEvent extends BlockEvent {
        private final UseOnContext context;
        private final boolean simulate;
        private BlockState state;

        public BlockToolModificationEvent(BlockState originalState, @NotNull UseOnContext context, boolean simulate) {
            super(context.getLevel(), context.getClickedPos(), originalState);
            this.context = context;
            this.state = originalState;
            this.simulate = simulate;
        }

        /**
         * @return the player using the tool.
         * May be null based on what was provided by {@link #getContext() the use on context}.
         */
        @Nullable
        public Player getPlayer() {
            return this.context.getPlayer();
        }

        /**
         * @return the tool being used
         */
        public ItemStack getHeldItemStack() {
            return this.context.getItemInHand();
        }

        /**
         * Returns {@code true} if this events should not perform any actions that modify the level.
         * If {@code false}, then level-modifying actions can be performed.
         *
         * @return {@code true} if this events should not perform any actions that modify the level.
         * If {@code false}, then level-modifying actions can be performed.
         */
        public boolean isSimulated() {
            return this.simulate;
        }

        /**
         * Returns the nonnull use on context that this events was performed in.
         *
         * @return the nonnull use on context that this events was performed in
         */
        @NotNull
        public UseOnContext getContext() {
            return context;
        }

        /**
         * Returns the state to transform the block into after tool use.
         * If {@link #setFinalState(BlockState)} is not called, this will return the original state.
         * If {@link #isCanceled()} is {@code true}, this value will be ignored and the tool action will be canceled.
         *
         * @return the state to transform the block into after tool use
         */
        public BlockState getFinalState() {
            return state;
        }

        /**
         * Sets the state to transform the block into after tool use.
         *
         * @param finalState the state to transform the block into after tool use
         * @see #getFinalState()
         */
        public void setFinalState(@Nullable BlockState finalState) {
            this.state = finalState;
        }
    }
}
