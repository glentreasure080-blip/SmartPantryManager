package com.example.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import java.util.List;

public class RecipeDetailActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_recipe_detail);
        DatabaseHelper db=new DatabaseHelper(this);
        int id=getIntent().getIntExtra("recipe_id",-1);
        Recipe target=null;
        for(Recipe r:db.getSuggestedRecipes()) if(r.getId()==id) {target=r; break;}
        if(target==null){ finish(); return; }
        ((TextView)findViewById(R.id.txtRecipeName)).setText(target.getName());
        StringBuilder ingredients=new StringBuilder();
        List<RecipeIngredient> list=db.getIngredients(id);
        for(RecipeIngredient x:list) ingredients.append("• ").append(x.getName()).append(" — ").append(x.getQuantity()).append(" ").append(x.getUnit()).append("\n");
        ((TextView)findViewById(R.id.txtIngredients)).setText(ingredients.toString());
        ((TextView)findViewById(R.id.txtSteps)).setText(target.getSteps());
        ((Button)findViewById(R.id.btnBack)).setOnClickListener(v->finish());
    }
}
