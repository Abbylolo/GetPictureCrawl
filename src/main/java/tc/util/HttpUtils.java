package tc.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.aspectj.util.FileUtil;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

//该注解用于创建Spring实例
@Component
public class HttpUtils {

    private PoolingHttpClientConnectionManager cm;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public HttpUtils() {
        //创建HttpUtils时就会创建连接池
        this.cm = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        this.cm.setMaxTotal(100);
        //设置每个主机的最大连接数
        this.cm.setDefaultMaxPerRoute(10);

    }

    /**
     * 根据请求地址下载页面数据
     * @param url 页面地址
     * @return 页面数据
     */
    public String doGetHtml(String url){
        // 获取httpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();
        // 创建httpGet请求对象，设置url地址
        HttpGet httpGet = new HttpGet(url);

        // 设置头部信息进行模拟登录（添加登录后的Cookie 存在客户端的（session存在服务端） 模拟用户登录）
        // User-Agent反爬
        httpGet.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36 Edg/100.0.1185.29");
        // Request配置（比如请求超时如何处理。连接数有限，不可能无节制等待）

        logger.info("请求地址:{}", httpGet);

        // 配置连接的各项信息
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;

        try {
            // 使用httpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            // 解析响应，返回结果
            if(response.getStatusLine().getStatusCode() == 200) {
                // 判断响应体Entity是否为空，如果不为空可以使用EntityUtils
                if(response.getEntity() != null){
                    // content - html正文
                    String content = EntityUtils.toString(response.getEntity());
                    // logger.info("响应的数据:{}", content);
                    return content;
                }
            } else {
                logger.error("获取失败状态码:{}", response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭response(httpClient由连接池管理，我们不必关闭）
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 无数据时返回空串
        return "";
    }


    /**
     * 下载图片
     * @param url
     * @return 图片名称
     */
    public String doGetImage(String url){
        //获取httpClient对象,发送 HTTP 请求的会话
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        //创建httpGet请求对象，设置url地址
        HttpGet httpGet = new HttpGet(url);

        //设置请求信息
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;

        try {
            //使用httpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            //解析响应，返回结果
            if(response.getStatusLine().getStatusCode() == 200) {
                //判断响应体Entity是否为空，如果不为空可以使用EntityUtils
                if(response.getEntity() != null){
                    //下载图片
                    //获取图片的后缀名
                    int index = url.lastIndexOf(".");
                    String extName = url.substring(index, index + 4);

                    //创建图片名，重命名图片
                    //UUID.randomUUID()-生成唯一识别码
                    String uuid = UUID.randomUUID().toString();
                    String picName = uuid+extName;

                    //下载图片
                    //声明OutPutStream
                    OutputStream outPutStream = new FileOutputStream(new File("E:\\Project\\Crawl\\outcome\\tongchen\\"+picName));
                    response.getEntity().writeTo(outPutStream);

                    //返回图片名称
                    return picName;
                }
            } else {
                logger.error("状态码:{}", response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭response(httpClient由连接池管理，我们不必关闭）
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //下载失败时返回空串
        return "";
    }

    // 获取截图
    public void snapShotBySelenium(String url) {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");

        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");

            ChromeDriver driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.get(url);
            String jsHeight = "return document.body.clientHeight";
            long height = (long) driver.executeScript(jsHeight);
            int k = 1;
            int size = 500;
            while (k * size < height) {
                String jsMove = String.format("window.scrollTo(0,%s)", k * 500);
                driver.executeScript(jsMove);
                Thread.sleep(100);
                height = (long) driver.executeScript(jsHeight);
                k += 1;
            }

            // 通过执行脚本解决Selenium截图不全问题
            long maxWidth = (long) driver.executeScript(
                    "return Math.max(document.body.scrollWidth, document.body.offsetWidth, document.documentElement.clientWidth, document.documentElement.scrollWidth, document.documentElement.offsetWidth);");
            long maxHeight = (long) driver.executeScript(
                    "return Math.max(document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight);");
            Dimension targetSize = new Dimension((int)maxWidth, (int)maxHeight);
            driver.manage().window().setSize(targetSize);

            //使用getScreenshotAs进行截取屏幕
            File img = driver.getScreenshotAs(OutputType.FILE);
            String uuid = UUID.randomUUID().toString();
            FileUtil.copyFile(img, new File("E:\\Project\\Crawl\\outcome\\SnapShots\\"+uuid + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置请求信息
    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom()
                //创建连接的最长时间
                .setConnectTimeout(1000)
                //获取连接的最长时间
                .setConnectionRequestTimeout(500)
                //数据传输的最长时间
                .setSocketTimeout(10000)
                .build();
        return config;
    }
}
