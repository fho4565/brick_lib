package com.arc_studio.brick_lib_core.tools.quick.component;

import net.minecraft.network.chat.MutableComponent;
import org.junit.jupiter.api.Test;

class QuickComponentTest {

    @Test
    void build() {
        MutableComponent component = QuickComponent.plaintext("plain").bold(true)
                .append(new QuickComponent.Translate("one %s", "make you better").copyToClipboard("125"))
                .build();
        MutableComponent component1 = QuickComponent.plaintext("plain").bold(true)
                .translate("one %s","make you better").copyToClipboard("125")
                .build();
        System.out.println("result = " + component);
    }
}