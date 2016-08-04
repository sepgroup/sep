package com.sepgroup.sep.analysis;

import com.sepgroup.sep.model.InvalidInputException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Wishe on 8/3/2016.
 */
public final class CriticalPath
{
	private CriticalPath() {}

	/**
	 * Computes the set of critical paths in a given project.
	 * @param project The project to compute the critical paths of.
	 * @return The set of critical paths.
	 */
	public static Collection<List<TaskModel>> computeCriticalPaths(final ProjectModel project)
	{
		final Collection<List<TaskModel>> criticalPaths = new HashSet<>();

		try
		{
			final List<TaskModel> tasks = project.getTasks();
		}
		catch(ModelNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InvalidInputException e)
		{
			e.printStackTrace();
		}

		// TODO Placeholder
		return null;
	}

	public static Graph setTaskTimes(final Graph graph)
	{
		Node root = graph.getRoot();
		forwardPass(root);
		backwardsPass(graph.getTerminal(), root.data.earliestFinish);
		return graph;
	}

	private static void forwardPass(final Node node)
	{
		node.data.earliestFinish =
				(node.data.earliestStart = latestOfEarliestParentFinishes(node)) + node.data.task.getExpectedDuration();
		node.outNodes.forEach(CriticalPath::forwardPass);
	}

	private static void backwardsPass(final Node node, final float rootEarliestFinish)
	{
		node.data.latestStart =
				(node.data.latestFinish = earliestOfLatestParentFinishes(node, rootEarliestFinish)) - node.data.task.getExpectedDuration();
		for (final Node ancestor : node.inNodes)
			backwardsPass(ancestor, rootEarliestFinish);
	}

	private static float latestOfEarliestParentFinishes(final Node node)
	{
		float max = 0.0f;

		for (final Node parent : node.inNodes)
		{
			if (parent.data.earliestFinish > max)
				max = parent.data.earliestFinish;
		}

		return max;
	}

	private static float earliestOfLatestParentFinishes(final Node node, final float rootEarliestFinish)
	{
		float min = rootEarliestFinish;

		for (final Node parent : node.outNodes)
		{
			if (parent.data.latestFinish < min)
				min = parent.data.latestFinish;
		}

		return min;
	}
}
