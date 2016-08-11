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
import javafx.scene.control.RadioButton;
import javafx.scene.control.DatePicker;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.ZoomEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;

import java.time.LocalDate;
import java.util.Date;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * Created by Andres Gonzalez on 2016-08-07.
 */
public class GraphViewController extends AbstractController
{

    private class CustomArrow extends Line {
        Polygon tip = new Polygon();

        protected CustomArrow(double x1, double y1, double x2, double y2) {
            super(x1, y1, x2, y2);
            graphArea.getChildren().add(this);
            graphArea.getChildren().add(tip);
            recalculateTip();
            getStyleClass().add("line");
        }

        public void updateOrigin(int x1, int y1)
        {
            this.setStartX(x1);
            this.setStartY(y1);

            recalculateTip();
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

            tip.getPoints().clear();

            tip.getPoints().addAll(new Double[]{trianglePosX + 10 * Math.cos(angle), trianglePosY + 10 * Math.sin(angle),
                    trianglePosX - 5 * Math.sin(angle), trianglePosY + 5 * Math.cos(angle),
                    trianglePosX + 5 * Math.sin(angle), trianglePosY - 5 * Math.cos(angle)});
        }
    }

    private class NodeButton extends Button {
        ArrayList<CustomArrow> inNodes = new ArrayList<CustomArrow>();
        ArrayList<CustomArrow> outNodes = new ArrayList<CustomArrow>();
        Node taskNode;

        protected NodeButton(Node taskNode, double posX, double posY, double width, double height)
        {
            super(taskNode.getData().task.getName());
            graphArea.getChildren().add(this);
            taskNode.myButton = this;
            setLayoutX(posX);
            setLayoutY(posY);
            setDimensions(width, height);
            this.taskNode = taskNode;

            setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        TaskViewerController tvc = (TaskViewerController) Main.setPrimaryScene(TaskViewerController.getFxmlPath());
                        tvc.setModel(taskNode.getData().task);
                        tvc.setReturnProject(project);
                    }
                }
            });

            Button thisButton = this;
            setOnMousePressed(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    deltaX = event.getX();
                    deltaY = event.getY();

                    if(selectedTask != null) {
                        selectedTask.getStyleClass().remove("selected-button");
                    }

                    selectedTask = thisButton;
                    getStyleClass().add("selected-button");

                    if(cPView.isSelected())
                        onCPSelected();
                }
            });

            setOnMouseDragged(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent mouseEvent) {
                    setLayoutX(mouseEvent.getSceneX() - graphArea.getLayoutX() - deltaX);
                    setLayoutY(mouseEvent.getSceneY() - graphArea.getLayoutY() - deltaY);
                    updateRelations();
                }
            });
        }

        protected Node getTaskNode()
        {
            return taskNode;
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
    private Graph graph;
    private double deltaX, deltaY;
    private Button selectedTask;
    private Scene scene;

    @FXML
    public Pane graphArea;
    @FXML
    public RadioButton normalView;
    @FXML
    public RadioButton cPView;
    @FXML
    public RadioButton pertView;
    @FXML
    public DatePicker pertDate;
    @FXML
    public Pane progressLegend;
    @FXML
    public Pane cPLegend;
    @FXML
    public Pane pertLegend;
    @FXML
    public ParallelCamera camera;

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

    @FXML
    public void onPaneClicked()
    {
        if(selectedTask != null) {
            selectedTask.getStyleClass().remove("selected-button");
            selectedTask = null;
        }
    }

    @FXML
    public void onZoom(ZoomEvent e)
    {
        double zoomFactor = e.getZoomFactor();
        System.out.println("CHINCHILLA");
    }

    @FXML
    public void onDateChanged()
    {
        if(pertView.isSelected())
        {
            try {
                PERTItAll();
            }
            catch (NullPointerException e) {
                pertView.setSelected(false);
                DialogCreator.showErrorDialog("Project Start Date Not Set", "This project's start date has not been set. P.E.R.T." +
                        " analysis cannot be performed without it.");
                return;
            }
            catch (Exception e) {
                pertView.setSelected(false);
                DialogCreator.showExceptionDialog(e);
                return;
            }
        }
    }

    @FXML
    public void onNormalSelected() {
        if(!normalView.isSelected())
        {
            normalView.setSelected(true);
            return;
        }
        
        boolean wasCPSelected = cPView.isSelected();
        boolean wasPERTSelected = pertView.isSelected();

        cPView.setSelected(false);
        pertView.setSelected(false);

        cPLegend.setVisible(false);
        pertLegend.setVisible(false);
        progressLegend.setVisible(true);

        try {
            for (Node n : graph.nodes) {
                n.myButton.getStyleClass().clear();
                n.myButton.getStyleClass().add("button");

                LocalDate date = project.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                boolean supposedToHaveStarted = LocalDate.now().isAfter(date);

                if (n.getData().task.isDone() || (n == graph.getRoot() && supposedToHaveStarted))
                    n.myButton.getStyleClass().add("done-button");
                else {
                    boolean inProgress = supposedToHaveStarted;

                    if (supposedToHaveStarted)
                        for (Node inNode : n.getInNodes())
                            if (!inNode.getData().task.isDone() && inNode != graph.getRoot()) {
                                inProgress = false;
                                break;
                            }

                    if (inProgress) {
                        if (n.getData().task.shouldBeDone())
                            n.myButton.getStyleClass().add("critical-path-button");
                        else
                            n.myButton.getStyleClass().add("meh-button");
                    }
                    else
                        n.myButton.getStyleClass().add("graph-button");
                }

                if(n.myButton == selectedTask)
                    n.myButton.getStyleClass().add("selected-button");

                n.myButton.setStyle("");
            }
        }
        catch(Exception e)
        {
            normalView.setSelected(false);
            progressLegend.setVisible(false);
            cPView.setSelected(wasCPSelected);
            pertView.setSelected(wasPERTSelected);
            cPLegend.setVisible(wasCPSelected);
            pertLegend.setVisible(wasPERTSelected);
            DialogCreator.showExceptionDialog(e);
        }
    }

    @FXML
    public void onCPSelected() {
        if(!cPView.isSelected())
        {
            cPView.setSelected(true);
            return;
        }

        boolean wasProgressSelected = normalView.isSelected();
        boolean wasPERTSelected = pertView.isSelected();

        normalView.setSelected(false);
        pertView.setSelected(false);

        progressLegend.setVisible(false);
        pertLegend.setVisible(false);
        cPLegend.setVisible(true);

        try {
            PERTAnalysisTools.setPasses(graph);
            if(selectedTask == null)
                PERTAnalysisTools.getCriticalPath(graph, graph.getTerminal());
            else
                PERTAnalysisTools.getCriticalPath(graph, ((NodeButton)selectedTask).getTaskNode());

            for (Node n : graph.nodes) {
                n.myButton.getStyleClass().clear();
                n.myButton.getStyleClass().add("button");

                if (n.getCritical())
                    n.myButton.getStyleClass().add("critical-path-button");
                else
                    n.myButton.getStyleClass().add("graph-button");

                if(n.myButton == selectedTask)
                    n.myButton.getStyleClass().add("selected-button");

                n.myButton.setStyle("");
            }
        }
        catch(Exception e)
        {
            cPView.setSelected(false);
            cPLegend.setVisible(false);
            normalView.setSelected(wasProgressSelected);
            pertView.setSelected(wasPERTSelected);
            progressLegend.setVisible(wasProgressSelected);
            pertLegend.setVisible(wasPERTSelected);
            DialogCreator.showExceptionDialog(e);
        }
    }

    @FXML
    public void onPERTSelected() {
        if(!pertView.isSelected())
        {
            pertView.setSelected(true);
            return;
        }

        boolean wasProgressSelected = normalView.isSelected();
        boolean wasCPSelected = cPView.isSelected();

        normalView.setSelected(false);
        cPView.setSelected(false);

        progressLegend.setVisible(false);
        cPLegend.setVisible(false);
        pertLegend.setVisible(true);

        try {
            PERTItAll();
        }
        catch (NullPointerException e) {
            pertView.setSelected(false);
            pertLegend.setVisible(false);
            normalView.setSelected(wasProgressSelected);
            cPView.setSelected(wasCPSelected);
            progressLegend.setVisible(wasProgressSelected);
            cPLegend.setVisible(wasCPSelected);

            DialogCreator.showErrorDialog("Project Start Date Not Set", "This project's start date has not been set. P.E.R.T." +
                    " analysis cannot be performed without it.");
            return;
        }
        catch (Exception e) {
            pertView.setSelected(false);
            pertLegend.setVisible(false);
            normalView.setSelected(wasProgressSelected);
            cPView.setSelected(wasCPSelected);
            progressLegend.setVisible(wasProgressSelected);
            cPLegend.setVisible(wasCPSelected);
            DialogCreator.showExceptionDialog(e);
            return;
        }
    }

    private void PERTItAll() throws Exception
    {
        LocalDate projectStartDate = null;
        LocalDate expectedFinishingDate = null;

        projectStartDate = project.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        expectedFinishingDate = pertDate.getValue();
        Period diff = Period.between(projectStartDate, expectedFinishingDate);
        double dayDifference = diff.getDays() + diff.getMonths() * 30 + diff.getYears() * 365;

        PERTAnalysisTools.setPasses(graph);

        for(Node n: graph.nodes) {
            ArrayList<ArrayList<Node>> criticalPaths = PERTAnalysisTools.getCriticalPath(graph, n);
            double prob = PERTAnalysisTools.calculateProbability(criticalPaths, n, (int)dayDifference);

            n.myButton.getStyleClass().clear();
            n.myButton.getStyleClass().add("button");
            if(n.myButton == selectedTask)
                n.myButton.getStyleClass().add("selected-button");

            int r;
            int g;
            String redGreen;
            if(prob < 0.5)
            {
                r = 255;
                g = (int)(210 * prob + 150);
            }
            else
            {
                g = 255;
                r = (int)(210 * (1 - prob) + 150);
            }

            redGreen = String.valueOf(r) + ", " + String.valueOf(g);

            n.myButton.setStyle("-fx-background-color: rgb(" + redGreen + ", 150); ");
        }
    }

    public void update() {
        try {
            ProjectModel.tempTasks = null;
            PhysicsGraphController pgc = new PhysicsGraphController(false, project.getProjectId());
            pgc.positionNodes();

            this.graph = pgc.getGraph();

            double relativeWidth = 0.5 / pgc.getGraph().getTerminal().getDepth();
            double relativeHeight = 0.1;
            for (Node n : pgc.getGraph().nodes) {
                String assignee = "", taskIdString = "";
                if(n.getData().task.getAssignee() != null && n.getData().task.getAssignee().getFullName() != null)
                    assignee = "\r" + n.getData().task.getAssignee().getFullName();
                if(n.getData().task.getTaskId() > 0)
                    taskIdString = n.getData().task.getTaskId() + ": ";
                NodeButton button = new NodeButton(n, graphArea.getPrefWidth() * (1 - relativeWidth) * ((PhysicsNode) n).getRelX(),
                                                    graphArea.getPrefHeight() * (1 - relativeHeight)  * ((PhysicsNode) n).getRelY(),
                                                    relativeWidth * graphArea.getPrefWidth(), relativeHeight * graphArea.getPrefHeight());

                for (Node outNode : n.getOutNodes()) {
                    CustomArrow newLine = new CustomArrow(
                            button.getLayoutX() + button.getMinWidth(), button.getLayoutY() + button.getMinHeight() / 2,
                            graphArea.getPrefWidth() * (1 - relativeWidth) * ((PhysicsNode) outNode).getRelX(),
                            graphArea.getPrefHeight() * (1 - relativeHeight) * ((PhysicsNode) outNode).getRelY() + button.getMinHeight() / 2);

                    button.addOutNode(newLine);
                    n.outArrows.add(newLine);
                    outNode.inArrows.add(newLine);

                    if (((PhysicsNode) n).getCritical() && ((PhysicsNode) outNode).getCritical()) {
                        newLine.getStyleClass().add("critical-path-line");
                    }
                }
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

            normalView.setSelected(true);
            onNormalSelected();
        }
        catch(Exception e)
        {
            System.err.print(e);
            onBackButtonClicked();
        }
    }
}
