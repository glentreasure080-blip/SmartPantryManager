package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.adapter.RecipeAdapter;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import java.util.List;

public class SuggestedRecipesActivity extends Activity {
    private DatabaseHelper db; private RecipeAdapter adapter; private TextView message;
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_suggested);
        db=new DatabaseHelper(this); message=findViewById(R.id.txtMessage);
        RecyclerView rv=findViewById(R.id.recyclerRecipes); rv.setLayoutManager(new LinearLayoutManager(this));
        adapter=new RecipeAdapter(db.getSuggestedRecipes(), r -> {
            Intent i=new Intent(this, RecipeDetailActivity.class); i.putExtra("recipe_id", r.getId()); startActivity(i);
        }); rv.setAdapter(adapter);
        ((Button)findViewById(R.id.navHome)).setOnClickListener(v->{startActivity(new Intent(this,MainActivity.class));finish();});
        ((Button)findViewById(R.id.navPantry)).setOnClickListener(v->startActivity(new Intent(this,PantryActivity.class)));
        refresh();
    }
    @Override protected void onResume(){super.onResume(); if(adapter!=null) refresh();}
    private void refresh(){
        List<Recipe> list=db.getSuggestedRecipes(); adapter.setRecipes(list);
        message.setText(list.isEmpty() ? "No recipes match your pantry yet. Add the missing ingredients." : list.size()+" recipe(s) can be made right now.");
    }
}
