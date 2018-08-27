package com.mylexz.utils;
public class PrimitiveData
{
	private int __desc__ = -1;
	static {
		System.loadLibrary("mPDATA");
	}
	public native void open(String file, String signature);
	public native void close();
	public native int getErrors();
	public native int getContentPosition(String element, String data);
	public native int getContentPosition(String element, int data);
	public native int getContentPosition(String element, long data);
	public native int getContentPosition(String element, double data);
	public native int getContentPosition(String element, char data);
	
	public native int getArrLength(String element);
	public native void addElement(String element, String data);
	public native void addElement(String element, int data);
	public native void addElement(String element, char data);
	public native void addElement(String element, long data);
	public native void addElement(String element, double data);
	
	public native void deleteElement(String element);
	public native String getStrElement(String element);
	public native int getIntElement(String element);
	public native long getLongElement(String element);
	public native double getDoubleElement(String element);
	public native char getCharElement(String element);
	
	public native void setElement(String element, String reserved);
	public native void setElement(String element, int reserved);
	public native void setElement(String element, long reserved);
	public native void setElement(String element, double reserved);
	public native void setElement(String element, char reserved);
	
	public native void addArray(String element, int[] data);
	public native void addArray(String element, long[] data);
	public native void addArray(String element, double[] data);
	public native void addArray(String element, char[] data);
	
	public native int[] getAllIntContentInArr(String element);
	public native long[] getAllLongContentInArr(String element);
	public native double[] getAllDoubleContentInArr(String element);
	public native char[] getAllCharContentInArr(String element);
	
	public native int getIntContentArrAtPos(String element, int position);
	public native long getLongContentArrAtPos(String element, int position);
	public native double getDoubleContentArrAtPos(String element, int position);
	public native char getCharContentArrAtPos(String element, int position);
	
	public native void appendArray(String element, int[] data);
	public native void appendArray(String element, long[] data);
	public native void appendArray(String element, double[] data);
	public native void appendArray(String element, char[] data);
	
	public native void setArray(String element, int[] data);
	public native void setArray(String element, long[] data);
	public native void setArray(String element, double[] data);
	public native void setArray(String element, char[] data);
	
	public native void setDataAtArrPos(String element, int data, int position);
	public native void setDataAtArrPos(String element, long data, int position);
	public native void setDataAtArrPos(String element, double data, int position);
	public native void setDataAtArrPos(String element, char data, int position);
	
	public native boolean isArrayType(String element);
	public native int getElementDataType(String element);

	/* function for string array */
	public native void addStrArray(String element, String str[], int dataLength);
	public native String getStrAtPos(String element, int position);
	public native String[] getStrArray(String element);
	public native void appendStrArray(String element,  String str[], int dataLength);
	public native void setStrArray(String element,  String str[], int dataLength);
	public native void setStrArrAtPosition(String element,  String str, int position);
	public native String readString(String element);
	public native void reset();
	public native boolean isElementExists(String element);
}
