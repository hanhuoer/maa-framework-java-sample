package club.scoder.maa.fish.custom;

import club.scoder.maa.fish.model.DataItem;
import club.scoder.maa.fish.service.ChanifyService;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import io.github.hanhuoer.maa.core.AdbController;
import io.github.hanhuoer.maa.core.base.Instance;
import io.github.hanhuoer.maa.core.custom.CustomAction;
import io.github.hanhuoer.maa.core.custom.SyncContext;
import io.github.hanhuoer.maa.model.Rect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AssetsReportAction extends CustomAction {

    private final ChanifyService chanifyService;
    private final Instance instance;
    private Date lastReportDate;


    @Override
    public boolean run(SyncContext context, String taskName, String customParam, Rect box, String recDetail) {
        Date now = new Date();

        int hour = DateUtil.hour(now, true);
        int minute = DateUtil.minute(now);
        if (hour != 23) {
            return true;
        }
        if (minute < 30) {
            return true;
        }
        if (lastReportDate != null) {
            if (DateUtil.isSameDay(now, lastReportDate)) {
                return true;
            }
        }

        log.info("====================================");
        log.info("starting assets report action");
        log.info("====================================");


        log.info("taskName: {}", taskName);
        log.info("customParam: {}", customParam);
        log.info("box: {}", box);
        log.info("recDetail: {}", recDetail);

        AdbController controller = (AdbController) instance.getController();
        String report = parseReport(recDetail);
        chanifyService.send(
                String.join("\n",
                        List.of("咸鱼简报", DateUtil.formatDateTime(now),
                                report,
                                JSONObject.toJSONString(controller.getAdbInfo())))
        );

        lastReportDate = now;

        return true;
    }

    @Override
    public void stop() {

    }

    private String parseReport(String ocrDetail) {
        List<DataItem> dataList = DataItem.toList(ocrDetail);

        List<String> reportList = new ArrayList<>();
        for (DataItem dataItem : dataList) {
            String text = dataItem.getText();
            if (StringUtils.isEmpty(text)) continue;

            if (text.contains("木质宝箱")
                    || text.contains("青铜宝箱")
                    || text.contains("黄金宝箱")
                    || text.contains("铂金宝箱")
                    || text.contains("钻石宝箱")
                    || text.contains("招募令")
                    || text.contains("金砖")
                    || text.contains("白玉")
                    || text.contains("黄金鱼竿")
                    || text.contains("普通鱼竿")) {
                reportList.add(text);
            }
        }

        return String.join("\n", reportList);
    }

}
