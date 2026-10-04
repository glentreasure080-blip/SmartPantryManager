package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_main);
        ((Button)findViewById(R.id.btnPantry)).setOnClickListener(v -> startActivity(new Intent(this, PantryActivity.class)));
        ((Button)findViewById(R.id.btnSuggested)).setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));
    }
}
