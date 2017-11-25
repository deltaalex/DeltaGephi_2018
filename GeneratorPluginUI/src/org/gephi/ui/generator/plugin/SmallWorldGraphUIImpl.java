package org.gephi.ui.generator.plugin;

import javax.swing.JPanel;
import org.gephi.io.generator.plugin.SmallWorldGraph;
import org.gephi.io.generator.plugin.SmallWorldGraphUI;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = SmallWorldGraphUI.class)
public class SmallWorldGraphUIImpl implements SmallWorldGraphUI {

    private SmallWorldGraphPanel panel;
    private SmallWorldGraph swGraph;

    public SmallWorldGraphUIImpl() {
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new SmallWorldGraphPanel();
        }
        return SmallWorldGraphPanel.createValidationPanel(panel);
    }

    public void setup(Generator generator) {
        this.swGraph = (SmallWorldGraph) generator;

        //Set UI
        if (panel == null) {
            panel = new SmallWorldGraphPanel();
        }
        panel.nodeField.setText(String.valueOf(swGraph.getNumberOfNodes()));
        panel.kField.setText(String.valueOf(swGraph.getKNeighbors()));
        panel.wiringField.setText(String.valueOf(swGraph.getWiringProbability()));

        panel.radioWS.setSelected(false);
        panel.radioHK.setSelected(false);
        panel.radioTv.setSelected(false);
        panel.radioUSF.setSelected(false);

        switch (swGraph.getType()) {
            case WS:
                panel.radioWS.setSelected(true);
                break;
            case HK:
                panel.radioHK.setSelected(true);
                break;
            case Tv:
                panel.radioTv.setSelected(true);
                break;
            case uSF:
                panel.radioUSF.setSelected(true);
                break;
        }

        panel.animateCheckBox.setSelected(swGraph.getAnimate());
        panel.tAnimateNode.setText("" + swGraph.getAnimationNodeDelay());
        panel.tAnimateEdge.setText("" + swGraph.getAnimationEdgeDelay());
    }

    public void unsetup() {
        //Set params
        swGraph.setNumberOfNodes(Integer.parseInt(panel.nodeField.getText()));
        swGraph.setKNeighbors(Integer.parseInt(panel.kField.getText()));
        swGraph.setWiringProbability(Double.parseDouble(panel.wiringField.getText()));

        if (panel.radioWS.isSelected()) {
            swGraph.setType(1);
        } else if (panel.radioHK.isSelected()) {
            swGraph.setType(2);
        } else if (panel.radioTv.isSelected()) {
            swGraph.setType(3);
        } else if (panel.radioUSF.isSelected()) {
            swGraph.setType(4);
        } else {
            swGraph.setType(1); // error ?
        }


        swGraph.setAnimate(panel.animateCheckBox.isSelected());
        swGraph.setAnimationNodeDelay(Integer.parseInt(panel.tAnimateNode.getText()));
        swGraph.setAnimationEdgeDelay(Integer.parseInt(panel.tAnimateEdge.getText()));

        panel = null;
    }
}
