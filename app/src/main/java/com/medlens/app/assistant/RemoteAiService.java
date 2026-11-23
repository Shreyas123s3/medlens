package com.medlens.app.assistant;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/** Configured only when a backend URL is supplied; the app never contains an AI provider key. */
public final class RemoteAiService implements AiService {
    public final Retrofit retrofit;
    public RemoteAiService(String baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS).readTimeout(25, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }
    @Override public String answer(String message) {
        return "Online assistant is not configured. Continuing offline.";
    }
}
