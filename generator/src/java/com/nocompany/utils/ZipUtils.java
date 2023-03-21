package com.nocompany.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils{

    public static byte[] unzipBytes(byte[] zippedBytes) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zippedBytes));
        byte[] buff = new byte[1024];
        if (zipInputStream.getNextEntry() != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int l;
            while ((l = zipInputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, l);
            }
            return outputStream.toByteArray();
        }
        return new byte[0];
    }

    public static byte[] zipBytes(String filename, byte[] input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry entry = new ZipEntry(filename);
        entry.setSize(input.length);
        zos.putNextEntry(entry);
        zos.write(input);
        zos.closeEntry();
        zos.close();
        return baos.toByteArray();
    }

}