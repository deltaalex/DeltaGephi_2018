package org.gephi.ui.generator.plugin;

import javax.swing.JPanel;
import org.gephi.io.generator.plugin.organicscale.OrganicGraphUI;
import org.gephi.io.generator.plugin.organicscale.OrganicWeightGraph;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = OrganicGraphUI.class)
public class OrganicGraphUIImpl implements OrganicGraphUI {

    private OrganicGraphPanel panel;
    private OrganicWeightGraph organicGraph;

    public OrganicGraphUIImpl() {
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new OrganicGraphPanel();
        }
        return OrganicGraphPanel.createValidationPanel(panel);
    }

    public void setup(Generator generator) {
        this.organicGraph = (OrganicWeightGraph) generator;

        //Set UI
        if (panel == null) {
            panel = new OrganicGraphPanel();
        }
        panel.nodesField.setText(String.valueOf(organicGraph.getNumberOfNodes()));
        panel.iterationsField.setText(String.valueOf(organicGraph.getDuration()));
        panel.xmaxField.setText(String.valueOf(organicGraph.getXmax()));   
        panel.ymaxField.setText(String.valueOf(organicGraph.getYmax()));  
        panel.exponentField.setText(String.valueOf(organicGraph.getExponent()));  
        
        panel.animateCheckBox.setSelected(organicGraph.getAnimate());
        panel.tAnimateNode.setText(""+organicGraph.getAnimationNodeDelay());
        panel.tAnimateEdge.setText(""+organicGraph.getAnimationEdgeDelay());
    }

    public void unsetup() {
        //Set params
        organicGraph.setNumberOfNodes(Integer.parseInt(panel.nodesField.getText()));
        organicGraph.setDuration(Integer.parseInt(panel.iterationsField.getText()));
        organicGraph.setXmax(Integer.parseInt(panel.xmaxField.getText()));  
        organicGraph.setYmax(Integer.parseInt(panel.ymaxField.getText()));
        organicGraph.setExponent(Double.parseDouble(panel.exponentField.getText()));
        
        organicGraph.setAnimate(panel.animateCheckBox.isSelected());
        organicGraph.setAnimationNodeDelay(Integer.parseInt(panel.tAnimateNode.getText()));
        organicGraph.setAnimationEdgeDelay(Integer.parseInt(panel.tAnimateEdge.getText()));
        
        panel = null;
    }
}
