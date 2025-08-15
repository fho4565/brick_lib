package com.arc_studio.brick_lib.core.global_pack;

import com.arc_studio.brick_lib.core.global_pack.files.GlobalPackFileType;
import com.arc_studio.brick_lib.core.global_pack.files.PackMcMetaFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalPack {
    private final String name;
    private final PackMcMetaFile packMcMetaFile;
    private final HashMap<GlobalPackFileType, ArrayList<Path>> files;

    public GlobalPack(String name,PackMcMetaFile packMcMetaFile, HashMap<GlobalPackFileType, ArrayList<Path>> files) {
        this.name = name;
        this.packMcMetaFile = packMcMetaFile;
        this.files = files;
    }

    public PackMcMetaFile packMcMetaFile() {
        return packMcMetaFile;
    }

    public String name() {
        return name;
    }
}
