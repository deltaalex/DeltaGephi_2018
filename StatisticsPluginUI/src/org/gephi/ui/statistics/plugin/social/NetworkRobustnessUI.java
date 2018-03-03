package org.gephi.ui.statistics.plugin.social;

import javax.swing.JPanel;
import org.gephi.statistics.plugin.social.NetworkRobustness;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsUI.class)
public class NetworkRobustnessUI implements StatisticsUI {

    private NetworkRobustnessPanel panel;
    private NetworkRobustness networkRobustness;

    public JPanel getSettingsPanel() {
        if (panel == null) {
            panel = new NetworkRobustnessPanel();
        }
        return NetworkRobustnessPanel.createValidationPanel(panel);
    }

    public void setup(Statistics statistics) {
        this.networkRobustness = (NetworkRobustness) statistics;

        if (panel != null) {
            panel.iterationsField.setText(String.valueOf(networkRobustness.getMaxIterations()));          
            panel.attackField.setText(String.valueOf(networkRobustness.getAttackRatio()));
            panel.repairField.setText(String.valueOf(networkRobustness.getRepairRatio()));
            panel.attackCombo.setSelectedIndex(networkRobustness.getAttackType());
            panel.repairCombo.setSelectedIndex(networkRobustness.getRepairType());
        }
    }

    public void unsetup() {
        if (panel != null) {
            networkRobustness.setMaxIterations(Integer.parseInt(panel.iterationsField.getText()));          
            networkRobustness.setAttackRatio(Double.parseDouble(panel.attackField.getText()));           
            networkRobustness.setRepairRatio(Double.parseDouble(panel.repairField.getText()));   
            networkRobustness.setAttackType(panel.attackCombo.getSelectedIndex());
            networkRobustness.setRepairType(panel.repairCombo.getSelectedIndex());
        }      
        //evolvingWeights = null;
        //panel = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return NetworkRobustness.class;
    }

    public String getValue() {
        return "Done";
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "NetworkRobustnessUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_SOCIAL;
    }

    public int getPosition() {
        return 900;
    }

    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "NetworkRobustnessUI.shortDescription");
    }
}
