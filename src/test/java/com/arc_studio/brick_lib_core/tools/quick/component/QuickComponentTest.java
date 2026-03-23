package com.arc_studio.brick_lib_core.tools.quick.component;

import net.minecraft.network.chat.MutableComponent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class QuickComponentTest {

    @Disabled("Not needed")
    @Test
    void build() {
        MutableComponent component = QuickComponent.plainText("plain").bold(true)
                .append(QuickComponent.translate("one %s", "make you better").copyToClipboard("125"))
                .build();
        MutableComponent component1 = QuickComponent.plainText("plain").bold(true)
                .translate("one %s","make you better").copyToClipboard("125")
                .build();
        System.out.println("result = " + component);
    }
}