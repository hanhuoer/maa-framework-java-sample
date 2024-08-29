package club.scoder.maa.fish.sample;

import club.scoder.maa.fish.configuration.MaaConfiguration;
import cn.hutool.core.date.DateUtil;
import io.github.hanhuoer.maa.core.base.Instance;
import io.github.hanhuoer.maa.model.TaskDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
public class QaTests {

    @Autowired
    private MaaConfiguration configuration;

    @Test
    public void test1() {
        System.out.println("每日答题");
        Instance instance = configuration.getInstance();
        TaskDetail taskDetail = instance.runTask("每日答题");

        log.info("每日答题: {}", taskDetail);
    }

    @Test
    public void test1_1() {
        System.out.println("每日答题");

        for (Instance instance : configuration.getInstanceList()) {
            TaskDetail taskDetail = instance.runTask("每日答题");
            log.info("每日答题: {}", taskDetail);
        }
    }

    @Test
    public void test2() {
        System.out.println("免费钓鱼");
        Instance instance = configuration.getInstance();
        TaskDetail taskDetail = instance.runTask("免费钓鱼");

        log.info("免费钓鱼: {}", taskDetail);
    }

    @Test
    public void test2_2() {
        System.out.println("免费钓鱼");
        for (Instance instance : configuration.getInstanceList()) {
            TaskDetail taskDetail = instance.runTask("免费钓鱼");
            log.info("免费钓鱼: {}", taskDetail);
        }
    }

    @Test
    public void test3() {
        System.out.println("离线奖励_领取");
        Instance instance = configuration.getInstance();
        TaskDetail taskDetail = instance.runTask("离线奖励_领取");

        log.info("离线奖励_领取: {}", taskDetail);
    }

    @Test
    public void test3_3() {
        System.out.println("离线奖励_领取");
        for (Instance instance : configuration.getInstanceList()) {
            TaskDetail taskDetail = instance.runTask("离线奖励_领取");
            log.info("离线奖励_领取: {}", taskDetail);
        }
    }

    @Test
    public void test4() {
        System.out.println("离线奖励_加钟");
        Instance instance = configuration.getInstance();
        TaskDetail taskDetail = instance.runTask("离线奖励_加钟");

        log.info("离线奖励_加钟: {}", taskDetail);
    }

    @Test
    public void test4_4() {
        System.out.println("离线奖励_加钟");
        for (Instance instance : configuration.getInstanceList()) {
            TaskDetail taskDetail = instance.runTask("离线奖励_加钟");
            log.info("离线奖励_加钟: {}", taskDetail);
        }
    }

    @Test
    public void test5() {
        System.out.println("发条_检查");
        Instance instance = configuration.getInstance();
        TaskDetail taskDetail = instance.runTask("发条_检查");

        log.info("发条_检查: {}", taskDetail);
    }

    @Test
    public void test5_5() {
        System.out.println("发条_检查");
        for (Instance instance : configuration.getInstanceList()) {
            TaskDetail taskDetail = instance.runTask("发条_检查");
            log.info("发条_检查: {}", taskDetail);
        }
    }

    @Test
    public void test6() {
        System.out.println("答题_领取任务奖励");
        Instance instance = configuration.getInstance();
        TaskDetail taskDetail = instance.runTask("答题_领取任务奖励");

        log.info("答题_领取任务奖励: {}", taskDetail);
    }

    @Test
    public void test6_6() {
        System.out.println("答题_领取任务奖励");
        for (Instance instance : configuration.getInstanceList()) {
            TaskDetail taskDetail = instance.runTask("答题_领取任务奖励");
            log.info("答题_领取任务奖励: {}", taskDetail);
        }
    }

    @Test
    public void test7() {
        System.out.println("答题_领取联动任务奖励");
        Instance instance = configuration.getInstance();
        TaskDetail taskDetail = instance.runTask("答题_领取联动任务奖励");

        log.info("答题_领取联动任务奖励: {}", taskDetail);
    }

    @Test
    public void test7_7() {
        System.out.println("答题_领取联动任务奖励");
        for (Instance instance : configuration.getInstanceList()) {
            TaskDetail taskDetail = instance.runTask("答题_领取联动任务奖励");
            log.info("答题_领取联动任务奖励: {}", taskDetail);
        }
    }

    @Test
    public void test8() {
        System.out.println("咸鱼简报");
        Instance instance = configuration.getInstance();
        TaskDetail taskDetail = instance.runTask("咸鱼简报");

        log.info("咸鱼简报: {}", taskDetail);
    }

    @Test
    public void test8_8() {
        System.out.println("咸鱼简报");
        for (Instance instance : configuration.getInstanceList()) {
            TaskDetail taskDetail = instance.runTask("咸鱼简报");
            log.info("咸鱼简报: {}", taskDetail);
        }
    }

    @Test
    public void test_loop() throws Exception {
        long recentTime = 0;
        long gap = 10 * (60 * 1000); // 10 min
        int times = 0;

        while (true) {
            long currentTime = System.currentTimeMillis();

            if (recentTime == 0 || currentTime - recentTime >= gap) {
                recentTime = currentTime;
                times++;

                log.info("[loop] times: {}", times);

                // 钓鱼
                test2_2();
                // 离线领取
                test3_3();
                // 离线加钟
                test4_4();
                // 发条
                test5_5();
                // 每日答题
//                test1_1();
                // 答题_领取任务奖励
//                test6_6();
                // 答题_领取联动任务奖励
//                test7_7();
                if (DateUtil.hour(new Date(), true) >= 23) {
                    // 咸鱼简报
                    test8_8();
                }

            }

            Thread.sleep(1000 * 10);
        }

    }

}
