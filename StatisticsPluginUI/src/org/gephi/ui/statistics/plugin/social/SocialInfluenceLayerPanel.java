package org.gephi.ui.statistics.plugin.social;

import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

/**
 *
 * @author Alexandru Topirceanu
 */
public class SocialInfluenceLayerPanel extends javax.swing.JPanel {

    public SocialInfluenceLayerPanel() {
        initComponents();
    }
   
    public static ValidationPanel createValidationPanel(SocialInfluenceLayerPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new SocialInfluenceLayerPanel();
        }
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();       

        return validationPanel;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        header = new org.jdesktop.swingx.JXHeader();
        labelRandomize4 = new javax.swing.JLabel();
        checkDegree = new javax.swing.JCheckBox();
        checkBtw = new javax.swing.JCheckBox();
        checkEigen = new javax.swing.JCheckBox();
        checkClose = new javax.swing.JCheckBox();
        checkClustering = new javax.swing.JCheckBox();

        setForeground(new java.awt.Color(51, 51, 255));

        header.setDescription(org.openide.util.NbBundle.getMessage(SocialInfluenceLayerPanel.class, "SocialInfluenceLayerPanel.header.description")); // NOI18N
        header.setTitle(org.openide.util.NbBundle.getMessage(SocialInfluenceLayerPanel.class, "SocialInfluenceLayerPanel.header.title")); // NOI18N

        labelRandomize4.setFont(labelRandomize4.getFont().deriveFont(labelRandomize4.getFont().getSize()-1f));
        labelRandomize4.setForeground(new java.awt.Color(102, 102, 102));
        labelRandomize4.setText(org.openide.util.NbBundle.getMessage(SocialInfluenceLayerPanel.class, "SocialInfluenceLayerPanel.labelRandomize4.text")); // NOI18N

        buttonGroup1.add(checkDegree);
        checkDegree.setText(org.openide.util.NbBundle.getMessage(SocialInfluenceLayerPanel.class, "SocialInfluenceLayerPanel.checkDegree.text")); // NOI18N

        buttonGroup1.add(checkBtw);
        checkBtw.setSelected(true);
        checkBtw.setText(org.openide.util.NbBundle.getMessage(SocialInfluenceLayerPanel.class, "SocialInfluenceLayerPanel.checkBtw.text")); // NOI18N

        buttonGroup1.add(checkEigen);
        checkEigen.setText(org.openide.util.NbBundle.getMessage(SocialInfluenceLayerPanel.class, "SocialInfluenceLayerPanel.checkEigen.text")); // NOI18N

        buttonGroup1.add(checkClose);
        checkClose.setText(org.openide.util.NbBundle.getMessage(SocialInfluenceLayerPanel.class, "SocialInfluenceLayerPanel.checkClose.text")); // NOI18N

        buttonGroup1.add(checkClustering);
        checkClustering.setText(org.openide.util.NbBundle.getMessage(SocialInfluenceLayerPanel.class, "SocialInfluenceLayerPanel.checkClustering.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkClose)
                            .addComponent(checkClustering))
                        .addGap(391, 442, Short.MAX_VALUE))
                    .addComponent(labelRandomize4, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkBtw)
                            .addComponent(checkEigen)
                            .addComponent(checkDegree))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelRandomize4)
                .addGap(18, 18, 18)
                .addComponent(checkDegree)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBtw)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkEigen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkClose)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkClustering)
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    protected javax.swing.JCheckBox checkBtw;
    protected javax.swing.JCheckBox checkClose;
    protected javax.swing.JCheckBox checkClustering;
    protected javax.swing.JCheckBox checkDegree;
    protected javax.swing.JCheckBox checkEigen;
    private org.jdesktop.swingx.JXHeader header;
    private javax.swing.JLabel labelRandomize4;
    // End of variables declaration//GEN-END:variables
}
