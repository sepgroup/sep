package com.sepgroup.sep.controller;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;



	public class GanttController extends JFrame {


	    /**
	     * Creates a new demo.
	     *
	     * @param title  the frame title.
	     * @throws InvalidInputException 
	     * @throws ModelNotFoundException 
	     */
	    public GanttController(final String title) throws ModelNotFoundException, InvalidInputException {

	        super(title);

	        final IntervalCategoryDataset dataset = ProjectViewerController.createDataset();
	        final JFreeChart chart = createChart(dataset);

	        // add the chart to a panel...
	        final ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	        setContentPane(chartPanel);

	    }

	    // ****************************************************************************
	    // * JFREECHART DEVELOPER GUIDE                                               *
	    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
	    // * to purchase from Object Refinery Limited:                                *
	    // *                                                                          *
	    // * http://www.object-refinery.com/jfreechart/guide.html                     *
	    // *                                                                          *
	    // * Sales are used to provide funding for the JFreeChart project - please    * 
	    // * support us so that we can continue developing free software.             *
	    // ****************************************************************************
	    
	    /**
	     * Creates a sample dataset for a Gantt chart.
	     *
	     * @return The dataset.
	     */

	       
	    private JFreeChart createChart(final IntervalCategoryDataset dataset) {
	        final JFreeChart chart = ChartFactory.createGanttChart(
	            "Gantt Chart",  // chart title
	            "Task",              // domain axis label
	            "Date",              // range axis label
	            dataset,             // data
	            true,                // include legend
	            true,                // tooltips
	            false                // urls
	        );    
	        return chart;    
	    }
	    


	}


