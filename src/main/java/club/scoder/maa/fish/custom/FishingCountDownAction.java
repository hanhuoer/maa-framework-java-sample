package club.scoder.maa.fish.custom;

import club.scoder.maa.fish.service.ChanifyService;
import io.github.hanhuoer.maa.core.custom.CustomAction;
import io.github.hanhuoer.maa.core.custom.SyncContext;
import io.github.hanhuoer.maa.model.Rect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FishingCountDownAction extends CustomAction {

    private final ChanifyService chanifyService;


    /**
     * <pre>
     * ============================
     * taskName: 钓鱼_检测到免费普通捕获倒计时
     * customParam: {"my_act_key":"my_act_value"}
     * box: Rect(x=109, y=1109, w=166, h=27)
     * recDetail: {"all":[{"box":[109,1109,166,27],"score":0.995300,"text":"02:49:17后免费"},{"box":[134,991,137,37],"score":0.990605,"text":"普通捕获"},{"box":[205,939,28,17],"score":0.688089,"text":"X1"}],"best":{"box":[109,1109,166,27],"score":0.995300,"text":"02:49:17后免费"},"filtered":[{"box":[109,1109,166,27],"score":0.995300,"text":"02:49:17后免费"}]}
     * ============================
     * </pre>
     */
    @Override
    public boolean run(SyncContext context, String taskName, String customParam, Rect box, String recDetail) {
        log.info("============================");

        log.info("taskName: {}", taskName);
        log.info("customParam: {}", customParam);
        log.info("box: {}", box);
        log.info("recDetail: {}", recDetail);

        log.info("============================");
        return true;
    }

    @Override
    public void stop() {

    }

}
