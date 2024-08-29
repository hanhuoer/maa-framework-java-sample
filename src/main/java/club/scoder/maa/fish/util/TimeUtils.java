package club.scoder.maa.fish.util;

import com.alibaba.fastjson2.util.DateUtils;

import java.util.Date;

public class TimeUtils {

    /**
     * 比较 a 与 b 的时间
     *
     * @param a 时:分:秒
     * @param b 时:分:秒
     * @return a 与 b 相等则返回 0, 小于返回 -1, 大于返回 1
     */
    public static int compare(String a, String b) {
        checkTime(a);
        checkTime(b);

        String[] aSplit = a.split(":");
        String[] bSplit = b.split(":");

        // h -> m -> s
        for (int i = 0; i < 3; i++) {
            String aItem = aSplit[i];
            String bItem = bSplit[i];
            int aInt = Integer.parseInt(aItem);
            int bInt = Integer.parseInt(bItem);
            if (aInt > bInt) {
                return 1;
            } else if (aInt < bInt) {
                return -1;
            }
        }

        return 0;
    }

    /**
     * @param time 时:分:秒
     */
    public static void checkTime(String time) {
        if (time == null || time.isEmpty()) {
            throw new IllegalArgumentException("time can not be empty");
        }
        String[] split = time.split(":");
        if (split.length != 3) {
            throw new IllegalArgumentException("please check the time format");
        }

        // check the format
        for (int i = 0; i < split.length; i++) {
            String s = split[i];

            if (s.isEmpty()) {
                throw new IllegalArgumentException("please check the time format");
            }

            if (i > 0) {
                // min sec
                if (s.length() > 2) {
                    throw new IllegalArgumentException("please check the time format");
                }
            }

            char[] charArray = s.toCharArray();
            for (char c : charArray) {
                // must be digit
                if (c < 48 || c > 57) {
                    throw new IllegalArgumentException("please check the time format");
                }
            }
        }
    }

    public static int compare(String time) {
        return compare(DateUtils.format(new Date(), "HH:mm:ss"), time);
    }

    public static void main(String[] args) {

        System.out.println(DateUtils.format(new Date(), "HH:mm:ss"));

        checkTime("00:00:00");
        System.out.println("1");
        checkTime("00:00:0");
        System.out.println("2");
        checkTime("00:0:0");
        System.out.println("3");
        checkTime("100:0:0");
        System.out.println("4");
//        checkTime("100:h:0");
//        System.out.println("5");

        System.out.println(compare("00:00:00", "00:00:00"));
        System.out.println(compare("00:00:00", "00:00:01"));
        System.out.println(compare("00:00:02", "00:00:01"));
        System.out.println(compare("01:00:01", "00:00:01"));
    }

}
