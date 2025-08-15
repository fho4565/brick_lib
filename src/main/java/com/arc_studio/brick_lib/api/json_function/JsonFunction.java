package com.arc_studio.brick_lib.api.json_function;

import com.google.gson.JsonArray;

@FunctionalInterface
public interface JsonFunction {
    Object execute(JsonArray args);
}