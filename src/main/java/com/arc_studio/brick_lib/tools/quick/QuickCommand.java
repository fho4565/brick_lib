package com.arc_studio.brick_lib.tools.quick;

import com.arc_studio.brick_lib.tools.quick.command.RootNode;

public class QuickCommand {
    public static RootNode create(String root){
        return new RootNode(root);
    }
}
