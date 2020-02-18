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
            double pSeeders = socialInfluence.getPSeeders();
            double nSeeders = socialInfluence.getNSeeders();
            int period = socialInfluence.getInjectPeriod();
            double fillingFactor = socialInfluence.getFillingFactor();

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
            
            panel.checkKShell.setSelected(centrality.equals(BenchmarkCentrality.KSHELL));
            panel.checkCommunityRank.setSelected(centrality.equals(BenchmarkCentrality.COMMUNITYRANK));
            panel.checkGeneticRank.setSelected(centrality.equals(BenchmarkCentrality.GENETICRANK));

            panel.spreaderPField.setText(String.valueOf(pSeeders));
            panel.checkSR1e4.setSelected(pSeeders == 0.0001);
            panel.checkSR3e4.setSelected(pSeeders == 0.0003);
            panel.checkSR1e3.setSelected(pSeeders == 0.001);
            panel.checkSR3e3.setSelected(pSeeders == 0.003);
            panel.checkSR1e2.setSelected(pSeeders == 0.01);
            panel.checkSR3e2.setSelected(pSeeders == 0.03);
            panel.checkSR1e1.setSelected(pSeeders == 0.1);
            panel.checkSR3e1.setSelected(pSeeders == 0.3);            
            
            panel.spreaderNField.setText(String.valueOf(nSeeders));            

            panel.periodField.setText(String.valueOf(period));
            panel.checkPeriod10.setSelected(period == 10);
            panel.checkPeriod20.setSelected(period == 20);
            panel.checkPeriod50.setSelected(period == 50);
            panel.checkPeriod100.setSelected(period == 100);
            panel.checkPeriod200.setSelected(period == 200);
            panel.checkPeriod500.setSelected(period == 500);
            panel.checkPeriod1000.setSelected(period == 1000);
            panel.checkPeriod2000.setSelected(period == 2000);

            panel.fillingFactorField.setText(String.valueOf(fillingFactor));
            panel.checkFill20.setSelected(fillingFactor == 0.2);
            panel.checkFill40.setSelected(fillingFactor == 0.4);
            panel.checkFill60.setSelected(fillingFactor == 0.6);
            panel.checkFill80.setSelected(fillingFactor == 0.8);
            panel.checkFill100.setSelected(fillingFactor == 1.0);
        }
    }

    public void unsetup() {
        if (panel != null) {
            BenchmarkCentrality centrality = BenchmarkCentrality.DEGREE;
            double pSeeders = Double.parseDouble(panel.spreaderPField.getText());
            double nSeeders = Double.parseDouble(panel.spreaderNField.getText());
            int period = Integer.parseInt(panel.periodField.getText());
            double fillingFactor = Double.parseDouble(panel.fillingFactorField.getText());

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
            if (panel.checkKShell.isSelected()) {
                centrality = BenchmarkCentrality.KSHELL;
            }
            if (panel.checkCommunityRank.isSelected()) {
                centrality = BenchmarkCentrality.COMMUNITYRANK;
            }
            if (panel.checkGeneticRank.isSelected()) {
                centrality = BenchmarkCentrality.GENETICRANK;
            }

            if (panel.checkSR1e4.isSelected()) {
                pSeeders = 0.0001;
            }
            if (panel.checkSR3e4.isSelected()) {
                pSeeders = 0.0003;
            }
            if (panel.checkSR1e3.isSelected()) {
                pSeeders = 0.001;
            }
            if (panel.checkSR3e3.isSelected()) {
                pSeeders = 0.003;
            }
            if (panel.checkSR1e2.isSelected()) {
                pSeeders = 0.01;
            }
            if (panel.checkSR3e2.isSelected()) {
                pSeeders = 0.03;
            }
            if (panel.checkSR1e1.isSelected()) {
                pSeeders = 0.1;
            }
            if (panel.checkSR3e1.isSelected()) {
                pSeeders = 0.3;
            }

            if (panel.checkPeriod10.isSelected()) {
                period = 10;
            }
            if (panel.checkPeriod20.isSelected()) {
                period = 20;
            }
            if (panel.checkPeriod50.isSelected()) {
                period = 50;
            }
            if (panel.checkPeriod100.isSelected()) {
                period = 100;
            }
            if (panel.checkPeriod200.isSelected()) {
                period = 200;
            }
            if (panel.checkPeriod500.isSelected()) {
                period = 500;
            }
            if (panel.checkPeriod1000.isSelected()) {
                period = 1000;
            }
            if (panel.checkPeriod2000.isSelected()) {
                period = 2000;
            }

            if (panel.checkFill20.isSelected()) {
                fillingFactor = 0.2;
            }
            if (panel.checkFill40.isSelected()) {
                fillingFactor = 0.4;
            }
            if (panel.checkFill60.isSelected()) {
                fillingFactor = 0.6;
            }
            if (panel.checkFill80.isSelected()) {
                fillingFactor = 0.8;
            }
            if (panel.checkFill100.isSelected()) {
                fillingFactor = 1.0;
            }

            socialInfluence.setCentrality(centrality);
            socialInfluence.setPSeeders(pSeeders);
            socialInfluence.setNSeeders(nSeeders);
            socialInfluence.setInjectPeriod(period);
            socialInfluence.setFillingFactor(fillingFactor);
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
