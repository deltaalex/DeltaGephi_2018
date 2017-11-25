package org.gephi.statistics.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsCallbackProvider;
import org.gephi.statistics.spi.StatisticsUICallbackProvider;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;

/**
 * Compares two networks using the delta fidelity metric. <br> Uses basic
 * metrics as inputs: average D, L, C, modularity, density, diameter. <br>
 *
 * @author Alexander
 */
public class DeltaComparison implements Statistics, LongTask, StatisticsCallbackProvider {

    /**
     * Remembers if the Cancel function has been called.
     */
    private boolean isCanceled;
    /**
     * Keep track of the work done.
     */
    private ProgressTicket progress;
    /**
     * Threshold for simulation stop
     */
    private double dissimilarity = 0.5;
    /**
     * flag for simulation instead of delta measurement
     */
    private boolean enableSimulation = false;
    /**
     * Edge type
     */
    private boolean directed = false;
    /**
     * Base metrics to which the comparison is done.
     */
    private Double[] baseMetrics;
    /**
     * Measured metrics which are compared to the base metrics.
     */
    private Double[] measuredMetrics;
    /**
     * List of metrics being used for comparison
     */
    private ArrayList<Metric> metrics = new ArrayList<Metric>();
    /**
     * List of enabled measures (actually taken into account)
     */
    private Boolean[] enabledMetrics;
    /**
     * Number of enabled metrics
     */
    private int numEnabled;
    // delta metrics
    private double deltaA;
    private double deltaG;
    private double deltaH;
    // UI parent
    private StatisticsUICallbackProvider parent;

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    private double getDelta(double measured, int index) {

        if (baseMetrics[index] > measured && measured >= 0) {
            return baseMetrics[index] / (2 * baseMetrics[index] - measured);
        } else if (0 < baseMetrics[index] && baseMetrics[index] <= measured) {
            return baseMetrics[index] / measured;
        } else {
            return 1.0 / (measured + 1);
        }
    }

    public double getDeltaArithmetic() {
        return deltaA;
    }

    public double getDeltaGeometric() {
        return deltaG;
    }

    public double getDeltaHarmonic() {
        return deltaH;
    }

    public Boolean[] getEnabledMetrics() {
        if (enabledMetrics == null) {
            numEnabled = 6;
            enabledMetrics = new Boolean[numEnabled];
            for (int i = 0; i < numEnabled; ++i) {
                enabledMetrics[i] = true;
            }
        }

        return enabledMetrics;
    }

    public boolean getEnableSimulation() {
        return enableSimulation;
    }

    public double getDissimilarity() {
        return dissimilarity;
    }

    public void setMetrics(List<Double> metrics) {
        this.baseMetrics = metrics.toArray(new Double[]{});

        this.measuredMetrics = new Double[this.baseMetrics.length];
    }

    public void setEnabledMetrics(ArrayList<Boolean> enabledMetrics) {
        this.enabledMetrics = enabledMetrics.toArray(new Boolean[]{});

        // measure how many metrics are enabled
        numEnabled = 0;
        for (boolean b : enabledMetrics) {
            if (b) {
                numEnabled++;
            }
        }
    }

    public void setEnableSimulation(boolean enableSimulation) {
        this.enableSimulation = enableSimulation;
    }

    public void setDissimilarity(double dissimilarity) {
        this.dissimilarity = dissimilarity;
    }

    // </editor-fold> 
    // <editor-fold defaultstate="collapsed" desc="Execution">
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {

        if (enableSimulation) {
            int watchdog = 0;

            PrintWriter pw = null;
            try {
                File tmp = new File(System.getProperty("user.home") + "/Desktop/fidelity.csv");
                pw = new PrintWriter(tmp);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

            // save initial metrics as base metrics
            executeOneStep(graph, attributeModel);
            setMetrics(Arrays.asList(measuredMetrics));

            // repat simulation until fidelity drops
            deltaA = 1;
            while (deltaA > dissimilarity && watchdog <2000) {
                // measure fidelity
                executeOneStep(graph, attributeModel);

                pw.println(watchdog + "," + deltaA + "," + deltaG + "," + deltaH);

                // do random step
                swapRandomEdge(graph);
                watchdog++;
            }
            
            pw.close();
        } else {
            executeOneStep(graph, attributeModel);
        }
    }

    private void executeOneStep(HierarchicalGraph graph, AttributeModel attributeModel) {
        graph.readLock();
        Progress.start(progress);
        progress.switchToIndeterminate();

        // average degree        
        Degree degree = new Degree();
        degree.setProgressTicket(progress);
        degree.execute(graph.getGraphModel(), attributeModel);
        double adeg = degree.getAverageDegree();

        // average path length        
        GraphDistance distance = new GraphDistance();
        distance.setNormalized(false);
        distance.setDirected(directed);
        distance.setProgressTicket(progress);
        distance.execute(graph.getGraphModel(), attributeModel);
        double apl = distance.getPathLength();

        // clustering coefficient
        ClusteringCoefficient clustering = new ClusteringCoefficient();
        clustering.setDirected(directed);
        clustering.setProgressTicket(progress);
        clustering.execute(graph.getGraphModel(), attributeModel);
        double cc = clustering.getAverageClusteringCoefficient();

        // modularity               
        Modularity modularity = new Modularity();
        modularity.setRandom(true);
        modularity.setUseWeight(false);
        modularity.setResolution(1.0);
        modularity.setProgressTicket(progress);
        modularity.execute(graph.getGraphModel(), attributeModel);
        double mod = modularity.getModularity();

        // density
        GraphDensity density = new GraphDensity();
        density.setDirected(directed);
        density.execute(graph.getGraphModel(), attributeModel);
        double dns = density.getDensity();

        // diameter        
        double dmt = distance.getDiameter();

        measuredMetrics[0] = adeg;
        measuredMetrics[1] = apl;
        measuredMetrics[2] = cc;
        measuredMetrics[3] = mod;
        measuredMetrics[4] = dns;
        measuredMetrics[5] = dmt;

        progress.switchToDeterminate(100);
        progress.finish();
        graph.readUnlockAll();

        //
        // compute deltas
        //
        deltaA = 0.0;
        deltaG = 1.0;
        deltaH = 0.0;

        // harmonic = G^n / A(prod/xi)
        // http://en.wikipedia.org/wiki/Harmonic_mean#Harmonic_mean_of_two_numbers                
        double[] deltas = new double[measuredMetrics.length];

        // arithmetic / geometric 
        for (int i = 0; i < deltas.length; ++i) {
            // only if enabled
            if (enabledMetrics[i]) {
                deltas[i] = getDelta(measuredMetrics[i], i);

                deltaA += deltas[i];
                deltaG *= deltas[i];
            }
        }

        // A(prod/xi)
        double arit = 0.0;
        for (int i = 0; i < deltas.length; ++i) {
            if (enabledMetrics[i]) {
                arit += deltaG / deltas[i];
            }
        }

        arit /= numEnabled;
        deltaH = deltaG == 0 ? 0 : deltaG / arit;

        deltaA /= numEnabled;
        deltaG = Math.pow(deltaG, 1.0 / numEnabled);

        //
        // UI callback
        //
        if (parent != null) {
            parent.callback(measuredMetrics);
        }
    }

    private void swapRandomEdge(HierarchicalGraph graph) {
        Random rand = new Random();

        Edge[] edges = graph.getEdges().toArray();
        Edge anEdge = edges[rand.nextInt(edges.length)];
        Node[] nodes = graph.getNodes().toArray();

        Node source = anEdge.getSource();
        Node target = anEdge.getTarget();

        // change target to other random node
        Node otherNode = target;
        while (target.equals(otherNode)) {
            otherNode = nodes[rand.nextInt(nodes.length)];
        }

        Edge newEdge = graph.getGraphModel().factory().newEdge(source, otherNode);
        graph.removeEdge(anEdge);
        graph.addEdge(newEdge);
    }
    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="Misc Area">
    private String errorReport = "";

    public String getReport() {
        NumberFormat f = new DecimalFormat("#0.0000");

        String report = "<HTML> <BODY> <h1>Delta Comparison Report </h1> "
                + "<hr><br>";

        report += "<table border=\"1\"><tr><th></th>";
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
        report += "Harmonic Delta: " + f.format(getDeltaHarmonic()) + "<br>";

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

    public void setStatisticsUIProvider(StatisticsUICallbackProvider parent) {
        this.parent = parent;
    }

    public static enum Metric {

        AVGDEG, APL, CC, MOD, DNS, DMT
    }
}
