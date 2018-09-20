/*
 * Copyright (C) 2018 Alexzander Purwoko Widiantoro
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

import com.jakewharton.disklrucache.DiskLruCache;
import java.io.File;
import java.io.IOException;
import java.io.Closeable;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.ByteArrayOutputStream;
import org.apache.commons.codec.binary.Base64;
import java.io.ByteArrayInputStream;
public class DiskLruObjectCache implements Closeable
{
	private DiskLruCache mCacheDisk;
	private File source;
	public static final long MAX_BUFFER_SIZE = 512 * 1024;
	private DiskLruCache.Snapshot currentSnaps = null;
	public DiskLruObjectCache(File source, int version, int valueCount, long max_buffsize) throws IOException
	{
		mCacheDisk = DiskLruCache.open(source, version, valueCount, max_buffsize);
		this.source = source;
	}
	public DiskLruObjectCache(File source, int version, long max_buffer) throws IOException
	{
		this(source, version, 1, max_buffer);
	}
	public DiskLruObjectCache(File source) throws IOException
	{
		this(source, 1, MAX_BUFFER_SIZE);
	}
	public void put(String key, InputStream valueStream) throws IOException
	{
		String encoded_key = BaseEnDoc.encode(key);
		DiskLruCache.Editor editor = mCacheDisk.edit(encoded_key);
		OutputStream os = null;
		try
		{
			os = new BufferedOutputStream(editor.newOutputStream(0));
			byte[] buf = new byte[valueStream.available()];
			valueStream.read(buf);

			os.write(buf);
			os.flush();
		}
		catch (IOException e)
		{
			if (os != null)os.close();
			editor.abort();
			throw new IOException(e);
		}
		finally
		{
			mCacheDisk.flush();
			editor.commit();
			os.close();
		}
	}
	public void put(String key, byte[] value) throws IOException
	{
		String encoded_key = BaseEnDoc.encode(key);
		DiskLruCache.Editor editor = mCacheDisk.edit(encoded_key);
		OutputStream os = null;
		try
		{
			os = new BufferedOutputStream(editor.newOutputStream(0));
			os.write(value);
			os.flush();
		}
		catch (IOException e)
		{
			if (os != null)os.close();
			editor.abort();
			throw new IOException(e);
		}
		finally
		{
			mCacheDisk.flush();
			editor.commit();
			os.close();
		}
	}
	public void putObject(String key, Object value) throws IOException
	{
		String encoded_key = BaseEnDoc.encode(key);
		DiskLruCache.Editor editor = mCacheDisk.edit(encoded_key);
		OutputStream os = null;
		ObjectOutputStream oos = null;
		ByteArrayOutputStream bos = null;
		boolean reachedEx = false;
		try
		{
			os = new BufferedOutputStream(editor.newOutputStream(0));
			oos = new ObjectOutputStream(os);
			oos.writeObject(value);
			oos.flush();
			os.flush();
		}
		catch (IOException e)
		{
			reachedEx = true;
			if (os != null)os.close();
			if (oos != null) oos.close();
			editor.abort();
			throw new IOException(e);
		}
		finally
		{
			if (bos != null)bos.close();
			if (reachedEx)
				editor.abort();
			else
			{
				mCacheDisk.flush();
				editor.commit();
				oos.close();
				os.close();
			}
		}
	}
	public void putObjectWithEncode(String key, Object value) throws IOException
	{
		String encoded_key = BaseEnDoc.encode(key);
		DiskLruCache.Editor editor = mCacheDisk.edit(encoded_key);
		OutputStream os = null;
		ObjectOutputStream oos = null;
		ByteArrayOutputStream bos = null;
		boolean reachedEx = false;
		try
		{

			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(value);
			oos.flush();
			bos.flush();
			os = new BufferedOutputStream(editor.newOutputStream(0));
			os.write(Base64.encodeBase64(bos.toByteArray()));
			os.flush();
		}
		catch (IOException e)
		{
			reachedEx = true;
			if (os != null)os.close();
			if (oos != null) oos.close();
			editor.abort();
			throw new IOException(e);
		}
		finally
		{
			if (bos != null)bos.close();
			if (reachedEx)
				editor.abort();
			else
			{
				mCacheDisk.flush();
				editor.commit();
				oos.close();
				os.close();
			}
		}
	}
	public Object getObjectWithDecode(String key) throws IOException, ClassNotFoundException
	{
		String encoded_key = BaseEnDoc.encode(key);
		Object result = null;
		if (currentSnaps != null)
			throw new IOException("The current buffer for reading is not closed!");
		currentSnaps = mCacheDisk.get(encoded_key);
		InputStream stream = currentSnaps.getInputStream(0);
		if (stream == null)
		{
			throw new IOException("Couldn't open the selected key: " + key);
		}
		//first read the files
		byte[] avail64 = new byte[stream.available()];
		stream.read(avail64);
		closeReading();
		// pass into ByteArrayInputStream
		ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(avail64));
		// read it!
		ObjectInputStream ois = new ObjectInputStream(bis);
		result = ois.readObject();
		ois.close();
		bis.close();
		return result;
	}
	public Object getObject(String key) throws IOException, ClassNotFoundException
	{
		String encoded_key = BaseEnDoc.encode(key);
		Object result = null;
		if (currentSnaps != null)
			throw new IOException("The current buffer for reading is not closed!");
		currentSnaps = mCacheDisk.get(encoded_key);
		InputStream stream = currentSnaps.getInputStream(0);
		if (stream == null)
		{
			throw new IOException("Couldn't open the selected key: " + key);
		}
		ObjectInputStream ois = new ObjectInputStream(stream);
		result = ois.readObject();
		ois.close();
		return result;
	}
	public InputStream get(String key) throws IOException
	{
		String encoded_key = BaseEnDoc.encode(key);
		InputStream result = null;
		if (currentSnaps != null)
			throw new IOException("The current buffer for reading is not closed!");
		currentSnaps = mCacheDisk.get(encoded_key);
		result = currentSnaps.getInputStream(0);
		if (result == null)
		{
			throw new IOException("Couldn't open the selected key: " + key);
		}
		return result;
	}
	public void closeReading()
	{
		if (currentSnaps != null)
		{
			currentSnaps.close();
			currentSnaps = null;
			System.gc();
		}
	}
	public byte[] readBytesFromKey(String key) throws IOException
	{
		InputStream is = get(key);
		byte[] res = new byte[is.available()];
		is.read(res);
		is.close();
		return res;
	}
	public boolean removeKey(String key) throws IOException
	{
		return mCacheDisk.remove(BaseEnDoc.encode(key));
	}
	public void clean() throws IOException
	{
		mCacheDisk.delete();
	}
	public File getDirectory()
	{
		return mCacheDisk.getDirectory();
	}
	public long getMaxCacheFileSize()
	{
		return mCacheDisk.getMaxSize();
	}
	public void setMaxCacheFileSize(long maxSize)
	{
		mCacheDisk.setMaxSize(maxSize);
	}
	public DiskLruCache.Editor getEditorFromKey(String key) throws IOException
	{
		return mCacheDisk.get(BaseEnDoc.encode(key)).edit();
	}
	public boolean isKeyExists(String key)
	{
		DiskLruCache.Snapshot resl = null;
		try
		{
			resl = mCacheDisk.get(BaseEnDoc.encode(key));
			return (resl != null) ? true : false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (resl != null) resl.close();
		}
	}
	@Override
	public void close() throws IOException
	{
		mCacheDisk.close();
		mCacheDisk = null;
		System.gc();
	}
	private final static class BaseEnDoc
	{
		// {0, 1} -> {encoder, decoder}
		private static final int HEADER = '0';
		private static final int FOOTER = HEADER;
		private static final String STRING_KEY_PATTERN = "[a-z0-9_-]{1,120}";
		private static final Pattern LEGAL_KEY_PATTERN = Pattern.compile(STRING_KEY_PATTERN);
		private static final int[][] matcher = {
			// ABCDEF
			{'a', '0'},
			{'b', 'y'},
			{'c', 'u'},
			{'d', 'g'},
			{'e', '9'},
			{'f', 'q'},
			// GHIJKL
			{'g', 'v'},
			{'h', 'o'},
			{'i', '1'},
			{'j', 'm'},
			{'k', 'e'},
			{'l', '8'},
			// MNOPQR
			{'m', '2'},
			{'n', 'h'},
			{'o', '7'},
			{'p', 'c'},
			{'q', 'r'},
			{'r', 'w'},
			// STUVWX
			{'s', 'k'},
			{'t', 'b'},
			{'u', '6'}, 
			{'v', 'd'},
			{'w', 'j'},
			{'x', '3'},

			{'y', '4'},
			{'z', '_'}, //**

			// - _
			{'-', '5'},
			{'_', 'z'}, //**
			// 0 - 9
			{'0', 'a'}, //
			{'1', 'p'}, //
			{'2', 's'}, //**
			{'3', '-'}, //
			{'4', 'i'}, //
			{'5', 'x'}, //
			{'6', 'l'}, //
			{'7', 't'}, //**
			{'8', 'n'}, //
			{'9', 'f'}, //


		};

		private static final int ENCODE = 0;
		private static final int DECODE = 1;
		public static String encode(String src)
		{
			if (src == null)return null;
			if (src.length() < 1) return null;
			validateKey(src);
			StringBuffer strBuf = new StringBuffer();
			strBuf.append((char)HEADER);
			for (int x = 0; x < src.length(); x++)
			{
				// search the replacement
				int replacement = getReplacementChars(src.charAt(x), ENCODE);
				// write it
				strBuf.append((char)replacement);
			}
			strBuf.append((char)FOOTER);
			return strBuf.toString();
		}

		public static String decode(String src)
		{
			if (src == null)return null;
			if (src.length() < 1) return null;
			if (!(src.charAt(0) == HEADER && src.charAt(src.length() - 1) == FOOTER))return null;
			StringBuffer strBuf = new StringBuffer();
			for (int x = 1; x < src.length() - 1; x++)
			{
				// search the replacement
				int replacement = getReplacementChars(src.charAt(x), DECODE);
				// write it
				strBuf.append((char)replacement);
			}
			return strBuf.toString();
		}
		private static final int getReplacementChars(int charInput, int mode)
		{
			for (int x = 0; x < matcher.length; x++)
			{
				if (mode == ENCODE)
				{
					if (charInput == matcher[x][0])return matcher[x][1];
				}
				else if (mode == DECODE)
				{
					if (charInput == matcher[x][1])return matcher[x][0];
				}
			}
			return charInput;
		}
		private static void validateKey(String key)
		{
			Matcher match = LEGAL_KEY_PATTERN.matcher(key);
			if (!match.matches())
			{
				throw new IllegalArgumentException("keys must match regex "
												   + STRING_KEY_PATTERN + ": \"" + key + "\"");
			}
		}

	}
}
