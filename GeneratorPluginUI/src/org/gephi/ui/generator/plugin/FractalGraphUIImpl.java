package org.gephi.ui.generator.plugin;

import javax.swing.JPanel;
import org.gephi.io.generator.plugin.FractalGraph;
import org.gephi.io.generator.plugin.FractalGraphUI;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = FractalGraphUI.class)
public class FractalGraphUIImpl implements FractalGraphUI {

    private FractalGraphPanel panel;
    private FractalGraph fractalGraph;

    public FractalGraphUIImpl() {
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new FractalGraphPanel();
        }
        return FractalGraphPanel.createValidationPanel(panel);
    }

    public void setup(Generator generator) {
        this.fractalGraph = (FractalGraph) generator;

        //Set UI
        if (panel == null) {
            panel = new FractalGraphPanel();
        }
        panel.sizeField.setText(String.valueOf(fractalGraph.getNumberOfNodes()));
        panel.wiringField.setText(String.valueOf(fractalGraph.getWiring()));       
        panel.comField.setText(String.valueOf(fractalGraph.getNumberOfCommunities()));
        panel.levelsField.setText(String.valueOf(fractalGraph.getFractalLevels()));
        
        panel.animateCheckBox.setSelected(fractalGraph.getAnimate());
        panel.tAnimateNode.setText(""+fractalGraph.getAnimationNodeDelay());
        panel.tAnimateEdge.setText(""+fractalGraph.getAnimationEdgeDelay());
    }

    public void unsetup() {
        //Set params
        fractalGraph.setNumberOfNodes(Integer.parseInt(panel.sizeField.getText()));
        fractalGraph.setWiring(Double.parseDouble(panel.wiringField.getText()));       
        fractalGraph.setNumberOfCommunities(Integer.parseInt(panel.comField.getText()));
        fractalGraph.setFractalLevels(Integer.parseInt(panel.levelsField.getText()));
        
        fractalGraph.setAnimate(panel.animateCheckBox.isSelected());
        fractalGraph.setAnimationNodeDelay(Integer.parseInt(panel.tAnimateNode.getText()));
        fractalGraph.setAnimationEdgeDelay(Integer.parseInt(panel.tAnimateEdge.getText()));
        
        panel = null;
    }
}
