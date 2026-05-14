package com.androidcourse.partner_map.data.repository;

import com.androidcourse.partner_map.data.remote.ApiClient;
import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class BaseRepository {
    protected static final ExecutorService IO = Executors.newCachedThreadPool();
    protected final ApiClient apiClient = ApiClient.getInstance();
    private final Gson gson = new Gson();

    protected String getErrorMessage(Response<? extends ApiResponse<?>> response, String fallback) {
        if (response == null) {
            return fallback;
        }
        ApiResponse<?> body = response.body();
        if (body != null && body.getMessage() != null && !body.getMessage().trim().isEmpty()) {
            return body.getMessage();
        }
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
            try {
                ApiResponse<?> parsed = gson.fromJson(errorBody.string(), ApiResponse.class);
                if (parsed != null && parsed.getMessage() != null && !parsed.getMessage().trim().isEmpty()) {
                    return parsed.getMessage();
                }
            } catch (IOException ignored) {
            }
        }
        return response.code() > 0 ? fallback + " (HTTP " + response.code() + ")" : fallback;
    }
}
