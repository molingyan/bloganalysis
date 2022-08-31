package com.zjr;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

/**
 * 指定对象及格式
 * @author 张杰荣
 * @version 1.0.0
 * @ClassName Result.java
 * @Description TODO
 * @createTime 2022年08月30日 14:01
 */

public class Result {

    //标题
    @ColumnWidth(67)
    @ExcelProperty(value = "文章标题",index = 0)
    private String title;


    //发布时间
    @ColumnWidth(17)
    @ExcelProperty(value = "发布时间",index = 1)
    private String date;

    //文章浏览量
    @ColumnWidth(8)
    @ExcelProperty(value = "浏览量",index = 2)
    private int num;

    //图片地址

    @ColumnWidth(98)
    @ExcelProperty(value = "图片",index = 3)
    private String imgUrl;

    public Result() {
    }

    public Result(String title, String date, int num, String imgUrl) {
        this.title = title;
        this.date = date;
        this.num = num;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Result{" +
                "title='" + title + '\'' +
                ", Date='" + date + '\'' +
                ", num=" + num +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
