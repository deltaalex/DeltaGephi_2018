package org.gephi.statistics.plugin.social;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

/**
 * Adds power-law distributed weights on a social network based on the
 * normalized betweenness of nodes in each dyad. <br> TODO: Determines influence
 * of nodes in social network based on community heterogeneity <br>
 *
 * @author Alexander
 */
public class EdgeTwist implements Statistics, LongTask {

    /**
     * Remembers if the Cancel function has been called.
     */
    private boolean isCanceled;
    /**
     * Keep track of the work done.
     */
    private ProgressTicket progress;

    // <editor-fold defaultstate="collapsed" desc="Execution">
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {

        //graph.readLock();
        Progress.start(progress);
        progress.switchToIndeterminate();

        //
        // 1) get all edges and remove duplicates (s->t == t->s)
        //
        List<Edge> edges = new ArrayList<Edge>();
        for (Edge newEdge : graph.getEdges()) {
            // add first edge
            if (edges.isEmpty()) {
                edges.add(newEdge);
                continue;
            }

            boolean match = false;
            for (int i = 0; i < edges.size(); ++i) {
                // if both sources and targets are equal s1=s2 && t1=t2, or if s1=t2 and s2=t1, then don't add new edge
                if ((newEdge.getSource().equals(edges.get(i).getSource()) && newEdge.getTarget().equals(edges.get(i).getTarget()))
                        || (newEdge.getSource().equals(edges.get(i).getTarget()) && newEdge.getTarget().equals(edges.get(i).getSource()))) {
                    match = true;
                    continue;
                }
            }

            if (!match) {
                edges.add(newEdge);
            }
        }
        int numberOfNodes = edges.size();

        //
        // 2) Clear grpah, iterate edges and create new nodes
        //        

        // clear graph
        graph.clear();
        Map<Node, Edge> nodeToEdgeMap = new HashMap<Node, Edge>();

        // create nodes from edges and save link from new node to old edge for later use
        Node newnode;
        for (Edge edge : edges) {
            newnode = createNode(graph.getGraphModel(), edge.getSource().getNodeData().getLabel() + "-" + edge.getTarget().getNodeData().getLabel());
            //newnode = createNode(graph.getGraphModel(), "-");
            nodeToEdgeMap.put(newnode, edge);
        }

        // save new nodes to list
        List<Node> nodes = new ArrayList<Node>();
        for (Node newNode : graph.getNodes()) {
            nodes.add(newNode);
        }

        //
        // 3) Connect new nodes baed on common (old) nodes                
        //

        Edge e1, e2;
        for (int i = 0; i < nodes.size() - 1; ++i) {
            for (int j = i + 1; j < nodes.size(); ++j) {
                e1 = nodeToEdgeMap.get(nodes.get(i));
                e2 = nodeToEdgeMap.get(nodes.get(j));
                if (e1 != null && e2 != null) {
                    // if edges were incident
                    if (e1.getTarget() == e2.getTarget() || e1.getTarget() == e2.getSource() || e1.getSource() == e2.getTarget() || e1.getSource() == e2.getSource()) {
                        createUndirectedEdge(graph.getGraphModel(), nodes.get(i), nodes.get(j));
                    }
                }
            }
        }

        progress.switchToDeterminate(100);
        progress.finish();
        //graph.readUnlockAll();
    }
    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="Misc Area">
    private String errorReport = "";

    public String getReport() {
        NumberFormat f = new DecimalFormat("#0.0000");

        String report = "<HTML> <BODY> <h1>Edge Twist Report </h1> "
                + "<hr><br>";

        report += "<table border=\"1\"><tr><th></th>";
        report += "<th>ADeg</th>"
                + "<th>APL</th>"
                + "<th>CC</th>"
                + "<th>Mod</th>"
                + "<th>Dns</th>"
                + "<th>Dmt</th>"
                + "</tr>";

        report += "<br><br><font color=\"red\">" + errorReport + "</font>"
                + "</BODY></HTML>";

        return report;
    }

    /**
     *
     * @return
     */
    public boolean cancel() {
        this.isCanceled = true;
        return true;
    }

    private boolean isCanceled() {
        return isCanceled;
    }

    /**
     *
     * @param progressTicket
     */
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }
    private static final float NODE_SIZE = 5f;

    private Node createNode(GraphModel graphModel, String label) {
        // create node
        Node newNode = graphModel.factory().newNode();
        // initialize node
        newNode.getNodeData().setSize(NODE_SIZE);
        newNode.getNodeData().setLabel(label);
        // add to graph
        graphModel.getGraph().addNode(newNode);

        return newNode;
    }

    protected Edge createUndirectedEdge(GraphModel graphModel, Node source, Node target) {

        if (graphModel.getGraph().getEdge(source, target) == null
                && graphModel.getGraph().getEdge(target, source) == null) {

            Edge newEdge = graphModel.factory().newEdge(source, target);
            graphModel.getGraph().addEdge(newEdge);

            return newEdge;
        }

        return null;
    }
    // </editor-fold>
}
