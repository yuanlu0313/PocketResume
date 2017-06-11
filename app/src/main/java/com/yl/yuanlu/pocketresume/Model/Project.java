package com.yl.yuanlu.pocketresume.Model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by LUYUAN on 6/4/2017.
 */

public class Project {

    public String id;
    public String projectName;
    public String description;
    public Date startDate;
    public Date endDate;
    public List<String> details;

    public Project() {
        id = UUID.randomUUID().toString();
    }

}
