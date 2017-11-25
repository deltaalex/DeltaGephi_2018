/**
 * Creates a Static-Geographic network
 */
package org.gephi.io.generator.plugin;

import java.util.Random;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.graph.api.*;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = Generator.class)
public class MeshGraph extends AbstractGraph implements Generator {

    private final int Position = 1000;
    private int numberOfNodes = 100;
    private double dissimilarity = 0.5;

    @Override
    protected int initialize() {
        return numberOfNodes + numberOfNodes * numberOfNodes;
    }

    @Override
    protected void runGeneration(GraphModel graphModel, Random random) {

        // matrix of nodes
        int N = (int) Math.sqrt(numberOfNodes);
        Node[][] nodes = new Node[N][N];
        float spacing = Position / N;

        // place nodes in lattice
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {

                // create node
                Node node = graphModel.factory().newNode();
                // initialize node
                node.getNodeData().setSize(NODE_SIZE);
                node.getNodeData().setLabel("" + (N * i + j + 1));

                // add to graph
                graphModel.getGraph().addNode(node);
                nodes[i][j] = node;
                node.getNodeData().setX(spacing * i);
                node.getNodeData().setY(spacing * j);

                //Sleep some time
                animateNode();
                progressTick();
            }
        }

        // link nodes in lattice             
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {

                addEdge(graphModel, nodes, i, j, i + 1, j);
                addEdge(graphModel, nodes, i, j, i - 1, j);
                addEdge(graphModel, nodes, i, j, i, j - 1);
                addEdge(graphModel, nodes, i, j, i, j + 1);
            }
            progressTick();
        }
    }

    private void addEdge(GraphModel graphModel, Node[][] nodes, int i, int j, int i2, int j2) {
        try {
            Edge edge = graphModel.factory().newEdge(nodes[i][j], nodes[i2][j2]);
            graphModel.getGraph().addEdge(edge);
            //Sleep some time
            animateEdge();
        } catch (IndexOutOfBoundsException e) {/*ignore*/ }
    }

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getName() {
        return NbBundle.getMessage(MeshGraph.class, "MeshGraph.name");
    }

    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(MeshGraphUI.class);
    }

    public void setNumberOfNodes(int numberOfNodes) {
        if (numberOfNodes < 0) {
            throw new IllegalArgumentException("# of nodes must be greater than 0");
        }
        this.numberOfNodes = numberOfNodes;
    }

    public void setDissimilarity(double dissimilarity) {
        this.dissimilarity = dissimilarity;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public double getDissimilarity() {
        return dissimilarity;
    }

    // </editor-fold>
}
