package com.bilibili.util;

import com.bilibili.entity.Count;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * Excel输入(读取)工具类
 * 直接读取整个工作薄并将内容存入dataMap里，每次read都会替换dataMap里的内容
 *
 * @author Dex
 */
public class ExcelInputUtil {

    //读取的Excel路径
    private String excelPath;

    //是否打印消息
    private boolean isPrint = Count.PRINT_MSG;

    private Map<String, Row[]> rowMap = new HashMap();

    //存储当前read()的内容(key->表名或表位置,value->表的内容)
    private Map<Object, String[][]> dataMap = new HashMap<>();

    public ExcelInputUtil() {
    }

    public ExcelInputUtil(String excelPath) {
        this.excelPath = excelPath;
    }

    public String getExcelPath() {
        return excelPath;
    }

    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath;
    }

    public boolean isPrint() {
        return isPrint;
    }

    public void setPrint(boolean print) {
        isPrint = print;
    }

    public Map<String, Row[]> getRowMap() {
        return rowMap;
    }

    public Map<Object, String[][]> getDataMap() {
        return dataMap;
    }

    public String[][] getData(int index) {
        return dataMap.get(index);
    }

    public String[][] getData(String sheetName) {
        return dataMap.get(sheetName);
    }

    public String getData(String sheetName, String cellIndex) {
        int[] ints = regex(cellIndex);
        String[][] data = getData(sheetName);
        int rol = ints[1] - 1;
        int col = ints[0] - 1;
        if (data.length >= rol && data[rol].length >= col) return data[rol][col];

        return null;
    }

    /**
     * 读取当前成员变量excelPath路径的Excel工作薄
     * 返回此工作薄的Map类型(key->工作表名称或下标,从0开始，value->表的数据)数据
     *
     * @return
     */
    public Map<Object, String[][]> read() {
        return read(excelPath);
    }

    /**
     * 读取指定路径的Excel工作薄
     * 返回此工作薄的Map类型(key->工作表名称或下标,从0开始，value->表的数据)数据
     *
     * @param excelPath
     * @return
     */
    public Map<Object, String[][]> read(String excelPath) {
        Workbook wb = creatWorkbook(excelPath);
        if (wb != null) readExcel(wb);

        return dataMap;
    }

    /**
     * 通过判断文件的后缀获取特定版本的Workbook实体类
     * 如果后缀错误，则先尝试使用xlsx实体类打开，再尝试xls实体类打开
     * 此处打印不受isPrint控制
     *
     * @param excelPath 路径
     * @return
     */
    private Workbook creatWorkbook(String excelPath) {
        try {
            File file = new File(excelPath);
            if (!file.exists()) throw new FileNotFoundException("文件不存在-->" + excelPath);
            String[] cont = excelPath.split("\\.");
            String suffix = cont[cont.length - 1].toLowerCase();

            switch (suffix) {
                case "xlsx":
                    return new XSSFWorkbook(new FileInputStream(file));
                case "xls":
                    return new HSSFWorkbook(new FileInputStream(file));
                default:
                    try {
                        PrintUtil.println(8, "通过后缀识别版本失败，正在尝试以xlsx方式打开");
                        Workbook wb = new XSSFWorkbook(new FileInputStream(file));
                        return wb;
                    } catch (EmptyFileException e) {
                        // e.printStackTrace();
                        PrintUtil.println(8, "以xlsx方式打开失败，正在尝试以xls方式打开");
                        try {
                            Workbook wb = new HSSFWorkbook(new FileInputStream(file));
                            return wb;
                        } catch (NotOLE2FileException e1) {
                            // e1.printStackTrace();
                            PrintUtil.println(8, "以xls方式打开失败,请确定当前文件是Excel文件,dataMap内容为上次读取内容");
                        }
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 通用读取Excel内容
     *
     * @param wb
     * @return
     */
    private void readExcel(Workbook wb) {
        dataMap.clear();                //清除上次存储的内容
        rowMap.clear();                 //清除上次存储的内容
        Sheet sheet;
        //循环读取工作薄
        int sheetNum = wb.getNumberOfSheets();      //获取工作薄中有多少张表
        for (int s = 0; s < sheetNum; s++) {
            sheet = wb.getSheetAt(s);
            String name = sheet.getSheetName();     //获取当前下标的表名称
            int rowNum = sheet.getLastRowNum() + 1; //因为poi的问题,所以获取最后一行需要+1
//            String[] sheetCont = new String[rowNum];
            Row[] rows = new Row[rowNum];
            String[][] sheetCont = new String[rowNum][];
            Row row;
            print(3, "开始读取工作薄:" + name, true);
            for (int r = 0; r < rowNum; r++) {
                if ((row = sheet.getRow(r)) == null) continue;
                sheetCont[r] = getRowValue(row);
            }

            rowMap.put(name, rows);         //添加名称->row数据 键值对
            dataMap.put(name, sheetCont);   //添加名称->内容    键值对
            dataMap.put(s, sheetCont);      //添加下标->内容    键值对
            print(4, name + "-->读取完成", true);
        }

        print(3, "共读取" + sheetNum + "张工作薄", true);
    }

    /**
     * 传入一行单元格，返回此行String[]类型的数据
     *
     * @param row
     * @return
     */
    private String[] getRowValue(Row row) {
        Cell cell;
        int cellNum = row.getLastCellNum();
        String value[] = new String[cellNum];

        for (int c = 0; c < cellNum; c++) {
            if ((cell = row.getCell(c)) == null) continue;
            value[c] = getCellValue(cell);
        }

        return value;
    }

    /**
     * 传入一个单元格，获取此单元格的String类型的数据
     *
     * @param cell
     * @return
     */
    @SuppressWarnings("all")
    private String getCellValue(Cell cell) {
        Object value = "";
        switch (cell.getCellType()) {
            case NUMERIC:
                value = DateUtil.isCellDateFormatted(cell) ? TimeUtil.getDate(cell.getDateCellValue()) : cell.getNumericCellValue();
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            case ERROR:
                value = cell.getErrorCellValue();
                break;
            case BLANK:
                break;
        }

        return value.toString();
    }

    /**
     * 通过正则表达式,将单元格的位置拆封成英文和数字，在通过二十六进制转换成十进制int类型的数字
     *
     * @param cellIndex
     * @return
     */
    private int[] regex(String cellIndex) {
        int[] ints = new int[2];
        ints[0] = ChangeNumEngUtil.ChangeNumber(RegexUtil.regex(cellIndex, "[a-zA-Z]+"));
        ints[1] = Integer.parseInt(RegexUtil.regex(cellIndex, "\\d+"));

        return ints;
    }

    /**
     * 打印消息(由isPrint控制全局)
     *
     * @param cont 内容
     * @param line 换行
     */
    private void print(int level, String cont, boolean line) {
        if (isPrint) {
            if (line) PrintUtil.println(level, cont);
            else PrintUtil.println(level, cont);
        }
    }

}
