package com.example.smartestbite;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewMealActivity extends AppCompatActivity {

    private EditText editTextCalories, editTextPreferences;
    private Button buttonGenerate, buttonSave, buttonRefresh;
    private TextView textViewMealTitle, textViewMealRecipe;
    private Retrofit retrofit;
    private ChatGPTService chatGPTService;
    private FirebaseFirestore db; // Declare Firestore instance
    private boolean isToastShown = false; // Flag to prevent multiple toasts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meal);

        editTextCalories = findViewById(R.id.editTextCalories);
        editTextPreferences = findViewById(R.id.editTextPreferences);
        buttonGenerate = findViewById(R.id.buttonGenerate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonRefresh = findViewById(R.id.buttonRefresh);
        textViewMealTitle = findViewById(R.id.textViewMealTitle);
        textViewMealRecipe = findViewById(R.id.textViewMealRecipe);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/") // Correct base URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request().newBuilder()
                                        .addHeader("Authorization", "Bearer sk-proj-XSXywTmBZnQhABkuRP6CT3BlbkFJznTNUzCYewHRfxv2LDcl") // Your API key
                                        .build();
                                return chain.proceed(request);
                            }
                        }).build())
                .build();

        chatGPTService = retrofit.create(ChatGPTService.class);

        buttonGenerate.setOnClickListener(v -> generateMeal());
        buttonSave.setOnClickListener(v -> saveMeal());
        buttonRefresh.setOnClickListener(v -> generateMeal());
    }

    private void generateMeal() {
        String calories = editTextCalories.getText().toString();
        String preferences = editTextPreferences.getText().toString();
        String messageContent = "(Always respond like you're talking with a customer, take in account that some desserts and foods could be considered 0 calories like sugar free items. Give the recipe in brief bullet points.) Suggest a realistic meal (if the meal is not realistic reply with Oops! That sadly does not exit. and give a more realistic response)  with " + calories + " calories and preferences: " + preferences;

        List<ChatGPTRequest.Message> messages = List.of(new ChatGPTRequest.Message("user", messageContent));

        // Use gpt-3.5-turbo
        ChatGPTRequest request = new ChatGPTRequest("gpt-3.5-turbo", messages, 250);

        chatGPTService.generateMeal(request).enqueue(new Callback<ChatGPTResponse>() {
            @Override
            public void onResponse(Call<ChatGPTResponse> call, Response<ChatGPTResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_SUCCESS", "Response: " + response.body().toString());
                    textViewMealTitle.setText("Suggested Meal:");
                    textViewMealRecipe.setText(response.body().getText());
                    isToastShown = false;
                } else {
                    Log.e("API_ERROR", "Error Body: " + response.errorBody());
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<ChatGPTResponse> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void handleErrorResponse(Response<ChatGPTResponse> response) {
        String errorBody = "";
        if (response.errorBody() != null) {
            try {
                errorBody = response.errorBody().string();
            } catch (IOException e) {
                errorBody = "Error reading error body: " + e.getMessage();
            }
        }

        Log.e("API_ERROR", "Failed to generate meal: " + errorBody);
        if (errorBody.contains("insufficient_quota")) {
            showToast("Quota exceeded. Please check your OpenAI plan.");
        } else if (errorBody.contains("missing_required_parameter")) {
            showToast("Missing required parameter: 'messages'. Check your request format.");
        } else {
            showToast("Failed to generate meal. Check log for details.");
        }
    }

    private void saveMeal() {
        String mealTitle = textViewMealTitle.getText().toString();
        String mealRecipe = textViewMealRecipe.getText().toString();

        if (mealTitle.isEmpty() || mealRecipe.isEmpty()) {
            Toast.makeText(NewMealActivity.this, "Meal details are incomplete.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> meal = new HashMap<>();
        meal.put("title", mealTitle);
        meal.put("recipe", mealRecipe);

        db.collection("meals")
                .add(meal)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NewMealActivity.this, "Meal saved successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving meal", e);
                    Toast.makeText(NewMealActivity.this, "Error saving meal.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showToast(String message) {
        if (!isToastShown) {
            Toast.makeText(NewMealActivity.this, message, Toast.LENGTH_SHORT).show();
            isToastShown = true;
        }
    }
}
