package com.example.smartpantry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.adapter.PantryAdapter;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import java.util.List;

public class PantryActivity extends Activity {
    private DatabaseHelper db; private PantryAdapter adapter;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_pantry);
        db = new DatabaseHelper(this);
        RecyclerView rv = findViewById(R.id.recyclerPantry);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(db.getAllPantry(), new PantryAdapter.Listener() {
            public void onEdit(PantryItem item) {
                Intent i = new Intent(PantryActivity.this, AddEditPantryActivity.class);
                i.putExtra("id", item.getId()); startActivity(i);
            }
            public void onDelete(PantryItem item) {
                new AlertDialog.Builder(PantryActivity.this).setTitle("Delete item?")
                        .setMessage("Delete " + item.getName() + " from the pantry?")
                        .setNegativeButton("Cancel", null).setPositiveButton("Delete", (d,w) -> { db.deletePantry(item.getId()); refresh(); }).show();
            }
        });
        rv.setAdapter(adapter);
        ((Button)findViewById(R.id.btnAdd)).setOnClickListener(v -> startActivity(new Intent(this, AddEditPantryActivity.class)));
        ((Button)findViewById(R.id.navHome)).setOnClickListener(v -> { startActivity(new Intent(this, MainActivity.class)); finish(); });
        ((Button)findViewById(R.id.navSuggested)).setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));
    }
    @Override protected void onResume() { super.onResume(); if (adapter != null) refresh(); }
    private void refresh() { List<PantryItem> list = db.getAllPantry(); adapter.setItems(list); }
}
