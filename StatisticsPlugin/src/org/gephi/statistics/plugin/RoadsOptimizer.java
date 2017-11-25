package org.gephi.statistics.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;

/**
 * Takes a directed graph (road network) as input and heuristically swaps edge
 * directions until the betweenness distribution is even out from a power-law to
 * a more uniform one. <br>
 *
 * @author Alexander
 */
public class RoadsOptimizer extends WSNOptimizer implements Statistics, LongTask {

    // parameters
    private double resolution = 0.3;
    private int inversedEdges, balancedNodes;
    private double slope = -1;
    private boolean animate = false;
    private int animationNodeDelay = 10;
    private int animationEdgeDelay = 50;
    /**
     * Remembers if the Cancel function has been called.
     */
    private boolean isCanceled;
    /**
     * Keep track of the work done.
     */
    private ProgressTicket progress;

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public double getResolution() {
        return resolution;
    }

    public double getSlope() {
        return slope;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    // </editor-fold> 
    // <editor-fold defaultstate="collapsed" desc="Common Getters/Setters">
    public boolean getAnimate() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public int getAnimationNodeDelay() {
        return animationNodeDelay;
    }

    public void setAnimationNodeDelay(int animationNodeDelay) {
        this.animationNodeDelay = animationNodeDelay;
    }

    public int getAnimationEdgeDelay() {
        return animationEdgeDelay;
    }

    public void setAnimationEdgeDelay(int animationEdgeDelay) {
        this.animationEdgeDelay = animationEdgeDelay;
    }
    // </editor-fold> 
    // <editor-fold defaultstate="collapsed" desc="Execution">    

    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {
        Progress.start(progress);
        progress.switchToIndeterminate();

        /**
         * 1. Randomize randomize % of edges
         */
        int k = (int) (graph.getNodeCount() * resolution);
        inversedEdges = 0;
        balancedNodes = k;
        // output file
        File flog = new File(System.getProperty("user.home") + "/Desktop/roads_btw.txt");
        PrintWriter log = null;
        try {
            log = new PrintWriter(flog);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        // compute and order by betweenness
        GraphDistance betweenness = new GraphDistance();
        betweenness.setNormalized(false);
        betweenness.setDirected(true);
        betweenness.setProgressTicket(progress);
        betweenness.execute(graph.getGraphModel(), attributeModel);
        betweenness.getPathLength();

        ArrayList<Node> nodes = new ArrayList<Node>();
        for (Node node : graph.getNodes()) {
            nodes.add(node);
        }
        Collections.sort(nodes, new WSNOptimizer.NodeComparator(GraphDistance.BETWEENNESS, false));

        // balance first k nodes in terms of centrality
        for (int i = 0; i < k; ++i) {
            // get in/out degrees
            Node node = nodes.get(i);
            Edge[] incidentEdges = graph.getEdges(node).toArray();

            int[] degrees = getDegrees(node, incidentEdges);
            int inDegree = degrees[0];
            int outDegree = degrees[1];

            // balance in/out degree
            if (inDegree < outDegree) {
                balanceEdges(graph.getGraphModel(), node, incidentEdges, true, outDegree - inDegree);
            } else {
                balanceEdges(graph.getGraphModel(), node, incidentEdges, false, inDegree - outDegree);
            }
        }

        progress.switchToDeterminate(100);
        progress.finish();
    }
// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Misc Area">
    private String errorReport = "";

    /**
     * Creates and edge between the swapped nodes (target->source) and removes
     * the original edge (source->target)
     */
    private Edge inverseEdge(GraphModel graphModel, Edge edge) throws Exception {

        Node source = edge.getSource();
        Node target = edge.getTarget();

        // reflection hack
        Field field = AbstractEdge.class.getDeclaredField("source");
        field.setAccessible(true);
        field.set(edge, target);
        field = AbstractEdge.class.getDeclaredField("target");
        field.setAccessible(true);
        field.set(edge, source);

//        Edge newEdge = graphModel.factory().newEdge(target, source);
//        graphModel.getGraph().addEdge(newEdge);
//        if (edge != null) {
//            try {                
//                graphModel.getGraph().removeEdge(edge);                
//            } catch (NullPointerException e) {
//                /**/
//            }
//        }

        //Sleep some time
        animateEdge();

        return null; //newEdge;
    }

    private int[] getDegrees(Node node, Edge[] edges) {
        int[] degrees = {0, 0};

        for (Edge edge : edges) {
            // outdegree
            if (edge.getSource().equals(node)) {
                degrees[1]++;
            } // indegree
            else if (edge.getTarget().equals(node)) {
                degrees[0]++;
            } else {
                throw new IllegalArgumentException("Edge not incident to node!");
            }
        }

        return degrees;
    }

    private void balanceEdges(GraphModel graphModel, Node node, Edge[] edges, boolean increaseIndegree, int k) {
        if (increaseIndegree) {
            for (Edge edge : edges) {
                if (k > 0 && edge.getSource().equals(node)) {
                    try {
                        inverseEdge(graphModel, edge);
                        k--;
                        inversedEdges++;
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        } else {
            for (Edge edge : edges) {
                if (k > 0 && edge.getTarget().equals(node)) {
                    try {
                        inverseEdge(graphModel, edge);
                        k--;
                        inversedEdges++;
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    public String getReport() {
        NumberFormat f = new DecimalFormat("#0.0000");

        String report = "<HTML> <BODY> <h1>WSN Optimizer Report </h1> "
                + "<hr><br>";

        report += "Optimized " + balancedNodes + " intersections, a total of " + inversedEdges + " roads<br>";
//        report += "Number of communities: " + numberofComm + "<br>";
//        report += "Average delay (hops): " + avgDelay + "<br>";

        /*report += "<table border=\"1\"><tr><th></th>";
         report += "<th>ADeg</th>"
         + "<th>APL</th>"
         + "<th>CC</th>"
         + "<th>Mod</th>"
         + "<th>Dns</th>"
         + "<th>Dmt</th>"
         + "</tr>";

         report += "<tr><td><b>Base model</b></td>";
         for (Double value : baseMetrics) {
         report += "<td>" + f.format(value) + "</td>";
         }

         report += "</tr><tr><td><b>Measured model</b></td>";
         for (Double value : measuredMetrics) {
         report += "<td>" + f.format(value) + "</td>";
         }

         report += "</tr><tr><td><b>Individual deltas</b></td>";
         for (int i = 0; i < measuredMetrics.length; ++i) {
         report += "<td>" + f.format(getDelta(measuredMetrics[i], i)) + "</td>";
         }

         report += "</tr></table><br><br>";

         report += "Arithmetic Delta: " + f.format(getDeltaArithmetic()) + "<br>";
         report += "Geometric Delta: " + f.format(getDeltaGeometric()) + "<br>";
         report += "Harmonic Delta: " + f.format(getDeltaHarmonic()) + "<br>";*/

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
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Utility">

    protected void sleep(int delay) {
        //Sleep some time
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void animateNode() {
        if (animate) {
            sleep(animationNodeDelay);
        }
    }

    protected void animateEdge() {
        if (animate) {
            sleep(animationEdgeDelay);
        }
    }

    private int getPowerLawValue(Random random, int min, int max) {
        // Gaussian number between -3 and +3 with mean 0.
        double x = random.nextGaussian();

        // the closer to 0, the smaller the cluster
        // the further from 0, the larger the cluster
        double dx = Math.abs(x);
        if (dx > 3) {
            dx = 3;
        }
        dx /= 3.0;

        return min + (int) ((max - min) * (dx));
    }
    // </editor-fold>
}
