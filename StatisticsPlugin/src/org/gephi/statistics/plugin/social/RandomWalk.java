package org.gephi.statistics.plugin.social;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.SEdge;
import org.gephi.graph.api.SNode;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

public class RandomWalk implements Statistics, LongTask {

    /**
     * Remembers if the Cancel function has been called.
     */
    private boolean isCanceled;
    /**
     * Keep track of the work done.
     */
    private ProgressTicket progress;
    /**
     *
     */
    private boolean useRandomWalks = false;
    private int repeats = 1000, maxPath = 100;
    private boolean addSAs = true;
    private int foundSame = 0, foundDifferent = 0;

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public int getRepeats() {
        return repeats;
    }

    public int getMaxPath() {
        return maxPath;
    }

    public boolean addSAs() {
        return addSAs;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public void setMaxPath(int maxPath) {
        this.maxPath = maxPath;
    }

    public void setAddSAs(boolean addSAs) {
        this.addSAs = addSAs;
    }

    public double getFoundRatio() {
        return 1.0 * foundSame / (foundSame + foundDifferent);
    }

    // </editor-fold> 
    // <editor-fold defaultstate="collapsed" desc="Execution">
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {
        // test if current graph is a facebook graph
        graph.getAttributes().setValue(SNode.TAG_SOCIALIZED, true);

        isCanceled = false;

        Random random = new Random();

        graph.readLock();
        Progress.start(progress, graph.getNodeCount());

        if (useRandomWalks) {
            // add one SA at every size/100 nodes
            if (addSAs) {
                int step = (int) (graph.getNodeCount() * 0.01);
                for (int i = 0; i < graph.getNodeCount(); ++i) {
                    SNode _node = new SNode(graph.getNodes().toArray()[i]);
                    _node.setValue(SNode.Stubborn, 0);

                    if (i % step == 0) {
                        SNode node = new SNode(graph.getNodes().toArray()[i]);
                        node.setValue(SNode.Stubborn, 1);
                    }
                }
            }

            for (int i = 0; i < repeats; ++i) {
                // random source node
                int rIndex = random.nextInt(graph.getNodeCount());
                SNode source = new SNode(graph.getNodes().toArray()[rIndex]);
                Float opinion = source.getValueAsFloat(SNode.Opinion);

                int pathLength = 0;
                while (pathLength < maxPath) {
                    pathLength++;
                    Node[] neighbors = graph.getNeighbors(source.getNode()).toArray();
                    if (neighbors.length > 0) {
                        // pick random target neighbor
                        SNode target = new SNode(neighbors[random.nextInt(neighbors.length)]);
                        // check if stubborn
                        Integer stubborn = target.getValueAsInteger(SNode.Stubborn);
                        if (stubborn != null && stubborn > 0) {
                            Float opinion2 = target.getValueAsFloat(SNode.Opinion);
                            if ((opinion - 0.5f) * (opinion2 - 0.5f) > 0) // same
                            {
                                foundSame++;
                            } else {
                                foundDifferent++;
                            }

                            break;
                        } else {
                            pathLength++;
                            source = target;
                        }
                    }

                    if (isCanceled) {
                        break;
                    }
                    Progress.progress(progress, i);
                }
            }
        } else {
            // measure the raio of same vs different opinion for each node taking into consideration all direct neighbors.
            for (int i = 0; i < graph.getNodeCount(); ++i) {
                SNode source = new SNode(graph.getNodes().toArray()[i]);
                Float opinion = source.getValueAsFloat(SNode.Opinion);

                Node[] neighbors = graph.getNeighbors(source.getNode()).toArray();
                for (Node n : neighbors) {
                    SNode neighbor = new SNode(n);
                    Float opinion2 = neighbor.getValueAsFloat(SNode.Opinion);

                    if ((opinion - 0.5f) * (opinion2 - 0.5f) > 0) // same
                    {
                        foundSame++;
                    } else {
                        foundDifferent++;
                    }
                }

            }
        }
        graph.readUnlockAll();
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="Misc Area">
    private String errorReport = "";

    public String getReport() {
        NumberFormat f = new DecimalFormat("#0.00");

        String report = "<HTML> <BODY> <h1>Opinion Random Walk</h1> "
                + "<hr>"
                + "<br> <h2> Results: </h2>"
                + "Number of iterations : " + repeats
                + "<br> Paths leading to same opinion : " + foundSame
                + "<br> Paths leading to other opinion : " + foundDifferent
                + "<br> Miu/lambda : " + f.format(1.0 * foundSame / (foundSame + foundDifferent)) + " %"
                + "<br><br> " + errorReport
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
}
