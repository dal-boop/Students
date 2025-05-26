package com.example.students.model;

import android.view.View;

public class OptionItem {
    public final int    iconRes;
    public final String title;
    public final View.OnClickListener listener;

    public OptionItem(int iconRes, String title, View.OnClickListener listener) {
        this.iconRes  = iconRes;
        this.title    = title;
        this.listener = listener;
    }
}
