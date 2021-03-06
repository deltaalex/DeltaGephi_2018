package org.gephi.ui.statistics.plugin;

import org.gephi.graph.api.GraphController;
import org.openide.util.Lookup;

/**
 *
 * @author pjmcswee
 */
public class EdgeBetweennessPanel extends javax.swing.JPanel {

  /**
   * Creates new form EdgeBetweennessPanel
   */
  public EdgeBetweennessPanel() {
    initComponents();

    //Disable directed if the graph is undirected
    GraphController graphController = Lookup.getDefault().lookup(GraphController.class);

    if (graphController.getModel().isUndirected()) {
      directedRadioButton.setEnabled(false);
    }
  }

  public boolean isNormalized() {
    return normalizeButton.isSelected();
  }

  public boolean isDirected() {
    return directedRadioButton.isSelected();
  }

  public void setDirected(boolean directed) {
    buttonGroup1.setSelected(directed ? directedRadioButton.getModel() : undirectedRadioButton.getModel(), true);
    if (!directed) {
      directedRadioButton.setEnabled(false);
    }
  }
  
  void doNormalize(boolean normalized) {
    this.normalizeButton.setSelected(normalized);
  }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        header = new org.jdesktop.swingx.JXHeader();
        directedRadioButton = new javax.swing.JRadioButton();
        undirectedRadioButton = new javax.swing.JRadioButton();
        normalizeButton = new javax.swing.JCheckBox();

        header.setDescription(org.openide.util.NbBundle.getMessage(EdgeBetweennessPanel.class, "EdgeBetweennessPanel.header.description_2")); // NOI18N
        header.setTitle(org.openide.util.NbBundle.getMessage(EdgeBetweennessPanel.class, "EdgeBetweennessPanel.header.title_2")); // NOI18N

        buttonGroup1.add(directedRadioButton);
        directedRadioButton.setText(org.openide.util.NbBundle.getMessage(EdgeBetweennessPanel.class, "EigenvectorCentralityPanel.directedButton.text")); // NOI18N

        buttonGroup1.add(undirectedRadioButton);
        undirectedRadioButton.setText(org.openide.util.NbBundle.getMessage(EdgeBetweennessPanel.class, "EigenvectorCentralityPanel.undirectedButton.text")); // NOI18N
        undirectedRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undirectedRadioButtonActionPerformed(evt);
            }
        });

        normalizeButton.setSelected(true);
        normalizeButton.setText(org.openide.util.NbBundle.getMessage(EdgeBetweennessPanel.class, "EdgeBetweennessPanel.normalizeButton.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(directedRadioButton)
                    .addComponent(undirectedRadioButton)
                    .addComponent(normalizeButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(directedRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(undirectedRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(normalizeButton)
                .addContainerGap(31, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void undirectedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undirectedRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_undirectedRadioButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton directedRadioButton;
    private org.jdesktop.swingx.JXHeader header;
    protected javax.swing.JCheckBox normalizeButton;
    private javax.swing.JRadioButton undirectedRadioButton;
    // End of variables declaration//GEN-END:variables

}
