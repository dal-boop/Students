// app/src/main/java/com/example/students/ui/AdminExpandableAdapter.java
package com.example.students.ui;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.students.R;
import java.util.*;

public class AdminExpandableAdapter extends BaseExpandableListAdapter {
    private final Context ctx;
    private final List<String> groups;
    private final Map<String,List<String>> children;
    private final Map<String,Integer> groupIcons = new HashMap<>();
    private final Map<String,Integer> actionIcons = new HashMap<>();

    public AdminExpandableAdapter(Context ctx,
                                  List<String> groups,
                                  Map<String,List<String>> children) {
        this.ctx = ctx;
        this.groups = groups;
        this.children = children;

        // иконки групп
        groupIcons.put("Классы",    R.drawable.ic_class);
        groupIcons.put("Предметы",  R.drawable.ic_book);
        groupIcons.put("Ученики",   R.drawable.ic_person_student);
        groupIcons.put("Учителя",   R.drawable.ic_person_teacher);

        // иконки действий
        actionIcons.put("Добавить",      R.drawable.ic_add);
        actionIcons.put("Редактировать", R.drawable.ic_edit);
        actionIcons.put("Удалить",       R.drawable.ic_delete);
    }

    @Override public int getGroupCount() { return groups.size(); }
    @Override public int getChildrenCount(int gp) { return children.get(groups.get(gp)).size(); }
    @Override public String getGroup(int gp) { return groups.get(gp); }
    @Override public String getChild(int gp,int cp) {
        return children.get(groups.get(gp)).get(cp);
    }
    @Override public long getGroupId(int gp) { return gp; }
    @Override public long getChildId(int gp,int cp) { return cp; }
    @Override public boolean hasStableIds() { return false; }
    @Override public boolean isChildSelectable(int gp,int cp) { return true; }

    @Override
    public View getGroupView(int gp, boolean isExpanded,
                             View convert, ViewGroup parent) {
        if (convert==null) {
            convert = LayoutInflater.from(ctx)
                    .inflate(R.layout.exp_group_item, parent, false);
        }
        ImageView ivIcon = convert.findViewById(R.id.ivGroupIcon);
        TextView tv = convert.findViewById(R.id.tvGroupTitle);
        ImageView ivInd = convert.findViewById(R.id.ivIndicator);

        String g = getGroup(gp);
        ivIcon.setImageResource(groupIcons.get(g));
        tv.setText(g);
        ivInd.setRotation(isExpanded?180f:0f);
        return convert;
    }

    @Override
    public View getChildView(int gp, int cp, boolean isLast,
                             View convert, ViewGroup parent) {
        if (convert==null) {
            convert = LayoutInflater.from(ctx)
                    .inflate(R.layout.exp_child_item, parent, false);
        }
        ImageView iv = convert.findViewById(R.id.ivChildIcon);
        TextView tv = convert.findViewById(R.id.tvChildTitle);

        String action = getChild(gp,cp);
        iv.setImageResource(actionIcons.get(action));
        tv.setText(action);
        return convert;
    }
}
