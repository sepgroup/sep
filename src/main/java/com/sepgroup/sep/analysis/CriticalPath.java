package com.sepgroup.sep.analysis;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Wishe on 8/3/2016.
 */
public final class CriticalPath {
	private CriticalPath() {}

	/**
	 * Computes the set of critical paths in a given project. A critical path is an ordered collection of tasks with
	 * greater expected duration than all other paths in the project.
	 * @param projectID The ID number of the project to compute the critical paths of.
	 * @return The set of critical paths.
	 */

	public static Collection<TaskNodePath> computeCriticalPaths(final int projectID) {
		return computeCriticalPaths(new Graph(projectID));
	}

	public static Collection<TaskNodePath> computeCriticalPaths(final Graph graph) {
		return getCriticalPathsStartingFrom(graph.getRoot());
	}

	private static Collection<TaskNodePath> getCriticalPathsStartingFrom(final Node node) {
		System.out.print(node.getID() + ", ");
		final Collection<TaskNodePath> criticalPaths = new ArrayList<>();
		final Collection<Node> children = node.outNodes;

		if (children.size() == 0)
		{
			criticalPaths.add(new TaskNodePath(node));
		} else {
			final Collection<TaskNodePath> paths = new ArrayList<>();
			float maxDuration = 0.0f;
			for (final Node child : children)
			{
				final Collection<TaskNodePath> childCriticalPaths = getCriticalPathsStartingFrom(child);

				for (final TaskNodePath childCriticalPath : childCriticalPaths) {
					if (maxDuration < childCriticalPath.duration())
						maxDuration = childCriticalPath.duration();

					paths.add(childCriticalPath);
				}
			}

			for (final TaskNodePath path : paths)
			{
				if (path.duration() == maxDuration)
					criticalPaths.add(path);
			}
		}

		return criticalPaths;
	}
}
