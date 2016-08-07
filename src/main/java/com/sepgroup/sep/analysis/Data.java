package com.sepgroup.sep.analysis;

import com.sepgroup.sep.model.TaskModel;

/**
 * Created by Demo on 8/3/2016.
 */
public class Data {
    public TaskModel task;
    Data(){}
    Data(TaskModel t){
        task = t;
    }

    public float earliestStart;
    public float latestStart;
    public float earliestFinish;
    public float latestFinish;

    public float getFloat(){
        return latestStart - earliestStart;
    }

    public float getExpectedDuration() {
        final float a = task.getOptimisticTimeToFinish();
        final float b = task.getMostLikelyTimeToFinish();
        final float c = task.getPesimisticTimeToFinish();
        return (a + 4.0f * b + c) / 6.0f;
    }
}
