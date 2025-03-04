package com.fho4565.brick_lib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class EntityUtils {
    /**
     * 对目标实体造成伤害
     * @param source 伤害来源实体
     * @param target 目标实体
     * @param damageType 伤害类型
     * @param damage 伤害数值
     * @see DamageTypes
     */
    public static void hurt(Entity source, @NotNull Entity target, ResourceKey<DamageType> damageType, float damage){
        target.hurt(new DamageSource(source.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), source), damage);
    }
    /**
     * 对目标实体造成伤害，伤害类型为GENERIC
     * @param target 目标实体
     * @param damage 伤害数值
     * @see DamageTypes
     */
    public static void hurt(@NotNull Entity target, float damage){
        hurt(target,DamageTypes.GENERIC, damage);
    }
    /**
     * 对目标实体造成伤害
     *
     * @param target 目标实体
     * @param damageType 伤害类型
     * @param damage 伤害值
     * @see DamageTypes
     */
    public static void hurt(@NotNull Entity target, ResourceKey<DamageType> damageType, float damage){
        target.hurt(new DamageSource(target.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType)), damage);
    }
    /**
     * 检查实体是否在某个维度
     * */
    public static boolean isInDimension(Entity entity, ResourceKey<Level> resourceLocation) {
        return entity.level().dimension().compareTo(resourceLocation) == 0;
    }
    /**
     * 将实体传送到某个维度
     * */
    public static void travelToDimension(ServerPlayer serverPlayer, ResourceKey<Level> resourceLocation) {
        if (!serverPlayer.level().isClientSide()) {
            if (serverPlayer.level().dimension() == resourceLocation) {
                return;
            }
            ServerLevel nextLevel = serverPlayer.server.getLevel(resourceLocation);
            if (nextLevel != null) {
                serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
                serverPlayer.teleportTo(nextLevel, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), serverPlayer.getYRot(), serverPlayer.getXRot());
                serverPlayer.connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
                for (MobEffectInstance effectInstance : serverPlayer.getActiveEffects())
                    serverPlayer.connection.send(new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), effectInstance,false));
                serverPlayer.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
            }
        }

    }
    /**
     * 将实体传送到某个维度
     * */
    public static @NotNull ResourceKey<DamageType> getDamageType(LivingEntity holder, ResourceLocation location) {
        return holder.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, location)).key();
    }
    /**
     * 检查某实体是否在移动
     * */
    public static boolean isMoving(LivingEntity entity) {
        return entity.getDeltaMovement().lengthSqr() > 0.015;
    }
    /**
     * 检查某实体是否在攻击
     * */
    public static boolean isAttacking(LivingEntity entity) {
        return entity.getAttackAnim(1.0F) > 0.0F;
    }
    /**
     * 随机传送实体
     * */
    public static void spreadEntity(ServerLevel serverlevel, Entity entity, Vec2 center, float spreadDistance, float maxDistance, int maxHeight) {
        int minBuildHeight = serverlevel.getMinBuildHeight();
        if (maxHeight >= minBuildHeight) {
            RandomSource randomsource = RandomSource.create();
            double pMinX = center.x - maxDistance;
            double pMinZ = center.y - maxDistance;
            double pMaxX = center.x + maxDistance;
            double pMaxZ = center.y + maxDistance;
            Position position = new Position();
            position.randomize(randomsource, pMinX, pMinZ, pMaxX, pMaxZ);
            boolean flag = true;
            for (int i = 0; i < 10000 && flag; ++i) {
                flag = false;
                Position pos = new Position();
                double d1 = position.dist(position);
                int k = 0;
                if (d1 < spreadDistance) {
                    ++k;
                }
                if (k > 0) {
                    pos.x /= k;
                    pos.z /= k;
                    if (pos.getLength() > 0.0D) {
                        pos.normalize();
                        position.moveAway(pos);
                    } else {
                        position.randomize(randomsource, pMinX, pMinZ, pMaxX, pMaxZ);
                    }
                    flag = true;
                }

                if (position.clamp(pMinX, pMinZ, pMaxX, pMaxZ)) {
                    flag = true;
                }


                if (!flag) {
                    if (!position.isSafe(serverlevel, maxHeight)) {
                        position.randomize(randomsource, pMinX, pMinZ, pMaxX, pMaxZ);
                        flag = true;
                    }
                }
            }
            EntityTeleportEvent.SpreadPlayersCommand event = ForgeEventFactory.onEntityTeleportSpreadPlayersCommand(entity, (double) Mth.floor(position.x) + 0.5D, position.getSpawnY(serverlevel, maxHeight), (double) Mth.floor(position.z) + 0.5D);
            if (!event.isCanceled()) {
                entity.teleportToWithTicket(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            }
        }
    }
    /**
     * 获取一个可以放置实体的随机坐标
     * */
    public static Vec3 getRandomSpreadPosition(ServerLevel serverLevel, Vec2 center, float maxDistance, int maxHeight) {
        double minX = center.x - maxDistance;
        double minZ = center.y - maxDistance;
        double maxX = center.x + maxDistance;
        double maxZ = center.y + maxDistance;
        Position position = new Position();
        RandomSource randomSource = RandomSource.create();
        position.randomize(randomSource, minX, minZ, maxX, maxZ);
        boolean flag = true;
        for (int i = 0; i < 10000 && flag; ++i) {
            flag = false;
            Position pos = new Position();
            double distance = position.dist(position);
            int k = 0;
            if (distance < maxDistance) {
                ++k;
            }
            if (k > 0) {
                pos.x /= k;
                pos.z /= k;
                if (pos.getLength() > 0.0D) {
                    pos.normalize();
                    position.moveAway(pos);
                } else {
                    position.randomize(randomSource, minX, minZ, maxX, maxZ);
                }
                flag = true;
            }
            if (position.clamp(minX, minZ, maxX, maxZ)) {
                flag = true;
            }
            if (!flag) {
                if (!position.isSafe(serverLevel, maxHeight)) {
                    position.randomize(randomSource, minX, minZ, maxX, maxZ);
                    flag = true;
                }
            }
        }
        return new Vec3((double) Mth.floor(position.x) + 0.5D, position.getSpawnY(serverLevel, maxHeight), (double) Mth.floor(position.z) + 0.5D);
    }

    static class Position {
        double x;
        double z;

        double dist(Position pOther) {
            double d0 = this.x - pOther.x;
            double d1 = this.z - pOther.z;
            return Math.sqrt(d0 * d0 + d1 * d1);
        }

        void normalize() {
            double d0 = this.getLength();
            this.x /= d0;
            this.z /= d0;
        }

        double getLength() {
            return Math.sqrt(this.x * this.x + this.z * this.z);
        }

        public void moveAway(Position pOther) {
            this.x -= pOther.x;
            this.z -= pOther.z;
        }

        public boolean clamp(double pMinX, double pMinZ, double pMaxX, double pMaxZ) {
            boolean flag = false;
            if (this.x < pMinX) {
                this.x = pMinX;
                flag = true;
            } else if (this.x > pMaxX) {
                this.x = pMaxX;
                flag = true;
            }

            if (this.z < pMinZ) {
                this.z = pMinZ;
                flag = true;
            } else if (this.z > pMaxZ) {
                this.z = pMaxZ;
                flag = true;
            }

            return flag;
        }

        public int getSpawnY(BlockGetter pLevel, int pY) {
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(this.x, pY + 1, this.z);
            boolean flag = pLevel.getBlockState(blockPos).isAir();
            blockPos.move(Direction.DOWN);
            boolean flag2;
            for (boolean flag1 = pLevel.getBlockState(blockPos).isAir(); blockPos.getY() > pLevel.getMinBuildHeight(); flag1 = flag2) {
                blockPos.move(Direction.DOWN);
                flag2 = pLevel.getBlockState(blockPos).isAir();
                if (!flag2 && flag1 && flag) {
                    return blockPos.getY() + 1;
                }
                flag = flag1;
            }

            return pY + 1;
        }

        public boolean isSafe(BlockGetter pLevel, int pY) {
            BlockPos blockpos = BlockPos.containing(this.x, this.getSpawnY(pLevel, pY) - 1, this.z);
            BlockState blockstate = pLevel.getBlockState(blockpos);
            return blockpos.getY() < pY && !(blockstate.liquid() || blockstate.getBlock() instanceof LiquidBlock) && !blockstate.is(BlockTags.FIRE);
        }

        public void randomize(RandomSource pRandom, double pMinX, double pMinZ, double pMaxX, double pMaxZ) {
            this.x = Mth.nextDouble(pRandom, pMinX, pMaxX);
            this.z = Mth.nextDouble(pRandom, pMinZ, pMaxZ);
        }
    }
}
