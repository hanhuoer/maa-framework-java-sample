package club.scoder.maa.fish.custom;

import club.scoder.maa.fish.model.DataItem;
import club.scoder.maa.fish.service.QAService;
import com.alibaba.fastjson2.JSONObject;
import io.github.hanhuoer.maa.core.custom.CustomAction;
import io.github.hanhuoer.maa.core.custom.SyncContext;
import io.github.hanhuoer.maa.model.Rect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class QACustomAction extends CustomAction {

    private final QAService qaService;

    @Override
    public boolean run(SyncContext syncContext, String name, String param, Rect rect, String detail) {
        try {
            log.info("[myAct] name: {}, param: {}, rect: {}, detail: {}", name, param, rect, detail);

            JSONObject detailJson = JSONObject.parseObject(detail);

            // 游戏问题
            List<DataItem> questionJsonList = detailJson.getJSONArray("all")
                    .stream().map(i -> (JSONObject) i).filter(item -> {
                        return (item).getJSONArray("box").getInteger(1) < 300;
                    }).filter(item -> {
                        return (item).getJSONArray("box").getInteger(1) > 100;
                    }).map(item -> item.toJavaObject(DataItem.class)).toList();
            // 游戏结果
            List<DataItem> questionResultList = detailJson.getJSONArray("all")
                    .stream().map(i -> (JSONObject) i).filter(item -> {
                        return (item).getJSONArray("box").getInteger(1) > 300;
                    }).map(item -> item.toJavaObject(DataItem.class)).toList();

            // 数据排序
//        questionJsonList.sort(Comparator.comparingInt((DataItem a) ->
//                a.getBox().get(1)).thenComparingInt(a -> a.getBox().get(0)));
//        questionResultList.sort(Comparator.comparingInt((DataItem a) ->
//                a.getBox().get(1)).thenComparingInt(a -> a.getBox().get(0)));

            questionJsonList = questionJsonList.stream().sorted((a, b) -> {
                int y1 = a.getBox().get(1);
                int y2 = b.getBox().get(1);
                if (y1 - y2 != 0) {
                    return y1 - y2;
                }
                int x1 = a.getBox().get(0);
                int x2 = b.getBox().get(0);
                return x1 - x2;
            }).toList();

            questionResultList = questionResultList.stream().sorted((a, b) -> {
                int y1 = a.getBox().get(1);
                int y2 = b.getBox().get(1);
                if (y1 - y2 != 0) {
                    return y1 - y2;
                }
                int x1 = a.getBox().get(0);
                int x2 = b.getBox().get(0);
                return x1 - x2;
            }).toList();


            // 组合字符串
            String questionTarget = questionJsonList.stream().map(DataItem::getText).collect(Collectors.joining());
            String gameTarget = questionResultList.stream().map(DataItem::getText).collect(Collectors.joining());

            log.info("[custom myAct] origin question target: {}", questionTarget);
            log.info("[custom myAct] origin game target: {}", gameTarget);

            if (gameTarget.contains("胜利")
                    || gameTarget.contains("失败")
                    || gameTarget.contains("答对")
                    || gameTarget.contains("淘汰")) {
                log.info("识别到游戏结束");
                return syncContext.runTask("判断是否答题结束");
            }
            if (StringUtils.isBlank(questionTarget)) {
                log.info("question target is none");
                return true;
            }
            if (questionTarget.contains("淘汰赛") || questionTarget.contains("大冲关")) {
                log.info("question target is 开始倒计时");
                return true;
            }

            String match = qaService.match(questionTarget);
            log.info("[custom myAct] question: {}, answer: {}", questionTarget, match);
            if (match == null) {
                log.info("匹配到的答案为空 null");
                return true;
            } else if (match.contains("对")) {
                log.info("点击正确按钮");
                return syncContext.runTask("答题_点击正确按钮", "{}");
            } else if (match.contains("错")) {
                log.info("点击错误按钮");
                return syncContext.runTask("答题_点击错误按钮", "{}");
            }
            log.info("答案是其他 不是对 也不是错");
            return false;
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
//            return false;
            return true;
//            throw new RuntimeException("");
        }
    }

    @Override
    public void stop() {

    }

}
