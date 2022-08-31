package com.zjr;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 用于指定网址生成excel主操作类
 * @author 张杰荣
 * @version 1.0.0
 * @ClassName AnalysisMain.java
 * @Description TODO
 * @createTime 2022年08月30日 12:56
 */

public class AnalysisMain {
    //解析网址
    public static final String STAR_URL = "https://www.aquanliang.com/blog";

    public static void main(String[] args) {
        boolean flag = true;

        //循环遍历，直至用户输入0自行退出
        while (flag) {

            //提示用户输入并获取内容
            System.out.println("输入‘go’开始执行，输入0退出程序");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();

            //判断用户的指令是执行还是退出
            if ("0".equals(s)) {

                flag = false;

            } else if ("go".equals(s)) {

                //获得页面所有内容
                String html = getBodyByUrl(STAR_URL);

                //生成内容节点
                Document parse = Jsoup.parse(html);

                //获得页数
                int i = LastPage(parse);

                //创建list，用于存储所有扫描到的result对象
                List<Result> results = new ArrayList<>();
                List<Result> list = service(STAR_URL, results, i);

                //使用工具类对列表对象写入excel
                ExcelWriteUtils.writeExcel(list);
                System.out.println("解析完成");
            }
        }
    }


    /**
     * @param
     * @return
     * @author Zjr
     * @createTime 2022/8/31 14:36
     * @deprecated 解析网页全部内容
     */
    public static String getBodyByUrl(String url) {

        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建request和response对象
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = null;

        try {
            //发起请求，获取响应
            response = httpClient.execute(request);

            //判断响应状态
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {

                //获取响应体内容
                HttpEntity entity = response.getEntity();
                String entityStr = EntityUtils.toString(entity, "utf-8");
                return entityStr;

            } else {
                return "当前页面无法正常访问或服务异常";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭对象
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
    }


    /**
     * @param
     * @return
     * @author Zjr
     * @createTime 2022/8/31 14:37
     * @deprecated 单页解析
     */

    public static List<Result> service(String html, List<Result> results, int lastPage) {
        //获取当前页数
        int page;

        //判断网址尾巴是否为blog
        // 是则当前是第一页
        // 不是则切割字符串获得当前页数
        if (html.endsWith("blog")) {
            page = 1;
        } else {
            String s = html.substring(html.lastIndexOf("/") + 1);
            page = Integer.parseInt(s);
        }

        System.out.println("开始遍历第" + page + "页");

        //获得页面所有内容
        String body = getBodyByUrl(html);

        try {
            //判空，有时页面刷新获得标签速度过慢，导致空指针异常，
            // 此处处理方案：
            // 如果为空，则休眠1000毫秒后重新请求回调该方法。
            // 因此处还没将该页数据存入list，所以回调不会造成数据冗余和丢失。
            if (body == null || "".equals(body)) {

                Thread.sleep(1000);

                service(STAR_URL + "/page/" + page, results, lastPage);

                return results;
            }

            body = getBodyByUrl(html);

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        //生成内容节点
        Document parse = Jsoup.parse(body);

        //获取最小父标签
        Element box = parse.select("div[class^=_1ySUUwWwmubujD8B44ZDzy]").first();

        try {

            if (box == null) {

                //获取不到先休眠再重新刷新
                Thread.sleep(1000);

                //重新获取
                box = parse.select("div[class^=_1ySUUwWwmubujD8B44ZDzy]").first();

                //如果依旧为空，则休眠后回调该方法，重新获取页面
                if (box == null) {

                    System.out.println("第" + page + "页获取不到box，将重新刷新，继续获取");

                    Thread.sleep(5000);

                    service(STAR_URL + "/page/" + page, results, lastPage);

                    return results;
                }
            }
        } catch (InterruptedException e) {

            System.out.println(e.getMessage());

        }

        //判断当前页数，如果是最后一页，则获取模块数量，否则模块数量均为10
        Boolean flag = (page != lastPage);

        //确定当前页模块个数,用于循环遍历
        int modelNum;
        if (flag) {
            modelNum = 10;
        } else {
            modelNum = getPageModelNum(parse);
        }

        //开始获取内容
        for (int i = 0; i < modelNum; i++) {

            //获去span标签的内容
            Element span = box.select("span").get(i)
                    .select("div[class^=_3gcd_TVhABEQqCcXHsrIpT]").first();

            //依据前端标签关系--获取图片具体位置
            Element img = span.select("a").first()
                    .select("div[class^=_1wTUfLBA77F7m-CM6YysS6 _3NQyvscVRjQNS9gEgQoDDo]").first()
                    .select("div[class^=_2ahG-zumH-g0nsl6xhsF0s]").first()
                    .select("div").first()
                    .select("img").first();

            //获取图片具体网址，用于存入对象
            String imgStr = img.attr("src");

            //获取主要模块标签
            Element model = span.select("div[class^=_3HG1uUQ3C2HBEsGwDWY-zw]").first();

            //获取标题标签
            Element title = model.select("a").first()
                    .select("div[class^=_3_JaaUmGUCjKZIdiLhqtfr]").first();

            //获取标题，用于存入对象
            String titleStr = title.text();

            //获取模块时间与浏览量标签
            Elements minModel = model.select("div[class^=_1nlYtcrR408yNacE0R0s3M]");

            //获取时间标签
            Element time = minModel.select("div[class^=_3TzAhzBA-XQQruZs-bwWjE]").first();

            //获取时间,用于存入对象
            String timeStr = time.text();

            //获取浏览量标签
            Elements num = minModel.select("div[class^=_2gvAnxa4Xc7IT14d5w8MI1]");

            //获取浏览量,用于存入对象
            int volume = Integer.parseInt(num.text());

            //创建对象，将以上得到的内容，塞进list
            results.add(new Result(titleStr, timeStr, volume, imgStr));
        }

        //刚刚判断是否不是最后一页，
        // 此处可以利用网址参数进行页数修改，使用递归的方式遍历获取下一页内容，直至最后一页
        if (flag) {
            service(STAR_URL + "/page/" + (page + 1), results, lastPage);
        }
        return results;
    }


    /**
     * @param
     * @return
     * @author Zjr
     * @createTime 2022/8/31 14:37
     * @deprecated 获取当前页数
     */
    public static int getPage(Document parse) {
        //获取分页栏标签
        Element box = parse.select("div[class^=_1rGJJd-K0-f7qJoR9CzyeL]").first();
        //获取当前第几页
        Element page = box.select("ul").first()
                .select("li[class^=.*ant-pagination-item-active]").first();
        //获取title title标签标明了第几页
        String pageStr = page.attr("title");
        System.out.println(pageStr);
        return Integer.parseInt(pageStr);
    }

    /**
     * @param
     * @return
     * @author Zjr
     * @createTime 2022/8/31 14:38
     * @deprecated 获取当前下一页按钮是否激活，可用于判断是否是最后一页。暂时不用
     */
    public static int LastPage(Document parse) {

        //获取分页栏下所有属于页数的li标签
        Elements lis = parse.select("ul[class^=ant-pagination]")
                .first().select("li[class^=ant-pagination-item]");

        //获取最后一个属于页数的li标签
        Element last = lis.last();

        //获取属性title，此处是作为所属页数的
        String s = last.attr("title");

        return Integer.parseInt(s);
    }


    /**
     * @param
     * @return
     * @author Zjr
     * @createTime 2022/8/31 14:37
     * @deprecated 获取当前页模块数
     */
    public static int getPageModelNum(Document parse) {

        //获取分页栏标签
        Element box = parse.select("div[class^=_1rGJJd-K0-f7qJoR9CzyeL]").first();

        //获取当前第几页
        Elements page = box.select("ul").first()
                .select("li[class^=ant-pagination-item]");

        //获取总模块数，一般只有最后一页需要单独判断
        int modelNum = page.size();
        return modelNum;
    }


}
