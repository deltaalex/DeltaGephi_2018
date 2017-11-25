package org.gephi.ui.statistics.plugin.social;

import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.gephi.statistics.plugin.social.RandomWalk;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsUI.class)
public class RandomWalkUI implements StatisticsUI {

    //private final StatSettings settings = new StatSettings();
    private RandomWalkPanel panel;
    private RandomWalk opinion;
    
    public JPanel getSettingsPanel() {
        if (panel == null) {
            panel = new RandomWalkPanel();
        }
        return RandomWalkPanel.createValidationPanel(panel);
    }
    
    public void setup(Statistics statistics) {
        this.opinion = (RandomWalk) statistics;
        if (panel != null) {
            panel.tRepeats.setText(opinion.getRepeats() + "");
            panel.tMaxPath.setText(opinion.getMaxPath() + "");
            panel.rStubborn.setSelected(opinion.addSAs());
        }
    }
    
    public void unsetup() {
        if (panel != null) {
            opinion.setRepeats(Integer.parseInt(panel.tRepeats.getText()));
            opinion.setMaxPath(Integer.parseInt(panel.tMaxPath.getText()));
            opinion.setAddSAs(panel.rStubborn.isSelected());
        }
        opinion = null;
        panel = null;
    }
    
    public Class<? extends Statistics> getStatisticsClass() {
        return RandomWalk.class;
    }
    
    public String getValue() {
        DecimalFormat df = new DecimalFormat("###.###");
        return "" + df.format(opinion.getFoundRatio());
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "RandomWalkUI.name");
    }
    
    public String getCategory() {
        return StatisticsUI.CATEGORY_SOCIAL;
    }
    
    public int getPosition() {
        return 4;
    }
    
    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "RandomWalkUI.shortDescription");
    }
}
