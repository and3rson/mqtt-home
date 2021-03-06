package com.dunai.home.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.dunai.home.R;
import com.google.android.material.textfield.TextInputEditText;

public class KeyValueView extends LinearLayout {
    TextInputEditText keyEdit;
    TextInputEditText valueEdit;
    private OnClickListener onMoveUpRequestedListener;
    private OnClickListener onMoveDownRequestedListener;
    private OnClickListener onDeleteRequestedListener;
    private OnKeyValueChangedListener onKeyValueChangedListener;

    public KeyValueView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.key_value_edit, this, true);

        this.keyEdit = findViewById(R.id.keyEdit);
        this.valueEdit = findViewById(R.id.valueEdit);

        this.findViewById(R.id.upButton).setOnClickListener(v -> {
            if (onMoveUpRequestedListener != null) {
                onMoveUpRequestedListener.onClick(v);
            }
        });

        this.findViewById(R.id.downButton).setOnClickListener(v -> {
            if (onMoveDownRequestedListener != null) {
                onMoveDownRequestedListener.onClick(v);
            }
        });

        this.findViewById(R.id.deleteButton).setOnClickListener(v -> {
            if (onDeleteRequestedListener != null) {
                this.onDeleteRequestedListener.onClick(v);
            }
        });

        this.keyEdit.addTextChangedListener(new TextWatcher() {
            private String lastKey = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onKeyValueChangedListener != null && !lastKey.equals(s.toString())) {
                    onKeyValueChangedListener.keyValueChanged(keyEdit.getText().toString(), valueEdit.getText().toString());
                }
                lastKey = s.toString();
            }
        });
        this.valueEdit.addTextChangedListener(new TextWatcher() {
            private String lastValue = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onKeyValueChangedListener != null && !lastValue.equals(s.toString())) {
                    onKeyValueChangedListener.keyValueChanged(keyEdit.getText().toString(), valueEdit.getText().toString());
                }
                lastValue = s.toString();
            }
        });
    }

    public void setOnMoveUpRequested(OnClickListener listener) {
        this.onMoveUpRequestedListener = listener;
    }

    public void setOnMoveDownRequested(OnClickListener listener) {
        this.onMoveDownRequestedListener = listener;
    }

    public void setOnKeyValueChangedListener(OnKeyValueChangedListener listener) {
        this.onKeyValueChangedListener = listener;
    }

    public void setOnDeleteRequestedListener(OnClickListener listener) {
        this.onDeleteRequestedListener = listener;
    }

    public void setKey(String key) {
        this.keyEdit.setText(key);
    }

    public void setValue(String value) {
        this.valueEdit.setText(value);
    }

    public interface OnKeyValueChangedListener {
        void keyValueChanged(String key, String value);
    }
}
