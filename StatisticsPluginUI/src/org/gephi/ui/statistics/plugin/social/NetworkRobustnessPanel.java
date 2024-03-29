package org.gephi.ui.statistics.plugin.social;

import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.gephi.lib.validation.PositiveNumberValidator;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexandru Topirceanu
 */
public class NetworkRobustnessPanel extends javax.swing.JPanel {

    public NetworkRobustnessPanel() {
        initComponents();
    }

    public static ValidationPanel createValidationPanel(NetworkRobustnessPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new NetworkRobustnessPanel();
        }
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();

        // graph parameters
        group.add(innerPanel.iterationsField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
        group.add(innerPanel.attackField, Validators.REQUIRE_NON_EMPTY_STRING, new BetweenZeroAndOneValidator());
        group.add(innerPanel.repairField, Validators.REQUIRE_NON_EMPTY_STRING, new Validator<String>() {
            @Override
            public boolean validate(Problems problems, String compName, String model) {
                boolean result = false;
                try {
                    Double d = Double.parseDouble(model);
                    result = d >= 0 && d <= 100.0;
                } catch (Exception e) {
                }
                if (!result) {
                    String message = NbBundle.getMessage(PositiveNumberValidator.class,
                            "PositiveNumberValidator_NOT_POSITIVE", model);
                    problems.add(message);
                }
                return result;
            }
        });

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

        header = new org.jdesktop.swingx.JXHeader();
        labelRandomize4 = new javax.swing.JLabel();
        lIterations = new javax.swing.JLabel();
        iterationsField = new javax.swing.JTextField();
        lSolutionSize3 = new javax.swing.JLabel();
        lAlpha = new javax.swing.JLabel();
        attackField = new javax.swing.JTextField();
        lSolutionSize5 = new javax.swing.JLabel();
        lBeta = new javax.swing.JLabel();
        repairField = new javax.swing.JTextField();
        lSolutionSize6 = new javax.swing.JLabel();
        lBeta1 = new javax.swing.JLabel();
        attackCombo = new javax.swing.JComboBox();
        lBeta2 = new javax.swing.JLabel();
        repairCombo = new javax.swing.JComboBox();

        setForeground(new java.awt.Color(51, 51, 255));

        header.setDescription(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.header.description")); // NOI18N
        header.setTitle(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.header.title")); // NOI18N

        labelRandomize4.setFont(labelRandomize4.getFont().deriveFont(labelRandomize4.getFont().getSize()-1f));
        labelRandomize4.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.labelRandomize4.text")); // NOI18N
        labelRandomize4.setForeground(new java.awt.Color(102, 102, 102));

        lIterations.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lIterations.text")); // NOI18N

        iterationsField.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.iterationsField.text")); // NOI18N
        iterationsField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                iterationsFieldKeyReleased(evt);
            }
        });

        lSolutionSize3.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lSolutionSize3.text")); // NOI18N
        lSolutionSize3.setToolTipText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lSolutionSize3.toolTipText")); // NOI18N
        lSolutionSize3.setForeground(new java.awt.Color(102, 102, 102));

        lAlpha.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lAlpha.text")); // NOI18N

        attackField.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.attackField.text")); // NOI18N
        attackField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                attackFieldKeyReleased(evt);
            }
        });

        lSolutionSize5.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lSolutionSize5.text")); // NOI18N
        lSolutionSize5.setToolTipText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lSolutionSize5.toolTipText")); // NOI18N
        lSolutionSize5.setForeground(new java.awt.Color(102, 102, 102));

        lBeta.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lBeta.text")); // NOI18N

        repairField.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.repairField.text")); // NOI18N
        repairField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                repairFieldKeyReleased(evt);
            }
        });

        lSolutionSize6.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lSolutionSize6.text")); // NOI18N
        lSolutionSize6.setToolTipText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lSolutionSize6.toolTipText")); // NOI18N
        lSolutionSize6.setForeground(new java.awt.Color(102, 102, 102));

        lBeta1.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lBeta1.text")); // NOI18N

        attackCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Random", "Degree", "Betweenness", "Eigenvector", "Clustering coefficient", "Random rewiring", "Preferential rewiring" }));

        lBeta2.setText(org.openide.util.NbBundle.getMessage(NetworkRobustnessPanel.class, "NetworkRobustnessPanel.lBeta2.text")); // NOI18N

        repairCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Random", "Degree (HDF)", "Betweenness (HBF)", "Eigenvector (HEF)", "Lowest Degree First", "Lowest Betweenness First", "Lowest Eigenvector First", "None", " " }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(labelRandomize4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lAlpha)
                    .addComponent(lBeta)
                    .addComponent(lIterations)
                    .addComponent(lBeta1)
                    .addComponent(lBeta2))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(repairCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iterationsField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lSolutionSize3))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(attackField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lSolutionSize5))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(repairField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lSolutionSize6))
                    .addComponent(attackCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelRandomize4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lIterations)
                    .addComponent(iterationsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lSolutionSize3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lAlpha)
                    .addComponent(attackField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lSolutionSize5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lBeta)
                    .addComponent(repairField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lSolutionSize6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lBeta1)
                    .addComponent(attackCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lBeta2)
                    .addComponent(repairCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void iterationsFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_iterationsFieldKeyReleased
    }//GEN-LAST:event_iterationsFieldKeyReleased

    private void attackFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_attackFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_attackFieldKeyReleased

    private void repairFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_repairFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_repairFieldKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JComboBox attackCombo;
    protected javax.swing.JTextField attackField;
    private org.jdesktop.swingx.JXHeader header;
    protected javax.swing.JTextField iterationsField;
    private javax.swing.JLabel lAlpha;
    private javax.swing.JLabel lBeta;
    private javax.swing.JLabel lBeta1;
    private javax.swing.JLabel lBeta2;
    private javax.swing.JLabel lIterations;
    protected javax.swing.JLabel lSolutionSize3;
    protected javax.swing.JLabel lSolutionSize5;
    protected javax.swing.JLabel lSolutionSize6;
    private javax.swing.JLabel labelRandomize4;
    protected javax.swing.JComboBox repairCombo;
    protected javax.swing.JTextField repairField;
    // End of variables declaration//GEN-END:variables
}
