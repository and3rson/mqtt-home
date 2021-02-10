package com.dunai.home.renderers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.Button;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.ColorWidget;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

/**
 * TODO: document your custom view class.
 */
public class ColorWidgetRenderer extends WidgetRenderer {
    private ColorWidget workspaceColorWidget;

    private Button button;
    private int color = -1;

    public ColorWidgetRenderer(Context context, ColorWidget workspaceColorWidget, String value) {
        super(context, workspaceColorWidget);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.setForceDarkAllowed(false);
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.color_renderer, this.findViewById(R.id.rendererContainer), true);

        this.workspaceColorWidget = workspaceColorWidget;

        this.button = this.findViewById(R.id.colorRendererButton);

        this.setValue(value);

        this.button.setOnClickListener(v -> {
            ColorPickerDialogBuilder
                    .with(getContext())
//            ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this.getContext())
                    .setTitle("Select color")
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int selectedColor) {
//                            String hexCode = envelope.getHexCode();
                            // TODO
//                            switch (workspaceColorWidget.format) {
//                                case HTML:
//                                    finalValue =
//                                    break;
//                                case COMMA:
//                                    break;
//                                case HSV:
//                                    break;
//                                case INT:
//                                    break;
//                            }
//                            if (workspaceColorWidget.alpha) {
//                                //
//                            } else {
//                                //
//                            }
                            HomeClient.getInstance().publish(
                                    workspaceColorWidget.topic,
                                    formatColor(selectedColor),
                                    workspaceColorWidget.retain
                            );
//                            button.getBackground().setColorFilter(envelope.getColor(), PorterDuff.Mode.SRC_ATOP);
                        }
                    })
//                    .setPositiveButton("Done", new ColorPickerClickListener() {
//                        @Override
//                        public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
//                            //
//                        }
//                    })
                    .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .density(12)
                    .initialColor(this.color != -1 ? this.color : Color.parseColor("#FFFFFF"))
                    .build()
                    .show()
            ;
        });
    }

    public String formatColor(int color) {
        String hexCode = String.format("%08X", color);
        return "#" + (this.workspaceColorWidget.alpha ? hexCode : hexCode.substring(2));
    }

    public void setValue(String value) {
        if (value != null) {
            this.color = Color.parseColor(value);
            this.button.setBackgroundTintList(ColorStateList.valueOf(this.color));
            this.button.setText(this.formatColor(this.color));
            super.notifyValueChanged();
        } else {
            this.button.setBackgroundTintList(ColorStateList.valueOf(0));
            this.button.setText("(None)");
        }
    }
}