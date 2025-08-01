package com.arc_studio.brick_lib.api.core.interfaces.function;

import com.arc_studio.brick_lib.api.core.container.Pair;

/**
 * 表示有两个返回值的函数
 * @param <T> 参数类型
 * @param <R1> 第一个返回值类型
 * @param <R2> 第二个返回值类型
 * @see java.util.function.Function
 * */
public interface BiReturnFunction<T, R1, R2> {
    Pair<R1, R2> apply(T t);
}
