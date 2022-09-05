package com.zjr.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.zjr.AnalysisMain;
import com.zjr.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 用于对List插入excel表格
 *
 * @author 张杰荣
 * @version 1.0.0
 * @ClassName ExcelWriteUtils.java
 * @Description TODO
 * @createTime 2022年08月31日 16:12
 */

public class ExcelWriteUtils {

    public static void writeExcel(List<Result> results) {
        //文件路径和文件名称
        String fileName = ".\\圈量文章2.xlsx";
        //实现写操作
        EasyExcel.write(fileName, Result.class).sheet("圈量").doWrite(results);

    }

    public static void insertExcel(List<Result> results) {
        String fileName = ".\\圈量文章2.xlsx";
        String filetextName = ".\\圈量文章.xlsx";
        ExcelWriter excelWriter = null;
        File destFile = new File(fileName);
        File transFile = new File(filetextName); //这个文件名取为什么都可以，中转文件
        try {
            if (destFile.exists()) {
                //创建中转文件
                //追加数据，中转文件与目标文件不能是同一个文件名
                //withTemplate()指定模板文件,即复制一份; file() 中间文件名; autoCloseStream() 必须为True,自动关闭输入流
                excelWriter = EasyExcel.write().withTemplate(destFile)
                        //.file() 指定目标文件，不能与模板文件是同一个文件
                        .file(transFile).autoCloseStream(true).build(); //
            } else {
                excelWriter = EasyExcel.write(destFile).build();
            }
            WriteSheet writeSheet = EasyExcel.writerSheet("圈量").needHead(false).build();
            excelWriter.write(results, writeSheet);
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }

        if (transFile.exists()) {
            //删除原模板文件，新生成的文件变成新的模板文件
            destFile.delete();
            transFile.renameTo(destFile);
        }


    }
}