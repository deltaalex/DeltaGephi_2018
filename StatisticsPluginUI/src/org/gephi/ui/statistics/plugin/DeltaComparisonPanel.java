package org.gephi.ui.statistics.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import org.gephi.statistics.plugin.DeltaComparison;

/**
 *
 * @author Alexander
 */
public class DeltaComparisonPanel extends javax.swing.JPanel {

    /**
     * Common preferences folder
     */
    private static final String PrefsFolder = System.getenv("APPDATA") + "/.gephi/common/";
    /**
     * Preferences file to be stored in the common preferences folder
     */
    private static final String PrefsFile = "deltacomparison.properties";
    // "org/gephi/ui/statistics/plugin/deltacomparison.properties";
    private Properties prop = new Properties();
    // property keys
    private static final String[] KEYS = new String[]{"ADeg", "APL", "CC",
        "Mod", "Dns", "Dmt"};
    // checkbox list
    private ArrayList<JCheckBox> metricBoxes;

    public DeltaComparisonPanel() {
        initComponents();

        metricBoxes = new ArrayList<JCheckBox>();
        metricBoxes.add(checkADeg);
        metricBoxes.add(checkAPL);
        metricBoxes.add(checkCC);
        metricBoxes.add(checkMod);
        metricBoxes.add(checkDns);
        metricBoxes.add(checkDmt);
    }

    void setup() {
        setup(PrefsFolder + PrefsFile);
    }

    private void setup(String file) {
        ArrayList<Double> values;
        try {
            // load preferences file                
            InputStream in = new FileInputStream(file);
            prop.load(in);
            in.close();

            // read properties iteratively
            values = new ArrayList<Double>();
            for (String key : KEYS) {
                Object value = prop.get(key);
                if (value != null) {
                    values.add(Double.parseDouble((String) value));
                } else {
                    values.add(0.0);
                }
            }

        } catch (IOException e) {
            values = null;
        }

        // if no file was stored
        if (values == null) {
            for (int i = 0; i < tableBase.getModel().getColumnCount(); ++i) {
                tableBase.getModel().setValueAt(null, 0, i);
            }
        } // if preferences are found
        else {
            for (int i = 0; i < values.size(); ++i) {
                tableBase.getModel().setValueAt(values.get(i), 0, i);
            }
        }
    }

    void unsetup(DeltaComparison delta) {

        List<Double> metrics = new ArrayList<Double>();
        try {
            // create preferences file
            File dir = new File(PrefsFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream out = new FileOutputStream(PrefsFolder + PrefsFile, false);

            // read table properties iteratively and store them
            for (int i = 0; i < KEYS.length; ++i) {
                Double metric = 0.0;
                Object value = tableBase.getModel().getValueAt(0, i);

                if (value != null) {
                    metric = Double.parseDouble(value + "");
                }

                metrics.add(metric);
                prop.setProperty(KEYS[i], metric + "");
            }

            prop.store(out, "");
            out.close();

        } catch (IOException e) {
            // ?
        }

        if (delta != null) {
            // save metric values
            delta.setMetrics(metrics);

            // save enabled metrics
            ArrayList<Boolean> enabledMetrics = new ArrayList<Boolean>();
            for (int i = 0; i < metricBoxes.size(); ++i) {
                enabledMetrics.add(metricBoxes.get(i).isSelected());
            }

            delta.setEnabledMetrics(enabledMetrics);
        }
    }

    void unsetup(Double[] values) {

        for (int i = 0; i < values.length; ++i) {
            tableBase.getModel().setValueAt(values[i], 0, i);
        }

        DeltaComparison delta = null;
        unsetup(delta);
    }

    void setEnabledMetrics(Boolean[] enabledMetrics) {
        if (enabledMetrics.length == 6) {
            for (int i = 0; i < metricBoxes.size(); ++i) {
                metricBoxes.get(i).setSelected(enabledMetrics[i]);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toggleSimulation1 = new javax.swing.JToggleButton();
        desriptionLabel = new org.jdesktop.swingx.JXLabel();
        header = new org.jdesktop.swingx.JXHeader();
        bLoad = new javax.swing.JButton();
        labelResolution2 = new org.jdesktop.swingx.JXLabel();
        labelResolution3 = new org.jdesktop.swingx.JXLabel();
        checkADeg = new javax.swing.JCheckBox();
        labelResolution4 = new org.jdesktop.swingx.JXLabel();
        checkAPL = new javax.swing.JCheckBox();
        checkCC = new javax.swing.JCheckBox();
        checkMod = new javax.swing.JCheckBox();
        checkDns = new javax.swing.JCheckBox();
        checkDmt = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableBase = new javax.swing.JTable();
        checkSave = new javax.swing.JCheckBox();
        labelResolution5 = new org.jdesktop.swingx.JXLabel();
        jSeparator1 = new javax.swing.JSeparator();
        edgeLabel = new javax.swing.JLabel();
        dissimilarityField = new javax.swing.JTextField();
        toggleSimulation = new javax.swing.JToggleButton();

        toggleSimulation1.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.toggleSimulation1.text")); // NOI18N

        desriptionLabel.setLineWrap(true);
        desriptionLabel.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.desriptionLabel.text")); // NOI18N
        desriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        header.setDescription(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.header.description")); // NOI18N
        header.setTitle(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.header.title")); // NOI18N

        bLoad.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.bLoad.text")); // NOI18N
        bLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLoadActionPerformed(evt);
            }
        });

        labelResolution2.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution2.setLineWrap(true);
        labelResolution2.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.labelResolution2.text")); // NOI18N
        labelResolution2.setToolTipText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.labelResolution2.toolTipText")); // NOI18N
        labelResolution2.setFocusable(false);
        labelResolution2.setFont(labelResolution2.getFont().deriveFont(labelResolution2.getFont().getSize()-1f));
        labelResolution2.setPreferredSize(new java.awt.Dimension(500, 12));

        labelResolution3.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution3.setLineWrap(true);
        labelResolution3.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.labelResolution3.text")); // NOI18N
        labelResolution3.setFocusable(false);
        labelResolution3.setFont(labelResolution3.getFont().deriveFont(labelResolution3.getFont().getSize()-1f));
        labelResolution3.setPreferredSize(new java.awt.Dimension(500, 12));

        checkADeg.setSelected(true);
        checkADeg.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.checkADeg.text")); // NOI18N

        labelResolution4.setLineWrap(true);
        labelResolution4.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.labelResolution4.text")); // NOI18N
        labelResolution4.setFocusable(false);
        labelResolution4.setFont(labelResolution4.getFont().deriveFont(labelResolution4.getFont().getSize()-1f));
        labelResolution4.setPreferredSize(new java.awt.Dimension(500, 12));

        checkAPL.setSelected(true);
        checkAPL.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.checkAPL.text")); // NOI18N

        checkCC.setSelected(true);
        checkCC.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.checkCC.text")); // NOI18N

        checkMod.setSelected(true);
        checkMod.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.checkMod.text")); // NOI18N

        checkDns.setSelected(true);
        checkDns.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.checkDns.text")); // NOI18N

        checkDmt.setSelected(true);
        checkDmt.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.checkDmt.text")); // NOI18N

        tableBase.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ADeg", "APL", "CC", "Mod", "Dns", "Dmt"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tableBase);

        checkSave.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.checkSave.text")); // NOI18N

        labelResolution5.setForeground(new java.awt.Color(102, 102, 102));
        labelResolution5.setLineWrap(true);
        labelResolution5.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.labelResolution5.text")); // NOI18N
        labelResolution5.setFocusable(false);
        labelResolution5.setFont(labelResolution5.getFont().deriveFont(labelResolution5.getFont().getSize()-1f));
        labelResolution5.setPreferredSize(new java.awt.Dimension(500, 12));

        edgeLabel.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.edgeLabel.text")); // NOI18N

        dissimilarityField.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.dissimilarityField.text")); // NOI18N

        toggleSimulation.setText(org.openide.util.NbBundle.getMessage(DeltaComparisonPanel.class, "DeltaComparisonPanel.toggleSimulation.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addComponent(desriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(391, 391, 391))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(bLoad)
                                .addGap(18, 18, 18)
                                .addComponent(labelResolution2, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(checkSave)
                                .addGap(24, 24, 24)
                                .addComponent(labelResolution3, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelResolution5, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkCC)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(labelResolution4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(checkADeg)
                                        .addComponent(checkAPL))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(checkDns)
                                        .addComponent(checkMod)
                                        .addComponent(checkDmt)))))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(edgeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dissimilarityField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(toggleSimulation))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bLoad)
                    .addComponent(labelResolution2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelResolution3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkSave))
                .addGap(18, 18, 18)
                .addComponent(labelResolution5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelResolution4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkADeg)
                    .addComponent(checkMod))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAPL)
                    .addComponent(checkDns))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkCC)
                    .addComponent(checkDmt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dissimilarityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edgeLabel)
                    .addComponent(toggleSimulation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(desriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void bLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLoadActionPerformed


        //String filename = ".";
        JFileChooser fc = new JFileChooser();// (new File(filename));

        // Show open dialog; this method does not return until the dialog is closed
        fc.showOpenDialog(this);
        File selFile = fc.getSelectedFile();
        if (selFile != null) {
            setup(selFile.getAbsolutePath());
        }

    }//GEN-LAST:event_bLoadActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bLoad;
    protected javax.swing.JCheckBox checkADeg;
    protected javax.swing.JCheckBox checkAPL;
    protected javax.swing.JCheckBox checkCC;
    protected javax.swing.JCheckBox checkDmt;
    protected javax.swing.JCheckBox checkDns;
    protected javax.swing.JCheckBox checkMod;
    protected javax.swing.JCheckBox checkSave;
    private org.jdesktop.swingx.JXLabel desriptionLabel;
    protected javax.swing.JTextField dissimilarityField;
    private javax.swing.JLabel edgeLabel;
    private org.jdesktop.swingx.JXHeader header;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private org.jdesktop.swingx.JXLabel labelResolution2;
    private org.jdesktop.swingx.JXLabel labelResolution3;
    private org.jdesktop.swingx.JXLabel labelResolution4;
    private org.jdesktop.swingx.JXLabel labelResolution5;
    private javax.swing.JTable tableBase;
    protected javax.swing.JToggleButton toggleSimulation;
    protected javax.swing.JToggleButton toggleSimulation1;
    // End of variables declaration//GEN-END:variables
}
