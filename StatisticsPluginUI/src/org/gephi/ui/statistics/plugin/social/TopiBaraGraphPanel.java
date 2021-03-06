package org.gephi.ui.statistics.plugin.social;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.gephi.lib.validation.IntegerIntervalValidator;
import org.gephi.lib.validation.Multiple4NumberValidator;
import org.gephi.lib.validation.PositiveNumberValidator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.util.Lookup;

/**
 *
 * @author Alexandru Topirceanu
 */
public class TopiBaraGraphPanel extends javax.swing.JPanel {

    private static final int minNodeDelay = 1;
    private static final int maxNodeDelay = 500;
    private static final int minEdgeDelay = 1;
    private static final int maxEdgeDelay = 100;  

    public TopiBaraGraphPanel() {
        initComponents();
    }

    public static ValidationPanel createValidationPanel(TopiBaraGraphPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new TopiBaraGraphPanel();
        }
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();

        // graph parameters
        group.add(innerPanel.sizeField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
        group.add(innerPanel.growthField, Validators.REQUIRE_NON_EMPTY_STRING, new BetweenZeroAndOneValidator());
        //group.add(innerPanel.avgDegField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
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

        desriptionLabel = new org.jdesktop.swingx.JXLabel();
        header = new org.jdesktop.swingx.JXHeader();
        sizeField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        lUsage = new javax.swing.JLabel();
        lSolutionSize3 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        growthField = new javax.swing.JTextField();
        lSolutionSize4 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        avgDegField = new javax.swing.JTextField();
        lSolutionSize5 = new javax.swing.JLabel();
        animateCheckBox = new javax.swing.JCheckBox();
        tAnimateNode = new javax.swing.JTextField();
        tAnimateEdge = new javax.swing.JTextField();
        labelResolution = new org.jdesktop.swingx.JXLabel();
        labelResolution1 = new org.jdesktop.swingx.JXLabel();
        labelResolution2 = new org.jdesktop.swingx.JXLabel();

        setForeground(new java.awt.Color(51, 51, 255));

        desriptionLabel.setLineWrap(true);
        desriptionLabel.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.desriptionLabel.text")); // NOI18N
        desriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        header.setDescription(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.header.description")); // NOI18N
        header.setTitle(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.header.title")); // NOI18N

        sizeField.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.sizeField.text")); // NOI18N
        sizeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sizeFieldKeyReleased(evt);
            }
        });

        jLabel2.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.jLabel2.text")); // NOI18N

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));

        jLabel5.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.jLabel5.text")); // NOI18N

        lUsage.setForeground(new java.awt.Color(0, 0, 204));
        lUsage.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.lUsage.text")); // NOI18N

        lSolutionSize3.setForeground(new java.awt.Color(102, 102, 102));
        lSolutionSize3.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.lSolutionSize3.text")); // NOI18N
        lSolutionSize3.setToolTipText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.lSolutionSize3.toolTipText")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.jLabel3.text")); // NOI18N

        growthField.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.growthField.text")); // NOI18N
        growthField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                growthFieldKeyReleased(evt);
            }
        });

        lSolutionSize4.setForeground(new java.awt.Color(102, 102, 102));
        lSolutionSize4.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.lSolutionSize4.text")); // NOI18N
        lSolutionSize4.setToolTipText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.lSolutionSize4.toolTipText")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.jLabel4.text")); // NOI18N

        avgDegField.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.avgDegField.text")); // NOI18N
        avgDegField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                avgDegFieldKeyReleased(evt);
            }
        });

        lSolutionSize5.setForeground(new java.awt.Color(102, 102, 102));
        lSolutionSize5.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.lSolutionSize5.text")); // NOI18N
        lSolutionSize5.setToolTipText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.lSolutionSize5.toolTipText")); // NOI18N

        animateCheckBox.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.animateCheckBox.text")); // NOI18N
        animateCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                animateCheckBoxStateChanged(evt);
            }
        });

        tAnimateNode.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tAnimateNode.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.tAnimateNode.text")); // NOI18N

        tAnimateEdge.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tAnimateEdge.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.tAnimateEdge.text")); // NOI18N

        labelResolution.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution.setLineWrap(true);
        labelResolution.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.labelResolution.text")); // NOI18N
        labelResolution.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution.setFont(labelResolution.getFont().deriveFont(labelResolution.getFont().getSize()-1f));
        labelResolution.setPreferredSize(new java.awt.Dimension(500, 12));

        labelResolution1.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution1.setLineWrap(true);
        labelResolution1.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.labelResolution1.text")); // NOI18N
        labelResolution1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution1.setFont(labelResolution1.getFont().deriveFont(labelResolution1.getFont().getSize()-1f));
        labelResolution1.setPreferredSize(new java.awt.Dimension(500, 12));

        labelResolution2.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution2.setLineWrap(true);
        labelResolution2.setText(org.openide.util.NbBundle.getMessage(TopiBaraGraphPanel.class, "TopiBaraGraphPanel.labelResolution2.text")); // NOI18N
        labelResolution2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution2.setFont(labelResolution2.getFont().deriveFont(labelResolution2.getFont().getSize()-1f));
        labelResolution2.setPreferredSize(new java.awt.Dimension(500, 12));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 612, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 678, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(lUsage)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(avgDegField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lSolutionSize5))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(sizeField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addGap(29, 29, 29)
                                                .addComponent(growthField, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lSolutionSize4)
                                            .addComponent(lSolutionSize3)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(tAnimateNode)
                                    .addComponent(animateCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tAnimateEdge))
                                .addGap(49, 49, 49)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelResolution1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelResolution2, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelResolution, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)))
                .addComponent(desriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(desriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addGap(79, 79, 79))
            .addGroup(layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lUsage)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(sizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lSolutionSize3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(growthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lSolutionSize4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(avgDegField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lSolutionSize5))
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sizeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sizeFieldKeyReleased
       
    }//GEN-LAST:event_sizeFieldKeyReleased

    private void growthFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_growthFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_growthFieldKeyReleased

    private void avgDegFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_avgDegFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_avgDegFieldKeyReleased

    private void animateCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_animateCheckBoxStateChanged
        tAnimateNode.setEnabled(((JCheckBox) evt.getSource()).isSelected());
        tAnimateEdge.setEnabled(((JCheckBox) evt.getSource()).isSelected());
    }//GEN-LAST:event_animateCheckBoxStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JCheckBox animateCheckBox;
    protected javax.swing.JTextField avgDegField;
    private org.jdesktop.swingx.JXLabel desriptionLabel;
    protected javax.swing.JTextField growthField;
    private org.jdesktop.swingx.JXHeader header;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    protected javax.swing.JLabel lSolutionSize3;
    protected javax.swing.JLabel lSolutionSize4;
    protected javax.swing.JLabel lSolutionSize5;
    protected javax.swing.JLabel lUsage;
    private org.jdesktop.swingx.JXLabel labelResolution;
    private org.jdesktop.swingx.JXLabel labelResolution1;
    private org.jdesktop.swingx.JXLabel labelResolution2;
    protected javax.swing.JTextField sizeField;
    protected javax.swing.JTextField tAnimateEdge;
    protected javax.swing.JTextField tAnimateNode;
    // End of variables declaration//GEN-END:variables
}
