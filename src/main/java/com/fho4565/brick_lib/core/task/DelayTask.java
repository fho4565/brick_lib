package com.fho4565.brick_lib.core.task;

public class DelayTask extends Task{
    int delay;

    public DelayTask( int delay,Runnable runnable) {
        super(runnable);
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
