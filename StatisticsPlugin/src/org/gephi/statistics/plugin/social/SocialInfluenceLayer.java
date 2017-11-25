package org.gephi.statistics.plugin.social;

import org.gephi.statistics.plugin.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.openide.util.NbBundle;

/**
 * Adds power-law distributed weights on a social network based on the
 * normalized betweenness of nodes in each dyad. <br> TODO: Determines influence
 * of nodes in social network based on community heterogeneity <br>
 *
 * @author Alexander
 */
public class SocialInfluenceLayer implements Statistics, LongTask {

    public static final String INFLUENCE1 = "influence1";
    public static final String INFLUENCE2 = "influence2";
    public static final String INFLUENCE3 = "influence3";
    public static final String INFLUENCE4 = "influence4";
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
    /*
     * List of enabled fitnesses
     */
    private List<Fitness> fitnesses;

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">   
    public void setFitnesses(List<Fitness> fitnesses) {
        this.fitnesses = fitnesses;
    }

    public List<Fitness> getFitnesses() {
        return fitnesses;
    }

    private Integer getDegree(Node node) {
        return (Integer) node.getAttributes().getValue(Degree.DEGREE);
    }

    private Double getBetweenness(Node node) {
        return (Double) node.getAttributes().getValue(GraphDistance.BETWEENNESS);
    }

    private Integer getCommunity(Node node) {
        return (Integer) node.getAttributes().getValue(Modularity.MODULARITY_CLASS);
    }

    // </editor-fold> 
    // <editor-fold defaultstate="collapsed" desc="Execution">
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {

        // atributes
        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn inf1Col = nodeTable.getColumn(INFLUENCE1);
        AttributeColumn inf2Col = nodeTable.getColumn(INFLUENCE2);
        AttributeColumn inf3Col = nodeTable.getColumn(INFLUENCE3);
        AttributeColumn inf4Col = nodeTable.getColumn(INFLUENCE4);

        if (inf1Col == null) {
            inf1Col = nodeTable.addColumn(INFLUENCE1, NbBundle.getMessage(SocialInfluenceLayer.class, "SocialInfluenceLayer.nodecolumn.Influence1"), AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0));
        }
        if (inf2Col == null) {
            inf2Col = nodeTable.addColumn(INFLUENCE2, NbBundle.getMessage(SocialInfluenceLayer.class, "SocialInfluenceLayer.nodecolumn.Influence2"), AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0));
        }
        if (inf3Col == null) {
            inf3Col = nodeTable.addColumn(INFLUENCE3, NbBundle.getMessage(SocialInfluenceLayer.class, "SocialInfluenceLayer.nodecolumn.Influence3"), AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0));
        }
        if (inf4Col == null) {
            inf4Col = nodeTable.addColumn(INFLUENCE4, NbBundle.getMessage(SocialInfluenceLayer.class, "SocialInfluenceLayer.nodecolumn.Influence4"), AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0));
        }

        graph.readLock();
        Progress.start(progress);
        progress.switchToIndeterminate();

        // degree                
        Degree degree = new Degree();
        degree.setProgressTicket(progress);
        degree.execute(graph.getGraphModel(), attributeModel);
        degree.getAverageDegree();

        // betweenness
        GraphDistance distance = new GraphDistance();
        distance.setNormalized(false);
        distance.setDirected(directed);
        distance.setProgressTicket(progress);
        distance.execute(graph.getGraphModel(), attributeModel);
        distance.getPathLength();

        // modularity                       
        Modularity modularity = new Modularity();
        modularity.setRandom(true);
        modularity.setUseWeight(false);
        modularity.setResolution(1.0);
        modularity.setProgressTicket(progress);
        modularity.execute(graph.getGraphModel(), attributeModel);
        double mod = modularity.getModularity();

        progress.switchToDeterminate(100);
        progress.finish();
        graph.readUnlockAll();

        //
        // compute influences:         
        //

        // compute community centers
        HashMap<Integer, CommunityCenter> centers = new HashMap<Integer, CommunityCenter>();
        for (Node node : graph.getNodes()) {
            int id = getCommunity(node);
            float x = node.getNodeData().x();
            float y = node.getNodeData().y();

            CommunityCenter community = centers.get(id);

            // no community yet for this id
            if (community == null) {
                community = new CommunityCenter(id);
                centers.put(id, community);
            }

            // update community
            community.x.add(x);
            community.y.add(y);
        }

        // compute community average point of gravity (x & y)
        for (Integer id : centers.keySet()) {
            centers.get(id).computeAverages();
        }

        // compute influences for each nodeF
        for (Node node : graph.getNodes()) {
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();

            // get node metrics
            double deg = getDegree(node);
            double btw = getBetweenness(node);

            // influence 1: btw/deg
            double inf1 = 1.0 * btw / (deg != 0 ? deg : 1);

            // influence2: distance from center of community
            double distCX = centers.get(getCommunity(node)).xAvg;
            double distCY = centers.get(getCommunity(node)).yAvg;
            double distX = node.getNodeData().x();
            double distY = node.getNodeData().y();
            double dist = Math.sqrt((distX - distCX) * (distX - distCX) + (distY - distCY) * (distY - distCY));
            double inf2 = 1.0 / (dist != 0 ? dist : 1e-8);

            // influence 3: inf1 * inf2
            double inf3 = btw * inf2;            

            // influence 4: inf1 / inf2
            double inf4 = inf1 / inf2;

            inf2 = btw / inf2;
            
            // set attributes
            row.setValue(inf1Col, inf1);
            row.setValue(inf2Col, inf2);
            row.setValue(inf3Col, inf3);
            row.setValue(inf4Col, inf4);
        }

        errorReport = "Found " + centers.keySet().size() + " communities, each with the following centers: \n";
        for (Integer id : centers.keySet()) {
            errorReport += "community " + id + ": [" + centers.get(id).xAvg + ", " + centers.get(id).yAvg + "]\n";
        }

    }
    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="Misc Area">
    private String errorReport = "";

    public String getReport() {
        NumberFormat f = new DecimalFormat("#0.0000");

        String report = "<HTML> <BODY> <h1>Social Influence Report </h1> "
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
    // </editor-fold>

    public static enum Fitness {

        DEGREE, BETWEENNESS, EIGENVECTOR, CLOSENESS, CLUSTERING
    }

    class CommunityCenter {

        private int id;
        private List<Float> x = new ArrayList<Float>();
        private List<Float> y = new ArrayList<Float>();
        private double xAvg = 0.0, yAvg = 0.0;

        CommunityCenter(int id) {
            this.id = id;
        }

        private void computeAverages() {
            xAvg = 0;
            for (float i : x) {
                xAvg += i;
            }
            xAvg /= x.size();

            yAvg = 0;
            for (float i : y) {
                yAvg += i;
            }
            yAvg /= y.size();
        }
    }
}
