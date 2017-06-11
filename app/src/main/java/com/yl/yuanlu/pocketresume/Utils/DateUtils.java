package com.yl.yuanlu.pocketresume.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LUYUAN on 5/29/2017.
 */

public class DateUtils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
    private static SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat sdf_month = new SimpleDateFormat("MM");

    public static String dateToString(Date date) {
        return sdf.format(date);
    }

    public static String getYear(Date date) {
        return sdf_year.format(date);
    }

    public static String getMonth(Date date) {
        return sdf_month.format(date);
    }

}
