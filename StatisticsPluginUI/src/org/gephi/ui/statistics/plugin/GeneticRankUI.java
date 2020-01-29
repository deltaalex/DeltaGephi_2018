package org.gephi.ui.statistics.plugin;

import javax.swing.JPanel;
import org.gephi.statistics.plugin.InfluenceMetricEnum;
import org.gephi.statistics.plugin.GeneticRank;
import org.gephi.statistics.plugin.social.SocialInfluenceBenchmark;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = StatisticsUI.class)
public class GeneticRankUI implements StatisticsUI {

    private final StatSettings settings = new StatSettings();
    private GeneticRankPanel panel;
    private GeneticRank genRank;

    public JPanel getSettingsPanel() {
        panel = new GeneticRankPanel();
        return panel;
    }

    public void setup(Statistics statistics) {
        this.genRank = (GeneticRank) statistics;
        if (panel != null) {
            //settings.load(infRank);            

            int nSpreaders = genRank.getSpreaders();
            int nGenerations = genRank.getGenerations();
            int nIndividuals = genRank.getIndividuals();

            double elitism = genRank.getElitism();
            double crossover = genRank.getCrossover();
            double mutation = genRank.getMutation();

            panel.spreadersField.setText(String.valueOf(nSpreaders));
            panel.generationsField.setText(String.valueOf(nGenerations));
            panel.individualsField.setText(String.valueOf(nIndividuals));

            panel.elitismField.setText(String.valueOf(elitism));
            panel.crossoverField.setText(String.valueOf(crossover));
            panel.mutationField.setText(String.valueOf(mutation));
        }
    }

    public void unsetup() {
        if (panel != null) {
            int nSpreaders = Integer.parseInt(panel.spreadersField.getText());
            int nGenerations = Integer.parseInt(panel.generationsField.getText());
            int nIndividuals = Integer.parseInt(panel.individualsField.getText());
            
            double elitism = Double.parseDouble(panel.elitismField.getText());
            double crossover = Double.parseDouble(panel.crossoverField.getText());
            double mutation = Double.parseDouble(panel.mutationField.getText());
            
            genRank.setSpreaders(nSpreaders);
            genRank.setGenerations(nGenerations);
            genRank.setIndividuals(nIndividuals);
            
            genRank.setElitism(elitism);
            genRank.setCrossover(crossover);
            genRank.setMutation(mutation);
            
            //settings.save(infRank);
        }
        panel = null;
        genRank = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return GeneticRank.class;
    }

    public String getValue() {
        return null;
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "GeneticRankUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
    }

    public int getPosition() {
        return 1400;
    }

    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "GeneticRankUI.shortDescription");
    }

    private static class StatSettings {

        private boolean useEdgeWeight = false;
        private InfluenceMetricEnum metric;

        private void save(GeneticRank stat) {
            //this.useEdgeWeight = stat.isUseEdgeWeight();
            //this.metric = stat.getSelectedMetric();
        }

        private void load(GeneticRank stat) {
            //stat.setUseEdgeWeight(useEdgeWeight);
            //stat.setSelectedMetric(metric);
        }
    }
}
