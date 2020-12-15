package org.gephi.statistics.plugin;

import java.util.ArrayList;
import java.util.List;
import org.gephi.graph.api.Node;

public class GirvanNewmanClusterImpl {

  private List<Node> nodes = new ArrayList<Node>();
  private String clusterName = "untitled";
  private Node metaNode = null;

  public GirvanNewmanClusterImpl() {
  }

  public GirvanNewmanClusterImpl(List<Node> nodeList) {
    this.nodes = nodeList;
  }

  public void addNode(Node node) {
    this.nodes.add(node);
  }

  public void setName(String clusterName) {
    this.clusterName = clusterName;
  }

  public Node[] getNodes() {
    return this.nodes.toArray(new Node[0]);
  }

  public int getNodesCount() {
    return this.nodes.size();
  }

  public String getName() {
    return clusterName;
  }

  public Node getMetaNode() {
    return this.metaNode;
  }

  public void setMetaNode(Node node) {
    this.metaNode = node;
  }
}