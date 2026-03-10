package com.arc_studio.brick_lib_core.tools.quick.component;

import org.junit.jupiter.api.Test;

class QuickComponentTest {

    @Test
    void build() {
        System.out.println("result = " + QuickComponent.of().append(new QuickComponent.Plain("plain").bold(true))
                .append(new QuickComponent.Translate("one", "make you larger")
                        )
                .build());
    }
}