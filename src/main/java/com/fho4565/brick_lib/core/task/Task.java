package com.fho4565.brick_lib.core.task;

public abstract class Task {
    boolean remove = false;
    protected final Runnable runnable;
    public Task(Runnable runnable){
        this.runnable = runnable;
    }
    public abstract void update();
    public void markRemove(){
        this.remove = true;
    }
}
