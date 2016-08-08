package com.sepgroup.sep.controller;

import com.sepgroup.sep.Main;
import com.sepgroup.sep.analysis.Node;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.analysis.*;
import com.sepgroup.sep.analysis.GraphDisplay.*;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.shape.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;

import java.util.ArrayList;

/**
 * Created by Andres Gonzalez on 2016-08-07.
 */
public class GraphViewController extends AbstractController{

    class CustomArrow extends Line {
        Polygon tip;

        protected CustomArrow(double x1, double y1, double x2, double y2) {
            super(x1, y1, x2, y2);
            recalculateTip();
        }

        public void updateOrigin(int x1, int y1)
        {
            this.setStartX(x1);
            this.setStartY(y1);
        }

        public void updateTip(int x2, int y2)
        {
            this.setEndX(x2);
            this.setEndY(y2);

            recalculateTip();
        }

        private void recalculateTip()
        {
            double distanceFromOrigin = Math.hypot(getEndX() - getStartX(), getEndY() - getStartY()) - 10;
            double angle = Math.atan2(getEndY() - getStartY(), getEndX() - getStartX());
            double trianglePosX = getStartX() + distanceFromOrigin * Math.cos(angle);
            double trianglePosY = getStartY() + distanceFromOrigin * Math.sin(angle);

            tip = new Polygon();
            tip.getPoints().addAll(new Double[]{trianglePosX + 10 * Math.cos(angle), trianglePosY + 10 * Math.sin(angle),
                    trianglePosX - 5 * Math.sin(angle), trianglePosY + 5 * Math.cos(angle),
                    trianglePosX + 5 * Math.sin(angle), trianglePosY - 5 * Math.cos(angle)});
        }
    }

    class NodeButton extends Button {
        ArrayList<CustomArrow> inNodes = new ArrayList<CustomArrow>();
        ArrayList<CustomArrow> outNodes = new ArrayList<CustomArrow>();

        protected NodeButton(String name)
        {
            super(name);
        }

        protected void addInNode(CustomArrow inNode)
        {
            inNodes.add(inNode);
        }

        protected void addOutNode(CustomArrow outNode)
        {
            outNodes.add(outNode);
        }

        protected void removeNode(javafx.scene.shape.Shape node)
        {
            inNodes.remove(node);
            outNodes.remove(node);
        }

        protected void updateRelations()
        {
            int outX = (int)(this.getLayoutX() + this.getMinWidth());
            int outY = (int)(this.getLayoutY() + this.getMinHeight() / 2);
            int inX = (int)(this.getLayoutX());
            int inY = (int)(this.getLayoutY() + this.getMinHeight() / 2);

            for(CustomArrow origin: outNodes)
                origin.updateOrigin(outX, outY);

            for(CustomArrow tip: inNodes)
                tip.updateTip(inX, inY);
        }

        protected void setDimensions(double x, double y)
        {
            setMinWidth(x);
            setMinHeight(y);
            setMaxWidth(x);
            setMaxHeight(y);
        }
    }


    private static Logger logger = LoggerFactory.getLogger(TaskViewerController.class);
    private static final String fxmlPath = "/views/graphview.fxml";
    private ProjectModel project;
    private double deltaX, deltaY;

    @FXML
    public Pane graphArea;

    public GraphViewController() {
        setCssPath("/style/stylesheet.css");
    }

    public static String getFxmlPath() {
        return fxmlPath;
    }

    public void setReturnProject(ProjectModel p) {
        project = p;
        update();
    }

    @FXML
    public void onBackButtonClicked()
    {
        // Return to project viewer
        ProjectViewerController pvc = (ProjectViewerController) Main.setPrimaryScene(ProjectViewerController.getFxmlPath());
        pvc.setModel(project);
    }

    public void update() {
        try {
            PhysicsGraphController pgc = new PhysicsGraphController(false, project.getProjectId());
            pgc.positionNodes();

            PERTAnalysisTools.getCriticalPath(pgc.getGraph(), pgc.getGraph().getTerminal());

            //System.out.print(CriticalPath.computeCriticalPaths(pgc.getGraph()));

            double relativeWidth = 0.5 / pgc.getGraph().getTerminal().getDepth();
            double relativeHeight = 0.1;
            for (Node n : pgc.getGraph().nodes) {
                String assignee = "", taskIdString = "";
                if(n.getData().task.getAssignee() != null && n.getData().task.getAssignee().getFullName() != null)
                    assignee = "\n" + n.getData().task.getAssignee().getFullName();
                if(n.getData().task.getTaskId() > 0)
                    taskIdString = n.getData().task.getTaskId() + ": ";
                graphArea.getChildren().add(new NodeButton(taskIdString + n.getData().task.getName() + assignee));
                NodeButton button = (NodeButton) graphArea.getChildren().get(graphArea.getChildren().size() - 1);
                button.setLayoutX(graphArea.getPrefWidth() * (1 - relativeWidth) * ((PhysicsNode) n).getRelX());
                button.setLayoutY(graphArea.getPrefHeight() * (1 - relativeHeight) * ((PhysicsNode) n).getRelY());
                button.setDimensions(relativeWidth * graphArea.getPrefWidth(), relativeHeight * graphArea.getPrefHeight());
                button.setContentDisplay(ContentDisplay.TOP);

                if (((PhysicsNode) n).getCritical())
                    button.getStyleClass().add("critical-path-button");
                else
                    button.getStyleClass().add("graph-button");

                n.myButton = button;

                for (Node outNode : n.getOutNodes()) {
                    CustomArrow newLine = new CustomArrow(
                            button.getLayoutX() + button.getMinWidth(),
                            button.getLayoutY() + button.getMinHeight() / 2,
                            graphArea.getPrefWidth() * (1 - relativeWidth) * ((PhysicsNode) outNode).getRelX(),
                            graphArea.getPrefHeight() * (1 - relativeHeight) * ((PhysicsNode) outNode).getRelY() + button.getMinHeight() / 2);

                    graphArea.getChildren().add(newLine);
                    button.addOutNode(newLine);
                    n.outArrows.add(newLine);
                    outNode.inArrows.add(newLine);

                    newLine.getStyleClass().add("line");

                    if (((PhysicsNode) n).getCritical() && ((PhysicsNode) outNode).getCritical()) {
                        newLine.getStyleClass().add("critical-path-line");
                    }
                }

                button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        if(event.getClickCount() == 2) {
                            TaskViewerController tvc = (TaskViewerController) Main.setPrimaryScene(TaskViewerController.getFxmlPath());
                            tvc.setModel(n.getData().task);
                            tvc.setReturnProject(project);
                        }
                    }
                });

                button.setOnMousePressed(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        deltaX = event.getX();
                        deltaY = event.getY();
                    }
                });

                button.setOnMouseDragged(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent mouseEvent) {
                        button.setLayoutX(mouseEvent.getSceneX() - graphArea.getLayoutX() - deltaX);
                        button.setLayoutY(mouseEvent.getSceneY() - graphArea.getLayoutY() - deltaY);
                        button.updateRelations();
                    }
                });
            }

            for (Node n : pgc.getGraph().nodes) {
                ArrayList<Line> inArrows = n.inArrows;
                for(Node inNode: n.getInNodes())
                {
                    ArrayList<Line> outArrows = inNode.outArrows;
                    breakOut:
                    for(Line inArrow: inArrows)
                        for(Line outArrow: outArrows)
                            if(inArrow == outArrow)
                            {
                                ((NodeButton) n.myButton).addInNode((CustomArrow)outArrow);
                                break breakOut;
                            }
                }
            }
        }
        catch(Exception e)
        {
            System.err.print(e);
            onBackButtonClicked();
        }
    }
}
