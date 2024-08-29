package club.scoder.maa.fish.custom;

import club.scoder.maa.fish.service.ChanifyService;
import io.github.hanhuoer.maa.core.custom.CustomAction;
import io.github.hanhuoer.maa.core.custom.SyncContext;
import io.github.hanhuoer.maa.model.Rect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class NotifyAction extends CustomAction {

    private final ChanifyService chanifyService;


    @Override
    public boolean run(SyncContext context, String taskName, String customParam, Rect box, String recDetail) {
        chanifyService.send(customParam);
        return false;
    }

    @Override
    public void stop() {

    }
}
