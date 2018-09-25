package com.mylexz.utils;


import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleDiskLruCache implements Closeable{
    private File source;
    public static final long MAX_BUFFER_SIZE = 512 * 1024;
    private final File fileSource;
    InputStream currentStream = null;
    private static volatile SimpleDiskLruCache sInstance;

    public static SimpleDiskLruCache getsInstance(File fileSource) throws IOException {
        sInstance = new SimpleDiskLruCache(fileSource);
        return sInstance;
    }
    private SimpleDiskLruCache(File fileSource) throws IOException
    {
        this.fileSource = fileSource;
    }
    public void put(String key, InputStream valueStream) throws IOException
    {
        if(isKeyExists(key))throw new IOException(String.format("The key %s is exists", key));
        if(valueStream == null) throw new NullPointerException("valueStream cannot be null (InputStream)");
        String encoded_key = BaseEnDoc.encode(key);
        synchronized (this) {
            FileOutputStream fis = new FileOutputStream(new File(fileSource, encoded_key));
            int read;
            while((read = valueStream.read()) != -1) {
                fis.write(read);
            }
            fis.flush();
            fis.close();
        }
    }
    public void put(String key, byte[] value) throws IOException
    {
        if(isKeyExists(key))throw new IOException(String.format("The key %s is exists", key));
        if(value == null) throw new NullPointerException("values byte[] array cannot be null (InputStream)");
        String encoded_key = BaseEnDoc.encode(key);
        synchronized (this) {
            FileOutputStream fis = new FileOutputStream(new File(fileSource, encoded_key));
            fis.write(value);
            fis.flush();
            fis.close();
        }
    }
    public void putObject(String key, Object value) throws IOException
    {
        if(isKeyExists(key))throw new IOException(String.format("The key %s is exists", key));
        if(value == null) throw new NullPointerException("Object value cannot be null (Object)");
        if(!(value instanceof Serializable)) throw new IllegalArgumentException("Object value is not implement interface Seralizable!");
        String encoded_key = BaseEnDoc.encode(key);
        synchronized (this) {
            FileOutputStream fis = new FileOutputStream(new File(fileSource, encoded_key));
            ObjectOutputStream oos = new ObjectOutputStream(fis);
            try {
                oos.writeObject(value);
                oos.flush();
                fis.flush();
            }finally {
                oos.close();
                fis.close();
            }
        }
    }
    public void putObjectWithEncode(String key, Object value) throws IOException
    {

        if(isKeyExists(key))throw new IOException(String.format("The key %s is exists", key));
        if(value == null) throw new NullPointerException("Object value cannot be null (Object)");
        if(!(value instanceof Serializable)) throw new IllegalArgumentException("Object value is not implement interface Seralizable!");
        String encoded_key = BaseEnDoc.encode(key);
        synchronized (this) {
            FileOutputStream fis = new FileOutputStream(new File(fileSource, encoded_key));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            try {
                oos.writeObject(value);
                oos.flush();
                bos.flush();
                fis.write(Base64.encodeBase64(bos.toByteArray()));
                fis.flush();
            }finally {
                bos.close();
                oos.close();
                fis.close();
            }
        }
    }
    public Object getObjectWithDecode(String key) throws IOException, ClassNotFoundException
    {
        if(!isKeyExists(key))throw new IOException(String.format("The key %s is not found", key));
        String encoded_key = BaseEnDoc.encode(key);
        Object result;
        synchronized (this) {
            InputStream stream = new FileInputStream(new File(fileSource, encoded_key));
            if (stream == null) {
                throw new IOException("Couldn't open the selected key: " + key);
            }
            //first read the files
            byte[] avail64 = new byte[stream.available()];
            stream.read(avail64);
            stream.close();
            // pass into ByteArrayInputStream
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(avail64));
            // read it!
            ObjectInputStream ois = new ObjectInputStream(bis);
            result = ois.readObject();
            ois.close();
            bis.close();
        }
        return result;
    }
    public Object getObject(String key) throws IOException, ClassNotFoundException
    {
        if(!isKeyExists(key))throw new IOException(String.format("The key %s is not found", key));
        String encoded_key = BaseEnDoc.encode(key);
        Object result;
        synchronized (this) {
            InputStream stream = new FileInputStream(new File(fileSource, encoded_key));
            if (stream == null) {
                throw new IOException("Couldn't open the selected key: " + key);
            }
            //first read the files
            byte[] avail64 = new byte[stream.available()];
            stream.read(avail64);
            stream.close();
            // pass into ByteArrayInputStream
            ByteArrayInputStream bis = new ByteArrayInputStream(avail64);
            // read it!
            ObjectInputStream ois = new ObjectInputStream(bis);
            result = ois.readObject();
            ois.close();
            bis.close();
        }
        return result;
    }
    public synchronized InputStream get(String key) throws IOException
    {
        if(!isKeyExists(key))throw new IOException(String.format("The key %s is not found", key));
        if(currentStream != null)throw new IOException("currentStream is opened, must close it");
        String encoded_key = BaseEnDoc.encode(key);
        return currentStream = new FileInputStream(new File(fileSource, encoded_key));
    }
    public void closeReading() throws IOException {
        if (currentStream != null)
        {
            currentStream.close();
            currentStream = null;
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
        return isKeyExists(key) ? (new File(fileSource, BaseEnDoc.encode(key))).delete():false;
    }
    public void clean() throws IOException
    {
        File[] f = fileSource.listFiles();
        for(File curr: f){
            curr.delete();
        }
        System.gc();
    }
    public File getDirectory()
    {
        return fileSource;
    }
    public synchronized boolean isKeyExists(String key)
    {

        String encode_key = BaseEnDoc.encode(key);
        Log.i(this.getClass().getSimpleName(), String.format("The Absolute path is %s", fileSource.getAbsolutePath()));
        File f = new File(fileSource, encode_key);
        Log.i(this.getClass().getSimpleName(), String.format("The key is %s and encoded key is %s and is exists? : %s", f.getAbsolutePath(), key, String.valueOf(f.exists())));

        boolean results = f.exists();
        return results;
    }
    @Override
    public void close() throws IOException
    {
        closeReading();
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
