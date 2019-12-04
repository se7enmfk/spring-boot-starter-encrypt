/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.file;

import com.ftx.frame.util.BaseConstant;
import com.ftx.frame.util.string.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.net.URLEncoder;

public class FileUtil {

    /**
     * @param in
     * @param parent
     * @param fileName
     */
    public static void saveFile(File in, String parent, String fileName) {

        FileInputStream input = null;
        try {
            input = new FileInputStream(in);
            saveFile(input, parent, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * 保存文件
     *
     * @param in
     * @param parent
     * @param fileName
     */
    public static void saveFile(InputStream in, String parent, String fileName) {
        File dir = new File(parent);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(new File(dir, fileName));
            org.apache.commons.io.IOUtils.copy(in, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 下载文件 ie不支持201状态码，改为200
     *
     * @param parent           template路径
     * @param originalFilename 文件原名
     * @param fileName         下载文件名
     * @return
     */
    public static ResponseEntity downFile(String parent, String originalFilename, String fileName) {
        return downFile(parent, originalFilename, fileName, MediaType.APPLICATION_OCTET_STREAM);
    }

    public static ResponseEntity downFile(String parent, String originalFilename, String fileName, MediaType mediaType) {
        File file = new File(StringUtil.concat(parent, originalFilename));
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData(BaseConstant.ATTACHMENT, URLEncoder.encode(fileName, BaseConstant.UTF8));
            headers.setContentType(mediaType);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导出CSV文件
     *
     * @param fileName
     * @param fileContent
     * @return
     */
    public static ResponseEntity exportCSVFile(String fileName, String fileContent) {
        File file = new File(fileName);
        FileOutputStream out = null;
        try {
            file.deleteOnExit();
            HttpHeaders headers = new HttpHeaders();
            String downFileName = new String((fileName).getBytes(BaseConstant.UTF8), BaseConstant.ISO88591);
            headers.setContentDispositionFormData(BaseConstant.ATTACHMENT, downFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            out = new FileOutputStream(file);
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            out.write(bom);
            out.write(fileContent.getBytes(BaseConstant.UTF8));
            out.close();
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
        return null;
    }

    public static ResponseEntity exportExcelFile(XSSFWorkbook wb, String fileName) {
        File file = new File(fileName);
        FileOutputStream out = null;
        try {
            file.deleteOnExit();
            HttpHeaders headers = new HttpHeaders();
            String downFileName = new String((fileName).getBytes(BaseConstant.UTF8), BaseConstant.ISO88591);
            headers.setContentDispositionFormData(BaseConstant.ATTACHMENT, downFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            out = new FileOutputStream(file);
            wb.write(out);
            out.close();
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
        return null;
    }
    /**
     * 建目录
     */
    public static void mkdirs(File file) {
        String parent = file.getParent();
        File file1 = new File(parent);
        if (!file1.exists())
            file1.mkdirs();
    }

    public static String readFileToString(String filePath){

        File file = new File(filePath);

        FileReader reader;//定义一个fileReader对象，用来初始化BufferedReader
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        try {
            reader = new FileReader(file);

            BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
            String s;
            while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s);//将读取的字符串添加换行符后累加存放在缓存中
            }
            bReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
