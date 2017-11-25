package org.gephi.ui.generator.plugin;

import javax.swing.JCheckBox;
import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.gephi.lib.validation.IntegerIntervalValidator;
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
public class FractalGraphPanel extends javax.swing.JPanel {

    private static final int minNodeDelay = 1;
    private static final int maxNodeDelay = 500;
    private static final int minEdgeDelay = 1;
    private static final int maxEdgeDelay = 100;

    /**
     * Creates new form StaticGeographicGraphPanel
     */
    public FractalGraphPanel() {
        initComponents();
    }

    public static ValidationPanel createValidationPanel(FractalGraphPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new FractalGraphPanel();
        }
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();

        // graph parameters
        group.add(innerPanel.sizeField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
        group.add(innerPanel.wiringField, Validators.REQUIRE_NON_EMPTY_STRING, new Validator<String>() {
            @Override
            public boolean validate(Problems problems, String compName, String model) {
                boolean result = false;
                try {
                    Double i = Double.parseDouble(model);
                    result = i > 0;
                } catch (Exception e) {
                }
                if (!result) {
                    String message = "Value must be positive";
                    problems.add(message);
                }
                return result;
            }
        });
        group.add(innerPanel.comField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
        group.add(innerPanel.levelsField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
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
        edgeLabel = new javax.swing.JLabel();
        sizeField = new javax.swing.JTextField();
        wiringField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        animateCheckBox = new javax.swing.JCheckBox();
        labelResolution = new org.jdesktop.swingx.JXLabel();
        tAnimateNode = new javax.swing.JTextField();
        labelResolution1 = new org.jdesktop.swingx.JXLabel();
        tAnimateEdge = new javax.swing.JTextField();
        labelResolution2 = new org.jdesktop.swingx.JXLabel();
        labelResolution3 = new org.jdesktop.swingx.JXLabel();
        edgeLabel1 = new javax.swing.JLabel();
        comField = new javax.swing.JTextField();
        levelsField = new javax.swing.JTextField();
        edgeLabel2 = new javax.swing.JLabel();

        nodeLabel.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.nodeLabel.text_2")); // NOI18N

        edgeLabel.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.edgeLabel.text_2")); // NOI18N

        sizeField.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.cellField.text_1")); // NOI18N

        wiringField.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.intraEdgeField.text_1")); // NOI18N

        jSeparator1.setForeground(new java.awt.Color(0, 0, 204));

        animateCheckBox.setSelected(true);
        animateCheckBox.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.animateCheckBox.text")); // NOI18N
        animateCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                animateCheckBoxStateChanged(evt);
            }
        });

        labelResolution.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution.setLineWrap(true);
        labelResolution.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.labelResolution.text")); // NOI18N
        labelResolution.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution.setFont(labelResolution.getFont().deriveFont(labelResolution.getFont().getSize()-1f));
        labelResolution.setPreferredSize(new java.awt.Dimension(500, 12));

        tAnimateNode.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.tAnimateNode.text")); // NOI18N

        labelResolution1.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution1.setLineWrap(true);
        labelResolution1.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.labelResolution1.text")); // NOI18N
        labelResolution1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution1.setFont(labelResolution1.getFont().deriveFont(labelResolution1.getFont().getSize()-1f));
        labelResolution1.setPreferredSize(new java.awt.Dimension(500, 12));

        tAnimateEdge.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.tAnimateEdge.text")); // NOI18N

        labelResolution2.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution2.setLineWrap(true);
        labelResolution2.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.labelResolution2.text")); // NOI18N
        labelResolution2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution2.setFont(labelResolution2.getFont().deriveFont(labelResolution2.getFont().getSize()-1f));
        labelResolution2.setPreferredSize(new java.awt.Dimension(500, 12));

        labelResolution3.setForeground(new java.awt.Color(0, 0, 204));
        labelResolution3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelResolution3.setLineWrap(true);
        labelResolution3.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FacebookGraphPanel.labelResolution3.text")); // NOI18N
        labelResolution3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelResolution3.setFont(labelResolution3.getFont().deriveFont(labelResolution3.getFont().getSize()-1f));
        labelResolution3.setPreferredSize(new java.awt.Dimension(500, 12));

        edgeLabel1.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FractalGraphPanel.edgeLabel1.text")); // NOI18N

        comField.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FractalGraphPanel.comField.text")); // NOI18N

        levelsField.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FractalGraphPanel.levelsField.text")); // NOI18N

        edgeLabel2.setText(org.openide.util.NbBundle.getMessage(FractalGraphPanel.class, "FractalGraphPanel.edgeLabel2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nodeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(edgeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(edgeLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(edgeLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(wiringField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sizeField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                    .addComponent(comField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                    .addComponent(levelsField, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jSeparator1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(animateCheckBox)
                            .addComponent(tAnimateNode, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tAnimateEdge, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelResolution, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelResolution1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelResolution2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelResolution3, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nodeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wiringField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edgeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edgeLabel1))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(levelsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edgeLabel2))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(labelResolution3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void animateCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_animateCheckBoxStateChanged
        tAnimateNode.setEnabled(((JCheckBox) evt.getSource()).isSelected());
        tAnimateEdge.setEnabled(((JCheckBox) evt.getSource()).isSelected());
    }//GEN-LAST:event_animateCheckBoxStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JCheckBox animateCheckBox;
    protected javax.swing.JTextField comField;
    private javax.swing.JLabel edgeLabel;
    private javax.swing.JLabel edgeLabel1;
    private javax.swing.JLabel edgeLabel2;
    private javax.swing.JSeparator jSeparator1;
    private org.jdesktop.swingx.JXLabel labelResolution;
    private org.jdesktop.swingx.JXLabel labelResolution1;
    private org.jdesktop.swingx.JXLabel labelResolution2;
    private org.jdesktop.swingx.JXLabel labelResolution3;
    protected javax.swing.JTextField levelsField;
    private javax.swing.JLabel nodeLabel;
    protected javax.swing.JTextField sizeField;
    protected javax.swing.JTextField tAnimateEdge;
    protected javax.swing.JTextField tAnimateNode;
    protected javax.swing.JTextField wiringField;
    // End of variables declaration//GEN-END:variables
}
