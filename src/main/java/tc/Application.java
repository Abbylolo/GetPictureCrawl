package tc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// 使用定时任务，需要先开启定时任务，需要添加注解
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        // param1-引导类的class；param2-主函数的args
        SpringApplication.run(Application.class,args);
    }
}
