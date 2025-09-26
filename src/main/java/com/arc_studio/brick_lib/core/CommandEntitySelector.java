package com.arc_studio.brick_lib.core;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author fho4565
 */
public class CommandEntitySelector {
    private final String sign;
    private final Component description;
    private final Predicate<Entity> entityPredicate;
    private boolean includeEntities = true;
    private boolean selfSelector = false;
    private int maxCount = Integer.MAX_VALUE;
    private Sort sort = Sort.ARBITRARY;
    private EntityType<?> entityType;

    public CommandEntitySelector(String sign, Component description, Predicate<Entity> entityPredicate) {
        this.sign = sign;
        this.description = description;
        this.entityPredicate = entityPredicate;
    }

    public void setIncludeEntities(boolean includeEntities) {
        this.includeEntities = includeEntities;
    }

    public void setSelfSelector(boolean selfSelector) {
        this.selfSelector = selfSelector;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public Sort sort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public EntityType<?> entityType() {
        return entityType;
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public String sign() {
        return sign;
    }

    public Component description() {
        return description;
    }

    public boolean includeEntities() {
        return includeEntities;
    }

    public boolean selfSelector() {
        return selfSelector;
    }

    public int maxCount() {
        return maxCount;
    }

    public Predicate<Entity> entityPredicate() {
        return entityPredicate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (CommandEntitySelector) obj;
        return Objects.equals(this.sign, that.sign) &&
               Objects.equals(this.description, that.description) &&
               this.includeEntities == that.includeEntities &&
               this.selfSelector == that.selfSelector &&
               this.maxCount == that.maxCount &&
               Objects.equals(this.entityPredicate, that.entityPredicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sign, description, includeEntities, selfSelector, maxCount, entityPredicate);
    }

    @Override
    public String toString() {
        return "CommandEntitySelector[" +
               "sign=" + sign + ", " +
               "description=" + description + ", " +
               "includeEntities=" + includeEntities + ", " +
               "selfSelector=" + selfSelector + ", " +
               "maxCount=" + maxCount + ", " +
               "entityPredicate=" + entityPredicate + ']';
    }

    public enum Sort {
        NEAREST, FURTHEST, RANDOM, ARBITRARY
    }
}
