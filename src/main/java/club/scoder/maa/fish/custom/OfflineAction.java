package club.scoder.maa.fish.custom;

import club.scoder.maa.fish.model.DataItem;
import club.scoder.maa.fish.service.ChanifyService;
import club.scoder.maa.fish.util.TimeUtils;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.DateUtils;
import io.github.hanhuoer.maa.core.AdbController;
import io.github.hanhuoer.maa.core.base.Instance;
import io.github.hanhuoer.maa.core.custom.CustomAction;
import io.github.hanhuoer.maa.core.custom.SyncContext;
import io.github.hanhuoer.maa.model.RecognitionResult;
import io.github.hanhuoer.maa.model.Rect;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OfflineAction extends CustomAction {

    private final ChanifyService chanifyService;
    private final Instance instance;


    @SneakyThrows
    @Override
    public boolean run(SyncContext context, String taskName, String customParam, Rect box, String recDetail) {
        log.info("====================================");
        log.info("starting offline action");
        log.info("====================================");


        int times = 0;
        String offlineTime = null;
        DataItem rewardButton = null;
        DataItem offlineOverflow = null;
        do {
            times++;
            try {
                BufferedImage screencap = context.screencap();
                RecognitionResult ocrResult = context.runRecognition(screencap, "离线奖励_OCR");

                log.info("times: {}, ocr result: {}", times, ocrResult);

                rewardButton = findRewardButton(ocrResult.getDetail());
                offlineOverflow = findOfflineOverflow(ocrResult.getDetail());
                DataItem offlineAlready = findOfflineAlready(ocrResult.getDetail());
                offlineTime = parseOfflineTime(offlineAlready);
                if (!StringUtils.hasLength(offlineTime)) {
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                // pass
                log.warn("err: {}", e.getMessage(), e);
                quit(context);
                return false;
            }
        } while (!StringUtils.hasLength(offlineTime));

        // 已满
        if (offlineOverflow != null) {
            claimRewards(context, rewardButton, "已满");
            return false;
        }

        Date now = new Date();
        String nowTime = DateUtils.format(now, "HH:mm:ss");
        if (TimeUtils.compare("23:50:00") >= 0) {
            // 23:50 分以后离线时间如果不超过 10 分钟就忽略了
            if (TimeUtils.compare(offlineTime, "10:00:00") <= 0) {
                return false;
            }

            // 23:50 分以后直接领取
            claimRewards(context, rewardButton, offlineTime);
            return false;
        }

        int compare = TimeUtils.compare(offlineTime, "8:00:05");
        if (compare < 0) {
            log.info("未满足领取条件");
            quit(context);
            return false;
        } else {
            log.info("满足领取条件");
            // 点击领取按钮
            claimRewards(context, rewardButton, offlineTime);
            quit(context);
            return false;
        }
    }

    private void claimRewards(SyncContext context, DataItem rewardButton, String offlineTime) {
        context.click(rewardButton.getBox().get(0), rewardButton.getBox().get(1));
        AdbController controller = (AdbController) instance.getController();
        chanifyService.send("领取离线奖励\n" + offlineTime + "\n" + DateUtil.formatDateTime(new Date()) + "\n" + JSONObject.toJSONString(controller.getAdbInfo()));
    }

    private void quit(SyncContext context) throws InterruptedException {
        Thread.sleep(500);

        context.click(360, 1200);
        context.click(360, 1200);
        context.click(360, 1200);
    }

    @Override
    public void stop() {

    }

    private DataItem findRewardButton(String ocrDetail) {
        List<DataItem> dataList = DataItem.toList(ocrDetail);

        // {"box":[182,953,103,31],"score":0.996841,"text":"挂机奖励"}

        return dataList.stream()
                .filter(item -> item.getBox().get(0) < 300)
                .filter(item -> item.getBox().get(1) > 900)
                .filter(item -> item.getText().contains("离线奖励")
                        || item.getText().contains("挂机奖励")
                        || item.getText().contains("领取"))
                .findFirst().orElse(null);
    }

    private DataItem findOfflineOverflow(String ocrDetail) {
        List<DataItem> dataList = DataItem.toList(ocrDetail);

        return dataList.stream()
                .filter(item -> item.getBox().get(0) > 100)
                .filter(item -> item.getBox().get(1) > 800)
                .filter(item -> item.getBox().get(1) < 900)
                .filter(item -> item.getText().contains("已满"))
                .findFirst().orElse(null);
    }

    private DataItem findOfflineAlready(String ocrDetail) {
        List<DataItem> dataList = DataItem.toList(ocrDetail);

        return dataList.stream()
                .filter(item -> item.getBox().get(0) > 100)
                .filter(item -> item.getBox().get(1) > 800)
                .filter(item -> item.getBox().get(1) < 900)
                .filter(item -> item.getText().contains("你已挂机")
                        || item.getText().contains(":")
                        || item.getText().contains("："))
                .findFirst().orElse(null);
    }

    private String parseOfflineTime(DataItem offlineAlready) {
        String text = offlineAlready.getText();
        text = text.replace("：", ":")
                .replace("你已挂机", "");

        return text;
    }

}
