package com.fho4565.brick_lib.core.task;

import com.fho4565.brick_lib.BrickLib;

public class RepeatTask extends Task{
    final int repeatCount;

    public RepeatTask(int repeatCount,Runnable runnable) {
        super(runnable);
        this.repeatCount = repeatCount;
    }

    @Override
    public void update() {
        try {
            for (int i = 0; i < repeatCount; i++) {
                runnable.run();
            }
        } catch (Exception e) {
            BrickLib.LOGGER.error(e.getLocalizedMessage());
        } finally {
            markRemove();
        }
    }
}
