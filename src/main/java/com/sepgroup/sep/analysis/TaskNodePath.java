package com.sepgroup.sep.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wishe on 8/6/2016.
 */
public class TaskNodePath {
	private List<Node> nodes;
	private float duration;
	private boolean upToDate = false;

	public TaskNodePath() { nodes = new ArrayList<>(); }

	public TaskNodePath(final Node node) {
		this();
		nodes.add(node);
	}

	public TaskNodePath(final List<Node> nodeList) {
		this();
		nodes.addAll(nodeList);
	}

	public void add(final Node node) {
		nodes.add(node);
		upToDate = false;
	}

	public void addAll(final List<Node> nodeList) {
		nodes.addAll(nodeList);
		upToDate = false;
	}

	public float duration() {
		if (!upToDate) {
			duration = computeDuration();
			upToDate = true;
		}

		return duration;
	}

	private float computeDuration() {
		float duration = 0.0f;
		for (final Node node : nodes)
			duration += node.data.task.getExpectedDuration();
		return duration;
	}

	public List<Node> toList() {
		final List<Node> nodeList = new ArrayList<>();
		nodeList.addAll(nodes);
		return nodeList;
	}

	public String toString() {
		return toList().toString();
	}
}
