package com.arc_studio.brick_lib.core.global_pack.files;

import com.google.gson.JsonObject;

public abstract class GlobalPackFileType {
    private final String dirName;
    private final String fileSuffix;

    public GlobalPackFileType(String dirName, String fileSuffix) {
        this.dirName = dirName;
        this.fileSuffix = fileSuffix.startsWith("\\.") ? fileSuffix : "."+fileSuffix;
    }

    public String dirName() {
        return dirName;
    }

    public String fileSuffix() {
        return fileSuffix;
    }

    public JsonObject createEmpty(){
        return new JsonObject();
    }
}
