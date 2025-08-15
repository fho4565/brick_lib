package com.arc_studio.brick_lib.api.json_function;

import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    private final Map<String, JsonFunction> functions = new HashMap<>();
    
    public void register(String id, JsonFunction function) {
        functions.put(id, function);
    }
    
    public JsonFunction get(String id) {
        return functions.get(id);
    }
}