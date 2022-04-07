package tc.util;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * @author Abby
 * @date
 **/
public class SnapShots {
    private String path = "D://"; //文件路径名
    private String filePrefix = "GuiCamera"; //文件的前缀
    static int serialNum=0;
    private String imageFormat = "png"; //图像文件的格式

    private final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    private int ax = 0;
    private int ay = 0;
    private int bx = (int) d.getWidth();
    private int by = (int) d.getHeight();

    public SnapShots(String path, String prefix, String format) {
        this.path = path;
        this.filePrefix = prefix;
        this.imageFormat=format;
    }

    public SnapShots(String path, String prefix, String format, int ax, int ay, int bx, int by) {
        this.path = path;
        this.ax = ax;
        this.ay = ay;
        this.bx = bx;
        this.by = by;
        this.filePrefix = prefix;
        this.imageFormat=format;
    }

    /****************************************************************
     * 对屏幕进行拍照
     * snapShot the Gui once
     ****************************************************************/
    public void snapShot() {
        try {
            // 拷贝屏幕到一个BufferedImage对象screenshot（四点定位）
            BufferedImage screenshot = (new Robot()).createScreenCapture(new
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
            ImageIO.write(screenshot, imageFormat, f);
            System.out.print("..Finished!\n");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}

//class SnapShotsTest {
//    public static void main(String[] args) {
//        //Thread.sleep(5000);
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        SnapShots cam= new SnapShots("E:\\Project\\Crawl\\outcome\\SnapShots\\", "travel","jpg");
//        cam.snapShot();
//    }
//}