/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.datalab.plugin.manipulators.columns.ui;

import java.util.Map;
import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.plugin.manipulators.columns.ColumnValuesFrequency;
import org.gephi.datalab.spi.DialogControls;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulator;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulatorUI;
import org.gephi.ui.components.JFreeChartDialog;
import org.gephi.ui.components.SimpleHTMLReport;
import org.jfree.chart.JFreeChart;
import org.openide.windows.WindowManager;

/**
 * UI for ColumnValuesFrequency AttributeColumnsManipulator.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public class ColumnValuesFrequencyUI extends javax.swing.JPanel implements AttributeColumnsManipulatorUI {

    private ColumnValuesFrequency manipulator;
    private AttributeTable table;
    private AttributeColumn column;
    private Map<Object, Integer> valuesFrequencies;
    private JFreeChart pieChart;
    private JFreeChartDialog pieChartDialog;
    private SimpleHTMLReport reportDialog;

    /** Creates new form ColumnValuesFrequencyUI */
    public ColumnValuesFrequencyUI() {
        initComponents();
    }

    public void setup(AttributeColumnsManipulator m, AttributeTable table, AttributeColumn column, DialogControls dialogControls) {
        this.table = table;
        this.column = column;
        this.manipulator = (ColumnValuesFrequency) m;
        valuesFrequencies = manipulator.buildValuesFrequencies(table, column);

        configurePieChartButton.setEnabled(valuesFrequencies.size()<=ColumnValuesFrequency.MAX_PIE_CHART_CATEGORIES);
    }

    public void unSetup() {
        if (reportDialog != null) {
            reportDialog.dispose();
        }
        if (pieChartDialog != null) {
            pieChartDialog.dispose();
        }
    }

    public String getDisplayName() {
        return manipulator.getName();
    }

    public JPanel getSettingsPanel() {
        return this;
    }

    public boolean isModal() {
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        configurePieChartButton = new javax.swing.JButton();
        showReportButton = new javax.swing.JButton();

        configurePieChartButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/datalab/plugin/manipulators/resources/category.png"))); // NOI18N
        configurePieChartButton.setText(org.openide.util.NbBundle.getMessage(ColumnValuesFrequencyUI.class, "ColumnValuesFrequencyUI.configurePieChartButton.text")); // NOI18N
        configurePieChartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configurePieChartButtonActionPerformed(evt);
            }
        });

        showReportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/datalab/plugin/manipulators/resources/application-block.png"))); // NOI18N
        showReportButton.setText(org.openide.util.NbBundle.getMessage(ColumnValuesFrequencyUI.class, "ColumnValuesFrequencyUI.showReportButton.text")); // NOI18N
        showReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showReportButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(configurePieChartButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(showReportButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(configurePieChartButton)
                    .addComponent(showReportButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void configurePieChartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configurePieChartButtonActionPerformed
        if (pieChart == null) {
            pieChart = manipulator.buildPieChart(valuesFrequencies);
        }
        if (pieChartDialog != null) {
            pieChartDialog.setVisible(true);
        } else {
            pieChartDialog = new JFreeChartDialog(WindowManager.getDefault().getMainWindow(), pieChart.getTitle().getText(), pieChart, 1000, 1000);
        }
    }//GEN-LAST:event_configurePieChartButtonActionPerformed

    private void showReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showReportButtonActionPerformed

        if (pieChart == null) {
            pieChart = manipulator.buildPieChart(valuesFrequencies);
        }
        final String html = manipulator.getReportHTML(table, column, valuesFrequencies, pieChart, pieChartDialog != null ? pieChartDialog.getChartSize() : null);

        if (reportDialog != null) {
            reportDialog.dispose();
        }
        reportDialog = new SimpleHTMLReport(WindowManager.getDefault().getMainWindow(), html);
    }//GEN-LAST:event_showReportButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton configurePieChartButton;
    private javax.swing.JButton showReportButton;
    // End of variables declaration//GEN-END:variables
}
