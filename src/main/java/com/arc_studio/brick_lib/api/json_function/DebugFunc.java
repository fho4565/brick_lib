package com.arc_studio.brick_lib.api.json_function;

import com.google.gson.JsonElement;

public class DebugFunc {
    public static FunctionRegistry registry = new FunctionRegistry();
    public static void main(String[] args) {
        /*
        BrickRegisterManager.register(BrickRegistries.JSON_FUNCTION, BrickLib.createBrickRL("add"),
                () -> jsonValues -> {
                    double num1 = toDouble(jsonValues.get(0));
                    double num2 = toDouble(jsonValues.get(1));
                    return num1 + num2;
                }
        );
        BrickRegisterManager.register(BrickRegistries.JSON_FUNCTION, BrickLib.createBrickRL("greet"),
                () -> jsonValues -> "Hello, " + toText(jsonValues.get(0))
        );

        BrickRegisterManager.register(BrickRegistries.JSON_FUNCTION, BrickLib.createBrickRL("currentTime"),
                () -> jsonValues -> System.currentTimeMillis()
        );
       */
        registry.register("add", jsonValues -> {
            double num1 = toDouble(jsonValues.get(0));
            double num2 = toDouble(jsonValues.get(1));
            return num1 + num2;
        });
        registry.register("sum", jsonValues -> {
            double sum = 0;
            for (JsonElement jsonValue : jsonValues) {
                sum += toDouble(jsonValue);
            }
            return sum;
        });

        registry.register("greet", jsonValues -> "Hello, " + toText(jsonValues.get(0)));

        registry.register("currentTime", jsonValues -> System.currentTimeMillis());

        System.out.println("Result : " + InstructionExecutor.execute("""
                {"function":"add","args":[{"fun":"add","args":[{"f":"add","args":[{"value":"9"},3]},4]},3]}
                """));

        System.out.println("Greet : " + InstructionExecutor.execute("""
                {"func":"greet","args":["John"]}
                """));

        System.out.println("Sum : " + InstructionExecutor.execute("""
                {
                    "func": "sum",
                    "args": [
                        5,
                        8
                    ]
                }
                """));

        System.out.println("Current Time : " + InstructionExecutor.execute("""
                {
                    "func": "currentTime",
                    "args": [
                        5,
                        8
                    ]
                }
                """));

        System.out.println("Value : " + InstructionExecutor.execute("""
                {"value":"Direct Value"}
                """));

        System.out.println("Complex Value : " + InstructionExecutor.execute("""
                {"type":"value","value":{"name":"John","age":30,"scores":[90,85,95]}}
                """));
    }

    private static double toDouble(JsonElement value) {
        if (value.isJsonPrimitive()) {
            if (value.getAsJsonPrimitive().isNumber()) {
                return value.getAsNumber().doubleValue();
            }
        }
        return Double.parseDouble(value.toString());
    }

    private static String toText(JsonElement value) {
        if (value.isJsonPrimitive()) {
            if (value.getAsJsonPrimitive().isString()) {
                return value.getAsString();
            }
        }
        return value.toString();
    }
}
