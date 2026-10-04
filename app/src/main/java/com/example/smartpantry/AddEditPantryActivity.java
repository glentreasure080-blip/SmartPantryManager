package com.example.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;

public class AddEditPantryActivity extends Activity {
    private DatabaseHelper db; private EditText name, qty, unit, expiry; private TextView error; private int editId = -1;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_add_edit_pantry);
        db = new DatabaseHelper(this);
        name=findViewById(R.id.edtName); qty=findViewById(R.id.edtQuantity); unit=findViewById(R.id.edtUnit); expiry=findViewById(R.id.edtExpiry); error=findViewById(R.id.txtError);
        if (getIntent().hasExtra("id")) {
            editId=getIntent().getIntExtra("id",-1);
            PantryItem x=db.getPantry(editId);
            if(x!=null){ name.setText(x.getName()); qty.setText(String.valueOf(x.getQuantity())); unit.setText(x.getUnit()); expiry.setText(x.getExpiryDate()); ((TextView)findViewById(R.id.txtTitle)).setText("Edit Pantry Item"); }
        }
        ((Button)findViewById(R.id.btnSave)).setOnClickListener(v -> save());
        ((Button)findViewById(R.id.btnCancel)).setOnClickListener(v -> finish());
    }

    private void save() {
        String n=name.getText().toString().trim(), q=qty.getText().toString().trim(), u=unit.getText().toString().trim(), e=expiry.getText().toString().trim();
        if(n.isEmpty()){showError("Ingredient name is required."); return;}
        if(q.isEmpty()){showError("Quantity is required."); return;}
        if(u.isEmpty()){showError("Unit is required."); return;}
        double number;
        try { number=Double.parseDouble(q); } catch(Exception ex){ showError("Quantity must be a number."); return; }
        if(number<=0){showError("Quantity must be greater than 0."); return;}
        if(!e.isEmpty() && !e.matches("\\d{4}-\\d{2}-\\d{2}")){showError("Expiry date must use YYYY-MM-DD."); return;}
        PantryItem x=new PantryItem(editId,n,number,u,e);
        if(editId==-1) db.insertPantry(x); else db.updatePantry(x);
        finish();
    }
    private void showError(String s){ error.setText(s); error.setVisibility(TextView.VISIBLE); }
}
