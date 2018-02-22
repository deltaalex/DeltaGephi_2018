package org.gephi.statistics.plugin.social;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Edge;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;

/**
 * Given a topology G=(V,E) and a list of interactions between nodes V with
 * timestamp, run a simulation of evolving weights wij based on the formula: wij
 * = alpha * e^(-beta*tij); after every interaction (ij) the time counter tij is
 * reset to 0. All weights replicate the charge of a condenser loosing charge
 * gradually, and being refilled (+alpha) at every interaction. The list of
 * interactions is currently generated synthetically as: pick random pair (vi,
 * vj) and interact at every iteration k. Output: list of weights wij
 *
 * @author Alexander
 */
public class EvolvingWeights implements Statistics, LongTask {

    /**
     * Independent edges means every edge is activated (interaction) after an
     * individual random timeout . Dependent edges are activated in a
     * centralized manner (one per iteration).
     */
    private boolean independentEdges = true;
    /**
     * Remembers if the Cancel function has been called.
     */
    private boolean isCanceled;
    /**
     * Keep track of the work done.
     */
    private ProgressTicket progress;
    /**
     * Edge type
     */
    private boolean directed = false;
    /**
     * Stop condition for evolution processes
     */
    private int maxIterations = 1000;
    /**
     * How often the weights should be polled during the simulation
     */
    private int pollFrequency = 100;
    /**
     * Maximum timeout for an (any) interaction
     */
    private int edgeMaxTimeout = 100;
    /**
     * Alpha: amplitude gained in weight after an interaction. Unique for all
     * nodes. The higher the value the higher the weights are in general.
     */
    private double alpha = 0.5;
    /**
     * Beta: damping factor for weight. Unique for all nodes. The smaller the
     * value the more linear and slower the weights decrease.
     */
    private double beta = 0.5;

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">      
    public void setIndependentEdges(boolean independentEdges) {
        this.independentEdges = independentEdges;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setPollFrequency(int pollFrequency) {
        this.pollFrequency = pollFrequency;
    }

    public void setEdgeMaxTimeout(int edgeMaxTimeout) {
        this.edgeMaxTimeout = edgeMaxTimeout;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public boolean isIndependentEdges() {
        return independentEdges;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public int getPollFrequency() {
        return pollFrequency;
    }

    public int getEdgeMaxTimeout() {
        return edgeMaxTimeout;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="Execution">
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        if (independentEdges) {
            executeIndependent(graph, attributeModel);
        } else {
            execute(graph, attributeModel);
        }
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {

        // list of nodes        
        List<Node> nodes = new ArrayList<Node>();
        for (Node node : graph.getNodes()) {
            nodes.add(node);
        }
        int N = nodes.size();
        Random rand = new Random();

        // initialize map of timers: every node has a map of neighbours
        Map<Node, Map<Node, Integer>> timers = new HashMap<Node, Map<Node, Integer>>();
        for (Node node : nodes) {
            timers.put(node, new HashMap<Node, Integer>());
            for (Node neighbour : graph.getNeighbors(node)) {
                timers.get(node).put(neighbour, 0);
            }
        }
        // initialize map of amplitudes (alpha): every node has a map of neighbours
        Map<Node, Map<Node, Double>> alphas = new HashMap<Node, Map<Node, Double>>();
        for (Node node : nodes) {
            alphas.put(node, new HashMap<Node, Double>());
            for (Node neighbour : graph.getNeighbors(node)) {
                alphas.get(node).put(neighbour, alpha); // alpha * e^-(beta * 0)
            }
        }

        graph.readLock();
        Progress.start(progress);
        progress.switchToIndeterminate();

        //
        // generate interactions: random pair nodes
        //        

        Node node1, node2;
        Node[] neighbours;
        int _tij;
        double _aij, _wij;
        for (int t = 0; t < maxIterations; ++t) {
            // pick random node i
            node1 = nodes.get(rand.nextInt(N));
            // pick random neighbor
            neighbours = graph.getNeighbors(node1).toArray();
            node2 = neighbours[rand.nextInt(neighbours.length)];

            // evolve all weight timers i,j
            for (Node node : nodes) {
                for (Node neighbour : graph.getNeighbors(node)) {
                    _tij = timers.get(node).get(neighbour);
                    _tij++; // increase time
                    timers.get(node).put(neighbour, _tij); // update time++
                    //_aij = alpha * Math.exp(-beta * _tij); // apply dampening
                    //alphas.get(node).put(neighbour, _aij);
                }
            }

            // interact (1,2) at time t                                    
            // get current alpha
            _aij = alphas.get(node1).get(node2);
            // get current timer
            _tij = timers.get(node1).get(node2);
            // compute current weight
            _wij = _aij * Math.exp(-beta * _tij); // apply dampening
            // update current alpha
            _aij = Math.min(1.0, _wij + alpha); // because: _wij + _aij*e^0

            alphas.get(node1).put(node2, _aij);
            alphas.get(node2).put(node1, _aij);
            timers.get(node1).put(node2, 0);
            timers.get(node2).put(node1, 0);
        }


        // prepare log        
        try {
            File tmp = new File(System.getProperty("user.home") + "/Desktop/weights.txt");
            PrintWriter pw = new PrintWriter(tmp);

            Node[] _nodes = nodes.toArray(new Node[]{});
            double alpha, time, weight;

            for (int i = 0; i < _nodes.length - 1; ++i) {
                for (int j = i + 1; j < _nodes.length; ++j) {
                    if (timers.get(_nodes[i]) != null) {
                        if (timers.get(_nodes[i]).get(_nodes[j]) != null) {
                            alpha = alphas.get(_nodes[i]).get(_nodes[j]);
                            time = timers.get(_nodes[i]).get(_nodes[j]);
                            weight = alpha * Math.exp(-beta * time);
                            pw.println(weight);
                        }
                    }
                }
            }
            pw.close();

            //tmp.deleteOnExit(); // no-log on desktop
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

        progress.switchToDeterminate(100);
        progress.finish();
        graph.readUnlockAll();
    }

    public void executeIndependent(HierarchicalGraph graph, AttributeModel attributeModel) {

        // list of edges
        List<Edge> edges = new ArrayList<Edge>();
        for (Edge edge : graph.getEdges()) {
            edges.add(edge);
        }
        int E = edges.size();
        Random rand = new Random();
        int timeouts[] = new int[E];
        int timers[] = new int[E];
        double alphas[] = new double[E];

        // initialize edge timeout values >=1
        for (int i = 0; i < E; ++i) {
            timeouts[i] = rand.nextInt(edgeMaxTimeout + 1);
        }
        // initialize edge timers
        for (int i = 0; i < E; ++i) {
            timers[i] = 0;
        }
        // initialize edge amplitudes
        for (int i = 0; i < E; ++i) {
            alphas[i] = alpha;
        }

        graph.readLock();
        Progress.start(progress);
        progress.switchToIndeterminate();

        //
        // generate interactions: all edges with current timeout==0
        //        

        double _ai, _wi;

        for (int t = 0; t < maxIterations; ++t) {
            for (int i = 0; i < E; ++i) {
                // increase time for all edges
                timers[i]++;

                // interaction on edge 'i'
                if (timeouts[i] <= 0) {

                    // compute current weight
                    _wi = alphas[i] * Math.exp(-beta * timers[i]); // apply dampening
                    // update current alpha
                    _ai = Math.min(1.0, _wi + alpha); // because: _wij + alpha*e^0

                    alphas[i] = _ai;
                    timers[i] = 0;

                    // reset timeout value for this edge
                    timeouts[i] = rand.nextInt(edgeMaxTimeout + 1);

                } // no interaction
                else {
                    timeouts[i]--;
                }
            }
        }

        // prepare log        
        try {
            File tmp = new File(System.getProperty("user.home") + "/Desktop/weights.txt");
            PrintWriter pw = new PrintWriter(tmp);


            double weight;

            for (int i = 0; i < E; ++i) {
                weight = alphas[i] * Math.exp(-beta * timers[i]);
                pw.println(weight);
            }
            pw.close();

            //tmp.deleteOnExit(); // no-log on desktop
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

        progress.switchToDeterminate(100);
        progress.finish();
        graph.readUnlockAll();
    }
    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="Misc Area">
    private String errorReport = "";
    private String shortReport = "";

    public String getReport() {
        String report = "<HTML> <BODY> <h1>Social Influence Benchmark Report </h1> "
                + "<hr><br>";

        report += errorReport + "</BODY></HTML>";

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

    private class ExtraNodeData {

        int sleep = 0;
        float tolerance = 1f;
        float opinion = 0.5f;
        boolean isStubborn = false;
        // tolerance modification scaling used for intolerance (0)	 
        int scaling0 = 1;
        // tolerance modification scaling used for tolerance (1)	 
        int scaling1 = 1;

        ExtraNodeData(float opinion, float tolerance, int sleep, boolean isStubborn) {
            this.opinion = opinion;
            this.tolerance = tolerance;
            this.sleep = sleep;
            this.isStubborn = isStubborn;
        }

        boolean getNodeState() {
            return opinion >= 0.5f;
        }

        boolean getNodeState(float otherState) {
            return otherState >= 0.5f;
        }
    }
    // </editor-fold>
}