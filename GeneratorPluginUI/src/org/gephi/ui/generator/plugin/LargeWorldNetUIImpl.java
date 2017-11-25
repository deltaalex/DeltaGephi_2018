package org.gephi.ui.generator.plugin;

import javax.swing.JPanel;
import org.gephi.io.generator.plugin.LargeWorldNet;
import org.gephi.io.generator.plugin.LargeWorldNetUI;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = LargeWorldNetUI.class)
public class LargeWorldNetUIImpl implements LargeWorldNetUI {

    private LargeWorldNetPanel panel;
    private LargeWorldNet meshGraph;

    public LargeWorldNetUIImpl() {
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new LargeWorldNetPanel();
        }
        return LargeWorldNetPanel.createValidationPanel(panel);
    }

    public void setup(Generator generator) {
        this.meshGraph = (LargeWorldNet) generator;

        //Set UI
        if (panel == null) {
            panel = new LargeWorldNetPanel();
        }
        panel.nodesField.setText(String.valueOf(meshGraph.getNumberOfNodes()));
        //panel.dissimilarityField.setText(String.valueOf(meshGraph.getDissimilarity()));       
        
        panel.animateCheckBox.setSelected(meshGraph.getAnimate());
        panel.tAnimateNode.setText(""+meshGraph.getAnimationNodeDelay());
        panel.tAnimateEdge.setText(""+meshGraph.getAnimationEdgeDelay());
    }

    public void unsetup() {
        //Set params
        meshGraph.setNumberOfNodes(Integer.parseInt(panel.nodesField.getText()));        
        
        meshGraph.setAnimate(panel.animateCheckBox.isSelected());
        meshGraph.setAnimationNodeDelay(Integer.parseInt(panel.tAnimateNode.getText()));
        meshGraph.setAnimationEdgeDelay(Integer.parseInt(panel.tAnimateEdge.getText()));
        
        panel = null;
    }
}
