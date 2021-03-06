package org.gephi.ui.generator.plugin;

import javax.swing.JCheckBox;
import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.gephi.lib.validation.IntegerIntervalValidator;
import org.gephi.lib.validation.PositiveNumberValidator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

/**
 *
 * @author Alexandru Topirceanu
 */
public class WSDDGraphPanel extends javax.swing.JPanel {

    private static final int minNodeDelay = 1;
    private static final int maxNodeDelay = 500;
    private static final int minEdgeDelay = 1;
    private static final int maxEdgeDelay = 100;
    
    /**
     * Creates new form RandomGraphPanel
     */
    public WSDDGraphPanel() {
        initComponents();
    }

    public static ValidationPanel createValidationPanel(WSDDGraphPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new WSDDGraphPanel();
        }
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();

        // graph parameters
        group.add(innerPanel.cellField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());       
        group.add(innerPanel.cellSizeField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());           
        group.add(innerPanel.kField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());           
        group.add(innerPanel.wiringField, Validators.REQUIRE_NON_EMPTY_STRING, new BetweenZeroAndOneValidator());                   
        // animation parameters
        group.add(innerPanel.tAnimateNode, Validators.REQUIRE_NON_EMPTY_STRING, new IntegerIntervalValidator(minNodeDelay, maxNodeDelay));
        group.add(innerPanel.tAnimateEdge, Validators.REQUIRE_NON_EMPTY_STRING, new IntegerIntervalValidator(minEdgeDelay, maxEdgeDelay));

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

        nodeLabel = new javax.swing.JLabel();
        cellField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        animateCheckBox = new javax.swing.JCheckBox();
        labelResolution = new org.jdesktop.swingx.JXLabel();
        tAnimateNode = new javax.swing.JTextField();
        labelResolution1 = new org.jdesktop.swingx.JXLabel();
        tAnimateEdge = new javax.swing.JTextField();
        labelResolution2 = new org.jdesktop.swingx.JXLabel();
        labelResolution3 = new org.jdesktop.swingx.JXLabel();
        nodeLabel1 = new javax.swing.JLabel();
        kField = new javax.swing.JTextField();
        nodeLabel2 = new javax.swing.JLabel();
        wiringField = new javax.swing.JTextField();
        nodeLabel3 = new javax.swing.JLabel();
        cellSizeField = new javax.swing.JTextField();

        nodeLabel.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.nodeLabel.text_2")); // NOI18N

        cellField.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.cellField.text_1")); // NOI18N

        jSeparator1.setForeground(new java.awt.Color(0, 0, 204));

        animateCheckBox.setSelected(true);
        animateCheckBox.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.animateCheckBox.text")); // NOI18N
        animateCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                animateCheckBoxStateChanged(evt);
            }
        });

        labelResolution.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution.setLineWrap(true);
        labelResolution.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.labelResolution.text")); // NOI18N
        labelResolution.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution.setFont(labelResolution.getFont().deriveFont(labelResolution.getFont().getSize()-1f));
        labelResolution.setPreferredSize(new java.awt.Dimension(500, 12));

        tAnimateNode.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.tAnimateNode.text")); // NOI18N

        labelResolution1.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution1.setLineWrap(true);
        labelResolution1.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.labelResolution1.text")); // NOI18N
        labelResolution1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution1.setFont(labelResolution1.getFont().deriveFont(labelResolution1.getFont().getSize()-1f));
        labelResolution1.setPreferredSize(new java.awt.Dimension(500, 12));

        tAnimateEdge.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.tAnimateEdge.text")); // NOI18N

        labelResolution2.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution2.setLineWrap(true);
        labelResolution2.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.labelResolution2.text")); // NOI18N
        labelResolution2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution2.setFont(labelResolution2.getFont().deriveFont(labelResolution2.getFont().getSize()-1f));
        labelResolution2.setPreferredSize(new java.awt.Dimension(500, 12));

        labelResolution3.setForeground(new java.awt.Color(0, 0, 204));
        labelResolution3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelResolution3.setLineWrap(true);
        labelResolution3.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.labelResolution3.text")); // NOI18N
        labelResolution3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution3.setFont(labelResolution3.getFont().deriveFont(labelResolution3.getFont().getSize()-1f));
        labelResolution3.setPreferredSize(new java.awt.Dimension(500, 12));

        nodeLabel1.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.nodeLabel1.text")); // NOI18N

        kField.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.kField.text")); // NOI18N
        kField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kFieldActionPerformed(evt);
            }
        });

        nodeLabel2.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.nodeLabel2.text")); // NOI18N

        wiringField.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.wiringField.text")); // NOI18N

        nodeLabel3.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.nodeLabel3.text")); // NOI18N

        cellSizeField.setText(org.openide.util.NbBundle.getMessage(WSDDGraphPanel.class, "WSDDGraphPanel.cellSizeField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(labelResolution3, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(animateCheckBox)
                                    .addComponent(tAnimateNode, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tAnimateEdge, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelResolution, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelResolution1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelResolution2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(nodeLabel3)
                                    .addComponent(nodeLabel1)
                                    .addComponent(nodeLabel)
                                    .addComponent(nodeLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(wiringField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cellField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cellSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 56, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nodeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nodeLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nodeLabel1)
                    .addComponent(kField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nodeLabel2)
                    .addComponent(wiringField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelResolution, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(animateCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tAnimateNode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelResolution1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tAnimateEdge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelResolution2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(labelResolution3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void animateCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_animateCheckBoxStateChanged
        tAnimateNode.setEnabled(((JCheckBox) evt.getSource()).isSelected());
        tAnimateEdge.setEnabled(((JCheckBox) evt.getSource()).isSelected());
    }//GEN-LAST:event_animateCheckBoxStateChanged

    private void kFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JCheckBox animateCheckBox;
    protected javax.swing.JTextField cellField;
    protected javax.swing.JTextField cellSizeField;
    private javax.swing.JSeparator jSeparator1;
    protected javax.swing.JTextField kField;
    private org.jdesktop.swingx.JXLabel labelResolution;
    private org.jdesktop.swingx.JXLabel labelResolution1;
    private org.jdesktop.swingx.JXLabel labelResolution2;
    private org.jdesktop.swingx.JXLabel labelResolution3;
    private javax.swing.JLabel nodeLabel;
    private javax.swing.JLabel nodeLabel1;
    private javax.swing.JLabel nodeLabel2;
    private javax.swing.JLabel nodeLabel3;
    protected javax.swing.JTextField tAnimateEdge;
    protected javax.swing.JTextField tAnimateNode;
    protected javax.swing.JTextField wiringField;
    // End of variables declaration//GEN-END:variables
}
