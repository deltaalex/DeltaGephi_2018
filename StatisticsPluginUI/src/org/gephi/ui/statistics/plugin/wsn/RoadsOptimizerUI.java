package org.gephi.ui.statistics.plugin.wsn;

import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.gephi.statistics.plugin.RoadsOptimizer;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = StatisticsUI.class)
public class RoadsOptimizerUI implements StatisticsUI {

    private RoadsOptimizerPanel panel;
    private RoadsOptimizer roads;

    public JPanel getSettingsPanel() {
        if (panel == null) {
            panel = new RoadsOptimizerPanel();
        }
        return RoadsOptimizerPanel.createValidationPanel(panel);
    }

    public void setup(Statistics statistics) {
        this.roads = (RoadsOptimizer) statistics;
        if (panel != null) {
            panel.resolutionField.setText(String.valueOf(roads.getResolution()));

            panel.animateCheckBox.setSelected(roads.getAnimate());
            panel.tAnimateNode.setText(String.valueOf(roads.getAnimationNodeDelay()));
            panel.tAnimateEdge.setText(String.valueOf(roads.getAnimationEdgeDelay()));
        }
    }

    public void unsetup() {
        if (panel != null) {
            roads.setResolution(Double.parseDouble(panel.resolutionField.getText()));

            roads.setAnimate(panel.animateCheckBox.isSelected());
            roads.setAnimationNodeDelay(Integer.parseInt(panel.tAnimateNode.getText()));
            roads.setAnimationEdgeDelay(Integer.parseInt(panel.tAnimateEdge.getText()));
        }
        roads = null;
        panel = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return RoadsOptimizer.class;
    }

    public String getValue() {
        DecimalFormat df = new DecimalFormat("###.###");
        return "" + df.format(roads.getSlope());
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "RoadsOptimizerUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_WSN;
    }

    public int getPosition() {
        return 100;
    }

    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "RoadsOptimizerUI.shortDescription");
    }
}
