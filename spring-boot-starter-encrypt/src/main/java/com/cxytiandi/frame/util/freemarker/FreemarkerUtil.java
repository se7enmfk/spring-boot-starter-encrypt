/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 *
 */

package com.cxytiandi.frame.util.freemarker;

import com.cxytiandi.frame.util.BaseConstant;
import com.cxytiandi.frame.util.file.IOUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

public class FreemarkerUtil {

    private static Configuration getConfiguration(String path) {
        /** 初始化配置文件 **/
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        try {
            /** 设置编码 **/
            configuration.setDefaultEncoding(BaseConstant.UTF8);
            /** 加载总文件路径 **/
//            configuration.setDirectoryForTemplateLoading(new File(SystemConfig.FILES_TEMP_PATH));
            configuration.setDirectoryForTemplateLoading(new File(path));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return configuration;
    }

    /**
     * 根据freemarker模板导出文件
     *
     * @param tempFileName 模板路径
     * @param tempFileName 模板文件名
     * @param outFilePath  导出文件路径
     * @param data         模板数据
     */
    public static void genWord(String tempPath, String tempFileName, String outFilePath, Map<String, String> data) {
        Writer out = null;
        /** 初始化配置文件 **/
        Configuration configuration = getConfiguration(tempPath);
        /** 加载模板 **/
        Template template;
        try {
            template = configuration.getTemplate(tempFileName + ".xml", BaseConstant.UTF8);

            File docFile = new File(outFilePath);
            FileOutputStream fos = new FileOutputStream(docFile);
            out = new BufferedWriter(new OutputStreamWriter(fos, BaseConstant.UTF8), 10240);
            template.process(data, out);

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(out);
        }
    }
}
