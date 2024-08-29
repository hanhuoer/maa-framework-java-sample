package club.scoder.maa.fish.custom;

import club.scoder.maa.fish.model.DataItem;
import io.github.hanhuoer.maa.core.custom.CustomAction;
import io.github.hanhuoer.maa.core.custom.SyncContext;
import io.github.hanhuoer.maa.model.RecognitionResult;
import io.github.hanhuoer.maa.model.Rect;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
@Deprecated
@Slf4j
public class RewardAction extends CustomAction {

    public static final List<String> rewardList = List.of("金砖", "金币", "精铁");

    // Function to find the center of a box
    public static Point getBoxCenter(List<Integer> box) {
        return getBoxCenter(box.stream().mapToInt(i -> i).toArray());
    }

    public static Point getBoxCenter(int[] box) {
        int x = box[0];
        int y = box[1];
        int width = box[2];
        int height = box[3];
        return new Point(x + width / 2, y + height / 2);
    }

    // Function to find the nearest value box to a given reward name box
    public static DataItem findNearestValueBox(DataItem nameBox, List<DataItem> boxes) {
        Point nameCenter = getBoxCenter(nameBox.getBox());
        DataItem nearestValueBox = null;
        double minDistance = Double.MAX_VALUE;

        for (DataItem box : boxes) {
            if (!rewardList.contains(box.getText())) {
                Point valueCenter = getBoxCenter(box.getBox());

                // Ensure the value box is to the right of the name box and on the same horizontal level (within some tolerance)
                if (valueCenter.x > nameCenter.x && Math.abs(valueCenter.y - nameCenter.y) < 50) {
                    double distance = valueCenter.x - nameCenter.x;

                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestValueBox = box;
                    }
                }
            }
        }

        return nearestValueBox;
    }

    // Function to pair rewards with their values
    public static List<Reward> pairRewardsWithValues(List<DataItem> ocrItem) {
        List<Reward> pairs = new ArrayList<>();

        for (DataItem obj : ocrItem) {
            if (rewardList.contains(obj.getText())) {
                DataItem valueBox = findNearestValueBox(obj, ocrItem);

                if (valueBox != null) {
                    pairs.add(new Reward(obj.getText(), valueBox.getText()));
                }
            }
        }

        return pairs;
    }

    @Override
    public boolean run(SyncContext context, String taskName, String customParam, Rect box, String recDetail) {
        try {
            BufferedImage screencap = context.screencap();
            RecognitionResult ocrResult = context.runRecognition(screencap, "OCR");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return false;
    }

    @Override
    public void stop() {

    }

    private Object parseRewards(List<DataItem> ocrItem) {
        return pairRewardsWithValues(ocrItem);
    }

    @Data
    public static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Data
    public static class Reward {
        private String name;
        private String value;

        public Reward(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

}
