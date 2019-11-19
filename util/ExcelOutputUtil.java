package com.bilibili.util;

import com.bilibili.entity.Count;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * Excel输出(写入)工具类
 * 实现了自定义写入行数,是否从追加写入,需要使用flush写入文件
 * 写入的数据必须为Row类型(一行一行写入),调用write()写入
 * 一个实例类对应一个文件
 *
 * @author Dex
 */
public class ExcelOutputUtil implements Flushable {

    //写入的Excel路径
    private final String excelPath;

    //写入的工作薄名称(默认名称Sheet1)
    private String sheetName = "Sheet1";

    //写入的行数位置
    private int index;

    //是否追写
    private boolean isAppend;

    //是否打印消息
    private boolean isPrint = Count.PRINT_MSG;

    //是否替换单元格样式
    private boolean replaceStyle;

    //当前写入的Excel文档
    private Workbook wb;

    //用于获取Row对象以便存储写入数据
    private static Sheet wwb = new XSSFWorkbook().createSheet();

    public ExcelOutputUtil(String excelPath, boolean isAppend) {
        this.excelPath = excelPath;
        this.isAppend = isAppend;
    }

    public ExcelOutputUtil(String excelPath, boolean isAppend, boolean replaceStyle) {
        this(excelPath, isAppend);
        this.replaceStyle = replaceStyle;
    }

    public String getExcelPath() {
        return excelPath;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isAppend() {
        return isAppend;
    }

    public void setAppend(boolean append) {
        isAppend = append;
    }

    public boolean isPrint() {
        return isPrint;
    }

    public void setPrint(boolean print) {
        isPrint = print;
    }

    public boolean isReplaceStyle() {
        return replaceStyle;
    }

    public void setReplaceStyle(boolean replaceStyle) {
        this.replaceStyle = replaceStyle;
    }

    public void write(Row data) {
        List<Row> row = new ArrayList<>();
        row.add(data);
        write(row);
    }

    public void write(List<Row> data) {
        if (data == null || data.size() < 1) throw new NullPointerException("写入数据集合不能为空");
        if (wb == null) deteVersion();  //如果wb未初始化，则初始化wb
        writeExcel(data);
    }

    /**
     * 根据文件名判断写入的Excel版本,且初始化wb
     */
    private void deteVersion() {
        //如果路径为空或""或者data小于0，则抛出异常
        if (excelPath == null || excelPath.equals("")) throw new NullPointerException("Excel路径不能为空");
        String[] cont = excelPath.split("\\.");
        String suffix = cont[cont.length - 1].toLowerCase();      //获取文件名后缀
        File file = new File(excelPath);
        try {
            if (file.exists() && isAppend) {
                InputStream is = new FileInputStream(file);
                wb = "xls".equals(suffix) ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
            } else {
                wb = "xls".equals(suffix) ? new HSSFWorkbook() : new XSSFWorkbook();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入多行数据(此处只会写入内存)
     * 存储到文件需要flush
     *
     * @param data 要写入的数据
     */
    private void writeExcel(List<Row> data) {
        //如果工作薄已存在sheet表，则获取sheet表的实体类，否则创建sheet表
        Sheet sheet = wb.getSheet(sheetName) != null ? wb.getSheet(sheetName) : wb.createSheet(sheetName);
        System.out.println(sheetName);
        //如果追写文件，则将位置设置为最后有数据的一行+1，否则为index本身(默认为0)
        index = isAppend ? sheet.getLastRowNum() + 1 : index;
        index = index == 1 ? sheet.getRow(0) == null ? 0 : 1 : index;   //判断是否为写入第一行
        for (Row row : data) {
            writeExcel(sheet, row, index);
            print(5, "成功写入内存第->工作薄:" + sheetName + "\t" + ++index + "行", true);
        }
        index = 0;  //写完将行数指针指向0
    }

    /**
     * 传入一个工作表(sheet),一行数据(data),指定的行下标(index)
     * 将数据写入指定行下标的工作表
     * 此处行下标不需要减1，第1行为1
     *
     * @param sheet 工作表
     * @param data  数据
     * @param index 行下标
     * @return
     */
    private void writeExcel(Sheet sheet, Row data, int index) {
        if (data == null) return;
        Row row = sheet.getRow(index) != null ? sheet.getRow(index) : sheet.createRow(index);
        Cell oldCell, newCell;
        StringBuffer print = new StringBuffer("↓");
        for (int i = 0, rowney = data.getLastCellNum(); i < rowney; i++) {
            if ((oldCell = data.getCell(i)) == null) continue;
            newCell = row.getCell(i) != null ? row.getCell(i) : row.createCell(i); //获取要写入的单元格实体类
            String value = getCellValue(oldCell);   //获取要写入的数据
            newCell.setCellValue(value);            //设置此写入的单元格格的内容
            // setCellStyle(newCell, oldCell.getCellStyle());
            print.append(value).append(" |");
        }

    }

    /**
     * 传入一个单元格获取这个单元格为String类型的值
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
     * 将内存中的数据写入文件
     */
    public void flush() {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(excelPath));
            Objects.requireNonNull(wb).write(bos);  //判断wb是否为空
            bos.flush();
            print(6, "写入文件成功", true);
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            print(8, excelPath + " (另一个程序正在使用此文件，进程无法访问，5秒后重试)", true);
            TimeUtil.sleep(5000);
            flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(bos);
        }
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
            else PrintUtil.print(level, cont);
        }
    }

    /**
     * 通过静态获取写入的Row对象
     *
     * @return
     */
    public static Row getRow() {
        return wwb.createRow(0);
    }

}
