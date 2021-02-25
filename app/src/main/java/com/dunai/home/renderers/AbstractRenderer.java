package com.dunai.home.renderers;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class AbstractRenderer extends LinearLayout {
    public AbstractRenderer(Context context) {
        super(context);
    }

    public abstract void setValue(String value);
}
