package com.arc_studio.brick_lib.events.server.entity.living.player;

import com.arc_studio.brick_lib.api.event.ICancelableEvent;
import com.arc_studio.brick_lib.api.event.IClientOnlyEvent;
import com.arc_studio.brick_lib.api.event.IResultEvent;
import com.arc_studio.brick_lib.events.server.entity.living.LivingEntityEvent;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 当发生和玩家有关的事件时，会触发该事件。
 * @author fho4565
 */
public abstract class PlayerEvent extends LivingEntityEvent {
    protected Player player;

    public PlayerEvent(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public static abstract class Advancement extends PlayerEvent implements ICancelableEvent {
        net.minecraft.advancements.Advancement advancement;

        public net.minecraft.advancements.Advancement advancement() {
            return advancement;
        }

        public Advancement(Player player, net.minecraft.advancements.Advancement advancement) {
            super(player);
            this.advancement = advancement;
        }

        public static class Complete extends Advancement {
            public Complete(Player player, net.minecraft.advancements.Advancement advancement) {
                super(player, advancement);
            }
        }

        public static class Progress extends Advancement {
            private final AdvancementProgress advancementProgress;
            private final String criterionName;

            public Progress(Player player, net.minecraft.advancements.Advancement advancement, AdvancementProgress advancementProgress, String criterionName) {
                super(player, advancement);
                this.advancementProgress = advancementProgress;
                this.criterionName = criterionName;
            }

            public String criterionName() {
                return criterionName;
            }

            public AdvancementProgress advancementProgress() {
                return advancementProgress;
            }
        }

        public static class Revoke extends Advancement {
            private final AdvancementProgress advancementProgress;
            private final String criterionName;

            public Revoke(Player player, net.minecraft.advancements.Advancement advancement, AdvancementProgress advancementProgress, String criterionName) {
                super(player, advancement);
                this.advancementProgress = advancementProgress;
                this.criterionName = criterionName;
            }

            public String getCriterionName() {
                return criterionName;
            }

            public AdvancementProgress advancementProgress() {
                return advancementProgress;
            }
        }
    }

    public static class Sneak extends PlayerEvent implements IClientOnlyEvent,ICancelableEvent {
        public Sneak(Player player) {
            super(player);
        }
    }

    /**
     * 玩家刻事件，该事件会在每个tick中触发，用于实现玩家的逻辑。
     */
    public static class Tick extends PlayerEvent {
        public Tick(Player player) {
            super(player);
        }

        public static class Pre extends PlayerEvent {
            public Pre(Player player) {
                super(player);
            }
        }

        public static class Post extends PlayerEvent {
            public Post(Player player) {
                super(player);
            }
        }
    }

    /**
     * 当玩家右键点击时，会触发该事件。
     * 该事件可以被取消，取消后不会执行右键操作。
     */
    public static class RightClick extends PlayerEvent {
        private final PlayerClickContext clickContext;

        public RightClick(Player player, PlayerClickContext clickContext) {
            super(player);
            this.clickContext = clickContext;
        }

        public PlayerClickContext getClickContext() {
            return clickContext;
        }
    }

    //TODO 完成左键事件监听：左键空气，方块，实体
    @ApiStatus.Experimental
    public static class LeftClick extends PlayerEvent implements ICancelableEvent,IResultEvent {
        private final PlayerClickContext clickContext;

        public LeftClick(Player player, PlayerClickContext clickContext) {
            super(player);
            this.clickContext = clickContext;
        }

        public PlayerClickContext getClickContext() {
            return clickContext;
        }
    }

    public static abstract class BreakBlock extends PlayerEvent {
        private final BlockPos blockPos;
        private final BlockState blockState;

        public BreakBlock(Player player, BlockPos blockPos, BlockState blockState) {
            super(player);
            this.blockPos = blockPos;
            this.blockState = blockState;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public BlockState getBlockState() {
            return blockState;
        }

        public static class Start extends BreakBlock implements IResultEvent,ICancelableEvent,IClientOnlyEvent {
            public Direction getFace() {
                return face;
            }

            Direction face;

            public Start(Player player, BlockPos blockPos, BlockState blockState, Direction face) {
                super(player, blockPos, blockState);
                this.face = face;
            }
        }

        public static class Breaking extends BreakBlock {
            public Direction getFace() {
                return face;
            }

            Direction face;

            private final float destroyProgress;

            public Breaking(Player player, BlockPos blockPos, BlockState blockState, Direction face, float destroyProgress) {
                super(player, blockPos, blockState);
                this.face = face;
                this.destroyProgress = destroyProgress;
            }

            public float destroyProgress() {
                return destroyProgress;
            }
        }

        public static class Stop extends BreakBlock implements ICancelableEvent {
            public Direction getFace() {
                return face;
            }

            Direction face;

            public Stop(Player player, BlockPos blockPos, BlockState blockState, Direction face) {
                super(player, blockPos, blockState);
                this.face = face;
            }
        }

        public static class Finish extends BreakBlock {
            public Finish(Player player, BlockPos blockPos, BlockState blockState) {
                super(player, blockPos, blockState);
            }

            public static class Pre extends Finish implements ICancelableEvent {
                public Pre(Player player, BlockPos blockPos, BlockState blockState) {
                    super(player, blockPos, blockState);
                }
            }

            public static class Post extends Finish {
                public Post(Player player, BlockPos blockPos, BlockState blockState) {
                    super(player, blockPos, blockState);
                }
            }
        }
    }

    public static class RequestItemTooltip extends PlayerEvent {
        private final TooltipFlag flags;
        private final ItemStack itemStack;
        private final ArrayList<Component> toolTip;

        public RequestItemTooltip(Player player, TooltipFlag flags, ItemStack itemStack, ArrayList<Component> toolTip) {
            super(player);
            this.flags = flags;
            this.itemStack = itemStack;
            this.toolTip = toolTip;
        }

        public TooltipFlag getFlags() {
            return flags;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public ArrayList<Component> getToolTipLines() {
            return toolTip;
        }
    }

    public static class PermissionsChange extends PlayerEvent {
        final int from;
        final int to;
        public PermissionsChange(Player player,int from,int to) {
            super(player);
            this.from = from;
            this.to = to;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }
    }

    @ApiStatus.Experimental
    public static abstract class Gui extends PlayerEvent {
        public Gui(Player player) {
            super(player);
        }

        public static class TakeItem extends Gui {
            private final Slot slot;
            private final ItemStack stack;
            private final AbstractContainerMenu menu;

            public TakeItem(Player player, AbstractContainerMenu menu, Slot slot, ItemStack stack) {
                super(player);
                this.menu = menu;
                this.slot = slot;
                this.stack = stack;
            }
        }

        public static class PutItem extends Gui {
            private final Slot slot;
            private final ItemStack stack;
            private final AbstractContainerMenu menu;

            public PutItem(Player player, AbstractContainerMenu menu, Slot slot, ItemStack stack) {
                super(player);
                this.menu = menu;
                this.slot = slot;
                this.stack = stack;
            }
        }

        public static class Open extends Gui implements ICancelableEvent{
            Screen screen;
            public Open(Player player,Screen screen) {
                super(player);
                this.screen = screen;
            }

            public Screen screen() {
                return screen;
            }

            public void setScreen(Screen screen) {
                this.screen = screen;
            }
        }

        public static class Close extends Gui {
            Screen screen;
            public Close(Player player,Screen screen) {
                super(player);
                this.screen = screen;
            }

            public Screen screen() {
                return screen;
            }
        }
    }

/*    @ApiStatus.Experimental
    public static class DestroyItem extends PlayerEvent {
        private final ItemStack itemStack;

        public DestroyItem(Player player, ItemStack itemStack) {
            super(player);
            this.itemStack = itemStack;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }

    @ApiStatus.Experimental
    public static abstract class ElytraFly extends PlayerEvent {
        private final BlockPos blockPos;

        public ElytraFly(Player player, BlockPos blockPos) {
            super(player);
            this.blockPos = blockPos;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public static class Start extends ElytraFly {
            public Start(Player player, BlockPos blockPos) {
                super(player, blockPos);
            }
        }

        public static class Stop extends ElytraFly {
            public Stop(Player player, BlockPos blockPos) {
                super(player, blockPos);
            }
        }

        public static class SpeedUp extends ElytraFly {

            public SpeedUp(Player player, BlockPos blockPos) {
                super(player, blockPos);
            }
        }

        public static class Finish extends ElytraFly {
            public Finish(Player player, BlockPos blockPos) {
                super(player, blockPos);
            }
        }
    }

    @ApiStatus.Experimental
    public static class SetSpawnPoint extends PlayerEvent {
        private final BlockPos blockPos;
        private final boolean fromBed;

        public SetSpawnPoint(Player player, BlockPos blockPos) {
            super(player);
            this.blockPos = blockPos;
            fromBed = false;
        }

        public SetSpawnPoint(Player player, BlockPos blockPos, boolean fromBed) {
            super(player);
            this.blockPos = blockPos;
            this.fromBed = fromBed;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public boolean isFromBed() {
            return fromBed;
        }
    }

    @ApiStatus.Experimental
    public static abstract class SpawnPhantoms extends PlayerEvent {
        public SpawnPhantoms(Player player) {
            super(player);
        }

        public static class Count extends SpawnPhantoms {
            public Count(Player player) {
                super(player);
            }
        }

        public static class Spawn extends SpawnPhantoms {
            private final Phantom phantom;

            public Spawn(Player player, Phantom phantom) {
                super(player);
                this.phantom = phantom;
            }

            public Phantom getPhantom() {
                return phantom;
            }
        }

        public static class Finish extends SpawnPhantoms {

            public Finish(Player player) {
                super(player);
            }
        }
    }

    @ApiStatus.Experimental
    public static class PickupXp extends PlayerEvent {
        private final ExperienceOrb orb;

        public PickupXp(Player player, ExperienceOrb orb) {
            super(player);
            this.orb = orb;
        }

        public ExperienceOrb getExperienceOrb() {
            return orb;
        }
    }

    @ApiStatus.Experimental
    public static class XpAmountChange extends PlayerEvent {
        private final int amount;

        public XpAmountChange(Player player, int amount) {
            super(player);
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }
    }

    @ApiStatus.Experimental
    public static class XpLevelChange extends PlayerEvent {
        private final int level;

        public XpLevelChange(Player player, int level) {
            super(player);
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    @ApiStatus.Experimental
    public static abstract class VillageTrade extends PlayerEvent {
        private final MerchantOffer offer;
        private final AbstractVillager abstractVillager;

        public VillageTrade(Player player, MerchantOffer offer, AbstractVillager villager) {
            super(player);
            this.offer = offer;
            this.abstractVillager = villager;
        }

        public MerchantOffer getOffer() {
            return offer;
        }

        public AbstractVillager getAbstractVillager() {
            return abstractVillager;
        }

        public static class Pre extends VillageTrade {

            public Pre(Player player, MerchantOffer offer, AbstractVillager villager) {
                super(player, offer, villager);
            }
        }

        public static class Post extends VillageTrade {
            public Post(Player player, MerchantOffer offer, AbstractVillager villager) {
                super(player, offer, villager);
            }
        }
    }

    @ApiStatus.Experimental
    public static class LoadFromFile extends PlayerEvent {
        private final File playerDirectory;
        private final String playerUUID;

        public LoadFromFile(Player player, File originDirectory, String playerUUID) {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        *//**
         * Construct and return save recommended file for the supplied suffix
         *
         * @param suffix The suffix to use.
         *//*
        public File getPlayerFile(String suffix) {
            if ("dat".equals(suffix)) {
                throw new IllegalArgumentException("The suffix 'dat' is retained");
            }
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() + "." + suffix);
        }

        *//**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         *//*
        public File getPlayerDirectory() {
            return playerDirectory;
        }

        *//**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         *//*
        public String getPlayerUUID() {
            return playerUUID;
        }
    }

    @ApiStatus.Experimental
    public static class SaveToFile extends PlayerEvent {
        private final File playerDirectory;
        private final String playerUUID;

        public SaveToFile(Player player, File originDirectory, String playerUUID) {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        *//**
         * Construct and return save recommended file for the supplied suffix
         *
         * @param suffix The suffix to use.
         *//*
        public File getPlayerFile(String suffix) {
            if ("dat".equals(suffix)) {
                throw new IllegalArgumentException("The suffix 'dat' is retained");
            }
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() + "." + suffix);
        }

        *//**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         *//*
        public File getPlayerDirectory() {
            return playerDirectory;
        }

        *//**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         *//*
        public String getPlayerUUID() {
            return playerUUID;
        }
    }*/

    /**
     * 玩家加入服务器的事件，分为"加入前"和"加入后"两个具体事件
     * */
    public static class PlayerJoin extends PlayerEvent {
        public PlayerJoin(Player player) {
            super(player);
        }
        /**
         * 这个事件在玩家加入服务器前触发，这个时候客户端一些数据还没有处理好，玩家并未真正加入游戏
         * <p>一些操作(例如向玩家发送消息)在这时并没有效果，如果你要实现这些操作，使用{@link Post}</p>
         * */
        public static class Pre extends PlayerJoin{

            public Pre(Player player) {
                super(player);
            }
        }
        /**
         * 这个事件在玩家加入服务器后触发，所有数据都已经处理完毕
         * */
        public static class Post extends PlayerJoin{

            public Post(Player player) {
                super(player);
            }
        }
    }

/*    @ApiStatus.Experimental
    public static class PlayerLeave extends PlayerEvent {
        public PlayerLeave(Player player) {
            super(player);
        }
    }

    @ApiStatus.Experimental
    public static class PlayerRespawn extends PlayerEvent {
        public PlayerRespawn(Player player, boolean endConquered) {
            super(player);
        }
    }

    @ApiStatus.Experimental
    public static class ChangeGameMode extends PlayerEvent {
        private final GameType from;
        private GameType to;


        public ChangeGameMode(Player player, GameType from, GameType to, GameType gameType) {
            super(player);
            this.from = from;
            this.to = gameType;
        }

        public GameType getFrom() {
            return from;
        }

        public GameType getTo() {
            return to;
        }

        public void setTo(GameType to) {
            this.to = to;
        }
    }

    @ApiStatus.Experimental
    public static class Clone extends PlayerEvent {
        private final Player original;
        private final boolean wasDeath;

        public Clone(Player newPlayer, Player original, boolean wasDeath) {
            super(newPlayer);
            this.original = original;
            this.wasDeath = wasDeath;
        }

        public Player getOriginal() {
            return original;
        }

        public boolean isWasDeath() {
            return wasDeath;
        }
    }

    @ApiStatus.Experimental
    public static class RequestBreakSpeed extends PlayerEvent {
        private static final BlockPos LEGACY_UNKNOWN = new BlockPos(0, -1, 0);
        private final BlockState state;
        private final float originalSpeed;
        private float newSpeed = 0.0f;
        private final BlockPos pos;

        public RequestBreakSpeed(Player player, BlockState state, float originalSpeed, BlockPos pos) {
            super(player);
            this.state = state;
            this.originalSpeed = originalSpeed;
            this.newSpeed = this.originalSpeed;
            this.pos = pos;
        }

        public RequestBreakSpeed(Player player, BlockState state, float originalSpeed, float newSpeed, BlockPos pos) {
            super(player);
            this.state = state;
            this.originalSpeed = originalSpeed;
            this.newSpeed = newSpeed;
            this.pos = pos;
        }

        public BlockState getState() {
            return state;
        }

        public float getOriginalSpeed() {
            return originalSpeed;
        }

        public float getNewSpeed() {
            return newSpeed;
        }

        public void setNewSpeed(float newSpeed) {
            this.newSpeed = newSpeed;
        }

        public BlockPos getPos() {
            return this.pos;
        }
    }*/

    @ApiStatus.Experimental
    public abstract static class UseItem extends PlayerEvent implements IResultEvent {
        private final ItemStack usedItemStack;
        private final InteractionHand hand;

        public UseItem(Player player, ItemStack usedItemStack, InteractionHand hand) {
            super(player);
            this.usedItemStack = usedItemStack;
            this.hand = hand;
        }

        public ItemStack getUsedItemStack() {
            return usedItemStack;
        }

        public InteractionHand getHand() {
            return hand;
        }

        public static class Start extends UseItem implements ICancelableEvent {
            public Start(Player player, ItemStack usedItemStack, InteractionHand hand) {
                super(player, usedItemStack, hand);
            }
        }

        public static class Using extends UseItem {
            private final int useTicks;

            public Using(Player player, ItemStack usedItemStack, InteractionHand hand, int useTicks) {
                super(player, usedItemStack, hand);
                this.useTicks = useTicks;
            }

            public int getUseTicks() {
                return useTicks;
            }
        }

        public static class Stop extends UseItem {
            private final int usedTicks;

            public Stop(Player player, ItemStack usedItemStack, InteractionHand hand, int usedTicks) {
                super(player, usedItemStack, hand);
                this.usedTicks = usedTicks;
            }

            public int getUsedTicks() {
                return usedTicks;
            }
        }

        public static class Finish extends UseItem {
            ItemStack finish;

            public Finish(Player player, ItemStack usedItemStack, InteractionHand hand) {
                super(player, usedItemStack, hand);
                this.finish = usedItemStack;
            }

            public Finish(Player player, ItemStack usedItemStack,ItemStack finish, InteractionHand hand) {
                super(player, usedItemStack, hand);
                this.finish = finish;
            }

            public ItemStack getFinish() {
                return finish;
            }
        }
    }

    public static class WonTheGame extends PlayerEvent {
        public WonTheGame(Player player) {
            super(player);
        }
    }

    public static class Chat extends PlayerEvent {
        String message;
        private final String originalMessage;
        private final boolean isCommand;

        public Chat(Player player, String message, String originalMessage,boolean isCommand) {
            super(player);
            this.message = message;
            this.originalMessage = originalMessage;
            this.isCommand = isCommand;
        }

        public String getOriginalMessage() {
            return originalMessage;
        }

        public boolean isCommand(){
            return isCommand;
        }

        public String getMessage() {
            return message;
        }

        /**
         * 当客户端发送消息或命令时触发，分为Pre(准备发送)和Post(已经发送)
         * */
        public static class Send extends Chat implements IClientOnlyEvent {
            public Send(Player player, String message, String originalMessage,boolean isCommand) {
                super(player, message, originalMessage,isCommand);
            }

            /**
             * 当客户端准备发送消息或命令时触发
             * */
            public static class Pre extends Send implements ICancelableEvent {
                public Pre(Player player, String message, String originalMessage,boolean isCommand) {
                    super(player, message, originalMessage,isCommand);
                }

                public void setMessage(String message) {
                    this.message = message;
                }
            }

            /**
             * 当客户端已经发送消息或命令时触发
             * */
            public static class Post extends Send {
                public Post(Player player, String message,boolean isCommand) {
                    super(player, message, message,isCommand);
                }
            }
        }

        /**
         * 当服务端处理聊天消息时触发
         * */
        public static class ServerProcess extends Chat {
            public ServerProcess(Player player, String message, String originalMessage,boolean isCommand) {
                super(player, message, originalMessage,isCommand);
            }
        }

        /**
         * 当客户端接收一条聊天消息时触发
         * */
        public static class Receive extends Chat {
            public Receive(Player player, String message,boolean isCommand) {
                super(player, message, message,isCommand);
            }
        }

        /**
         * 当聊天界面尝试保存一个历史记录时触发
         * */
        public static class AddToRecent extends Chat implements ICancelableEvent, IClientOnlyEvent {
            public AddToRecent(Player player, String message, String originalMessage,boolean isCommand) {
                super(player, message, originalMessage,isCommand);
            }

            public void setMessage(String message) {
                this.message = message;
            }
        }
    }
}
