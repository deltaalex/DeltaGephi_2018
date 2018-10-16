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
public class FractalGraph extends AbstractGraph implements Generator {

    private final int radius = 1000;
    private int numberOfNodes = 100;
    private double pWiring = 1;
    private int numberOfCommunities = 5;
    private int fractalLevels = 2;

    @Override
    protected int initialize() {
        return numberOfNodes + numberOfNodes * numberOfNodes;
    }

    @Override
    protected void runGeneration(GraphModel graphModel, Random random) {
        fractalGeneration(graphModel, random, numberOfNodes, radius, pWiring, numberOfCommunities, fractalLevels);
    }

    private Community fractalGeneration(GraphModel graphModel, Random random, int N, int radius, double pWiringRatio, int numberOfCommunities, int fractalLevel) {
        if (fractalLevel == 1) {
            return createMeshCommunity(graphModel, random, numberOfNodes, radius, pWiring);
        } else {
            Community community = new Community();

            for (int i = 0; i < numberOfCommunities; ++i) {
                community.add(fractalGeneration(graphModel, random, N, radius, pWiringRatio, numberOfCommunities, fractalLevel - 1));
            }
            connectCommunity(graphModel, random, community);

            return community;
        }

    }

    private Community createMeshCommunity(GraphModel graphModel, Random random, int numberOfNodes, int radius, double pWiringRatio) {
        Community community = new Community();
        List<CommunityNode> nodes = new ArrayList<CommunityNode>();
        int N = random.nextInt(numberOfNodes) + 1;
        CommunityNode cNode;
        float x, y;
        double dist, p, pWiring;

        // create nodes and place them randomly inside circle with 'radius'
        for (int i = 0; i < N; ++i) {
            // create node
            cNode = new CommunityNode(createNode(graphModel, i, true));

            // generate random between [-radius, +radius]
            x = y = radius;
            while (x * x + y * y > radius * radius) {
                x = random.nextInt(2 * radius + 1) - radius;
                y = random.nextInt(2 * radius + 1) - radius;
            }
            cNode.node.getNodeData().setX(x);
            cNode.node.getNodeData().setY(y);

            progressTick();
            nodes.add(cNode);
        }

        // connect nodes based on position with probability 'p'
        for (int i = 0; i < N - 1; ++i) {
            pWiring = pWiringRatio * getPowerDistributedIntegerValue(random, 1, numberOfNodes) / numberOfNodes;
            for (int j = i + 1; j < N; ++j) {
                dist = getEuclideanDistance(nodes.get(i).node, nodes.get(j).node);
                p = pWiring * (1f - dist / (2f * radius));
                if (random.nextDouble() < p) {
                    createAddDirectedEdge(graphModel, nodes.get(i).node, nodes.get(j).node);
                }
            }
        }

        for (CommunityNode _cNode : nodes) {
            community.add(_cNode);
        }

        return community;
    }

    private void connectCommunity(GraphModel graphModel, Random random, Community community) {
        List<Community> communities = community.communities;
        Community c1, c2;
        int maxDegree;

        for (int i = 0; i < communities.size() - 1; ++i) {
            for (int j = i + 1; j < communities.size(); ++j) {
                c1 = communities.get(i);
                c2 = communities.get(j);

                //maxDegree = getPowerDistributedIntegerValue(random, 1, random.nextInt(Math.max(c1.size(), c2.size())));
                //maxDegree = random.nextInt(Math.max(c1.size(), c2.size()));
                maxDegree = Math.max(c1.size(), c2.size());

                while (maxDegree > 0) {
                    Node n1 = c1.getRandomNode(random);
                    Node n2 = c2.getRandomNode(random);

                    createAddDirectedEdge(graphModel, n1, n2);
                    maxDegree--;
                }
            }
        }

    }

    class Community {

        List<Community> communities = new ArrayList<Community>();

        public void add(Community community) {
            communities.add(community);
        }

        public Node getRandomNode(Random random) {
            return communities.get(random.nextInt(communities.size())).getRandomNode(random);
        }

        public int size() {
            int size = 0;
            for (Community community : communities) {
                size += community.size();
            }
            return size;
        }
    }

    class CommunityNode extends Community {

        Node node;

        public CommunityNode(Node node) {
            this.node = node;
        }

        public Node getRandomNode(Random random) {
            return node;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getName() {
        return NbBundle.getMessage(FractalGraph.class, "FractalGraph.name");
    }

    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(FractalGraphUI.class);
    }

    public void setNumberOfNodes(int numberOfNodes) {
        if (numberOfNodes < 0) {
            throw new IllegalArgumentException("# of nodes must be greater than 0");
        }
        this.numberOfNodes = numberOfNodes;
    }

    public void setWiring(double wiring) {
        this.pWiring = wiring;
    }

    public void setNumberOfCommunities(int numberOfCommunities) {
        this.numberOfCommunities = numberOfCommunities;
    }

    public void setFractalLevels(int fractalLevels) {
        this.fractalLevels = fractalLevels;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public double getWiring() {
        return pWiring;
    }

    public int getNumberOfCommunities() {
        return numberOfCommunities;
    }

    public int getFractalLevels() {
        return fractalLevels;
    }
    // </editor-fold>
}
