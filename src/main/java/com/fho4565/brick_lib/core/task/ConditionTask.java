package com.fho4565.brick_lib.core.task;

import java.util.function.Supplier;

public class ConditionTask extends Task{
    protected final Supplier<Boolean> condition;

    public ConditionTask(Supplier<Boolean> condition,Runnable runnable) {
        super(runnable);
        this.condition = condition;
    }

    @Override
    public void update() {
        if(condition.get()){
            runnable.run();
            markRemove();
        }
    }
}
