package com.mylexz.utils;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Conversion
{
	public static final float DENSITY_1X = 160;
	
	public static final float toPixels(Context mContext, String unit){
		int lastIndex = unit.length() - 1;
		StringBuffer unitTypes = new StringBuffer("");
		StringBuffer unitValue = new StringBuffer("");
		while(lastIndex >= 0){
			if(!Character.isLetter(unit.charAt(lastIndex)))break;
			unitTypes.append(unit.charAt(lastIndex));
			lastIndex--;
		}
		if(lastIndex < 0)return 0.0f;
		// if there is any space, skipping
		while(Character.isWhitespace(unit.charAt(lastIndex)))lastIndex--;
		if(lastIndex < 0)return 0.0f;
		// gets the unit value
		while(lastIndex >= 0){
			unitValue.append(unit.charAt(lastIndex));
			lastIndex--;
		}
		// reversing
		unitTypes.reverse();
		unitValue.reverse();
		String uTp = unitTypes.toString();
		String uVal = unitValue.toString();
		if(uTp.equalsIgnoreCase("dp") || uTp.equalsIgnoreCase("dip"))return getPixelsFromDip(mContext, Float.parseFloat(uVal));
		else if(uTp.equalsIgnoreCase("dpx") || uTp.equalsIgnoreCase("dipx"))return getXPixelsFromDip(mContext, Float.parseFloat(uVal));
		else if(uTp.equalsIgnoreCase("dpy") || uTp.equalsIgnoreCase("dipy"))return getYPixelsFromDip(mContext, Float.parseFloat(uVal));
		else if(uTp.equalsIgnoreCase("mm"))return fromMMtoPixel(mContext, Float.parseFloat(uVal));
		else if(uTp.equalsIgnoreCase("pt"))return fromPTtoPixel(mContext, Float.parseFloat(uVal));
		else if(uTp.equalsIgnoreCase("cm"))return fromCMtoPixel(mContext, Float.parseFloat(uVal));
		else if(uTp.equalsIgnoreCase("px"))return Float.parseFloat(uVal);
		else return 0.0f;
	}

	
	public static float getPixelsFromDip(Context mContext, float dipValues){
		//DisplayMetrics disp = mContext.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValues, mContext.getResources().getDisplayMetrics());//dipValues * ( disp.densityDpi / DENSITY_1X);
	}
	public static float getXPixelsFromDip(Context mContext, float dipValues){
		DisplayMetrics disp = mContext.getResources().getDisplayMetrics();
		return dipValues * ( disp.xdpi / DENSITY_1X);
	}
	public static float getYPixelsFromDip(Context mContext, float dipValues){
		DisplayMetrics disp = mContext.getResources().getDisplayMetrics();
		return dipValues * ( disp.ydpi / DENSITY_1X);
	}
	public static float fromPTtoPixel(Context mContext, float pt){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, pt, mContext.getResources().getDisplayMetrics());
	}
	public static float fromMMtoPixel(Context mContext, float milimeters){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, milimeters, mContext.getResources().getDisplayMetrics());
	}
	public static float fromCMtoPixel(Context mContext, float centimeters){
		return fromMMtoPixel(mContext, centimeters * 10);
	}
	public static float fromPixeltoMM(Context mContext, float pixels){
		return pixels / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, mContext.getResources().getDisplayMetrics());
	}
	public static float fromPixeltoCM(Context mContext, float milimeters){
		return fromPixeltoMM(mContext, milimeters) * 10;
	}
}
