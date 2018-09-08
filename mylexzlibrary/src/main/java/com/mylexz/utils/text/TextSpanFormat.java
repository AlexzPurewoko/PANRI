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
 
 /*
  * TextSpanFormat.java
  * For converting TextSpanned format into SpannableString That shows the text formatted
  */
  
  /*********** Text Queries Usages 
   * > 		**[text]** 			-> 			for make a bold text
   * > 		--[text]-- 			-> 			for make an underlined text
   * > 		__[text]__ 			-> 			for make an italic text
   * > 		~~[text]~~ 			-> 			for make a strikethrough text
   * > 		%+[text]%+ 			-> 			for make a superscript text
   * > 		%-[text]%- 			-> 			for make a subscript text
   * > 		[[background={color}]] ... [[background]] or [[bgcolor={color}]] ... [[bgcolor]]
   				-> change the background of text with color specified.
				   the color value can be hex value defined as '#' and between of cyan, blue, magenta, yellow, ltgray, dkgray, black, grey, white, transparent, or red
				-> Examples
					
				   1. [[bgcolor=yellow]]Text[[bgcolor]] 	-> change the background color into yellow
				   2. [[bgcolor=#ffffff]]Text[[bgcolor]] 	-> change the background color into white

   * > 		[[foreground={color}]] ... [[foreground]] or [[color={color}]] ... [[color]]
   				-> change the Text Color with color specified.
				   the color value can be hex value defined as '#' and between of cyan, blue, magenta, yellow, ltgray, dkgray, black, grey, white, transparent, or red
				-> Examples
					
				   1. [[color=yellow]]Text[[color]] 	-> change the text color into yellow
				   2. [[color=#ffffff]]Text[[color]] 	-> change the text color into white

   * > 		[[link={url}]] ... [[link]] 
 				-> makes the text linked witb url specified
 				   You must specify the TextView into [textViewField].setMovementMethod(LinkMovementMethod.getInstance()) tp get the link worked
				   
				-> Examples

 				   1. [[link=http://google.com]]Google[[link]] 	-> makes the "Google" text linked and when clicked its showed a dialog to go to browser

   * > 		[[size={floatsize}]] ... [[size]] 
 				-> change the size of text with floatsize specified.
  				   the floatsize value is the value to set the size of text
				-> Examples

 				   1. [[size=2.5f]]Text[[size]] 	-> change the text size into 2.5f

   * > 		[[text PARAMETERS ]] ... [[text]] 
 				-> Sets the text-formatting with the PARAMETERS
				
				-> List of PARAMETERS included :
				
				   1. bgcolor={color} or background={color}
				   2. foreground={color} or color={color}
				   3. size={floatSize} or font-size={floatSize}
				   4. typeface={fontTypeface} or font-family={fontTypeface}
				   	  -> for change the text font into fontTypeface specified
					     the value of fontTypeface can be serif, monospace, or sans-serif or you can load the custom font-family in the assets by typing '@' symbol following the path of font included
				   5. style={fontStyle} or font-style={fontStyle}
				      -> change the style text into bold, underline, strikethrough, italic, superscript or subscript as the fontStyle defined
					     the value can be multiple(ex: underline,italic), separated by commas (ex: we have to make underline-bolded text, so the value is style=underline,bold)
						 WARNING : if you sets the superscript and subscript int the same value, your app will crash!
				-> Examples
				   
				   1. [[text size=2.5f color=blue link=http://google.com style=bold ]]Go to Google![[text]]
				      -> will sets the text into 2.5f size with blue color, linked to google.com and bolded-style text

   * > WARNING :
   	   if you sets the superscript and subscript int the same text(eg. %+%-2%-%+), your app will crash!
   */
package com.mylexz.utils.text;

import android.graphics.Typeface;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import java.util.ArrayList;
import java.util.List;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import java.util.HashMap;
import android.text.Spanned;
import android.content.Context;
import android.text.style.TypefaceSpan;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

public final class TextSpanFormat
{

    private static final int UNDERLINE_CODE 		= 1;
    private static final int BOLD_CODE 				= 2;
    private static final int STRIKEOUT_CODE 		= 3;
    private static final int URLSTR_CODE 			= 4;
    private static final int ITALIC_CODE 			= 5;
    private static final int BACKGROUND_CODE 		= 6;
    private static final int FOREGROUND_CODE		= 7;
    private static final int RESIZETXT_CODE 		= 8;
    private static final int SUPERSCRIPT_CODE 		= 9;
    private static final int SUBSCRIPT_CODE 		= 10;
	private static final int TYPEFACE_CODE 			= 11;
	private static final int STYLESPAN_CODE 		= 12;
	private static final int TXTALLSPAN_CODE 		= 13;
	/*
	 * convertStrToSpan(context, text, flags);
	 * uses to convert TextSpanQueries into SpannableString object
	 * 
	 * @param ctx the current context activity
	 * @param text The NonNull text string, An input TextQueries
	 * @param spanFlags The SpannableString flags, can be zero to default
	 * @return spanResult The SpannableString object
	 *
	 * Usage :
	 * TextSpanFormat.convertStrToSpan(context, text, flags);
	 */
    public static SpannableString convertStrToSpan(@NonNull Context ctx, @NonNull String text, int spanFlags)
	{
        // first initializes for parameter list
		List<SpanMethod> paramList = new ArrayList<SpanMethod>();
        // for the real string without queries
		String originalString = "";
        // for store current position
        int bold_pos = -1, italic_pos = -1, underline_pos = -1, strikeout_pos = -1, superscript_pos = -1, subscript_pos = -1, csize_pos = -1, urltext_pos = -1, bkgcolor_pos = -1, foreground_pos = -1, specspan_pos = -1;
        // for total count
        int count = 0, count_spec = 0;
        for (int x = 0; x < text.length(); x++)
		{
            // for bold symbol
            if (text.charAt(x) == '*' && text.charAt(x + 1) == '*')
			{
                x++;count++;
                if (bold_pos == -1)
				{
					// add new SpanMethod into parameter lists
                    paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, BOLD_CODE, 0, null));
                    // push current location parameter lists
					bold_pos = paramList.size() - 1;
                }
                else
				{
					// get and modify end field in parameterLists
                    paramList.get(bold_pos).end = (((x + 1) - (count * 2)) - count_spec);
					// passing -1 into bold_pos
					bold_pos = -1;
                }
            }

            // for italic symbol
            else if (text.charAt(x) == '_' && text.charAt(x + 1) == '_')
			{
                x++;count++;
                if (italic_pos == -1)
				{
                    paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, ITALIC_CODE, 0, null));
                    italic_pos = paramList.size() - 1;
                }
                else
				{
                    paramList.get(italic_pos).end = (((x + 1) - (count * 2)) - count_spec);
					italic_pos = -1;
                }
            }

            // for UNDERLINE SYMBOL
            else if (text.charAt(x) == '-' && text.charAt(x + 1) == '-')
			{
                x++;count++;
                if (underline_pos == -1)
				{
                    paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, UNDERLINE_CODE, 0, null));
                    underline_pos = paramList.size() - 1;
                }
                else
				{
                    paramList.get(underline_pos).end = (((x + 1) - (count * 2)) - count_spec);
					underline_pos = -1;
                }

                continue;
            }

            // for STRIKEOUT symbol
            else if (text.charAt(x) == '~' && text.charAt(x + 1) == '~')
			{
                x++;count++;
                if (strikeout_pos == -1)
				{
                    paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, STRIKEOUT_CODE, 0, null));
                    strikeout_pos = paramList.size() - 1;
                }
                else
				{
                    paramList.get(strikeout_pos).end = (((x + 1) - (count * 2)) - count_spec);
					strikeout_pos = -1;
                }
            }
            // for superscript and subscript symbols
            else if (text.charAt(x) == '%')
			{
                // for superscript
                if (text.charAt(x + 1) == '+')
				{
                    x++;count++;
                    if (superscript_pos == -1)
					{
                        paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, SUPERSCRIPT_CODE, 0, null));
                        superscript_pos = paramList.size() - 1;
                    }
                    else
					{
                        paramList.get(superscript_pos).end = (((x + 1) - (count * 2)) - count_spec);
						superscript_pos = -1;
                    }
                    continue;
                }
                // for subscript
                else if (text.charAt(x + 1) == '-')
				{
                    x++;count++;
                    if (subscript_pos == -1)
					{
                        paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, SUBSCRIPT_CODE, 0, null));
                        subscript_pos = paramList.size() - 1;
                    }
                    else
					{
                        paramList.get(subscript_pos).end = (((x + 1) - (count * 2)) - count_spec);
						subscript_pos = -1;
                    }
                }
                else
                    originalString += '%';
            }

            //for special symbols
            else if (text.charAt(x) == '[' && text.charAt(x + 1) == '[')
			{
                x++;
                String op = "";
                // get identifier
                while (text.charAt(++x) != '=' && text.charAt(x) != ']')
				{
					if (Character.isWhitespace(text.charAt(x)))
					{
						count_spec++;
						continue;
					}
					op += text.charAt(x);
				}
                if (text.charAt(x) == '=')
				{
                    // change TextSize (size)
                    if (op.equalsIgnoreCase("size") && csize_pos == -1)
					{
                        count_spec += op.length() + 5;
                        op = "";
                        while (text.charAt(++x) != ']')
						{
							if (Character.isWhitespace(text.charAt(x)))
							{
								count_spec++;
								continue;
							}
							op += text.charAt(x);
						}
                        count_spec += op.length();
                        x++;
                        float size = Float.parseFloat(op);
                        paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, RESIZETXT_CODE, size));
                        csize_pos = paramList.size() - 1;

                    }
                    // link url
                    else if (op.equalsIgnoreCase("link") && urltext_pos == -1)
					{
                        count_spec += op.length() + 5;
                        op = "";
                        while (text.charAt(++x) != ']')
						{
							if (Character.isWhitespace(text.charAt(x)))
							{
								count_spec++;
								continue;
							}
							op += text.charAt(x);
						}
                        count_spec += op.length();
                        x++;
                        paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, URLSTR_CODE, 0, op));
                        urltext_pos = paramList.size() - 1;
                    }

					// background color
					else if ((op.equalsIgnoreCase("background") || op.equalsIgnoreCase("bgcolor")) && bkgcolor_pos == -1)
					{
                        count_spec += op.length() + 5;
                        op = "";
                        while (text.charAt(++x) != ']')
						{
							if (Character.isWhitespace(text.charAt(x)))
							{
								count_spec++;
								continue;
							}
							op += text.charAt(x);
						}
                        count_spec += op.length();
                        x++;
                        paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, BACKGROUND_CODE, getColor(op), null));
                        bkgcolor_pos = paramList.size() - 1;
                    }

					// change foreground color
					else if ((op.equalsIgnoreCase("foreground") || op.equalsIgnoreCase("color")) && foreground_pos == -1)
					{
                        count_spec += op.length() + 5;
                        op = "";
                        while (text.charAt(++x) != ']')
						{
							if (Character.isWhitespace(text.charAt(x)))
							{
								count_spec++;
								continue;
							}
							op += text.charAt(x);
						}
                        count_spec += op.length();
                        x++;
                        paramList.add(new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, FOREGROUND_CODE, getColor(op), null));
                        foreground_pos = paramList.size() - 1;
                    }
					else if (op.compareToIgnoreCase("text") > 0 && specspan_pos == -1)
					{
						count_spec += op.length() + 4;

						HashMap<Integer, String> listRes = new HashMap<Integer, String>();
						// put the first argument
						String op1 = op.substring(4, op.length());
						op = "";
						// for indexing and locking
						int lock = 1;
						while (text.charAt(x) != ']')
						{
							// getting the key
							if (lock == 0)
							{
								while (!Character.isWhitespace(text.charAt(x)) && text.charAt(x) != '=')
								{
									count_spec++;
									op1 += text.charAt(x);
									x++;
								}
								lock = 1;
							}
							// getting the value
							if (lock == 1)
							{
								count_spec++;
								while (!Character.isWhitespace(text.charAt(++x)) && text.charAt(x) != ']')
								{
									op += text.charAt(x);
									count_spec++;
								}
								if (Character.isWhitespace(text.charAt(x)))
									while (Character.isWhitespace(text.charAt(x)))
									{count_spec++;x++;}
								int meth = getTMethod(op1);
								if(meth != -1)
									listRes.put(meth, op);
								lock = 0;
								op1 = op = "";
							}

						}
						/////////
						op = "";
						x++;
						SpanMethod res = new SpanMethod((((x + 1) - (count * 2)) - count_spec), 0, TXTALLSPAN_CODE, listRes);
						paramList.add(res);
						specspan_pos = paramList.size() - 1;
					}
                }
                else if (text.charAt(x) == ']')
				{
                    // change TextSize (size)
                    if (op.equalsIgnoreCase("size") && csize_pos != -1)
					{
                        count_spec += op.length() + 4;
                        x++;
                        paramList.get(csize_pos).end = (((x + 1) - (count * 2)) - count_spec);
                        csize_pos = -1;
                    }
                    // link url
                    else if (op.equalsIgnoreCase("link") && urltext_pos != -1)
					{
                        count_spec += op.length() + 4;
                        x++;
                        paramList.get(urltext_pos).end = (((x + 1) - (count * 2)) - count_spec);
                        urltext_pos = -1;
                    }

					// change background color
					else if ((op.equalsIgnoreCase("background") || op.equalsIgnoreCase("bgcolor")) && bkgcolor_pos != -1)
					{
                        count_spec += op.length() + 4;
                        x++;
                        paramList.get(bkgcolor_pos).end = (((x + 1) - (count * 2)) - count_spec);
                        bkgcolor_pos = -1;
                    }

					// change foreground color
					else if ((op.equalsIgnoreCase("foreground") || op.equalsIgnoreCase("color")) && foreground_pos != -1)
					{
                        count_spec += op.length() + 4;
                        x++;
                        paramList.get(foreground_pos).end = (((x + 1) - (count * 2)) - count_spec);
                        foreground_pos = -1;
                    }
					// for special span
					else if ((op.equalsIgnoreCase("text")) && specspan_pos != -1)
					{
                        count_spec += op.length() + 4;
                        x++;
                        paramList.get(specspan_pos).end = (((x + 1) - (count * 2)) - count_spec);
                        specspan_pos = -1;
                    }
                }
                else originalString += "[[" + op;
            }
            else
                originalString += text.charAt(x);
        }
        // check all paramlists
        if (csize_pos != -1) paramList.remove(csize_pos);
        else if (urltext_pos != -1)paramList.remove(urltext_pos);
		else if (bkgcolor_pos != -1) paramList.remove(bkgcolor_pos);
		else if (foreground_pos != -1)paramList.remove(foreground_pos);
		else if (bold_pos != -1)paramList.remove(bold_pos);
		else if (strikeout_pos != -1)paramList.remove(strikeout_pos);
		else if (underline_pos != -1) paramList.remove(underline_pos);
		else if (italic_pos != -1)paramList.remove(italic_pos);
		else if (superscript_pos != -1)paramList.remove(superscript_pos);
		else if (subscript_pos != -1)paramList.remove(subscript_pos);
		else if (specspan_pos != -1)paramList.remove(specspan_pos);
        //
        SpannableString spanResult = new SpannableString(originalString);
        for (int x = 0; x < paramList.size(); x++)
		{
            switch (paramList.get(x).method)
			{
                case BOLD_CODE:
                    spanResult.setSpan(new StyleSpan(Typeface.BOLD), paramList.get(x).start, paramList.get(x).end, spanFlags);
                    break;
                case ITALIC_CODE:
                    spanResult.setSpan(new StyleSpan(Typeface.ITALIC), paramList.get(x).start, paramList.get(x).end, spanFlags);
                    break;
                case UNDERLINE_CODE:
                    spanResult.setSpan(new UnderlineSpan(), paramList.get(x).start, paramList.get(x).end, spanFlags);
                    break;
                case STRIKEOUT_CODE:
                    spanResult.setSpan(new StrikethroughSpan(), paramList.get(x).start, paramList.get(x).end, spanFlags);
                    break;
                case SUPERSCRIPT_CODE:
                    spanResult.setSpan(new SuperscriptSpan(), paramList.get(x).start, paramList.get(x).end, spanFlags);
                    break;
                case SUBSCRIPT_CODE:
                    spanResult.setSpan(new SubscriptSpan(), paramList.get(x).start, paramList.get(x).end, spanFlags);
                    break;
                case RESIZETXT_CODE:
                    spanResult.setSpan(new RelativeSizeSpan(paramList.get(x).size), paramList.get(x).start, paramList.get(x).end, spanFlags);
                    break;
                case URLSTR_CODE:
                    spanResult.setSpan(new URLSpan(paramList.get(x).url), paramList.get(x).start, paramList.get(x).end, spanFlags);
                    break;
				case BACKGROUND_CODE:
					spanResult.setSpan(new BackgroundColorSpan(paramList.get(x).color), paramList.get(x).start, paramList.get(x).end, spanFlags);
					break;
				case FOREGROUND_CODE:
					spanResult.setSpan(new ForegroundColorSpan(paramList.get(x).color), paramList.get(x).start, paramList.get(x).end, spanFlags);
					break;
				case TXTALLSPAN_CODE:
					{
						HashMap<Integer, String> map = paramList.get(x).parameters;
						if (map.containsKey(BACKGROUND_CODE))spanResult.setSpan(new BackgroundColorSpan(getColor(map.get(BACKGROUND_CODE))), paramList.get(x).start, paramList.get(x).end, spanFlags);
						if (map.containsKey(FOREGROUND_CODE)) spanResult.setSpan(new ForegroundColorSpan(getColor(map.get(FOREGROUND_CODE))), paramList.get(x).start, paramList.get(x).end, spanFlags);
						if (map.containsKey(RESIZETXT_CODE))  spanResult.setSpan(new RelativeSizeSpan(Float.parseFloat(map.get(RESIZETXT_CODE))), paramList.get(x).start, paramList.get(x).end, spanFlags);
						if (map.containsKey(URLSTR_CODE)) 	 spanResult.setSpan(new URLSpan(map.get(URLSTR_CODE)), paramList.get(x).start, paramList.get(x).end, spanFlags);
						if (map.containsKey(TYPEFACE_CODE)){
							String nameTypeFace = map.get(TYPEFACE_CODE);
							// if from assets
							if(nameTypeFace.charAt(0) == '@'){
								String pathAssets = nameTypeFace.substring(1);
								Typeface font = Typeface.createFromAsset(ctx.getAssets(), pathAssets);
								CustomTypefaceSpan cusSpan = new CustomTypefaceSpan(font);
								spanResult.setSpan(cusSpan, paramList.get(x).start, paramList.get(x).end, spanFlags);
							}
							// if from resource id { Not Supported }
							//else if(nameTypeFace.charAt(0) == '$'){}
							// if monospace, serif or sans-serif
							else {
								String result = nameTypeFace.toLowerCase();
								spanResult.setSpan(new TypefaceSpan(result), paramList.get(x).start, paramList.get(x).end, spanFlags);
								
								
							}
							
						}
						if (map.containsKey(STYLESPAN_CODE)){
							String result = map.get(STYLESPAN_CODE);
							String[] arr = result.split(",");
							// for avoiding superscript and subscript in a same usage
							int lc = 0;
							bold_pos = italic_pos = underline_pos = strikeout_pos = 0;
							for(int z = 0; z < arr.length; z++){
								if((arr[z].equalsIgnoreCase("underline") || arr[z].equalsIgnoreCase("uln")) && underline_pos == 0){
									spanResult.setSpan(new UnderlineSpan(), paramList.get(x).start, paramList.get(x).end, spanFlags);
									underline_pos++;
								}
								else if(arr[z].equalsIgnoreCase("bold") && bold_pos == 0){
									spanResult.setSpan(new StyleSpan(Typeface.BOLD), paramList.get(x).start, paramList.get(x).end, spanFlags);
									bold_pos++;
								}
								else if(arr[z].equalsIgnoreCase("strikethrough") && strikeout_pos == 0){
									spanResult.setSpan(new StrikethroughSpan(), paramList.get(x).start, paramList.get(x).end, spanFlags);
									strikeout_pos++;
								}
								else if(arr[z].equalsIgnoreCase("italic") && italic_pos == 0){
									spanResult.setSpan(new StyleSpan(Typeface.ITALIC), paramList.get(x).start, paramList.get(x).end, spanFlags);
									italic_pos++;
								}
								else if(arr[z].equalsIgnoreCase("superscript") && lc == 0){
									spanResult.setSpan(new SuperscriptSpan(), paramList.get(x).start, paramList.get(x).end, spanFlags);
									lc++;
								}
								else if(arr[z].equalsIgnoreCase("subscript") && lc == 0){
									spanResult.setSpan(new SubscriptSpan(), paramList.get(x).start, paramList.get(x).end, spanFlags);
									lc++;
								}
							}
						}

					}
            }

        }
        return spanResult;
    }
	/*
	 * Get the IDENTIFIER Integer of an parameters defined as string
	 * @param text NonNull parameters text
	 * @return intCodes -1 if not specified
	 
	 */
	private static int getTMethod(@NonNull String text)
	{
		if (text.equalsIgnoreCase("bgcolor") || text.equalsIgnoreCase("background")) return BACKGROUND_CODE;
		else if (text.equalsIgnoreCase("foreground") || text.equalsIgnoreCase("color")) return FOREGROUND_CODE;
		else if (text.equalsIgnoreCase("link") || text.equalsIgnoreCase("url")) return URLSTR_CODE;
		else if (text.equalsIgnoreCase("size") || text.equalsIgnoreCase("font-size")) return RESIZETXT_CODE;
		else if (text.equalsIgnoreCase("typeface") || text.equalsIgnoreCase("font-family")) return TYPEFACE_CODE;
		else if (text.equalsIgnoreCase("style") || text.equalsIgnoreCase("font-style")) return STYLESPAN_CODE;
		return -1;
	}
	/*
	 * Get the color int numbers
	 * @param color NonNull String hex color or String-defined value
	 * @return clr The Color Integer
	 */
	private static int getColor(@NonNull String color)
	{
		int clr = 0 ;
		if (color.charAt(0) == '#')
		{
			clr = Color.parseColor(color);
		}
		else
		{
			if (color.equalsIgnoreCase("black")) clr = Color.BLACK;
			else if (color.equalsIgnoreCase("blue")) clr = Color.BLUE;
			else if (color.equalsIgnoreCase("cyan")) clr = Color.CYAN;
			else if (color.equalsIgnoreCase("dkgray")) clr = Color.DKGRAY;
			else if (color.equalsIgnoreCase("gray")) clr = Color.GRAY;
			else if (color.equalsIgnoreCase("green")) clr = Color.GREEN;
			else if (color.equalsIgnoreCase("ltgray")) clr = Color.LTGRAY;
			else if (color.equalsIgnoreCase("magenta")) clr = Color.MAGENTA;
			else if (color.equalsIgnoreCase("red")) clr = Color.RED;
			else if (color.equalsIgnoreCase("transparent")) clr = Color.TRANSPARENT;
			else if (color.equalsIgnoreCase("white")) clr = Color.WHITE;
			else if (color.equalsIgnoreCase("yellow")) clr = Color.YELLOW;
			else clr = Color.BLACK;

		}
		return clr;
	}
	/*
	 * class for collecting Queries
	 */
    private static class SpanMethod
	{
        int start;
        int end;
        int method;
        int color;
        String url;
        float size;

		// for TXTALLSPAN_CODE (special)
		HashMap<Integer, String> parameters;
		public SpanMethod(int start, int end, int method, @NonNull HashMap<Integer, String> parameters)
		{
			this.start = start;
			this.end = end;
			this.method = method;
			this.parameters = parameters;
		}
        public SpanMethod(int start, int end, int method, int color, @Nullable String url)
		{
            this.start = start;
            this.end = end;
            this.method = method;
            this.color = color;
            this.url = url;

        }
        public SpanMethod(int start, int end, int method, float size)
		{
            this.start = start;
            this.end = end;
            this.method = method;
            this.size = size;
        }
    }
}
