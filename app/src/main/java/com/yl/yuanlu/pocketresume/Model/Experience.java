package com.yl.yuanlu.pocketresume.Model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by LUYUAN on 6/4/2017.
 */

public class Experience {

    public String id;
    public String company;
    public String title;
    public Date startDate;
    public Date endDate;
    public List<String> tasks;

    public Experience() {
        id = UUID.randomUUID().toString();
    }

}
