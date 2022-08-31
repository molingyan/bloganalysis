package com.zjr;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 用于对List插入excel表格
 * @author 张杰荣
 * @version 1.0.0
 * @ClassName ExcelWriteUtils.java
 * @Description TODO
 * @createTime 2022年08月31日 16:12
 */

public class ExcelWriteUtils {


    public static void writeExcel(List<Result> results)
    {
    //文件路径和文件名称
    String fileName = "D:\\圈量文章2.xlsx";
    //实现写操作
    EasyExcel.write(fileName,Result.class).sheet("圈量").doWrite(results);

}
}