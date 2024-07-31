package com.example.smartestbite;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatGPTService {
    @POST("v1/chat/completions") // Correct endpoint
    Call<ChatGPTResponse> generateMeal(@Body ChatGPTRequest request);
}
