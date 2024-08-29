package club.scoder.maa.fish.model;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class DataItem {

    private List<Integer> box;
    private Integer score;
    private String text;

    public static List<DataItem> toList(String ocrDetail) {
        JSONArray all = JSONObject.parseObject(ocrDetail).getJSONArray("all");
        return toList(all);
    }

    public static List<DataItem> toList(JSONArray allArr) {
        return allArr.toJavaList(DataItem.class);
    }

}
