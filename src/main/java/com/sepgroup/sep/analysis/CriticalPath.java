package com.sepgroup.sep.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Wishe on 8/3/2016.
 */
public final class CriticalPath {
	private CriticalPath() {}

	/**
	 * Computes the set of critical paths in a given project.
	 * @param graph The graph of the project to compute the critical paths of.
	 * @return The set of critical paths.
	 */
	public static Collection<List<Node>> computeCriticalPaths(final Graph graph) {
		final Collection<List<Node>> criticalPaths = new HashSet<>();

		Node node = graph.root;

		Collection<Node> nextNodes = new HashSet<>();
		for (final Node child : node.outNodes) {
			if (child.data.getFloat() == 0.0f) {

			}
		}

		return null;
	}
}
