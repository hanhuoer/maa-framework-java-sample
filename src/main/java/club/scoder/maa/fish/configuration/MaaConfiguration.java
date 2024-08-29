package club.scoder.maa.fish.configuration;

import club.scoder.maa.fish.custom.*;
import club.scoder.maa.fish.service.ChanifyService;
import club.scoder.maa.fish.service.QAService;
import io.github.hanhuoer.maa.Maa;
import io.github.hanhuoer.maa.MaaOptions;
import io.github.hanhuoer.maa.consts.MaaLoggingLevelEunm;
import io.github.hanhuoer.maa.core.AdbController;
import io.github.hanhuoer.maa.core.base.Instance;
import io.github.hanhuoer.maa.core.base.Resource;
import io.github.hanhuoer.maa.core.custom.CustomRecognizer;
import io.github.hanhuoer.maa.core.custom.SyncContext;
import io.github.hanhuoer.maa.model.AdbInfo;
import io.github.hanhuoer.maa.model.CustomRecognizerResult;
import io.github.hanhuoer.maa.util.FileUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class MaaConfiguration implements InitializingBean {

    private final List<Instance> instanceList = new ArrayList<>();
    private final QAService qaService;
    private final ChanifyService chanifyService;
    private Maa maa;

    @Override
    public void afterPropertiesSet() throws Exception {
        MaaOptions options = new MaaOptions();
        options.setStdoutLevel(MaaLoggingLevelEunm.OFF);
        this.maa = Maa.create(options);
        log.info("maa created.");

        startFirst();
//        startMultiDevice();
    }

    private void startMultiDevice() {
        AdbInfo adb1 = new AdbInfo().setName("MuMuPlayer12").setPath("J:/Program Files/Netease/MuMu Player 12/shell/adb.exe").setSerial("127.0.0.1:16448").setControllerType(16645886).setConfig("{\"extras\":{\"mumu\":{\"enable\":true,\"index\":2,\"path\":\"J:/Program Files/Netease/MuMu Player 12\"}}}");

        AdbInfo adb2 = new AdbInfo().setName("MuMuPlayer12").setPath("J:/Program Files/Netease/MuMu Player 12/shell/adb.exe").setSerial("127.0.0.1:16480").setControllerType(16645886).setConfig("{\"extras\":{\"mumu\":{\"enable\":true,\"index\":3,\"path\":\"J:/Program Files/Netease/MuMu Player 12\"}}}");

        handleDevice(adb1);
        handleDevice(adb2);

    }

    private void startFirst() {
        List<AdbInfo> adbInfos = AdbController.find();
        if (adbInfos.isEmpty()) {
            return;
        }

        handleDevice(adbInfos.get(0));
    }

    private void handleDevice(AdbInfo adbInfo) {
        AdbController adbController = new AdbController(adbInfo);
        adbController.connect();
        if (!adbController.connected()) {
            log.info("field connected.");
            return;
        }
        Resource resource = new Resource();
        resource.load(FileUtils.joinUserDir("resources", "resource").getAbsolutePath());
        Instance instance = new Instance();

        instanceList.add(instance);

        boolean bind = instance.bind(resource, adbController);
        log.info("bind result: {}", bind);

        boolean inited;
        do {
            inited = instance.inited();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("initial result: {}", inited);
        } while (!inited);

        setAction(instance);
        setRecognizer(instance);
    }

    private void setRecognizer(Instance instance) {
        log.info("[set recognizer] begin.");

        instance.registerRecognizer("myReco", new CustomRecognizer() {
            @Override
            public CustomRecognizerResult analyze(SyncContext syncContext, BufferedImage bufferedImage, String s, String s1) {
                return null;
            }
        });

        log.info("[set recognizer] end.");
    }

    private void setAction(Instance instance) {
        log.info("[set action] begin.");

        // 答题
        boolean qaActionRegisterResult = instance.registerAction("myAct", new QACustomAction(qaService));
        log.info("[set action] qa: result: {}", qaActionRegisterResult);

        // 钓鱼
        boolean fishingCountDownActionResult = instance.registerAction("FishingCountDownAction", new FishingCountDownAction(chanifyService));
        log.info("[set action] fishing count down: result: {}", fishingCountDownActionResult);
        boolean fishingActionResult = instance.registerAction("FishingAction", new FishingAction(chanifyService, instance));
        log.info("[set action] fishing: result: {}", fishingActionResult);

        // 钓鱼
        boolean offlineActionResult = instance.registerAction("OfflineAction", new OfflineAction(chanifyService, instance));
        log.info("[set action] offline: result: {}", offlineActionResult);

        // 通知
        boolean notifyAction = instance.registerAction("NotifyAction", new NotifyAction(chanifyService));
        log.info("[set action] notify: result: {}", notifyAction);

        // 通知
        boolean assetsReportAction = instance.registerAction("AssetsReportAction", new AssetsReportAction(chanifyService, instance));
        log.info("[set action] assets report: result: {}", assetsReportAction);

        log.info("[set action] end.");
    }


    public Instance getInstance() {
        return instanceList.get(0);
    }
}
