package com.mylexz.utils;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;
import android.support.annotation.ColorRes;
import java.util.ArrayList;
import com.mylexz.utils.NodeData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;
import com.mylexz.utils.Logger;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.mylexz.utils.LogPriority;

public class MylexzActivity extends AppCompatActivity
{
	protected static final int LOGMODE_FILE = 1;
	protected static final int LOGMODE_TRACE = 0;
	private boolean translucent_status = false;
	private ArrayList<NodeData> listNDATA = null;
	private Logger mAppLogger = null;
	private int mLogMode = 0;
	SystemBarTintManager systemTint = null;
	private int mCurrentPosNDATA = 0;
	// for app logging
	protected void setLoggingMode(int log_modes){
		if(log_modes == LOGMODE_FILE){
			mLogMode = 1;
			ApplicationInfo appInfo = getApplicationInfo();
			mAppLogger = new Logger(this, getPackageManager().getApplicationLabel(appInfo).toString()+".log", Logger.MODE_PRIVATE);
		}
	}
	protected void LOGI(@NonNull String tag, @NonNull String format, Object... p){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.I(tag, format, p);
		else
			Log.i(tag, String.format(format, p));
	}
	protected void LOGI(@NonNull String tag, @NonNull String message, Throwable exception){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.I(tag, message+" : %s", exception.toString());
		else
			Log.i(tag, message, exception);
	}
	
	protected void LOGE(@NonNull String tag, @NonNull String format, Object... p){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.E(tag, format, p);
		else
			Log.e(tag, String.format(format, p));
	}
	protected void LOGE(@NonNull String tag, @NonNull String message, Throwable exception){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.E(tag, message+" : %s", exception.toString());
		else
			Log.e(tag, message, exception);
	}
	
	protected void LOGD(@NonNull String tag, @NonNull String format, Object... p){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.D(tag, format, p);
		else
			Log.d(tag, String.format(format, p));
	}
	protected void LOGD(@NonNull String tag, @NonNull String message, Throwable exception){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.D(tag, message+" : %s", exception.toString());
		else
			Log.d(tag, message, exception);
	}
	
	protected void LOGV(@NonNull String tag, @NonNull String format, Object... p){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.V(tag, format, p);
		else
			Log.v(tag, String.format(format, p));
	}
	protected void LOGV(@NonNull String tag, @NonNull String message, Throwable exception){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.V(tag, message+" : %s", exception.toString());
		else
			Log.v(tag, message, exception);
	}
	
	protected void LOGW(@NonNull String tag, @NonNull String format, Object... p){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.W(tag, format, p);
		else
			Log.w(tag, String.format(format, p));
	}
	protected void LOGW(@NonNull String tag, @NonNull String message, Throwable exception){
		if(mLogMode == LOGMODE_FILE)
			mAppLogger.W(tag, message+" : %s", exception.toString());
		else
			Log.w(tag, message, exception);
	}
	
	protected Logger getLoggerInstance(){
		return mAppLogger;
	}
	/*
	 * 0 -> LogPriority
	 * 1 -> TAG and packageName
	 * 2 -> date
	 * 3 -> time
	 * 4 -> content
	 *
	 */
	protected String readLogs(){
		if(mLogMode != LOGMODE_FILE)return null;
		Logger curr = getLoggerInstance();
		Object[] log = curr.read(false);
		if(log == null)return null;
		return String.format("%s\t%s\t%c\t%s\t%s", log[2], log[3], getLogPriority(((Integer)log[0]).intValue()), log[1], log[4]);
	}
	protected String readAllLogs(){
		String log;
		String res ="";
		while((log = readLogs()) != null){
			res += readLogs();
			res += '\n';
		}
		resetReadLogRef();
		return res;
	}
	private char getLogPriority(int priority){
		switch(priority){
			case 0x0000a:
				return getLoggerInstance().getSymbol(LogPriority.ERRORS);
			case 0x0000b:
				return getLoggerInstance().getSymbol(LogPriority.INFO);
			case 0x0000c:
				return getLoggerInstance().getSymbol(LogPriority.DEBUG);
			case 0x0000d:
				return getLoggerInstance().getSymbol(LogPriority.VERBOSE);
			case 0x0000e:
				return getLoggerInstance().getSymbol(LogPriority.WARNING);
			case 0x0000f:
				return getLoggerInstance().getSymbol(LogPriority.FAILURE);
			default:
				return 0;
		}
	}
	protected void resetReadLogRef(){
		getLoggerInstance().reset();
	}
	//////////
	protected NodeData addNewNDATA(@NonNull String filepath, @NonNull String signature)
	{
		NodeData current = new NodeData(filepath, signature);
		current.open();
		if (listNDATA == null)
		{
			listNDATA = new ArrayList<NodeData>();
			listNDATA.add(current);
		}
		else listNDATA.add(current);
		return current;
	}
	protected NodeData addNewNDATA(@NonNull String file_name, @NonNull String signature, int mode)
	{
		NodeData current = new NodeData(this, file_name, signature, mode);
		current.open();
		if (listNDATA == null)
		{
			listNDATA = new ArrayList<NodeData>();
			listNDATA.add(current);
		}
		else listNDATA.add(current);
		return current;
	}
	// add the array types
	protected NodeData addNewNDATAIntArray(@Nullable String path, @NonNull String data_names, @Nullable int[] data, boolean encrypt)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		getCurrentNDATAInstance().addIntArray(path, data_names, data, encrypt);
		return getCurrentNDATAInstance();

	}
	protected NodeData addNewNDATAStringArray(@Nullable String path, @NonNull String data_names, @Nullable String[] data, boolean encrypt)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		getCurrentNDATAInstance().addStringArray(path, data_names, data, encrypt);
		return getCurrentNDATAInstance();

	}
	protected NodeData addNewNDATADoubleArray(@Nullable String path, @NonNull String data_names, @Nullable double[] data, boolean encrypt)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		getCurrentNDATAInstance().addDoubleArray(path, data_names, data, encrypt);
		return getCurrentNDATAInstance();

	}
	protected NodeData addNewNDATALongArray(@Nullable String path, @NonNull String data_names, @Nullable long[] data, boolean encrypt)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		getCurrentNDATAInstance().addLongArray(path, data_names, data, encrypt);
		return getCurrentNDATAInstance();

	}
	protected NodeData addNewNDATACharArray(@Nullable String path, @NonNull String data_names, @Nullable char[] data, boolean encrypt)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		getCurrentNDATAInstance().addCharArray(path, data_names, data, encrypt);
		return getCurrentNDATAInstance();

	}
	/////////////$//////
	// get the array data types
	protected Object[] getNDATAArray(@NonNull String fullpath) throws Exception
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		NodeData currentInstance = getCurrentNDATAInstance();
		int mDataTypes = currentInstance.getDataType(fullpath);
		int mDataLength = currentInstance.getArrayLength(fullpath);
		if(mDataLength == 0)return null;
		Object[] result = new Object[mDataLength];
		currentInstance.setReadArrayIteration(fullpath);
		for (int x = 0; x < mDataLength; x++)
		{
			Object curr = currentInstance.readNext();
			switch (mDataTypes)
			{
				case NodeData.TYPE_INT:
					result[x] = (Integer)curr;
					break;
				case NodeData.TYPE_STRING:
					result[x] = (String)curr;
					break;
				case NodeData.TYPE_DOUBLE:
					result[x] = (Double)curr;
					break;
				case NodeData.TYPE_BOOLEAN:
					result[x] = (Boolean)curr;
					break;
				case NodeData.TYPE_LONG:
					result[x] = (Long)curr;
					break;
				case NodeData.TYPE_CHAR:
					result[x] = (Character)curr;
					break;
				default:
					result = null;
			}
		}
		return result;

	}
	// add the primitive data types
	protected<T> NodeData addNewNDATAPrimitiveTypes(@Nullable String path, @NonNull String data_names, @NonNull T data, boolean encrypt)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		if (data instanceof Integer)getCurrentNDATAInstance().addIntData(path, data_names, ((Integer)data).intValue(), encrypt);
		else if (data instanceof Character)getCurrentNDATAInstance().addCharData(path, data_names, ((Character)data).charValue(), encrypt);
		else if (data instanceof Boolean)getCurrentNDATAInstance().addBoolData(path, data_names, ((Boolean)data).booleanValue(), encrypt);
		else if (data instanceof String)getCurrentNDATAInstance().addStrData(path, data_names, ((String)data).toString(), encrypt);
		else if (data instanceof Double)getCurrentNDATAInstance().addDoubleData(path, data_names, ((Double)data).doubleValue(), encrypt);
		else if (data instanceof Long)getCurrentNDATAInstance().addLongData(path, data_names, ((Long)data).longValue(), encrypt);
		return getCurrentNDATAInstance();
	}

	////////
	// Get the data
	protected Object getNDATAPrimitiveTypes(@NonNull String fullpath, Object defaultValue)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		Object result = null;
		NodeData currentInstance = getCurrentNDATAInstance();
		switch (currentInstance.getDataType(fullpath))
		{
			case NodeData.TYPE_INT:
				result =  currentInstance.getIntData(fullpath, (int)defaultValue);
				break;
			case NodeData.TYPE_STRING:
				result =  currentInstance.getStringData(fullpath, (String)defaultValue);
				break;
			case NodeData.TYPE_DOUBLE:
				result =  currentInstance.getDoubleData(fullpath, (double)defaultValue);
				break;
			case NodeData.TYPE_BOOLEAN:
				result =  currentInstance.getBooleanData(fullpath, (boolean)defaultValue);
				break;
			case NodeData.TYPE_LONG:
				result =  currentInstance.getLongData(fullpath, (long)defaultValue);
				break;
			case NodeData.TYPE_CHAR:
				result =  currentInstance.getCharData(fullpath, (char)defaultValue);
				break;
			default:
				result = null;
		}
		return result;
	}
	//////////////////////
	protected List<String> listNDATAContents(@Nullable String fullpath, @NonNull NodeData.Filter filter)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		return getCurrentNDATAInstance().listContents(fullpath, filter);
	}
	protected String[] readNDATAStringArray(@NonNull String fullpath)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		return getCurrentNDATAInstance().getStringArray(fullpath);
	}
	protected NodeData addNewNDATANodes(@Nullable String path, @NonNull String node_names)
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		getCurrentNDATAInstance().addNode(path, node_names);
		return getCurrentNDATAInstance();
	}

	protected NodeData getCurrentNDATAInstance()
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		return listNDATA.get(mCurrentPosNDATA);
	}
	protected NodeData moveNDATAToNext()
	{
		if (listNDATA == null || listNDATA.size() <= mCurrentPosNDATA)return null;
		return listNDATA.get(++mCurrentPosNDATA);

	}
	protected NodeData moveNDATAToFirst()
	{
		if (listNDATA == null || listNDATA.size() == 0)return null;
		return listNDATA.get(mCurrentPosNDATA = 0);

	}
	protected NodeData moveNDATAToLast()
	{
		if (listNDATA == null || listNDATA.size() == 0)return null;
		return listNDATA.get(mCurrentPosNDATA = (listNDATA.size() - 1));

	}
	protected NodeData moveNDATAToBack()
	{
		if (listNDATA == null || listNDATA.size() == 0)return null;
		return listNDATA.get(--mCurrentPosNDATA);

	}
	protected NodeData releaseNDATAAt(int position)
	{
		if (listNDATA == null || listNDATA.size() <= position)return null;
		return listNDATA.remove(position);
	}
	protected void releaseAllNDATA()
	{
		if (listNDATA == null)return;
		for (int x = 0; x < listNDATA.size(); x++)listNDATA.get(x).close();
		listNDATA.clear();
		listNDATA = null;
	}
	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		releaseAllNDATA();
		super.onDestroy();
	}

	protected void setStatusBarColor(int color)
	{
		if (!translucent_status)return;
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
		{
			systemTint.setStatusBarTintColor(color);
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			getWindow().setStatusBarColor(color);
		}
	}
	protected void setStatusBarColorResource(@ColorRes int resColor)
	{
		setStatusBarColor(getResources().getColor(resColor));
	}
	protected void setTranslucentStatus(boolean on)
	{
        // TODO Auto-generated method stub

        Window win = getWindow();
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
		{
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			if (on)
			{
				winParams.flags |= bits;
			}
			else
			{
				winParams.flags &= ~bits;
			}
			translucent_status = on;
			win.setAttributes(winParams);
			systemTint = new SystemBarTintManager(this);
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && on)
		{
			translucent_status = on;
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}
		else
		{
			return;
		}

    }
}
