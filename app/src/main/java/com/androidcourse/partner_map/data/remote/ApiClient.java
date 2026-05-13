package com.androidcourse.partner_map.data.remote;

import android.util.Log;

import com.androidcourse.partner_map.app.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static ApiClient instance;
    private final ApiService apiService;
    private String token;
    private AuthFailureListener authFailureListener;

    public interface AuthFailureListener {
        void onAuthFailure();
    }

    private static final TypeAdapter<Long> LONG_ADAPTER = new TypeAdapter<Long>() {
        @Override
        public Long read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return 0L;
            } else if (in.peek() == JsonToken.NUMBER) {
                return in.nextLong();
            } else if (in.peek() == JsonToken.STRING) {
                String s = in.nextString();
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    return parseIsoDate(s);
                }
            }
            return 0L;
        }

        @Override
        public void write(JsonWriter out, Long value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        private long parseIsoDate(String s) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(s).getTime();
            } catch (Exception e1) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(s).getTime();
                } catch (Exception e2) {
                    return 0L;
                }
            }
        }
    };

    private ApiClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .registerTypeAdapter(Long.class, LONG_ADAPTER)
                .registerTypeAdapter(long.class, LONG_ADAPTER)
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request.Builder builder = chain.request().newBuilder();
                    if (token != null && !token.isEmpty()) {
                        builder.addHeader("Authorization", "Bearer " + token);
                        Log.d(TAG, "Adding auth header, token prefix: " + token.substring(0, Math.min(20, token.length())) + "...");
                    } else {
                        Log.w(TAG, "No token set - request will be unauthenticated: " + chain.request().url());
                    }
                    return chain.proceed(builder.build());
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());
                        if (response.code() == 401) {
                            Log.e(TAG, "Received 401, clearing token");
                            token = null;
                            if (authFailureListener != null) {
                                new android.os.Handler(android.os.Looper.getMainLooper())
                                        .post(() -> authFailureListener.onAuthFailure());
                            }
                        }
                        return response;
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public void setToken(String token) {
        this.token = token;
        Log.d(TAG, "Token updated, present=" + (token != null && !token.isEmpty()));
    }

    public void setAuthFailureListener(AuthFailureListener listener) {
        this.authFailureListener = listener;
    }

    public ApiService getApiService() {
        return apiService;
    }
}
