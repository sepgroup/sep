package com.sepgroup.sep.analysis;

import com.sepgroup.sep.model.TaskModel;

/**
 * Created by Demo on 8/3/2016.
 */
public class Data {
    TaskModel task;
    Data(){}
    Data(TaskModel t){
        task = t;
    }

    public float earliestStart;
    public float latestStart;
    public float earliestFinish;
    public float latestFinish;
}
