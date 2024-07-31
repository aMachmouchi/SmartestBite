package com.example.smartestbite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class MealAdapter extends ArrayAdapter<Meal> {
    private final Context context;
    private final List<Meal> meals;

    public MealAdapter(Context context, List<Meal> meals) {
        super(context, 0, meals);
        this.context = context;
        this.meals = meals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.meal_item, parent, false);
        }

        Meal meal = meals.get(position);
        TextView titleView = convertView.findViewById(R.id.mealTitle);
        TextView recipeView = convertView.findViewById(R.id.mealRecipe);

        titleView.setText(meal.getTitle());
        recipeView.setText(meal.getRecipe());

        return convertView;
    }

    public void updateMeals(List<Meal> newMeals) {
        meals.clear();
        meals.addAll(newMeals);
        notifyDataSetChanged();
    }
}
