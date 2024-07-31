package com.example.smartestbite;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class SavedMealsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView listViewMeals;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_meals);

        db = FirebaseFirestore.getInstance();
        listViewMeals = findViewById(R.id.listViewMeals);
        mealAdapter = new MealAdapter(this, new ArrayList<>());
        listViewMeals.setAdapter(mealAdapter);

        fetchSavedMeals();
    }

    private void fetchSavedMeals() {
        db.collection("meals")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Meal> meals = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String recipe = document.getString("recipe");
                            meals.add(new Meal(title, recipe));
                        }
                        mealAdapter.updateMeals(meals);
                    } else {
                        Log.w("Firestore", "Error fetching meals", task.getException());
                        Toast.makeText(SavedMealsActivity.this, "Error fetching meals.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
