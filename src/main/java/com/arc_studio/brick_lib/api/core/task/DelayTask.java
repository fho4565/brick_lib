package com.arc_studio.brick_lib.api.core.task;

public class DelayTask extends Task{
    protected final int originDelay;
    protected int delay;

    public DelayTask( int delay,Runnable runnable) {
        super(runnable);
        this.originDelay = delay;
        this.delay = delay;
    }

    @Override
    public void update() {
        delay--;
        if(delay==0){
            runnable.run();
            markRemove();
        }
    }
}
