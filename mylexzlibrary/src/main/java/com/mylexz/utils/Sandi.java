/*
 * Copyright (C) 2018 @Alexzander Purwoko Widiantoro
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
 */
package com.mylexz.utils;
import java.util.List;
import java.util.ArrayList;

public final class Sandi
{
    public static final int BITS_8 = 0x00008;
	public static final int BITS_16 = 0x00010;
	private static native String ToMorse(String p);
	private static native String ToStr(String p);
	
	private static final char btoi(final char[] s){
		 int l = s.length;
		 int sum = 0;
		 int x;
		 for (x = l-1; x>=0; x--)
		 	sum+=((s[x] -'0')*((int)Math.pow(2, ((l-1)-x))));
		 return (char)sum;
	}
	private static final boolean cmp(char a, char b){
		return a == b ?true:false;
	}
	
   static {
       System.loadLibrary("msandi");
   }
	private static int[] StrToASCII(String code)
	{
		int[] asciiArr = new int[code.length()];
		for (int x = 0; x < code.length(); x++)
		{
			asciiArr[x] = (int)code.charAt(x);
		}
		return asciiArr;
	}
	private static int hexCode(char c)
	{
		if (c >= '0' && c <= '9')
		{
			return c - '0';
		}
		else if (c >= 'a' && c <= 'f')
		{
			return (c - 'a') + 10;
		}
		else if (c >= 'A' && c <= 'F')
		{
			return (c - 'A') + 10;
		}
		return 0;
	}
	public static String reverse(String in)
	{
		StringBuilder o = new StringBuilder(in);
		o.reverse();
		return o.toString();
	}
	
	// String To Any
	public static native String StringToBinary(String source);
	public static String StringToASCII(String str)
	{
		int[] ascii = StrToASCII(str);
		StringBuilder a = new StringBuilder();
		for (int x = 0; x < ascii.length; x++)
		{
			a.append(ascii[x]);
			if (x + 1 != ascii.length)a.append(' ');
		}
		return a.toString();
	}
	public static String StringToHex(String str)
	{
		int[] ascii = StrToASCII(str);
		StringBuilder a = new StringBuilder();
		for (int x = 0; x < ascii.length; x++)
		{
			a.append(Integer.toHexString(ascii[x]));
			if (x + 1 != ascii.length)a.append(' ');
		}
		return a.toString();
	}
	public static String StringToOctal(String str)
	{
		int[] ascii = StrToASCII(str);
		StringBuilder a = new StringBuilder();
		for (int x = 0; x < ascii.length; x++)
		{
			a.append(Integer.toOctalString(ascii[x]));
			if (x + 1 != ascii.length)a.append(' ');
		}
		return a.toString();
	}
	public static String StringToMorse(String str)
	{
		return ToMorse(str);
	}
	// Hex To Any
	/*********************************/
	public static String HexToString(String hex)
	{
		StringBuilder result = new StringBuilder();
		String tmp= reverse(hex);
		//System.out.println(tmp);
		int len = tmp.length();
		for (int x = 0, y = 0, temp = 0; x < len; x++)
		{
			y = 0;
			while (x < len && tmp.charAt(x) != ' ')
			{
				temp += (((int)Math.pow(16, y)) * (hexCode(tmp.charAt(x))));
				x++;
				y++;
			}
			//System.out.println(temp);
			result.append((char)temp);
			temp = 0;
		}
		result.reverse();
		return result.toString();
	}
	public static String HexToOctal(String hex){
		StringBuilder result = new StringBuilder();
		String tmp= reverse(hex);
		//System.out.println(tmp);
		int len = tmp.length();
		for (int x = 0, y = 0, temp = 0; x < len; x++)
		{
			y = 0;
			while (x < len && tmp.charAt(x) != ' ')
			{
				temp += (((int)Math.pow(16, y)) * (hexCode(tmp.charAt(x))));
				x++;
				y++;
			}
			//System.out.println(temp);
			result.append(reverse(Integer.toOctalString(temp)));
			if(x+1 != len)result.append(' ');
			temp = 0;
		}
		result.reverse();
		return result.toString();
	}
	public static String HexToASCII(String hex){
		StringBuilder result = new StringBuilder();
		String tmp= reverse(hex);
		//System.out.println(tmp);
		int len = tmp.length();
		for (int x = 0, y = 0, temp = 0; x < len; x++)
		{
			y = 0;
			while (x < len && tmp.charAt(x) != ' ')
			{
				temp += (((int)Math.pow(16, y)) * (hexCode(tmp.charAt(x))));
				x++;
				y++;
			}
			//System.out.println(temp);
			result.append(reverse(""+temp));
			if(x+1 != len)result.append(' ');
			temp = 0;
		}
		result.reverse();
		return result.toString();
	}
	public static String HexToMorse(String hex){
		return ToMorse(HexToString(hex));
	}
	public static String HexToBinary(String hex){
		return StringToBinary(HexToString(hex));
	}
	
	// Morse Converter
	public static String MorseToString(String morse)
	{
		return ToStr(morse);
	}
	public static String MorseToHex(String morse){
		return StringToHex(ToStr(morse));
	}
	public static String MorseToOctal(String morse){
		return StringToOctal(ToStr(morse));
	}
	public static String MorseToASCII(String morse){
		return StringToASCII(ToStr(morse));
	}
	public static String MorseToBinary(String morse){
		return StringToBinary(ToStr(morse));
	}
	// Binary Converter

	public static String BinaryToString(String source){
		int sz = source.length();
		int nbits = 8;
		char[] t = new char[nbits];
		int sum = 1;
		int a;
		for(a = 0; a < sz; a++){
			if(source.charAt(a) == ' '){
				if(source.charAt(++a) == ' ')continue;
				sum++;
			}
		}
		char[] tmp = new char[sum];
		/********* Kueri ********/

		int y, z;
		int x = (y = z = 0);
		while(x < sz) {
			int d = (x+1);
		 	if (d >= sz){
		 		t[y++] = source.charAt(x);
				y = 0;
				tmp[z] = btoi(t);
				z++;
				break;
			}
			if (cmp(source.charAt(x), ' ')){
				y = 0;
				tmp[z++] = btoi(t);
			}
			else 
				t[y++] = source.charAt(x);
			x++;
		}
		return String.valueOf(tmp);
	}
	public static String BinaryToMorse(String Binary){
		return ToMorse(BinaryToString(Binary));
	}
	public static String BinaryToASCII(String Binary){
		return StringToASCII(BinaryToString(Binary));
	}
	public static String BinaryToOctal(String Binary){
		return StringToOctal(BinaryToString(Binary));
	}
	public static String BinaryToHex(String Binary){
		return StringToHex(BinaryToString(Binary));
	}
	// ASCII To Any
	public static String ASCIIToString(String ascii)
	{
		StringBuilder result = new StringBuilder();
		for (int x = 0, temp = 0; x < ascii.length(); x++)
		{
			while( x < ascii.length() && ascii.charAt(x) != ' '){
				temp += (ascii.charAt(x) - '0');
				temp *= 10;
				x++;
			}
			temp /= 10;
			result.append((char)temp);
			temp = 0;

		}
		return result.toString();
	}
	public static String ASCIIToHex(String ascii){
		StringBuilder result = new StringBuilder();
		for (int x = 0, temp = 0; x < ascii.length(); x++)
		{
			while( x < ascii.length() && ascii.charAt(x) != ' '){
				temp += (ascii.charAt(x) - '0');
				temp *= 10;
				x++;
			}
			temp /= 10;
			result.append(Integer.toHexString(temp));
			if(x+1 != ascii.length())result.append(' ');
			temp = 0;

		}
		return result.toString();
	}
	public static String ASCIIToOctal(String ascii){
		StringBuilder result = new StringBuilder();
		for (int x = 0, temp = 0; x < ascii.length(); x++)
		{
			while( x < ascii.length() && ascii.charAt(x) != ' '){
				temp += (ascii.charAt(x) - '0');
				temp *= 10;
				x++;
			}
			temp /= 10;
			result.append(Integer.toOctalString(temp));
			if(x+1 != ascii.length())result.append(' ');
			temp = 0;

		}
		return result.toString();
	}
	public static String ASCIIToMorse(String ascii){
		return ToMorse(ASCIIToString(ascii));
	}
	public static String ASCIIToBinary(String ascii){
		return StringToBinary(ASCIIToString(ascii));
	}
	// Octal Converter

	public static String OctalToString(String octal)
	{
		StringBuilder result = new StringBuilder();
		String tmp= reverse(octal);
		//System.out.println(tmp);
		int len = tmp.length();
		for (int x = 0, y = 0, temp = 0; x < len; x++)
		{
			y = 0;
			while (x < len && tmp.charAt(x) != ' ')
			{
				temp += (((int)Math.pow(8, y)) * (tmp.charAt(x) - '0'));
				x++;
				y++;
			}
			//System.out.println(temp);
			result.append((char)temp);
			temp = 0;
		}
		result.reverse();
		return result.toString();
	}

	public static String OctalToHex(String octal)
	{
		StringBuilder result = new StringBuilder();
		String tmp= reverse(octal);
		//System.out.println(tmp);
		int len = tmp.length();
		for (int x = 0, y = 0, temp = 0; x < len; x++)
		{
			y = 0;
			while (x < len && tmp.charAt(x) != ' ')
			{
				temp += (((int)Math.pow(8, y)) * (tmp.charAt(x) - '0'));
				x++;
				y++;
			}
			//System.out.println(temp);
			result.append(reverse(Integer.toHexString(temp)));
			if(x+1 != octal.length())result.append(' ');
			temp = 0;
		}
		result.reverse();
		return result.toString();
	}
	public static String OctalToASCII(String octal)
	{
		StringBuilder result = new StringBuilder();
		String tmp= reverse(octal);
		//System.out.println(tmp);
		int len = tmp.length();
		for (int x = 0, y = 0, temp = 0; x < len; x++)
		{
			y = 0;
			while (x < len && tmp.charAt(x) != ' ')
			{
				temp += (((int)Math.pow(8, y)) * (tmp.charAt(x) - '0'));
				x++;
				y++;
			}
			//System.out.println(temp);
			result.append(reverse(temp+""));
			if(x+1 != octal.length())result.append(' ');
			temp = 0;
		}
		result.reverse();
		return result.toString();
	}
	public static String OctalToMorse(String octal){
		return ToMorse(OctalToString(octal));
	}
	public static String OctalToBinary(String octal){
		return StringToBinary(OctalToString(octal));
	}
}
