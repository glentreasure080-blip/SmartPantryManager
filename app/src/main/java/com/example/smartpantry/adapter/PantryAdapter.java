package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {
    public interface Listener { void onEdit(PantryItem item); void onDelete(PantryItem item); }
    private List<PantryItem> items; private final Listener listener;
    public PantryAdapter(List<PantryItem> items, Listener listener) { this.items = items; this.listener = listener; }
    public void setItems(List<PantryItem> items) { this.items = items; notifyDataSetChanged(); }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new Holder(LayoutInflater.from(p.getContext()).inflate(R.layout.row_pantry, p, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        PantryItem x = items.get(pos);
        h.name.setText(x.getName());
        h.qty.setText("Quantity: " + x.getQuantity() + " " + x.getUnit());
        h.expiry.setText(x.getExpiryDate() == null || x.getExpiryDate().isEmpty() ? "Expiry: Not set" : "Expiry: " + x.getExpiryDate());
        h.edit.setOnClickListener(v -> listener.onEdit(x));
        h.delete.setOnClickListener(v -> listener.onDelete(x));
    }
    @Override public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name, qty, expiry; Button edit, delete;
        Holder(View v) {
            super(v); name=v.findViewById(R.id.txtName); qty=v.findViewById(R.id.txtQuantity);
            expiry=v.findViewById(R.id.txtExpiry); edit=v.findViewById(R.id.btnEdit); delete=v.findViewById(R.id.btnDelete);
        }
    }
}
