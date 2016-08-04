package com.sepgroup.sep.analysis.GraphDisplay;

import com.sepgroup.sep.analysis.Graph;

import java.awt.*;

/**
 * Created by HP on 8/3/2016.
 */
public class PhysicsGraph extends Graph implements Physics,Drawable {

    public PhysicsGraph(int projectID){
        super(projectID);
    }
    public void step(){}
    public void draw(Graphics2D g){}

}
