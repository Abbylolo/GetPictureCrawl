package tc.util;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Abby
 * @date
 **/
public class SnapShotsBySelenium {
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

            // 图片命名
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String strTime = sdf.format(date);
            //使用getScreenshotAs进行截取屏幕
            File img = driver.getScreenshotAs(OutputType.FILE);
            FileUtil.copyFile(img, new File("E:\\Project\\Crawl\\outcome\\SnapShots\\"+strTime + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
