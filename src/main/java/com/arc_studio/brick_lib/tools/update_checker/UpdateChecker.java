package com.arc_studio.brick_lib.tools.update_checker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * @author fho4565
 */
public class UpdateChecker {
    public static final String MODRINTH_URL = "https://api.modrinth.com/v2/project/%s/version";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    public static List<ModrinthModInfo> checkFromModrinth(String projectId){
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(MODRINTH_URL.formatted(projectId))).GET().build();
            HttpResponse<String> res = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                return null;
            }
            String body = res.body() != null ? res.body() : "";
            TypeToken<List<ModrinthModInfo>> typeAdapter = new TypeToken<>() {
            };
            return new Gson().fromJson(body, typeAdapter);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static String checkFromCustom(String checkUrl){
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(checkUrl)).GET().build();
            HttpResponse<String> res = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                return null;
            }
            return res.body() != null ? res.body() : "";
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
