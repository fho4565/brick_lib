package com.arc_studio.brick_lib.api.core.container;

import java.util.Objects;

/**
 * 两个元素组成的对
 *
 * @param <L> left元素的类型
 * @param <R> right元素的类型
 */
public class Pair<L, R> {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * 交换left和right
     * */
    public Pair<R, L> swap() {
        return new Pair<R, L>(right, left);
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Pair) obj;
        return Objects.equals(this.left, that.left) &&
                Objects.equals(this.right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }

    /**
     * 两个元素类型都相同的Pair
     * */
    public static class SamePair<E> extends Pair<E, E> {
        public SamePair(E element1, E element2) {
            super(element1, element2);
        }
    }

}
