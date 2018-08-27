package com.mylexz.utils;

import android.*;
import android.content.*;
import java.io.*;
import java.util.*;
import android.util.*;
public class Logger
{
	/*** Constants */
	public static final int MODE_PRIVATE 	= 0x000001;
	public static final int MODE_PUBLIC 	= 0x000010;
	private static final int ERRORS 	= 0x0000a;
	private static final int INFO	= 0x0000b;
	private static final int DEBUG 	= 0x0000c;
	private static final int VERBOSE = 0x0000d;
	private static final int WARNING = 0x0000e;
	private static final int FAILURE = 0x0000f;
	public static final int AUTO_CLEAR 		= 0x000ab;
	public static final int CLEAR_AT 		= 0x000ac;
	public static final int DIRECT_CLEAR 	= 0x000ad;
	private static final char[] LogSymbol = {
		'E', 'I', 'D', 'V', 'W', 'F'
	};
	/**private identifier**/
	private String packageName;
	private String file;
	/**Constructor**/
	public Logger(Context c, String nameFile, int mode){
		packageName = c.getPackageName();
		File path;
		switch(mode){
			case MODE_PUBLIC :
				path = new File(c.getExternalFilesDir(null).getPath()+"/logs");
				path.mkdirs();
				file = (new File(path, nameFile)).getAbsolutePath();

				break;
			case MODE_PRIVATE:
				path = new File("/data/data/"+packageName+"/files/logs");
				path.mkdirs();
				file = (new File(path, nameFile)).getAbsolutePath();

		}
	}
	public Logger(Context c, String path, String nameFile){
		packageName = c.getPackageName();
		File pth = new File(path);
		pth.mkdirs();
		file = (new File(pth.getPath(), nameFile)).getAbsolutePath();
	}
	/**all methods**/
	public void write(LogPriority logPrio, String nameId, String format, Object... s){
		String res = String.format(format, s);
		int prio = getLogPriority(logPrio);
		if(prio == -1)
			return;
		Calendar cal = Calendar.getInstance();
		int hours = ((cal.get(Calendar.AM_PM) == Calendar.PM)?cal.get(Calendar.HOUR)+12:cal.get(Calendar.HOUR));
		long date = (cal.get(Calendar.DATE) * 1000000) + ((cal.get(Calendar.MONTH)+1) * 10000) + cal.get(Calendar.YEAR);
		long time = (hours*3600000)+(cal.get(Calendar.MINUTE)*60000) +(cal.get(Calendar.SECOND)*1000)+ cal.get(Calendar.MILLISECOND);
		writeLog(file, prio, time, date, packageName, nameId, res);
	}
	public void E(String nameId, String format, Object... s){
		write(LogPriority.ERRORS, nameId, format, s);
	}
	public void I(String nameId, String format, Object... s){
		write(LogPriority.INFO, nameId, format, s);
	}
	public void D(String nameId, String format, Object... s){
		write(LogPriority.DEBUG, nameId, format, s);
	}
	public void V(String nameId, String format, Object... s){
		write(LogPriority.VERBOSE, nameId, format, s);
	}
	public void W(String nameId, String format, Object... s){
		write(LogPriority.WARNING, nameId, format, s);
	}
	public void F(String nameId, String format, Object... s){
		write(LogPriority.FAILURE, nameId, format, s);
	}
	public Object[] readWithPriority(LogPriority logPrio, boolean deleteAfterCall)throws NullPointerException{
		int prio = getLogPriority(logPrio);
		Log.i("LOGGER", "value of logPrio is "+prio);
		return readLogAtPrio(file, prio, (deleteAfterCall ? 1 : 0));
		
	}
	public Object[] read(boolean deleteAfterCall)throws NullPointerException{
		return readL(file, (deleteAfterCall ? 1 : 0 ));
	}
	public boolean isNull(){
		return hasNull() == 1 ? true : false;
	}
	public int getLogPriority(LogPriority log){
		if(log == LogPriority.ERRORS)
			return ERRORS;
		else if(log == LogPriority.INFO)
			return INFO;
		else if(log == LogPriority.DEBUG)
			return DEBUG;
		else if(log == LogPriority.VERBOSE)
			return VERBOSE;
		else if(log == LogPriority.WARNING)
			return WARNING;
		else if(log == LogPriority.FAILURE)
			return FAILURE;
		else 
			return -1;
		
	}
	public static char getSymbol(LogPriority logPrio){
		int line = 0;
		LogPriority[] vals = LogPriority.values();
		for(; line < vals.length && vals[line] != logPrio; line++);
			//if(vals[line] == logPrio)
				;//break;
		return LogSymbol[line];
	}
	/** Native Methods **/
	private native void writeLog(String file, int prio, long millis, long date, String packageName, String nameId, String content);
	//private native String readLog(String file, int filterWhat, Object toFilter)
	private native Object[] readLogAtPrio(String file, int prio, int deleteAfterCall);//clear_at can't be 0 if chooseOpt is AUTO_CLEAR
	private native Object[] readL(String file, int deleteAfterCall);
	private native int hasNull();
	public native void reset();
	/** Loading Native Libraries **/
	static {
		System.loadLibrary("mlogger");
	}
}	
