package org.gephi.statistics.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class GirvanNewmanClusterer implements LongTask, Statistics {

    private static final int UNSEEN_CLUSTER = -1;
    private List<GirvanNewmanClusterImpl> result = new ArrayList<GirvanNewmanClusterImpl>();
    public static final String PLUGIN_NAME = "Girvan Newman";
    public static final String PLUGIN_DESCRIPTION = "Girvan Newman Clustering";
    public static final String CLUSTER = "cluster";
    public static final String PREV_CLUSTER = "prev_cluster";
    ProgressTicket progress = null;
    private static final Logger logger = Logger.getLogger(GirvanNewmanClusterer.class.getName());
    private Map<Integer, ArrayList<GirvanNewmanClusterImpl>> clusters = new HashMap<Integer, ArrayList<GirvanNewmanClusterImpl>>();
    boolean isCancelled = false;
    private GraphModel graphModel = null;
    private AttributeColumn clusterColumn;
    private Graph tempGraph;
    private AttributeColumn prevClusterColumn;
    private int selectedClustersCount = 0;
    private int preferredNumClusters = -1;
    private Vector<Integer> clusterCounts;

    public int getPreferredNumClusters() {
        return preferredNumClusters;
    }

    public void setPreferredNumClusters(int preferredNumClusters) {
        this.preferredNumClusters = preferredNumClusters;
    }

    @Override
    public void execute(GraphModel gm, AttributeModel attributeModel) {
        long startAlg = System.currentTimeMillis();

        this.graphModel = gm;
        this.isCancelled = false;
        if (progress != null) {
            this.progress.progress(NbBundle.getMessage(GirvanNewmanClusterer.class, "GirvanNewmanClusterer.setup"));
            this.progress.start();
        }

        GraphView view = graphModel.newView();
        tempGraph = graphModel.getGraph(view);

        long startInnerAlg = System.currentTimeMillis();
        this.clusterCounts = new Vector<Integer>();

        // Count betweenness for all edges - ONLY ONCE!
        recalculateBetweenness();

        while (tempGraph.getEdgeCount() > 0) {
            if (isCancelled) {
                return;
            }

            // Remove edge with highest betweeness
            EdgeIterable edges = tempGraph.getEdges();
            Double maxBetw = Double.NEGATIVE_INFINITY;

            for (Edge e : edges) {
                Double centrality = (Double) e.getEdgeData().getAttributes().getValue(EdgeBetweenness.EDGE_BETWEENNESS);
                if (centrality > maxBetw) {
                    maxBetw = centrality;
                }
            }


            for (Edge maxEdge : findEdgesWithBetweenness(maxBetw)) {
                tempGraph.removeEdge(maxEdge);
            }

            // after removal of edges, find clusters
            int clusterCount = findClusters();
            if (getClusterCounts().isEmpty() || (getClusterCounts().lastElement() != clusterCount)) {
                getClusterCounts().add(clusterCount);
            }
        }
        long endInnerAlg = System.currentTimeMillis() - startInnerAlg;
        System.out.println("endInnerAlg: " + endInnerAlg);

        if (this.preferredNumClusters > 0) {
            selectedClustersCount = preferredNumClusters; //findNearest(getClusterCounts());
            System.out.println("Cluster counts: " + getClusterCounts().toString());
        } else {
            //SelectClustersPanel panel = new SelectClustersPanel(getClusterCounts());

            //DialogDescriptor dd = new DialogDescriptor(panel, "Select number of clusters", true, null);
            //if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            //selectedClustersCount = panel.getSelectedClustersCount();
            //selectedClustersCount = 6;
            selectedClustersCount = preferredNumClusters;
            //}
        }

        result = clusters.get(selectedClustersCount);

        AttributeTable nodeTable = Lookup.getDefault().lookup(AttributeController.class).getModel().getNodeTable();

        GraphColorizer c = new GraphColorizer(nodeTable);
        if (result != null && result.size() > 0) {
            c.colorizeGraph(result.toArray(new GirvanNewmanClusterImpl[selectedClustersCount]));
        }


        if (progress != null) {
            this.progress.finish(NbBundle.getMessage(GirvanNewmanClusterer.class, "GirvanNewmanClusterer.finished"));
        }

        long endAlg = System.currentTimeMillis() - startAlg;
        System.out.println("endAlg: " + endAlg);
    }

    public GirvanNewmanClusterImpl[] getClusters() {
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.toArray(new GirvanNewmanClusterImpl[selectedClustersCount]);
    }

    @Override
    public boolean cancel() {
        this.progress.finish(NbBundle.getMessage(GirvanNewmanClusterer.class, "GirvanNewmanClusterer.cancelled"));
        return this.isCancelled = true;
    }

    @Override
    public void setProgressTicket(ProgressTicket pt) {
        this.progress = pt;
    }

    private AttributeColumn recalculateBetweenness() {
        if (tempGraph == null) {
            throw new IllegalStateException("tempGraph can not be null");
        }
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();

        return recalculateBetweenness(tempGraph.getGraphModel());
    }

    private AttributeColumn recalculateBetweenness(GraphModel model) {
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();

        EdgeBetweenness eb = new EdgeBetweenness();
        eb.setDirected(false);
        eb.execute(model, attributeModel);

        return attributeModel.getNodeTable().getColumn(EdgeBetweenness.EDGE_BETWEENNESS);
    }

    private Edge[] findEdgesWithBetweenness(Double maxBetw) {
        if (tempGraph == null) {
            throw new IllegalStateException("tempGraph can not be null");
        }
        ArrayList<Edge> res = new ArrayList<Edge>();

        for (Edge e : tempGraph.getEdges()) {
            double betw = (Double) e.getEdgeData().getAttributes().getValue(EdgeBetweenness.EDGE_BETWEENNESS);
            if (betw == maxBetw) {
                res.add(e);
            }
        }
        Edge[] resArr = new Edge[res.size()];
        return res.toArray(resArr);
    }

    private int findClusters() {
        if (tempGraph == null) {
            throw new IllegalStateException("tempGraph can not be null");
        }

        prevClusterColumn = copyClusterToPrev();
        clusterColumn = prepareClusterColumn(UNSEEN_CLUSTER);
        int clusterNumber = 0;

        ArrayList<GirvanNewmanClusterImpl> currCluster = new ArrayList<GirvanNewmanClusterImpl>();
        for (Node rootNode : tempGraph.getNodes()) {
            int nodeClusterId = getNodeClusterId(rootNode);

            if (nodeClusterId == UNSEEN_CLUSTER) {
                //doBFS
                GirvanNewmanClusterImpl tmpCluster = bfsMarkNodes(rootNode, clusterNumber++);
                currCluster.add(tmpCluster);
            }
        }

        int clustersCount = clusterNumber;
        clusters.put(clustersCount, currCluster);
        return clusterNumber;
    }

    private AttributeColumn prepareClusterColumn(int initialValue) {
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeTable nodeTable = ac.getModel().getNodeTable();
        AttributeColumn column = nodeTable.getColumn(CLUSTER, AttributeType.INT);

        if (column != null) {
            nodeTable.removeColumn(column);
        }

        return nodeTable.addColumn(CLUSTER, CLUSTER, AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(initialValue));
    }

    private AttributeColumn copyClusterToPrev() {
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeTable nodeTable = ac.getModel().getNodeTable();
        if (nodeTable.hasColumn(CLUSTER)) {
            clusterColumn = nodeTable.getColumn(CLUSTER, AttributeType.INT);

            if (nodeTable.hasColumn(PREV_CLUSTER)) {
                nodeTable.removeColumn(nodeTable.getColumn(PREV_CLUSTER));
            }

            AttributeColumn duplicateColumn = duplicateColumn(nodeTable, CLUSTER, PREV_CLUSTER, AttributeType.INT);

            return duplicateColumn;
        }
        return null;
    }

    private AttributeColumn duplicateColumn(AttributeTable nodeTable, String sourceColumnName, String newColumnName, AttributeType attributeType) {
        // create a new empty column named 'newName'
        AttributeColumn duplicateColumn = nodeTable.addColumn(newColumnName, attributeType);

        // copy data from 'column' to the new column by iterating each node
        for (Node node : tempGraph.getNodes()) {
            // get cluster value from source column
            Object value = node.getNodeData().getAttributes().getValue(sourceColumnName);
            // copy value to new column
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(duplicateColumn, value);
        }

        return duplicateColumn;
    }

    private int getNodeClusterId(Node node) {
        return getParamClusterId(node, clusterColumn);
    }

    private int getNodeClusterPrevId(Node node) {
        return getParamClusterId(node, prevClusterColumn);
    }

    private int getParamClusterId(Node node, AttributeColumn attr) {
        if (attr == null) {
            throw new IllegalStateException("cluster Column must be initialized before calling getParamClusterId");
        }
        AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
        return (Integer) row.getValue(attr);
    }

    private void setNodeClusterId(Node node, int newClusterId) {
        if (clusterColumn == null) {
            throw new IllegalStateException("clusterColumn must be initialized before calling setNodeClusterId");
        }
        AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();

        row.setValue(clusterColumn, newClusterId);
    }

    private GirvanNewmanClusterImpl bfsMarkNodes(Node rootNode, int newClusterId) {
        if (tempGraph == null) {
            throw new IllegalStateException("tempGraph cannot be null");
        }

        GirvanNewmanClusterImpl cluster = new GirvanNewmanClusterImpl();
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.addLast(rootNode);

        while (!queue.isEmpty()) {
            Node v = queue.removeFirst();
            // set node cluster id for node itself
            if (getNodeClusterId(v) == UNSEEN_CLUSTER) {
                setNodeClusterId(v, newClusterId);
                cluster.addNode(v);
                cluster.setName("Cluster " + Integer.toString(newClusterId + 1));
                if (prevClusterColumn != null) {
                    int prev = getNodeClusterPrevId(v);
                }
            }

            // set node cluster id for all its neighbors
            for (Node n : tempGraph.getNeighbors(v).toArray()) {
                if (getNodeClusterId(n) != newClusterId) {
                    queue.add(n);
                    setNodeClusterId(n, newClusterId);
                    cluster.addNode(n);
                }
            }
        }
        return cluster;
    }

    public void setPreferredNumberOfClusters(int clusters) {
        preferredNumClusters = clusters;
    }

    private int findNearest(Vector<Integer> clusterCounts) {
        if (clusterCounts.contains(this.preferredNumClusters)) {
            return this.preferredNumClusters;
        }

        int bestDistance = Integer.MAX_VALUE;
        int nearest = -1;

        for (int i : clusterCounts) {
            int d = Math.abs(this.preferredNumClusters - i);
            if (d < bestDistance) {
                nearest = i;
                bestDistance = d;
            }
        }
        return nearest;
    }

    /**
     * @return the clusterCounts
     */
    public Vector<Integer> getClusterCounts() {
        return clusterCounts;
    }

    public String getReport() {
        String report = "<HTML> <BODY> <h1>GirvanNewman Report </h1> "
                + "<hr>"
                + "Number of clusters: " + selectedClustersCount
                + "</BODY> </HTML>";

        return report;
    }
}