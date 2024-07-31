package com.example.smartestbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonNewMeal;
    private Button buttonSavedMeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNewMeal = findViewById(R.id.buttonNewMeal);
        buttonSavedMeals = findViewById(R.id.buttonSavedMeals);

        buttonNewMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewMealActivity.class);
                startActivity(intent);
            }
        });

        buttonSavedMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SavedMealsActivity.class);
                startActivity(intent);
            }
        });
    }
}
