package com.arc_studio.brick_lib.api.core.task;

import com.arc_studio.brick_lib.BrickLib;

public class RepeatTask extends Task{
    final int repeatCount;
    int interval = 0;

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
