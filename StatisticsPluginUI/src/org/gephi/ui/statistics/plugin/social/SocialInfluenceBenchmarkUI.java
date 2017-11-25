package org.gephi.ui.statistics.plugin.social;

import javax.swing.JPanel;
import org.gephi.statistics.plugin.social.SocialInfluenceBenchmark;
import org.gephi.statistics.plugin.social.SocialInfluenceBenchmark.BenchmarkCentrality;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsUI.class)
public class SocialInfluenceBenchmarkUI implements StatisticsUI {

    //private final StatSettings settings = new StatSettings();
    private SocialInfluenceBenchmarkPanel panel;
    private SocialInfluenceBenchmark socialInfluence;

    public JPanel getSettingsPanel() {
        if (panel == null) {
            panel = new SocialInfluenceBenchmarkPanel();
        }
        return SocialInfluenceBenchmarkPanel.createValidationPanel(panel);
    }

    public void setup(Statistics statistics) {
        this.socialInfluence = (SocialInfluenceBenchmark) statistics;
        if (panel != null) {
            BenchmarkCentrality centrality = socialInfluence.getCentrality();

            if (centrality == null) {
                panel.checkDegree.setSelected(true);
                return;
            }

            panel.checkDegree.setSelected(centrality.equals(BenchmarkCentrality.DEGREE));
            panel.checkBtw.setSelected(centrality.equals(BenchmarkCentrality.BETWEENNESS));
            panel.checkEigen.setSelected(centrality.equals(BenchmarkCentrality.EIGENVECTOR));
            panel.checkClose.setSelected(centrality.equals(BenchmarkCentrality.CLOSENESS));

            panel.checkPageRank.setSelected(centrality.equals(BenchmarkCentrality.PAGERANK));
            panel.checkHits.setSelected(centrality.equals(BenchmarkCentrality.HITS));
            panel.checkBDPower.setSelected(centrality.equals(BenchmarkCentrality.BDPOWER));
            panel.checkBDInfluence.setSelected(centrality.equals(BenchmarkCentrality.BDINFLUENCE));

            panel.checkHirschIndex.setSelected(centrality.equals(BenchmarkCentrality.HINDEX));
            panel.checkClusterRank.setSelected(centrality.equals(BenchmarkCentrality.CLUSTERRANK));
            panel.checkLeaderRank.setSelected(centrality.equals(BenchmarkCentrality.LEADERRANK));
            panel.checkLocalCentrality.setSelected(centrality.equals(BenchmarkCentrality.LOCALCENTRALITY));
        }
    }

    public void unsetup() {
        if (panel != null) {
            BenchmarkCentrality centrality = BenchmarkCentrality.DEGREE;

            if (panel.checkDegree.isSelected()) {
                centrality = BenchmarkCentrality.DEGREE;
            }
            if (panel.checkBtw.isSelected()) {
                centrality = BenchmarkCentrality.BETWEENNESS;
            }
            if (panel.checkEigen.isSelected()) {
                centrality = BenchmarkCentrality.EIGENVECTOR;
            }
            if (panel.checkClose.isSelected()) {
                centrality = BenchmarkCentrality.CLOSENESS;
            }
            if (panel.checkPageRank.isSelected()) {
                centrality = BenchmarkCentrality.PAGERANK;
            }
            if (panel.checkHits.isSelected()) {
                centrality = BenchmarkCentrality.HITS;
            }
            if (panel.checkBDPower.isSelected()) {
                centrality = BenchmarkCentrality.BDPOWER;
            }
            if (panel.checkBDInfluence.isSelected()) {
                centrality = BenchmarkCentrality.BDINFLUENCE;
            }
            if (panel.checkHirschIndex.isSelected()) {
                centrality = BenchmarkCentrality.HINDEX;
            }            
            if (panel.checkClusterRank.isSelected()) {
                centrality = BenchmarkCentrality.CLUSTERRANK;
            }
            if (panel.checkLeaderRank.isSelected()) {
                centrality = BenchmarkCentrality.LEADERRANK;
            }
            if (panel.checkLocalCentrality.isSelected()) {
                centrality = BenchmarkCentrality.LOCALCENTRALITY;
            }

            socialInfluence.setCentrality(centrality);
        }
        socialInfluence = null;
        panel = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return SocialInfluenceBenchmark.class;
    }

    public String getValue() {
        return "Done";
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "SocialInfluenceBenchmarkUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_SOCIAL;
    }

    public int getPosition() {
        return 700;
    }

    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "SocialInfluenceBenchmarkUI.shortDescription");
    }
}
