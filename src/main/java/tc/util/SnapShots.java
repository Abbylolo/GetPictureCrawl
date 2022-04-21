package tc.util;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Dimension;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * @author Abby
 * @date 2022/4/7
 * 根据当前屏幕显示，截取快照。可通过四点定位指定截图大小。可设置图片后缀和存取路径
 **/
public class SnapShots {
    // 文件路径名
    private String path = "D://SnapShots";
    // 文件的前缀
    private String filePrefix = "SnapShot";
    // 序列号
    static int serialNum=0;
    // 图像文件的格式
    private String imageFormat = "png";

    // 图片路径
    private String url = null;

    // 获取当前屏幕大小
    private final java.awt.Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    // 快照截屏四点位置
    private int ax = 0;
    private int ay = 0;
    private int bx = (int) d.getWidth();
    private int by = (int) d.getHeight();

    public SnapShots(String url, String path, String prefix, String format) {
        this.url = url;
        this.path = path;
        this.filePrefix = prefix;
        this.imageFormat=format;
    }

    public SnapShots(String url, String path, String prefix, String format, int ax, int ay, int bx, int by) {
        this.url = url;
        this.path = path;
        this.ax = ax;
        this.ay = ay;
        this.bx = bx;
        this.by = by;
        this.filePrefix = prefix;
        this.imageFormat=format;
    }

    public SnapShots() {

    }

    public void snapShot() {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
            Robot robot = new Robot();
            // 延长3s —— 不然页面还未加载出来，造成无效截图
            robot.delay(3000);
            // 拷贝屏幕到一个BufferedImage对象screenshot（四点定位）
            BufferedImage snapShot = (robot).createScreenCapture(new
                    Rectangle(ax, ay, bx-ax, by-ay));
            serialNum++;
            //根据文件前缀变量和文件格式变量，自动生成文件名
            String name= path + filePrefix + serialNum +"."+imageFormat;
            File f = new File(name);
            if(f.exists()){
                f.delete();
            }
            System.out.print("Save File "+name);
            //将screenshot对象写入图像文件
            ImageIO.write(snapShot, imageFormat, f);
            System.out.print("Finished!\n");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    public void se() throws IOException {
        //配置本地的chromediver.exe谷歌的内核
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        //设置ChromeOptions打开方式，设置headless：不弹出浏览器
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        //设置好使用ChromeDriver使用
        ChromeDriver driver = new ChromeDriver(options);
        //获取要截图的地址，注：需要先获取地址哦，不然下方获取的宽度高度就会是弹窗的高和宽，而不是页面内容的高宽
        driver.get("https://www.jianshu.com/u/a2b05c68b03b");
        //获取页面高宽使用：return document.documentElement.scrollWidth
        //Long width = (Long)driver.executeScript("return document.documentElement.scrollWidth");
        // Long height =(Long) driver.executeScript("return document.documentElement.scrollHeight");
        Long width = (Long) ((JavascriptExecutor)driver).executeScript("return document.documentElement.scrollWidth");
        Long height = (Long) ((JavascriptExecutor)driver).executeScript("return document.documentElement.scrollHeight");
        //设置浏览器弹窗页面的大小
        driver.manage().window().setSize(new Dimension(width.intValue(), height.intValue()));
        //使用getScreenshotAs进行截取屏幕
        File srcFile = driver.getScreenshotAs(OutputType.FILE);
        FileUtil.copyFile(srcFile, new File("G:\\pyChar\\jj.png"));
    }
}

class SnapShotsTest {
    public static void main(String[] args) throws IOException {
        //Thread.sleep(5000);
        /*SnapShots cam= new SnapShots("http://www.baidu.com", "E:\\Project\\Crawl\\outcome\\SnapShots\\", "travel","jpg");
        cam.snapShot();*/
        new SnapShots().se();

    }
}


class KeywordBrowserChrome {

    public static void main(String[] args) {
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://www.baidu.com");
    }
}
