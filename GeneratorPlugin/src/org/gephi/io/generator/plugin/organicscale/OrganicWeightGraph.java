/**
 * Creates a social network driven by randomly emerging encounters which stack
 * up to form an organic scale-free network
 */
package org.gephi.io.generator.plugin.organicscale;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.graph.api.*;
import org.gephi.io.generator.plugin.AbstractGraph;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = Generator.class)
public class OrganicWeightGraph extends AbstractGraph implements Generator {

    private int numberOfNodes = 250;
    private int duration = 1000;
    private int xmax = 1000, ymax = 1000;
    private double distmax = Math.sqrt(xmax * xmax + ymax * ymax);
    private double exponent = 1.2;

    @Override
    protected int initialize() {
        return numberOfNodes + numberOfNodes * numberOfNodes;
    }

    @Override
    protected void runGeneration(GraphModel graphModel, Random random) {

        List<Node> nodes = new ArrayList<Node>(numberOfNodes);
        int weights[][] = new int[numberOfNodes][numberOfNodes];
        Graph graph = graphModel.getGraph();
        graph.clear();

        // place nodes in lattice
        for (int i = 0; i < numberOfNodes; ++i) {

            // create node
            Node node = graphModel.factory().newNode();
            // initialize node
            node.getNodeData().setSize(NODE_SIZE);
            node.getNodeData().setLabel("" + (i + 1));

            // add to graph
            graphModel.getGraph().addNode(node);
            nodes.add(node);
            node.getNodeData().setX(xmax * random.nextFloat());
            node.getNodeData().setY(ymax * random.nextFloat());

            //Sleep some time
            animateNode();
            progressTick();
        }

        // link all nodes nodes in network
        for (int i = 0; i < numberOfNodes; ++i) {
            for (int j = 0; j < numberOfNodes; ++j) {
                weights[i][j] = 1;
                weights[j][i] = 1;
            }
        }

        // interaction iterations
        boolean interacted[] = new boolean[numberOfNodes];
        //int totalWeight;
        double totalFitness = 0;
        double[] nodefitness = new double[numberOfNodes];

        for (int k = 0; k < duration; ++k) {

            for (int i = 0; i < numberOfNodes; ++i) {
                interacted[i] = false;
                nodefitness[i] = 0;
            }

            // every node will interact with someone every iteration
            for (int i = 0; i < numberOfNodes; ++i) {
                //totalWeight = 0;
                totalFitness = 0;
                // only if node did not interact yet this iteration
                if (!interacted[i]) {
                    // compute sum of neighboring weights (i.e. all other nodes)
                    for (int j = 0; j < numberOfNodes; ++j) {
                        if (i != j) {
                            //totalWeight += weights[i][j];
                            nodefitness[j] = 1.0 * weights[i][j]
                                    / Math.pow(distnorm(nodes, i, j), exponent);
                            totalFitness += nodefitness[j];
                        }
                    }

                    double p = random.nextDouble();

                    int j = 0;
                    while (j < numberOfNodes && (p - nodefitness[j] / totalFitness /*1.0 * weights[i][j] / totalWeight*/) > 0) {
                        if (i == j) {
                            j++;
                            continue;
                        }
                        p -= nodefitness[j] / totalFitness;//1.0 * weights[i][j] / totalWeight;
                        j++;
                    }

                    // j=index of selected neighbor
                    weights[i][j]++;
                    weights[j][i]++;

                    interacted[i] = true;
                    interacted[j] = true;
                }
            }
        }

        // end: add edges if weight > K N=250, 1000*1000 => K=5 <k>=6
        Edge edge;
        for (int i = 0; i < numberOfNodes - 1; ++i) {
            for (int j = i + 1; j < numberOfNodes; ++j) {
                if (weights[i][j] > 1) {
                    edge = graphModel.factory().newEdge(nodes.get(i), nodes.get(j));
                    edge.setWeight(weights[i][j]);
                    graphModel.getGraph().addEdge(edge);

                    //Sleep some time
                    animateEdge();
                    progressTick();
                }
            }
        }
    }

    // return distance between nodes (i,j) normalized by the maximum distance
    private double distnorm(List<Node> nodes, int i, int j) {
        double dist = Math.sqrt(Math.pow(nodes.get(i).getNodeData().x() - nodes.get(j).getNodeData().x(), 2)
                + Math.pow(nodes.get(i).getNodeData().y() - nodes.get(j).getNodeData().y(), 2));

        return dist / distmax;
    }

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getName() {
        return NbBundle.getMessage(OrganicWeightGraph.class, "OrganicWeightGraph.name");
    }

    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(OrganicGraphUI.class);
    }

    public void setNumberOfNodes(int numberOfNodes) {
        if (numberOfNodes < 0) {
            throw new IllegalArgumentException("# of nodes must be greater than 0");
        }
        this.numberOfNodes = numberOfNodes;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setXmax(int xmax) {
        this.xmax = xmax;
        distmax = Math.sqrt(xmax * xmax + ymax * ymax);
    }

    public int getXmax() {
        return xmax;
    }

    public void setYmax(int ymax) {
        this.ymax = ymax;
        distmax = Math.sqrt(xmax * xmax + ymax * ymax);
    }

    public int getYmax() {
        return ymax;
    }

    public void setExponent(double exponent) {
        this.exponent = exponent;
    }

    public double getExponent() {
        return exponent;
    }
    // </editor-fold>
}
