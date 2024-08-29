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

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FishingAction extends CustomAction {

    private final ChanifyService chanifyService;
    private final Instance instance;


    @Override
    public boolean run(SyncContext context, String taskName, String customParam, Rect box, String recDetail) {
        log.info("====================================");
        log.info("starting offline action");
        log.info("====================================");

        log.info("taskName: {}", taskName);
        log.info("customParam: {}", customParam);
        log.info("box: {}", box);
        log.info("recDetail: {}", recDetail);

        AdbController controller = (AdbController) instance.getController();
        DataItem claimButton = findClaimButton(recDetail);
        context.click(claimButton.getBox().get(0), claimButton.getBox().get(1));
        chanifyService.send(String.join("\n", List.of("免费钓鱼", DateUtil.formatDateTime(new Date()), JSONObject.toJSONString(controller.getAdbInfo()))));

        return true;
    }

    @Override
    public void stop() {

    }

    private DataItem findClaimButton(String ocrDetail) {
        List<DataItem> dataList = DataItem.toList(ocrDetail);

        return dataList.stream()
                .filter(item -> item.getText().equals("免费"))
                .findFirst().orElse(null);
    }

}
