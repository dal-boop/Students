// com/example/studentmanage/ui/AdminOptionsAdapter.java
package com.example.students.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.students.R;
import com.example.students.model.OptionItem;

import java.util.List;

public class AdminOptionsAdapter
        extends RecyclerView.Adapter<AdminOptionsAdapter.VH> {

    private final List<OptionItem> items;

    public AdminOptionsAdapter(List<OptionItem> items) {
        this.items = items;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_option, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        OptionItem it = items.get(position);
        holder.ivIcon.setImageResource(it.iconRes);
        holder.tvTitle.setText(it.title);
        holder.itemView.setOnClickListener(it.listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        VH(View itemView) {
            super(itemView);
            ivIcon  = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
