package org.gephi.ui.statistics.plugin;

import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.gephi.lib.validation.IntegerIntervalValidator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

/**
 *
 * @author Alexander
 */
public class GeneticRankPanel extends javax.swing.JPanel {

    public GeneticRankPanel() {
        initComponents();                
    }
    
    public static ValidationPanel createValidationPanel(GeneticRankPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new GeneticRankPanel();
        }
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();       
        group.add(innerPanel.spreadersField, Validators.REQUIRE_NON_EMPTY_STRING, new IntegerIntervalValidator(1, 1000));
        group.add(innerPanel.generationsField, Validators.REQUIRE_NON_EMPTY_STRING, new IntegerIntervalValidator(1, 1000));
        group.add(innerPanel.individualsField, Validators.REQUIRE_NON_EMPTY_STRING, new IntegerIntervalValidator(1, 1000));
        
        group.add(innerPanel.elitismField, Validators.REQUIRE_NON_EMPTY_STRING, new BetweenZeroAndOneValidator());
        group.add(innerPanel.crossoverField, Validators.REQUIRE_NON_EMPTY_STRING, new BetweenZeroAndOneValidator());
        group.add(innerPanel.mutationField, Validators.REQUIRE_NON_EMPTY_STRING, new BetweenZeroAndOneValidator());

        return validationPanel;
    }
      
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        directedButtonGroup = new javax.swing.ButtonGroup();
        metricsGroup = new javax.swing.ButtonGroup();
        jXHeader1 = new org.jdesktop.swingx.JXHeader();
        jXLabel7 = new org.jdesktop.swingx.JXLabel();
        labelRandomize5 = new javax.swing.JLabel();
        individualsField = new javax.swing.JTextField();
        labelRandomize6 = new javax.swing.JLabel();
        generationsField = new javax.swing.JTextField();
        labelRandomize7 = new javax.swing.JLabel();
        spreadersField = new javax.swing.JTextField();
        labelRandomize8 = new javax.swing.JLabel();
        elitismField = new javax.swing.JTextField();
        labelRandomize9 = new javax.swing.JLabel();
        crossoverField = new javax.swing.JTextField();
        labelRandomize10 = new javax.swing.JLabel();
        mutationField = new javax.swing.JTextField();

        jXHeader1.setDescription(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.jXHeader1.description")); // NOI18N
        jXHeader1.setTitle(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.jXHeader1.title")); // NOI18N

        jXLabel7.setLineWrap(true);
        jXLabel7.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.jXLabel7.text")); // NOI18N
        jXLabel7.setFont(jXLabel7.getFont().deriveFont(jXLabel7.getFont().getSize()-1f));

        labelRandomize5.setFont(labelRandomize5.getFont().deriveFont(labelRandomize5.getFont().getSize()-1f));
        labelRandomize5.setForeground(new java.awt.Color(102, 102, 102));
        labelRandomize5.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.labelRandomize5.text")); // NOI18N

        individualsField.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.individualsField.text")); // NOI18N

        labelRandomize6.setFont(labelRandomize6.getFont().deriveFont(labelRandomize6.getFont().getSize()-1f));
        labelRandomize6.setForeground(new java.awt.Color(102, 102, 102));
        labelRandomize6.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.labelRandomize6.text")); // NOI18N

        generationsField.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.generationsField.text")); // NOI18N

        labelRandomize7.setFont(labelRandomize7.getFont().deriveFont(labelRandomize7.getFont().getSize()-1f));
        labelRandomize7.setForeground(new java.awt.Color(102, 102, 102));
        labelRandomize7.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.labelRandomize7.text")); // NOI18N

        spreadersField.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.spreadersField.text")); // NOI18N

        labelRandomize8.setFont(labelRandomize8.getFont().deriveFont(labelRandomize8.getFont().getSize()-1f));
        labelRandomize8.setForeground(new java.awt.Color(102, 102, 102));
        labelRandomize8.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.labelRandomize8.text")); // NOI18N

        elitismField.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.elitismField.text")); // NOI18N

        labelRandomize9.setFont(labelRandomize9.getFont().deriveFont(labelRandomize9.getFont().getSize()-1f));
        labelRandomize9.setForeground(new java.awt.Color(102, 102, 102));
        labelRandomize9.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.labelRandomize9.text")); // NOI18N

        crossoverField.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.crossoverField.text")); // NOI18N

        labelRandomize10.setFont(labelRandomize10.getFont().deriveFont(labelRandomize10.getFont().getSize()-1f));
        labelRandomize10.setForeground(new java.awt.Color(102, 102, 102));
        labelRandomize10.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.labelRandomize10.text")); // NOI18N

        mutationField.setText(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.mutationField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jXHeader1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jXLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelRandomize5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(individualsField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(labelRandomize10, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mutationField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(labelRandomize6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(generationsField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(labelRandomize9, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(crossoverField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelRandomize7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spreadersField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(labelRandomize8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(elitismField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jXHeader1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelRandomize8)
                        .addComponent(elitismField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelRandomize7)
                        .addComponent(spreadersField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelRandomize9)
                        .addComponent(crossoverField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelRandomize6)
                        .addComponent(generationsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelRandomize10)
                        .addComponent(mutationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelRandomize5)
                        .addComponent(individualsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(jXLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        jXLabel7.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GeneticRankPanel.class, "GeneticRankPanel.jXLabel7.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTextField crossoverField;
    private javax.swing.ButtonGroup directedButtonGroup;
    protected javax.swing.JTextField elitismField;
    protected javax.swing.JTextField generationsField;
    protected javax.swing.JTextField individualsField;
    private org.jdesktop.swingx.JXHeader jXHeader1;
    private org.jdesktop.swingx.JXLabel jXLabel7;
    private javax.swing.JLabel labelRandomize10;
    private javax.swing.JLabel labelRandomize5;
    private javax.swing.JLabel labelRandomize6;
    private javax.swing.JLabel labelRandomize7;
    private javax.swing.JLabel labelRandomize8;
    private javax.swing.JLabel labelRandomize9;
    private javax.swing.ButtonGroup metricsGroup;
    protected javax.swing.JTextField mutationField;
    protected javax.swing.JTextField spreadersField;
    // End of variables declaration//GEN-END:variables
}