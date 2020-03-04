package org.gephi.statistics.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

/**
 * Implements a genetic algorithm that assigns nSpreader nodes as uniformly
 * distanced nodes in a graph.
 *
 * @author Alexander
 */
public class GeneticRank implements Statistics, LongTask {

    /**
     * Number of nodes to assign as seeders
     */
    private int nSpreaders = 10;
    /**
     * Number of genetic algorithm generations
     */
    private int nGenerations = 10;
    /**
     * Number of individuals per generation
     */
    private int nIndividuals = 100;
    // elitism, crossover, mutation percentages
    private double elitism = 0.5;
    private double crosover = 0.3;
    private double mutation = 0.2;
    //
    private ProgressTicket progress;
    //
    private boolean isCanceled;
    //
    private boolean isDirected = false;
    //
    private String report = "";
    //
    public static final String GENETICRANKTAG = "GeneticRank";
    private AttributeColumn geneticRankCol;

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">      
    public void setSpreaders(int nSpreaders) {
        this.nSpreaders = nSpreaders;
    }

    public void setGenerations(int nGenerations) {
        this.nGenerations = nGenerations;
    }

    public void setIndividuals(int nIndividuals) {
        this.nIndividuals = nIndividuals;
    }

    public void setElitism(double elitism) {
        this.elitism = elitism;
    }

    public void setCrossover(double crosover) {
        this.crosover = crosover;
    }

    public void setMutation(double mutation) {
        this.mutation = mutation;
    }

    public int getSpreaders() {
        return nSpreaders;
    }

    public int getGenerations() {
        return nGenerations;
    }

    public int getIndividuals() {
        return nIndividuals;
    }

    public double getElitism() {
        return elitism;
    }

    public double getCrossover() {
        return crosover;
    }

    public double getMutation() {
        return mutation;
    }
    // </editor-fold> 

    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph;
        if (isDirected) {
            graph = graphModel.getHierarchicalDirectedGraphVisible();
        } else {
            graph = graphModel.getHierarchicalUndirectedGraphVisible();
        }
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph hgraph, AttributeModel attributeModel) {
        hgraph.readLock();
        Progress.start(progress);
        progress.switchToIndeterminate();

        // list of nodes
        List<Node> nodes = new ArrayList<Node>();
        for (Node node : hgraph.getNodes()) {
            nodes.add(node);
        }

        // create color attribute column
        // create LR column table
        AttributeTable nodeTable = attributeModel.getNodeTable();
        geneticRankCol = nodeTable.getColumn(GENETICRANKTAG);
        if (geneticRankCol == null) {
            geneticRankCol = nodeTable.addColumn(GENETICRANKTAG, GENETICRANKTAG, AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }
        Random rand = new Random();

        // 1. Generate p random solutions (p=nIndividuals)
        // Solution = list of n (nSpreaders) nodes

        List<Solution> solutions = new ArrayList<Solution>();
        List<Solution> nextSolutions = new ArrayList<Solution>();

        for (int i = 0; i < nIndividuals; ++i) {
            // new solution with n random non-overlapping nodes
            solutions.add(new Solution(hgraph, nodes, nSpreaders));
        }

        int g = 0;
        while (true) {

            // 2. Compute fitness of each solution
            // Fitness = for each solution, run graph coloring (coverage) algorithm with nSpreaders as seeds,
            // and count iterations needed to color whole graph (or 90% of graph)

            for (Solution solution : solutions) {
                solution.computeFitness();
            }

            // 3. Sort solutions in descending order of fitness

            Collections.sort(solutions, new Comparator<Solution>() {
                public int compare(Solution o1, Solution o2) {
                    return o2.getFitness().compareTo(o1.getFitness());
                }
            });

            // stop condition
            if (++g > nGenerations) {
                break;
            }

            // 4. apply genetic operators

            // 4a. Elitism: copy over elitism% of solutions on to next generation
            for (int i = 0; i < (int) (nIndividuals * elitism); ++i) {
                nextSolutions.add(solutions.get(i));
            }

            // 4b. Crossover: mix pairs of two elite solutions and create one new pair
            List<Solution> crossoverSolutions = new ArrayList<Solution>();
            for (int i = 0; i < (int) (nIndividuals * crosover) / 2; ++i) {
                Solution solA = nextSolutions.get(rand.nextInt(nextSolutions.size()));
                Solution solB = solA;
                while (solB.equals(solA)) {
                    solB = nextSolutions.get(rand.nextInt(nextSolutions.size()));
                }

                // add teh two new solutions
                crossoverSolutions.addAll(solA.crossover(solB));
            }
            // append crossover solutions to next generation
            nextSolutions.addAll(crossoverSolutions);
            crossoverSolutions = null; // clean

            // 4c. Muation: randomly change one node inside any elite solution        
            while (nextSolutions.size() < solutions.size()) {
                // get one random solution
                Solution solution = nextSolutions.get(rand.nextInt(nextSolutions.size()));
                // ... and mutate
                nextSolutions.add(solution.mutate());
            }

            // next solutions become current colutions
            solutions.clear();
            solutions.addAll(nextSolutions);
            nextSolutions.clear();

        } // end generations

        // pick first solution as best
        Solution bestSolution = solutions.get(0);
        report = "Best solution after " + nGenerations + " generations with " + nSpreaders
                + " spreaders has fitness = " + bestSolution.getFitness();

        // apply best solution
        bestSolution.applySolution();

        progress.switchToDeterminate(100);
        progress.finish();
        hgraph.readUnlockAll();
    }

    /**
     *
     * @return
     */
    public String getReport() {
        //distribution of values
        /*Map<Double, Integer> dist = new HashMap<Double, Integer>();
         for (int i = 0; i < leaderRanks.length; i++) {
         Double d = leaderRanks[i];
         if (dist.containsKey(d)) {
         Integer v = dist.get(d);
         dist.put(d, v + 1);
         } else {
         dist.put(d, 1);
         }
         }

         //Distribution series
         XYSeries dSeries = ChartUtils.createXYSeries(dist, "PageRanks");

         XYSeriesCollection dataset = new XYSeriesCollection();
         dataset.addSeries(dSeries);

         JFreeChart chart = ChartFactory.createXYLineChart(
         "PageRank Distribution",
         "Score",
         "Count",
         dataset,
         PlotOrientation.VERTICAL,
         true,
         false,
         false);
         chart.removeLegend();
         ChartUtils.decorateChart(chart);
         ChartUtils.scaleChart(chart, dSeries, true);
         String imageFile = ChartUtils.renderChart(chart, "pageranks.png");

         String report = "<HTML> <BODY> <h1>PageRank Report </h1> "
         + "<hr> <br />"
         + "<h2> Parameters: </h2>"
         + "<br> <h2> Results: </h2>"
         + imageFile
         + "<br /><br />" + "<h2> Algorithm: </h2>"
         + "Sergey Brin, Lawrence Page, <i>The Anatomy of a Large-Scale Hypertextual Web Search Engine</i>, in Proceedings of the seventh International Conference on the World Wide Web (WWW1998):107-117<br />"
         + "</BODY> </HTML>";

         return report;*/

        return report;

    }

    /**
     *
     * @return
     */
    public boolean cancel() {
        isCanceled = true;
        return true;
    }

    /**
     *
     * @param progressTicket
     */
    public void setProgressTicket(ProgressTicket progressTicket) {
        progress = progressTicket;
    }

    private void setAttribute(Node node, AttributeColumn column, Object value) {
        ((AttributeRow) node.getNodeData().getAttributes()).setValue(column, value);
    }

    private Object getAttribute(Node node, String key) {
        return node.getAttributes().getValue(key);
    }

    private class Solution {

        private final HierarchicalGraph graph;
        private final List<Node> nodes;
        private List<Node> solution;
        private double fitness = 0.0;
        private Random rand = new Random();

        public Solution(HierarchicalGraph hgraph, List<Node> nodes, int size) {
            this.graph = hgraph;
            this.nodes = nodes;

            solution = new ArrayList<Node>();
            // pick random node from network and ensure node does not exist in list
            while (solution.size() < size) {
                Node aNode = nodes.get(rand.nextInt(nodes.size()));
                if (!solution.contains(aNode)) {
                    solution.add(aNode);
                }
            }
        }

        /**
         * Color graph starting from nSpreaders
         */
        public void computeFitness() {
            // clean all color tags (=0)
            for (Node node : nodes) {
                setAttribute(node, geneticRankCol, 0);
            }
            // set nSpreaders to =1
            for (Node node : solution) {
                setAttribute(node, geneticRankCol, 1);
            }

            int k = 0, covered = 0;
            final double coverRatio = 0.95;
            boolean finished = false;

            // repeat until stop condition (>95% coverage)
            while (!finished) {
                k++; // counts iterations needed to reach stop condition => fitness

                // color graph iteratively
                for (Node node : nodes) {
                    // if speader node
                    if ((Integer) getAttribute(node, GENETICRANKTAG) == 1) {
                        // set pending color (=2) to all neighbours
                        for (Node neigbour : graph.getNeighbors(node)) {
                            // if neighbour is already spreader then leve as it is
                            if ((Integer) getAttribute(neigbour, GENETICRANKTAG) == 1) {
                                ;
                            } else {
                                setAttribute(neigbour, geneticRankCol, 2); // marked to become spreader at end of iteration
                            }
                        }
                    }
                }

                // update all marked nodes to spreaders (2->1)           
                for (Node node : nodes) {
                    // if marked node
                    if ((Integer) getAttribute(node, GENETICRANKTAG) == 2) {
                        setAttribute(node, geneticRankCol, 1);
                    }
                }

                // count covered nodes
                covered = 0;
                for (Node node : nodes) {
                    // if spreader node
                    if ((Integer) getAttribute(node, GENETICRANKTAG) == 1) {
                        covered++;
                    }
                }

                // stop condition: when more than coverRatio (95%) of nodes are covered
                if (covered >= (int) (coverRatio * nodes.size()) || k > 10) { /*k>10 avoids infinite loops in disconnected graphs*/
                    finished = true;
                }
            }

            fitness = /*1.0*/ 1.0 * covered / k;
        }

        /**
         * Mix this solution and the given parameter by randomly copying
         * proportions of the two solutions into two new solutions.
         */
        public List<Solution> crossover(Solution anotherSolution) {
            List<Solution> newSolutions = new ArrayList<Solution>();

            // random crossover index
            int cindex = rand.nextInt(solution.size());

            // two new solutions
            Solution solA = new Solution(graph, nodes, nSpreaders);
            Solution solB = new Solution(graph, nodes, nSpreaders);
            // clear solutions' spreader arrays
            solA.solution.clear();
            solB.solution.clear();

            // mix  [0,cindex) and [cindex,nSpreaders} into the new solutions
            for (int i = 0; i < cindex; ++i) {
                solA.solution.add(this.solution.get(i));
                solB.solution.add(anotherSolution.solution.get(i));
            }
            for (int i = cindex; i < nSpreaders; ++i) {
                solA.solution.add(anotherSolution.solution.get(i));
                solB.solution.add(this.solution.get(i));
            }

            newSolutions.add(solA);
            newSolutions.add(solB);

            return newSolutions;
        }

        /**
         * Returns a new solution that has one randomly picked node randomly
         * changed with another node from the whole graph
         */
        public Solution mutate() {
            Solution newSolution = new Solution(graph, nodes, nSpreaders);
            // clear all nodes in mutated solution
            newSolution.solution.clear();
            // copy over nodes from this solution
            for (Node node : this.solution) {
                newSolution.solution.add(node);
            }
            // pick one random node in new solution
            Node node = newSolution.solution.get(rand.nextInt(newSolution.solution.size()));
            int index = newSolution.solution.indexOf(node);
            // change with another node that does not exist in the solution
            while (newSolution.solution.contains(node)) {
                node = nodes.get(rand.nextInt(nodes.size()));
            }
            newSolution.solution.remove(index);
            newSolution.solution.add(node);

            return newSolution;
        }

        public void applySolution() {
            // for debugging only
            double closeness = 0.0;

            // clean all color tags (=0)
            for (Node node : nodes) {
                setAttribute(node, geneticRankCol, 0);
            }
            // set nSpreaders to =1
            for (Node node : solution) {
                setAttribute(node, geneticRankCol, 1);
                closeness += (Double) getAttribute(node, GraphDistance.CLOSENESS);
            }

            report += "\n Average closeness of " + solution.size() + " spreaders: " + (closeness / solution.size());
        }

        public Double getFitness() {
            return fitness;
        }
    }
}
