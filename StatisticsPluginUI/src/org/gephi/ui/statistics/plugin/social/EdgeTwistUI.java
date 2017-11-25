package org.gephi.ui.statistics.plugin.social;

import javax.swing.JPanel;
import org.gephi.statistics.plugin.social.EdgeTwist;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsUI.class)
public class EdgeTwistUI implements StatisticsUI {

    //private final StatSettings settings = new StatSettings();
    private EdgeTwistPanel panel;
    private EdgeTwist edgeTwist;

    public JPanel getSettingsPanel() {
        if (panel == null) {
            panel = new EdgeTwistPanel();
        }
        return EdgeTwistPanel.createValidationPanel(panel);
    }

    public void setup(Statistics statistics) {
        this.edgeTwist = (EdgeTwist) statistics;
        if (panel != null) {
           
        }
    }

    public void unsetup() {
        if (panel != null) {
            
        }
        edgeTwist = null;
        panel = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return EdgeTwist.class;
    }

    public String getValue() {
        return "Done";
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "EdgeTwistUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_SOCIAL;
    }

    public int getPosition() {
        return 700;
    }

    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "EdgeTwistUI.shortDescription");
    }
}
