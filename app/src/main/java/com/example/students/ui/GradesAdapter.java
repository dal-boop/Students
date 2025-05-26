// app/src/main/java/com/example/students/ui/GradesAdapter.java
package com.example.students.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.students.R;
import com.example.students.model.GradeItem;

import java.util.ArrayList;
import java.util.List;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.VH> {

    private final Context context;
    private final List<GradeItem> items = new ArrayList<>();

    public GradesAdapter(Context context) {
        this.context = context;
    }

    /**
     * Обновить список оценок и перерисовать.
     */
    public void setData(List<GradeItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_grade_subject, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        GradeItem it = items.get(position);
        holder.tvSubject.setText(it.subjectName);
        holder.gradeContainer.removeAllViews();

        // Динамически создаём TextView для каждой оценки
        for (int g : it.grades) {
            TextView tv = new TextView(context);
            tv.setText(String.valueOf(g));
            tv.setTextColor(context.getResources().getColor(android.R.color.white));
            tv.setTextSize(14);
            tv.setPadding(8, 4, 8, 4);

            // Выбираем фон в зависимости от оценки
            int bgColorRes;
            switch (g) {
                case 2: bgColorRes = R.color.grade2; break;
                case 3: bgColorRes = R.color.grade3; break;
                case 4: bgColorRes = R.color.grade4; break;
                case 5:
                default: bgColorRes = R.color.grade5; break;
            }
            tv.setBackgroundColor(context.getResources().getColor(bgColorRes));

            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(4, 0, 4, 0);
            holder.gradeContainer.addView(tv, lp);
        }

        holder.tvAverage.setText(String.format("Ø %.2f", it.average));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvSubject, tvAverage;
        ViewGroup gradeContainer;

        VH(View v) {
            super(v);
            tvSubject      = v.findViewById(R.id.tvSubjectName);
            gradeContainer = v.findViewById(R.id.llGrades);
            tvAverage      = v.findViewById(R.id.tvAverage);
        }
    }
}
