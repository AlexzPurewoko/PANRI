/********************** NodeData.java **********************/
/*
 * Copyright (C) 2018 by Alexzander Purwoko Widiantoro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.mylexz.utils; // Current package
import java.util.*; // for including Array List Class
import android.content.*; // for including Context class
import android.support.annotation.Nullable;

import java.io.*; // for including File operation class

public class NodeData
{	
	/*****
		* This class is include any several function into managing your data(String, Integer, Boolean, Character, Long, Double)
		* The operation is similar to NodeData.c that included from MyLEXZ-Cutils repository, because the method is implemented from this.
		* But, for fixing any memory management problems, I should move method from parent general methods into child method(JNI Method) for
		* directly operate and save the memory.
		* Don't worry about the memory, this class is useless memory, because only reclaime 1K - 1.5K memory for buffer.
	*****/
	// for Loading Libraries
	static {
		System.loadLibrary("mNDATA");
	}
	// Used as second parameters on listContents(), for filtering a list of content in a node
	public static enum Filter {
		NODE_ONLY, // Only shows a list of 'node' names in a node elements
		ARRAY_ONLY, // Only shows a list of 'array' names in a node element
		DATA_ONLY, // Only shows a list of 'data' names in a node element
		ARRAY_AND_DATA, // Shows a list of 'array' and 'data' names in a node element
		ARRAY_AND_NODE, // Shows a list of 'array' and 'node' names in a node element
		DATA_AND_NODE, // Shows a list of 'data' and 'node' names in a node element
		NO_FILTERS // Shows a full list with no filters in a node element
	}
	private int __id = -1; // for a current class position, this field is only used as native, indicate -1 for non-initialized class
	private String __filepath = ""; // for store the current filepath to the file
	private String __signature = ""; // signature, define its own file, security checking
	private String s_tmp = "";
	private int type = 0;
	private List<String> mlist;
	private List<String> mTemp;
	private int proc = 0;
	private static final String DEFAULT_SIGNATURE = "NDataFile";
	public static final int TYPE_INT = 'i';
	public static final int TYPE_LONG = 'L';
	public static final int TYPE_DOUBLE = 'F';
	public static final int TYPE_CHAR = 'c';
	public static final int TYPE_STRING = 's';
	public static final int TYPE_BOOLEAN = 'b';
	public static final int ID_NODE = '(';
	public static final int ID_ARRAY = '>';
	public static final int ID_DATA = '<';
	public static final int MODE_PRIVATE 	= 0x000001; // MODE_PRIVATE, The Data file is placed on internal storage
	public static final int MODE_PUBLIC 	= 0x000010; // MODE_PUBLIC, The Data file is placed on External mountable storage
	public NodeData(Context c, String nameFile, String signature, int mode){
		File path;
		String fpath = null;
		String p = c.getPackageName();
		switch(mode){
			case MODE_PUBLIC :
				fpath = c.getExternalFilesDir(null).getPath()+"/"+nameFile;
				break;
			case MODE_PRIVATE:
				path = new File("/data/data/"+p+"/files");
				path.mkdirs();
				fpath = path.getPath()+"/"+nameFile;
		}
		__filepath = fpath;
		__signature = signature;
		mlist = mTemp = new ArrayList<String>();
		
	}
	public NodeData(String filepath, String signature){
		__filepath = filepath;
		__signature = signature;
		mlist = mTemp = new ArrayList<String>();
	}
	public NodeData(String filepath){
		__filepath = filepath;
		__signature = DEFAULT_SIGNATURE;
		mlist = mTemp = new ArrayList<String>();
	}
	public native void open();
	public native int close();
	public native void addNode(String path, String node_name);
	public native void delNode(String fullpath);
	public native void clearContents(String fullpath);
	private native String[] listContents(String fullpath);
	public List<String> listContents(String fullpath, Filter filter){
		String[] list = listContents(fullpath);
		if(list == null)return null;
		List<String> lf = new ArrayList<String>();
		switch(filter){
			case NODE_ONLY:
				for(int x = 0; x < list.length; x++){
					String tmp = list[x];
					if(tmp.charAt(tmp.length() - 1) == ID_NODE)
						lf.add(tmp);
				}
				break;
			case DATA_ONLY:
				for(int x = 0; x < list.length; x++){
					String tmp = list[x];
					if(tmp.charAt(tmp.length() - 1) == ID_NODE)continue;
					else if(tmp.charAt(tmp.length() -3) == ID_DATA)
						lf.add(tmp);
				}
				break;
			case ARRAY_ONLY:
				for(int x = 0; x < list.length; x++){
					String tmp = list[x];
					if(tmp.charAt(tmp.length() - 1) == ID_NODE)continue;
					else if(tmp.charAt(tmp.length() -3) == ID_ARRAY)
						lf.add(tmp);
				}
				break;
			case ARRAY_AND_NODE:
				for(int x = 0; x < list.length; x++){
					String tmp = list[x];
					if(tmp.charAt(tmp.length() - 1) == ID_NODE)lf.add(tmp);
					else if(tmp.charAt(tmp.length() -3) == ID_ARRAY)
						lf.add(tmp);
				}
				break;
			case DATA_AND_NODE:
				for(int x = 0; x < list.length; x++){
					String tmp = list[x];
					if(tmp.charAt(tmp.length() - 1) == ID_NODE)lf.add(tmp);
					else if(tmp.charAt(tmp.length() -3) == ID_DATA)
						lf.add(tmp);
				}
				break;
			case ARRAY_AND_DATA:
				for(int x = 0; x < list.length; x++){
					String tmp = list[x];
					if(tmp.charAt(tmp.length() - 1) == ID_NODE)continue;
					else if(tmp.charAt(tmp.length() -3) == ID_ARRAY || tmp.charAt(tmp.length() -3) == ID_DATA)
						lf.add(tmp);
				}
				break;
			case NO_FILTERS:
				for(int x = 0; x < list.length; x++)
					lf.add(list[x]);
		}
		return lf;
	}
	public native void addIntData(String path, String elem, int content, boolean encrypted);
	public native void addLongData(String path, String elem, long content, boolean encrypted);
	public native void addCharData(String path, String elem, char content, boolean encrypted);
	public native void addBoolData(String path, String elem, boolean content, boolean encrypted);
	public native void addStrData(String path, String elem, String content, boolean encrypted);
	public native void addDoubleData(String path, String elem, double content, boolean encrypted);
	public native int getIntData(String fullpath, int defaultValue);
	public native boolean getBooleanData(String fullpath, boolean defaultValue);
	public native long getLongData(String fullpath, long defaultValue);
	public native double getDoubleData(String fullpath, double defaultValue);
	public native char getCharData(String fullpath, char defaultValue);
	public native String getStringData(String fullpath, String defaultValue);
	public native void setContentData(String fullpath, int content);
	public native void setContentData(String fullpath, long content);
	public native void setContentData(String fullpath, double content);
	public native void setContentData(String fullpath, String content);
	public native void setContentData(String fullpath, boolean content);
	public native void setContentData(String fullpath, char content);	
	public native boolean isAnyErrors();
	public native void deleteData(String fullpath);
	public native void clearDataValue(String fullpath);
	public native boolean isEmptyData(String fullpath);

	public native void addIntArray(@Nullable String path, @KeepString String name_arr, @Nullable int[] content, boolean encrypt_flags);
	public native void addStringArray(@Nullable String path, @KeepString String name_arr, @Nullable String[] content, boolean encrypt_flags);
	public native void addCharArray(@Nullable String path, @KeepString String name_arr, @Nullable char[] content, boolean encrypt_flags);
	public native void addDoubleArray(@Nullable String path, @KeepString String name_arr, @Nullable double[] content, boolean encrypt_flags);
	public native void addLongArray(@Nullable String path, @KeepString String name_arr, @Nullable long[] content, boolean encrypt_flags);
	public native void addBooleanArray(@Nullable String path, @KeepString String name_arr, @Nullable boolean[] content, boolean encrypt_flags);
	public native int getOccurences(String fullpath, int data);
	public native int getOccurences(String fullpath, long data);
	public native int getOccurences(String fullpath, double data);
	public native int getOccurences(String fullpath, String data);
	public native int getOccurences(String fullpath, boolean data);
	public native int getOccurences(String fullpath, char data);
	
	public native boolean isLocked();
	public native final String getErrorDesc();
	public native int getArrayLength(String fullpath);
	public native int getDataType(String fullpath);
	public native int[] getIntArray(String fullpath);
	public String[] getStringArray(String fullpath){
		int length = getArrayLength(fullpath);
		if(length <= 0)return null;
		String[] data = new String[length];
		setReadArrayIteration(fullpath);
		try
		{
			for (int x = 0; x < length; x++)
				data[x] = (String) readNext();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			return null;
		}
		resetArrayIteration();
		return data;

	}
	public char[] getCharArray(String fullpath){
		int length = getArrayLength(fullpath);
		if(length <= 0)return null;
		char[] data = new char[length];
		setReadArrayIteration(fullpath);
		try
		{
			for (int x = 0; x < length; x++)
				data[x] = ((Character)readNext()).charValue();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			return null;
		}
		resetArrayIteration();
		return data;
	}
	public native double[] getDoubleArray(String fullpath);
	public native long[] getLongArray(String fullpath);
	public native boolean[] getBooleanArray(String fullpath);
	/******* ITERATOR FUNCTION *******/
	///* collecting data with iterator ***/////
	public native void setReadArrayIteration(String fullpath);
	public native Object readNext()throws Exception;
	public native void resetArrayIteration();
	///////////////////////////////////////////
	/****************** APPEND ARR ITERATOR ***************/
	public void startAppendArray(String fullpath){
		if(fullpath == null || mTemp.isEmpty() || proc != 0)return;
		int tp = getDataType(fullpath);
		if(tp == 0)return;
		type = tp;
		mTemp.add(fullpath);
		proc = 'a';
	}
	public void nextAppend(int data){
		if(type != TYPE_INT || proc != 'a')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAppend(long data){
		if(type != TYPE_LONG || proc != 'a')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAppend(double data){
		if(type != TYPE_DOUBLE || proc != 'a')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAppend(boolean data){
		if(type != TYPE_BOOLEAN || proc != 'a')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAppend(char data){
		if(type != TYPE_CHAR || proc != 'a')return;
		mlist.add(String.valueOf(data));
	}
	// for string data array isn't native, because for memory allocation error
	public void nextAppend(String data){
		if(type != TYPE_STRING || proc != 'a')return;
		mlist.add(data);
	}
	/**********************************/
	/***************** ADD ARRAY ITERATOR METHODS *****************/
	public void startAddArray(String path, String name, int type, boolean encrypt_flags){
		if(type != 0 || mlist.isEmpty() || proc != 0)return;
		mTemp.add(path);
		mTemp.add(name);
		mTemp.add(String.valueOf(encrypt_flags));
		this.type = type;
		proc = 'b';
	}
	public void nextAddArray(int data){
		if(type != TYPE_INT || proc != 'b')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAddArray(long data){
		if(type != TYPE_LONG || proc != 'b')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAddArray(double data){
		if(type != TYPE_DOUBLE || proc != 'b')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAddArray(boolean data){
		if(type != TYPE_BOOLEAN || proc != 'b')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAddArray(char data){
		if(type != TYPE_CHAR || proc != 'b')return;
		mlist.add(String.valueOf(data));
	}
	public void nextAddArray(String data){
		if(type != TYPE_STRING || proc != 'b')return;
		mlist.add(data);
	}
	/***************** ADD NODE ITERATOR *****************/
	public native void addNodeIter(String fullpath);
	public native void nextaddNode(String node_name);
	/**********************************/
	/**************** SAVE ITERATOR ******************/
	public void save() throws Exception{
		switch(proc){
			case 'a':
				{
					switch(type){
						case TYPE_INT:
							{
								int[] data = new int[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] = Integer.parseInt(mlist.get(x));
								appendArray(mTemp.get(0), data);
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_STRING:
							{
								String[] mArr = (String[])mlist.toArray();
								appendArray(mTemp.get(0), mArr);
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_BOOLEAN:
							{
								boolean[] data = new boolean[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] = Boolean.parseBoolean(mlist.get(x));
								appendArray(mTemp.get(0), data);
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_DOUBLE:
							{
								double[] data = new double[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] = Double.parseDouble(mlist.get(x));
								appendArray(mTemp.get(0), data);
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_CHAR:
							{
								char[] data = new char[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] =(mlist.get(x)).charAt(0);
								appendArray(mTemp.get(0), data);
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_LONG:
							{
								long[] data = new long[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] = Long.parseLong(mlist.get(x));
								appendArray(mTemp.get(0), data);
								mTemp.clear();
								mlist.clear();

							}
							break;
						default :
							{
								mTemp.clear();
								mlist.clear();
								type = 0;
								proc = 0;
								throw new Exception("Unknown data types!");
							}
					}
				}
				break;
			case 'b':
				{
					switch(type){
						case TYPE_INT:
							{
								int[] data = new int[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] = Integer.parseInt(mlist.get(x));
								addIntArray(mTemp.get(0), mTemp.get(1), data, Boolean.parseBoolean(mTemp.get(2)));
								mTemp.clear();
								mlist.clear();
							}
							break;
						case TYPE_STRING:
							{
								String[] mArr = (String[])mlist.toArray();
								addStringArray(mTemp.get(0), mTemp.get(1), mArr, Boolean.parseBoolean(mTemp.get(2)));
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_BOOLEAN:
							{
								boolean[] data = new boolean[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] = Boolean.parseBoolean(mlist.get(x));
								addBooleanArray(mTemp.get(0), mTemp.get(1), data, Boolean.parseBoolean(mTemp.get(2)));
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_DOUBLE:
							{
								double[] data = new double[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] = Double.parseDouble(mlist.get(x));
								addDoubleArray(mTemp.get(0), mTemp.get(1), data, Boolean.parseBoolean(mTemp.get(2)));
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_CHAR:
							{
								char[] data = new char[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] =(mlist.get(x)).charAt(0);
								addCharArray(mTemp.get(0), mTemp.get(1), data, Boolean.parseBoolean(mTemp.get(2)));
								mTemp.clear();
								mlist.clear();

							}
							break;
						case TYPE_LONG:
							{
								long[] data = new long[mlist.size()];
								for(int x = 0; x < mlist.size(); x++)
									data[x] = Long.parseLong(mlist.get(x));
								addLongArray(mTemp.get(0), mTemp.get(1), data, Boolean.parseBoolean(mTemp.get(2)));
								mTemp.clear();
								mlist.clear();

							}
							break;
						default :
							throw new Exception("Unknown data types!");
					}
				}
				break;
			case 'c':
				saveIteration();
				break;
			case 'd':
				saveIteration();
				s_tmp = "";
				break;
			default:
				{
					proc = type = 0;
					mlist.clear();
					mTemp.clear();
					throw new Exception("the data file is closed. make sure you call method open()");
				}
		}
		proc = type = 0;
		return;
	}
	private native void saveIteration();
	/*********************************/
	public native void setArray(String fullpath, int[] content);
	public native void setArray(String fullpath, long[] content);
	public native void setArray(String fullpath, double[] content);
	public native void setArray(String fullpath, char[] content);
	public native void setArray(String fullpath, String[] content);
	public native void setArray(String fullpath, boolean[] content);
	
	public native void setArrayAtPos(String fullpath, int[] content, int start, int end);
	public native void setArrayAtPos(String fullpath, long[] content, int start, int end);
	public native void setArrayAtPos(String fullpath, double[] content, int start, int end);
	public native void setArrayAtPos(String fullpath, String[] content, int start, int end);
	public native void setArrayAtPos(String fullpath, boolean[] content, int start, int end);
	public native void setArrayAtPos(String fullpath, char[] content, int start, int end);
	public native void appendArray(String fullpath, int[] content);
	public native void appendArray(String fullpath, long[] content);
	public native void appendArray(String fullpath, double[] content);
	public native void appendArray(String fullpath, char[] content);
	public native void appendArray(String fullpath, boolean[] content);
	public native void appendArray(String fullpath, String[] content);
	public native int[] getIntArrayAtPos(String fullpath, int start, int end);
	public native char[] getCharArrayAtPos(String fullpath, int start, int end);
	public native double[] getDoubleArrayAtPos(String fullpath, int start, int end);
	public native long[] getLongArrayAtPos(String fullpath, int start, int end);
	public native boolean[] getBooleanArrayAtPos(String fullpath, int start, int end);
	public String[] getStringArrayAtPos(String fullpath, int start, int end){
		int length = getArrayLength(fullpath);
		if(length <= 0 || start > end || end >= length)return null;
		int x, y, z;
		String[] data =new String[(end - start)+1];
		String tmp = "";
		setReadArrayIteration(fullpath);
		for( x = y = z = 0; x < length; x++){
			try
			{
				tmp = (String) readNext();
			}
			catch (Exception e)
			{}
			if( x == start){
				y = 1;
			}
			if(y == 1)data[z++] = tmp;
			if(x == end)break;
		}
		resetArrayIteration();
		return data;
	}
	public native void strCatIter(String fullpath);
	public NodeData nextCat(int data){
		if(proc != 'd') return this;
		s_tmp += String.valueOf(data);
		return this;
	}
	public NodeData nextCat(double data){
		if(proc != 'd') return this;
		s_tmp += String.valueOf(data);
		return this;
	}
	public NodeData nextCat(char data){
		if(proc != 'd') return this;
		s_tmp += String.valueOf(data);
		return this;
	}
	public NodeData nextCat(boolean data){
		if(proc != 'd') return this;
		s_tmp += String.valueOf(data);
		return this;
	}
	public NodeData nextCat(String data){
		if(proc != 'd') return this;
		s_tmp += String.valueOf(data);
		return this;
	}
	public NodeData nextCat(long data){
		if(proc != 'd') return this;
		s_tmp += String.valueOf(data);
		return this;
	}
	public native boolean isElementExists(String fullpath);
	public native void clearArrayValue(String fullpath);
	public native void deleteArrAtPos(String fullpath, int start, int end);
}
