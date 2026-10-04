package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.R;
import com.example.smartpantry.model.Recipe;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {
    public interface Listener { void onClick(Recipe recipe); }
    private List<Recipe> recipes; private final Listener listener;
    public RecipeAdapter(List<Recipe> recipes, Listener listener) { this.recipes = recipes; this.listener = listener; }
    public void setRecipes(List<Recipe> recipes) { this.recipes = recipes; notifyDataSetChanged(); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new Holder(LayoutInflater.from(p.getContext()).inflate(R.layout.row_recipe, p, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        Recipe r = recipes.get(pos); h.name.setText(r.getName());
        h.hint.setText("All required ingredients and quantities are available.");
        h.itemView.setOnClickListener(v -> listener.onClick(r));
    }
    @Override public int getItemCount() { return recipes.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        TextView name, hint;
        Holder(View v) { super(v); name=v.findViewById(R.id.txtRecipeName); hint=v.findViewById(R.id.txtRecipeHint); }
    }
}
