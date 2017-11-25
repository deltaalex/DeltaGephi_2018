package org.gephi.ui.statistics.plugin;

import javax.swing.JPanel;
import org.gephi.statistics.plugin.InfluenceMetricEnum;
import org.gephi.statistics.plugin.InfluenceRankings;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = StatisticsUI.class)
public class InfluenceRankingsUI implements StatisticsUI {

    private final StatSettings settings = new StatSettings();
    private InfluenceRankingsPanel panel;
    private InfluenceRankings infRank;

    public JPanel getSettingsPanel() {
        panel = new InfluenceRankingsPanel();
        return panel;
    }

    public void setup(Statistics statistics) {
        this.infRank = (InfluenceRankings) statistics;
        if (panel != null) {
            //settings.load(infRank);            

            switch (infRank.getSelectedMetric()) {
                case HINDEX:
                    panel.radioHirsch.setSelected(true);
                    break;
                case LEADERRANK:
                    panel.radioLeaderRank.setSelected(true);
                    break;
                case CLUSTERRANK:
                    panel.radioClusterRank.setSelected(true);
                    break;
                case LOCALCENTRALITY:
                    panel.radioLocalCentrality.setSelected(true);
                    break;
            }

            panel.setDirected(infRank.getDirected());
            panel.setEdgeWeight(infRank.isUseEdgeWeight());
        }
    }

    public void unsetup() {
        if (panel != null) {
            if (panel.radioHirsch.isSelected()) {
                infRank.setSelectedMetric(InfluenceMetricEnum.HINDEX);
            } else if (panel.radioLeaderRank.isSelected()) {
                infRank.setSelectedMetric(InfluenceMetricEnum.LEADERRANK);
            } else if (panel.radioClusterRank.isSelected()) {
                infRank.setSelectedMetric(InfluenceMetricEnum.CLUSTERRANK);
            } else if (panel.radioLocalCentrality.isSelected()) {
                infRank.setSelectedMetric(InfluenceMetricEnum.LOCALCENTRALITY);
            }

            infRank.setDirected(panel.isDirected());
            infRank.setUseEdgeWeight(panel.isEdgeWeight());
            //settings.save(infRank);
        }
        panel = null;
        infRank = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return InfluenceRankings.class;
    }

    public String getValue() {
        return null;
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "InfluenceRankingsUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
    }

    public int getPosition() {
        return 1300;
    }

    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "InfluenceRankingsUI.shortDescription");
    }

    private static class StatSettings {

        private boolean useEdgeWeight = false;
        private InfluenceMetricEnum metric;

        private void save(InfluenceRankings stat) {
            this.useEdgeWeight = stat.isUseEdgeWeight();
            this.metric = stat.getSelectedMetric();
        }

        private void load(InfluenceRankings stat) {
            stat.setUseEdgeWeight(useEdgeWeight);
            stat.setSelectedMetric(metric);
        }
    }
}
