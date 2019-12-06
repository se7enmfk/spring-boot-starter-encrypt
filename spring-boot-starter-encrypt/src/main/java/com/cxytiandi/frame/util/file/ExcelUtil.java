/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.file;

import com.cxytiandi.frame.common.component.SystemConfig;
import com.cxytiandi.frame.util.collection.CollectionUtil;
import com.cxytiandi.frame.util.date.DateUtil;
import com.cxytiandi.frame.util.string.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * POI 工具类<br/>
 * 导入、导入Excel<br/>
 *
 * @author yeshujun
 */
public class ExcelUtil {

    public final static String errorMsgFormat = "第{0}行，第{1}列";
    public final static String errorMsgFormatEn = "Column {1} of row {0}";
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 根据file path获取当前workbook
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbook(String filePath, String type) {
        try {
            File file = new File(filePath);
            InputStream in;
            if (file.exists()) {
                in = new FileInputStream(filePath);
            } else {
                in = ExcelUtil.class.getResourceAsStream(filePath);
            }
            return getWorkbook(in, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据InputStream获取当前workbook
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbook(InputStream in, String type) {
        if ("xls".equals(type)) {
            try {
                return new HSSFWorkbook(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return new XSSFWorkbook(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 读取 excel到List
     * 列固定，循环行
     *
     * @param <T>        excel的列对应的java类
     * @param sheet      sheet
     * @param startRow   开始读取的行号
     * @param startCell  开始读取的列
     * @param fieldNames clazz的属性名，与excel的列相同的顺序
     * @param clazz      excel每一行映射的java类
     * @return
     * @throws Exception
     */
    public static <T> List<T> fromExcel(Sheet sheet, int startRow, int startCell,
                                        Class<? extends T> clazz, String... fieldNames) throws Exception {
        List<T> list = new ArrayList<T>();

        T obj = null;
        int totalRows = sheet.getLastRowNum();
        Row row = null;
        Cell cell = null;
        for (int i = startRow; i <= totalRows; i++) {//循环行
            obj = clazz.newInstance();
            row = sheet.getRow(i);
            int cellNum = 0;
            for (int j = 0; j < fieldNames.length; j++) {//循环列
                cellNum = j + startCell;
                //列值
                cell = row.getCell(cellNum);

                Object cellValue = getCellValue(cell);
                //
                setFieldValue(obj, fieldNames[cellNum], cellValue);

                //数据校验
                Field field = getAccessibleField(obj, fieldNames[cellNum]);

                //注解方式校验
                Annotation[] annotations = field.getAnnotations();
                if (annotations != null && annotations.length > 0) {
                    for (Annotation annotation : annotations) {

                        //其他校验
                    }
                }
            }
            list.add(obj);
        }
        return list;
    }

    /**
     * 获取List到excel
     * 列固定，循环行
     *
     * @param <T>
     * @param startRow   开始写入的行号
     * @param dataList   list数据
     * @param fieldNames T的属性名，与excel的列相同的顺序
     * @return
     * @throws Exception
     */
    public static <T> void setCellByList(Sheet sheet, int startRow, List<T> dataList, String... fieldNames) {
        //模板行
        Row rowTemplate = sheet.getRow(startRow);
        if (dataList.size() > 1) {
            shiftRows(sheet, startRow + 1, sheet.getLastRowNum(), dataList.size() - 1);
        }
        Object cellValue;
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                for (int j = 0; j < fieldNames.length; j++) {
                    cellValue = null;
                    if (StringUtil.isNotEmpty(fieldNames[j])) {
                        cellValue = getFieldValue(dataList.get(i), fieldNames[j]);
                    }
                    setCellByValue(sheet, startRow + i, j, null, cellValue, rowTemplate);
                }
            }
        }
    }


    /**
     * 将List数据导出excel的指定行
     * 行固定，循环列
     *
     * @param startCell 开始的列
     * @throws
     * @pparam sheetAt sheet号
     * @pparam rowNum 指定的行号
     * @pparam datas List数据
     * @pparam fieldNames T的属性名
     * @pparam
     */
    public static <T> void setCellInOneRowByList(Sheet sheet, int rowNum, int startCell,
                                                 List<T> datas, String... fieldNames) throws Exception {
        Object cellValue = null;
        //将第一个单元格的样式作为整行所有单元格的样式
        CellStyle cellStyle = sheet.getRow(rowNum).getCell(startCell).getCellStyle();
        int fieldLength = fieldNames.length;
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {//10

                for (int j = 0; j < fieldNames.length; j++) {//2
                    cellValue = null;
                    if (StringUtil.isNotEmpty(fieldNames[j])) {
                        cellValue = getFieldValue(datas.get(i), fieldNames[j]);
                    }
                    setCellByValue(sheet, rowNum, j + startCell, cellStyle, cellValue);
                }
                startCell = (startCell + 1) + (fieldLength - 1);
            }
        }
    }

    /**
     * 将List数据，写入到excel
     * 适合以列表展现数据的情况
     * 列固定，循环行
     *
     * @param <T>
     * @param startRow   开始写入的行号
     * @param startCell  开始写入的列号
     * @param datas      list数据
     * @param fieldNames T的属性名，与excel的列相同的顺序
     * @return
     * @throws Exception
     */
    public static <T> void setCellByListList(Sheet sheet, int startRow, int startCell,
                                             List<List<T>> datas, String... fieldNames) throws Exception {
        //模板行
        Row rowTemplate = sheet.getRow(startRow);
        Object cellValue = null;
        int maxDatasSize = 0;
        if (CollectionUtil.isNotEmpty(datas)) {
            for (int i = 0; i < datas.size(); i++) {
                if (maxDatasSize < datas.get(i).size()) {
                    maxDatasSize = datas.get(i).size();
                }
                int j = 0;
                for (T t : datas.get(i)) {
                    for (int k = 0; k < fieldNames.length; k++) {
                        if (StringUtil.isNotEmpty(fieldNames[k])) {
                            cellValue = getFieldValue(t, fieldNames[k]);
                        }
                        setCellByValue(sheet, startRow + i, k + j + startCell, null, cellValue, rowTemplate);
                    }
                    j++;
                }
            }
            refreshCellStyle(sheet, startRow, startRow + datas.size() - 1, startCell, startCell + maxDatasSize, null, rowTemplate);
        }
    }

    /**
     * 用数据填充excel的cell
     * 仅接受String和Number格式的数据，非Number的数据，需转成String类型的
     *
     * @param sheet
     * @param rowNum  行号
     * @param cellNum 列号
     * @param value   cell的值
     * @return
     */
    public static void setCellByValue(Sheet sheet, int rowNum, int cellNum, Object value) {
        setCellByValue(sheet, rowNum, cellNum, null, value, null);
    }

    /**
     * 用数据填充excel的cell
     * 仅接受String和Number格式的数据，非Number的数据，需转成String类型的
     *
     * @param sheet
     * @param cellStyle cell样式
     * @param rowNum    行号
     * @param cellNum   列号
     * @param value     cell的值
     * @return
     */
    public static void setCellByValue(Sheet sheet, int rowNum, int cellNum, CellStyle cellStyle, Object value) {
        setCellByValue(sheet, rowNum, cellNum, cellStyle, value, null);
    }

    /**
     * 填充单元格
     *
     * @throws
     */
    private static void setCellByValue(Sheet sheet, int rowNum, int cellNum, CellStyle cellStyle, Object value, Row rowTemplate) {
        Cell cell = getCell(sheet, rowNum, cellNum, cellStyle, rowTemplate);
        if (value == null || StringUtil.isEmpty(value + "") || "null".equals(value + "")) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue("");
        } else {
            if (value instanceof String) {
                cell.setCellType(CellType.STRING);
                cell.setCellValue(value.toString());
            } else if (value instanceof Number) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(Double.parseDouble(value.toString()));
            } else if (value instanceof java.util.Date) {
                cell.setCellType(CellType.STRING);
                cell.setCellValue(DateUtil.dateToString(new Date(((java.util.Date) value).getTime()), SystemConfig.DATE_FORMAT));
            }
        }
    }

    /**
     * 用公式+数据填充excel的cell
     *
     * @param sheet
     * @param rowNum  行号
     * @param cellNum 列号
     * @param value   cell的值
     * @return
     */
    public static void setCellByFormula(Sheet sheet, int rowNum, int cellNum, Object value) {
        setCellByFormula(sheet, rowNum, cellNum, null, value);
    }

    /**
     * 用公式+数据填充excel的cell
     *
     * @param sheet
     * @param cellStyle cell样式
     * @param rowNum    行号
     * @param cellNum   列号
     * @param value     cell的值
     * @return
     */
    public static void setCellByFormula(Sheet sheet, int rowNum, int cellNum, CellStyle cellStyle, Object value) {
        Cell cell = getCell(sheet, rowNum, cellNum, cellStyle, null);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula(value.toString());
    }

    /**
     * 获得cell的数值
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        Object result = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                //add by tanyb  Excel导入时，日期类型的字符特殊处理
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    java.util.Date date = cell.getDateCellValue();
                    result = DateUtil.dateToString(new Date(date.getTime()), SystemConfig.DATE_FORMAT);
                } else {
                    result = cell.getNumericCellValue();
                    if (result != null) {
                        result = result.toString();
                    }
                }
                break;
            case STRING:
                result = cell.getRichStringCellValue().getString().trim();
                break;
            case FORMULA:
                result = cell.getCellFormula().trim();
                break;
            case BOOLEAN:
                result = cell.getBooleanCellValue();
                break;
            case ERROR:
                result = cell.getErrorCellValue();
                break;
            case BLANK:
                result = "";
                break;
        }
        return result;
    }

    /**
     * 移动行，
     *
     * @param startRow   开始移动的行号
     * @param endRow     结束移动的行号
     * @param shitRowNum 向下移动的行数
     * @throws
     */
    public static void shiftRows(Sheet sheet, int startRow, int endRow, int shitRowNum) {
        sheet.shiftRows(startRow, endRow, shitRowNum);
    }

    /**
     * 取得公式单元格的公式,重新设置,刷新公式值
     *
     * @throws
     */
    public static void refreshCellFormula(Sheet sheet) {
        for (Row eachRow : sheet) {
            for (Cell cell : eachRow) {
                if (CellType.FORMULA == cell.getCellTypeEnum()) {
                    cell.setCellFormula(cell.getCellFormula());
                }
            }
        }
    }

    /**
     * 刷新CellStyle
     */
    protected static void refreshCellStyle(Sheet sheet, int startRow, int endRow, int startCell, int endCell,
                                           CellStyle cellStyle, Row rowTemplate) {
        Row row = null;
        Cell cell = null;
        for (int i = startRow; i < endRow; i++) {
            row = sheet.getRow(i);
            if (row != null) {
                for (int j = startCell; j < endCell; j++) {
                    cell = row.getCell(j);
                    if (cell == null) {
                        cell = row.createCell(j);
                        if (cellStyle != null) {
                            cell.setCellStyle(cellStyle);
                        } else {
                            if (rowTemplate != null && rowTemplate.getCell(j) != null && rowTemplate.getCell(j).getCellStyle() != null) {
                                cell.setCellStyle(rowTemplate.getCell(j).getCellStyle());
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 获得cell
     *
     * @throws
     */
    private static Cell getCell(Sheet sheet, int rowNum, int cellNum, CellStyle cellStyle, Row rowTemplate) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(cellNum);
        if (cell == null) {
            cell = row.createCell(cellNum);
        }

        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        if (rowTemplate != null) {
            Cell cellTemplata = rowTemplate.getCell(cellNum);
            if (cellTemplata != null && cellTemplata.getCellStyle() != null) {
                cell.setCellStyle(cellTemplata.getCellStyle());
            }
        }
        return cell;
    }

    /**
     * 合并单元格
     *
     * @param sheet
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    @SuppressWarnings("deprecation")
    public static void mergeCells(Sheet sheet, int x1, int x2, int y1, int y2) {
        sheet.addMergedRegion(new CellRangeAddress(x1, x2, y1, y2));
    }
    /**
     * 渲染excel到客户端
     * @throws
     */
    /*public static ResponseEntity renderExcel(Workbook wb,String fileName) throws Exception{
        if(!fileName.toLowerCase().endsWith(".xls") && !fileName.toLowerCase().endsWith(".xlsx")){
			throw new Exception(fileName+"不是一个合法的excel文件名..............................");
		}
        HttpHeaders headers = new HttpHeaders();
        String downFileName = new String((fileName).getBytes(BaseConstant.UTF8), BaseConstant.ISO88591);
        headers.setContentDispositionFormData(BaseConstant.ATTACHMENT, downFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        out = new FileOutputStream(file);

	    OutputStream os = response.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);

    }*/

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter方法.
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {

        }
        return result;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {//NOSONAR
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
                .isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter方法.
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        try {
            if (value != null && !"".equals((value + "").trim())) {
                field.set(obj, value);
            }
        } catch (IllegalAccessException e) {

        }
    }

    public static void saveExcelFile(Workbook wb, String file_path) {
        FileOutputStream out = null;
        File file = new File(file_path);
        try {
            out = new FileOutputStream(file);
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(out);
        }

    }

}

