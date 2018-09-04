package com.mylexz.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtility {
    private static final int BUFFER_SIZE = 8192; //8kb buffer
    public ZipUtility(){

    }
    private void unzipFile(String filepath, String dest)throws IOException {
        // check and make the destination dirrectory
        File destDir = new File(dest);
        if(!destDir.exists())
            destDir.mkdirs();
        // create a stream for zip files
        ZipInputStream zipStream = new ZipInputStream(new FileInputStream(filepath));
        // entry for zip files for extracting data
        ZipEntry z = zipStream.getNextEntry();
        while (z != null){
            // if the entry is a file extract
            String fp = dest + File.separator + z.getName();
            if(!z.isDirectory()){
                // extract file
                // creating an outputstream
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fp));
                byte[] bytesOut = new byte[BUFFER_SIZE];
                int read;
                // read into byte buffer and write to outputstream
                while((read = zipStream.read(bytesOut)) != -1){
                    bos.write(bytesOut, 0, read);

                }
                // close the outputStream
                bos.close();
            }
            else {
                // if is folder, create the directory
                new File(fp).mkdirs();
            }
            // jump to the next entry
            z = zipStream.getNextEntry();
        }
        // close the zip entry
        zipStream.closeEntry();
        // close zipStream
        zipStream.close();
    }
    ZipOutputStream zipOut;
    private int lock = 0;
    public void startZipFile(String fullNameZipFile) throws IOException{
        if(lock == 1)return;
        zipOut = new ZipOutputStream(new FileOutputStream(fullNameZipFile));
        lock = 1;
    }
    public void addEntryFile(String fullPathToFile, String fileEntry) throws FileNotFoundException, IOException{
        if (lock == 0)return;
        FileInputStream fis = new FileInputStream(fullPathToFile);
        ZipEntry zipEn = new ZipEntry(fileEntry);
        zipOut.putNextEntry(zipEn);
        byte[] bytesRead = new byte[BUFFER_SIZE];
        int read;
        while((read = fis.read(bytesRead)) != -1){
            zipOut.write(bytesRead, 0, read);
        }
        zipOut.closeEntry();
        fis.close();
    }
    public void finishAddEntry() throws IOException{
        if(lock == 0)return;
        zipOut.close();
        lock = 0;
    }
    public void addZipRecurse(String sourceFolder, String fullZipPath) throws IOException{
        File source = new File(sourceFolder);
        startZipFile(fullZipPath);
        File[] files = source.listFiles();
        int count = 0, x = 0;
        String tmp = "";
        while(files[x] == null){
            if(!files[x].isDirectory()){
                tmp = files[x].getPath();
            }
        }
    }
}
