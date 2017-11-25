/**
 * Creates a Static-Geographic network
 */
package org.gephi.io.generator.plugin;

import java.util.ArrayList;
import java.util.List;
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
public class LargeWorldNet extends ScaleFreeGraph implements Generator {

    private int numberOfWorlds = 100;
    private int avgDegree = 10;

    @Override
    protected int initialize() {
        return numberOfWorlds + numberOfWorlds;
    }

    @Override
    protected void runGeneration(GraphModel graphModel, Random random) {

        // list of nodes
        ArrayList<Node> nodes = new ArrayList<Node>();
        // empirical observation: 2ln(N)
        int maxDegree = (int) Math.sqrt(numberOfWorlds);

        FastZipfGenerator zipf = new FastZipfGenerator(maxDegree, 2.0);

        for (int i = 0; i < numberOfWorlds; ++i) {
            int degree = zipf.next() - 1;

            nodes.addAll(createLargeWorld(graphModel, degree, random));
        }

        /*
         // connect large worlds
         int toAdd = graphModel.getGraph().getEdgeCount() * (avgDegree - 1);
         int n = nodes.size();
         Metric[] metrics = {Metric.Degree, Metric.Clustering};

         // compute metrics on the graph
         progress.switchToIndeterminate();
         computeMetricOnGraph(graphModel, metrics);
         double[] sumpk = getMetricsFromGraph(graphModel, metrics);

         // add edges
         while (toAdd > 0) {
         // pick random source node
         Node node = nodes.get(random.nextInt(n));

         // try to connect the node to others baed on target fitness
         for (int i = 0; i < nodes.size(); ++i) {
         if (nodes.get(i).equals(node)) {
         continue;
         }

         double[] pi = new double[metrics.length];
         // metric of current sfNode
         for (int m = 0; m < metrics.length; ++m) {
         pi[m] = getNodeMetric(graphModel.getGraph(), node, metrics[m]);
         }

         // get random value
         double p = random.nextDouble();
         // weight of each metric
         double w = 1.0 / metrics.length;

         double fitness = 0.0;
         for (int m = 0; m < metrics.length; ++m) {
         fitness += w * pi[m] / sumpk[m];
         }

         // connect if p < probability
         if (p < fitness) {
         createUndirectedEdge(graphModel, node, nodes.get(i));
         toAdd--;
         }
         }

         computeMetricOnGraph(graphModel, metrics);
         sumpk = getMetricsFromGraph(graphModel, metrics);
         }
         */
    }

    private List<Node> createLargeWorld(GraphModel graphModel, int degree, Random rand) {
        List<Node> nodes = new ArrayList<Node>();

        // one solitary node - 0 edges
        if (degree == 0) {
            nodes.add(createNode(graphModel, 0, true));
        } // two connected nodes (A->B) - 1 edge
        else if (degree == 1) {
            nodes.add(createNode(graphModel, 0, true));
            nodes.add(createNode(graphModel, 1, true));
            createUndirectedEdge(graphModel, nodes.get(0), nodes.get(1));
        } // two nodes connected to a central one (A->B<-C) - 2 edges
        else if (degree == 2) {
            nodes.add(createNode(graphModel, 0, true));
            nodes.add(createNode(graphModel, 1, true));
            nodes.add(createNode(graphModel, 2, true));
            createUndirectedEdge(graphModel, nodes.get(1), nodes.get(0));
            createUndirectedEdge(graphModel, nodes.get(1), nodes.get(2));
        } // a row of four connected rows (A->B->C->D)
        // OR 3 connected to one (A,B,C->D)
        else if (degree == 3) {
            if (rand.nextBoolean()) {
                nodes.add(createNode(graphModel, 0, true));
                nodes.add(createNode(graphModel, 1, true));
                nodes.add(createNode(graphModel, 2, true));
                nodes.add(createNode(graphModel, 3, true));
                createUndirectedEdge(graphModel, nodes.get(0), nodes.get(1));
                createUndirectedEdge(graphModel, nodes.get(1), nodes.get(2));
                createUndirectedEdge(graphModel, nodes.get(2), nodes.get(3));
            } else {
                nodes.add(createNode(graphModel, 0, true));
                nodes.add(createNode(graphModel, 1, true));
                nodes.add(createNode(graphModel, 2, true));
                nodes.add(createNode(graphModel, 3, true));
                createUndirectedEdge(graphModel, nodes.get(0), nodes.get(3));
                createUndirectedEdge(graphModel, nodes.get(1), nodes.get(3));
                createUndirectedEdge(graphModel, nodes.get(2), nodes.get(3));
            }
        } // combine recursively n/2 and n-n/2
        else {
            nodes.addAll(createLargeWorld(graphModel, degree / 2, rand));
            nodes.addAll(createLargeWorld(graphModel, degree - degree / 2, rand));

            // pck tow random nodes from the different communities
            int index1 = getRandomAndDifferentIndex(0, degree / 2, -1, rand);
            int index2 = getRandomAndDifferentIndex(degree / 2, degree, -1, rand);

            // connect communitites
            createUndirectedEdge(graphModel, nodes.get(index1), nodes.get(index2));
        }

        return nodes;

    }

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getName() {
        return NbBundle.getMessage(LargeWorldNet.class, "LargeWorldNet.name");
    }

    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(LargeWorldNetUI.class);
    }

    public void setNumberOfNodes(int numberOfNodes) {
        if (numberOfNodes < 0) {
            throw new IllegalArgumentException("# of nodes must be greater than 0");
        }
        this.numberOfWorlds = numberOfNodes;
    }

    public int getNumberOfNodes() {
        return numberOfWorlds;
    }

    // </editor-fold>
}
