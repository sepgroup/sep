package com.sepgroup.sep.analysis;

import com.sepgroup.sep.model.TaskModel;

/**
 * Created by Demo on 8/3/2016.
 */
public class Data extends TaskModel{
    public TaskModel task;
    float earliestStart;
    float earliestFinish;
    float latestStart;
    float latestFinish;
    Data(){}

    Data(TaskModel t){
        task = t;
    }
}
