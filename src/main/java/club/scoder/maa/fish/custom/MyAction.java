package club.scoder.maa.fish.custom;

import io.github.hanhuoer.maa.core.custom.CustomAction;
import io.github.hanhuoer.maa.core.custom.SyncContext;
import io.github.hanhuoer.maa.model.Rect;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyAction extends CustomAction {

    @Override
    public boolean run(SyncContext context, String taskName, String customParam, Rect box, String recDetail) {
        log.info("MyAction run.");
        return true;
    }

    @Override
    public void stop() {

    }
}