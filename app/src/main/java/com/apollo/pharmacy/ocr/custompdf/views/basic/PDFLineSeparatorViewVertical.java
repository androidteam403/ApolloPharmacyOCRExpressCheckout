package com.apollo.pharmacy.ocr.custompdf.views.basic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class PDFLineSeparatorViewVertical extends PDFView {
    public PDFLineSeparatorViewVertical(Context context) {
        super(context);

        View separatorLine = new View(context);
        LinearLayout.LayoutParams separatorLayoutParam = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
        separatorLine.setPadding(0, 0, 0, 0);
        separatorLine.setLayoutParams(separatorLayoutParam);

        super.setView(separatorLine);
    }

    @Override
    protected PDFLineSeparatorViewVertical addView(@NonNull PDFView viewToAdd) throws IllegalStateException {
        throw new IllegalStateException("Cannot add subview to Line Separator");
    }

    @Override
    public PDFLineSeparatorViewVertical setLayout(@NonNull ViewGroup.LayoutParams layoutParams) {
        super.setLayout(layoutParams);
        return this;
    }

    public PDFLineSeparatorViewVertical setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        return this;
    }






    public View getDashLine(Context context){
        View divider = new View(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(0, 5, 0, 5);
        divider.setLayoutParams(lp);
        divider.setBackground(CreateDashedLined());
        return divider;
    }
    public static Drawable CreateDashedLined() {
        ShapeDrawable sd = new ShapeDrawable(new RectShape());
        Paint fgPaintSel = sd.getPaint();
        fgPaintSel.setColor(Color.BLACK);
        fgPaintSel.setStyle(Paint.Style.STROKE);
        fgPaintSel.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
        return sd;
    }
}
