package org.gephi.ui.statistics.plugin.social;

import javax.swing.JPanel;
import org.gephi.statistics.plugin.social.EvolvingWeights;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsUI.class)
public class EvolvingWeightsUI implements StatisticsUI {

    private EvolvingWeightsPanel panel;
    private EvolvingWeights evolvingWeights;

    public JPanel getSettingsPanel() {
        if (panel == null) {
            panel = new EvolvingWeightsPanel();
        }
        return EvolvingWeightsPanel.createValidationPanel(panel);
    }

    public void setup(Statistics statistics) {
        this.evolvingWeights = (EvolvingWeights) statistics;

        if (panel != null) {
            panel.iterationsField.setText(String.valueOf(evolvingWeights.getMaxIterations()));
            panel.pollField.setText(String.valueOf(evolvingWeights.getPollFrequency()));
            panel.timeoutField.setText(String.valueOf(evolvingWeights.getEdgeMaxTimeout()));
            panel.alphaField.setText(String.valueOf(evolvingWeights.getAlpha()));
            panel.betaField.setText(String.valueOf(evolvingWeights.getBeta()));
        }
    }

    public void unsetup() {
        if (panel != null) {
            evolvingWeights.setMaxIterations(Integer.parseInt(panel.iterationsField.getText()));
            evolvingWeights.setPollFrequency(Integer.parseInt(panel.pollField.getText()));
            evolvingWeights.setEdgeMaxTimeout(Integer.parseInt(panel.timeoutField.getText()));
            evolvingWeights.setAlpha(Double.parseDouble(panel.alphaField.getText()));           
            evolvingWeights.setBeta(Double.parseDouble(panel.betaField.getText()));            
        }      
        //evolvingWeights = null;
        //panel = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return EvolvingWeights.class;
    }

    public String getValue() {
        return "Done";
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "EvolvingWeightsUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_SOCIAL;
    }

    public int getPosition() {
        return 800;
    }

    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "EvolvingWeightsUI.shortDescription");
    }
}
