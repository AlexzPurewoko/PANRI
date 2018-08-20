package com.mylexz.utils.text.style;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.text.style.TypefaceSpan;
import android.text.TextPaint;

public class CustomTypefaceSpan extends TypefaceSpan
{
	private Typeface newType;
	public CustomTypefaceSpan(String family, Typeface type)
	{
		super(family);
		newType = type;
	}
	public CustomTypefaceSpan(Typeface type){
		super("");
		newType = type;
	}

	@Override
	public void updateDrawState(TextPaint ds)
	{
		// TODO: Implement this method
		applyCustomTypeFace(ds, newType);
	}

	@Override
	public void updateMeasureState(TextPaint paint)
	{
		// TODO: Implement this method
		applyCustomTypeFace(paint, newType);
	}
	
	private static void applyCustomTypeFace(Paint paint, Typeface tf) {
		int oldStyle;
		Typeface old = paint.getTypeface();
		if (old == null){
			oldStyle = 0;
		}
		else {
			oldStyle = old.getStyle();
		}
		int fake = oldStyle & ~tf.getStyle();
		if ((fake & Typeface.BOLD) != 0){
			paint.setFakeBoldText(true);
		}
		if ((fake & Typeface.ITALIC) != 0) {
			paint.setTextSkewX(-0.25f);
		}
		paint.setTypeface(tf);
	}
}
