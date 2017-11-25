package org.gephi.ui.generator.plugin;

import javax.swing.JPanel;
import org.gephi.io.generator.plugin.MeshGraph;
import org.gephi.io.generator.plugin.MeshGraphUI;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = MeshGraphUI.class)
public class MeshGraphUIImpl implements MeshGraphUI {

    private MeshGraphPanel panel;
    private MeshGraph meshGraph;

    public MeshGraphUIImpl() {
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new MeshGraphPanel();
        }
        return MeshGraphPanel.createValidationPanel(panel);
    }

    public void setup(Generator generator) {
        this.meshGraph = (MeshGraph) generator;

        //Set UI
        if (panel == null) {
            panel = new MeshGraphPanel();
        }
        panel.nodesField.setText(String.valueOf(meshGraph.getNumberOfNodes()));
        panel.dissimilarityField.setText(String.valueOf(meshGraph.getDissimilarity()));       
        
        panel.animateCheckBox.setSelected(meshGraph.getAnimate());
        panel.tAnimateNode.setText(""+meshGraph.getAnimationNodeDelay());
        panel.tAnimateEdge.setText(""+meshGraph.getAnimationEdgeDelay());
    }

    public void unsetup() {
        //Set params
        meshGraph.setNumberOfNodes(Integer.parseInt(panel.nodesField.getText()));
        meshGraph.setDissimilarity(Double.parseDouble(panel.dissimilarityField.getText()));       
        
        meshGraph.setAnimate(panel.animateCheckBox.isSelected());
        meshGraph.setAnimationNodeDelay(Integer.parseInt(panel.tAnimateNode.getText()));
        meshGraph.setAnimationEdgeDelay(Integer.parseInt(panel.tAnimateEdge.getText()));
        
        panel = null;
    }
}
