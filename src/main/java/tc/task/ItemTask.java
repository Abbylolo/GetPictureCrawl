package tc.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tc.pojo.Item;
import tc.service.ItemService;
import tc.service.impl.ItemServiceImpl;
import tc.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tc.util.SnapShots;

import java.io.IOException;
import java.util.*;

@Component
public class ItemTask {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HttpUtils httpUtils;
    private SnapShots snapShots;

    @Autowired
    private ItemServiceImpl itemService;

//    解析json的工具类
//    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 解析对象包括子节点
    private LinkedList<String> seeds = new LinkedList<>();

    //当下载任务完成后，间隔多长时间进行下一次任务
    @Scheduled(fixedDelay = 1000*1000 )
    public void itemTask() throws Exception{
        String url = null;
        seeds.add("https://www.lvmama.com/");
        seeds.add("https://www.toutiao.com/");
        seeds.add("https://www.ly.com/");
        seeds.add("https://www.ctrip.com/");
        for (int i = 0; i < seeds.size(); i++) {
            url = seeds.get(i);
            String html = httpUtils.doGetHtml(url);
            this.parse(html);
            System.out.println(url + "爬取完成！");
        }
    }


    /**
     * 解析页面，获取商品数据并存储
     * @param html 待爬取的html页面
     * @throws IOException
     */
    private void parse(String html) throws IOException {
        // 解析html，获取dom对象
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a");
        for(Element link : links) {
            seeds.add(link.attr("href"));
        }
        // 获取网页中所有图片
        Elements images = doc.select("img");
        String picUrl = null;
        for (Element image : images) {
            // 获取商品图片url
            // Elements.attr - 从具有该属性的第一个匹配元素中获取属性值。
            if(image.attr("src") != "") {
                picUrl = "https:"+image.attr("src");
            } else if(image.attr("nsrc") != "") {
                picUrl = "https:"+image.attr("nsrc");
            } else if(image.attr("orisrc") != "") {
                picUrl = "https:"+image.attr("orisrc");
            } else if(image.attr("data-src") != "") {
                picUrl = "https:"+image.attr("data-src");
            }else {
                logger.error("未识别到图片的来源标签:{}",image);
                continue;
            }
            Item item = new Item();

            item.setUrl(picUrl);

            String picName = this.httpUtils.doGetImage(picUrl);
            item.setPic(picName);

            // 获取截图
            snapShots = new SnapShots("E:\\Project\\Crawl\\outcome\\SnapShots\\", "travel", "png");
            snapShots.snapShot();

            // 创建时间
            item.setCreated(new Date());

            // 保存商品数据到数据库中
            this.itemService.save(item);
        }
    }
}
