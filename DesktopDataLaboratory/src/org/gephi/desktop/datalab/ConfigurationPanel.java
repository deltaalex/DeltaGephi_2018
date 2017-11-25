/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Mathieu Jacomy, Julian Bilcke, Eduardo Ramos
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
package org.gephi.desktop.datalab;

import org.gephi.dynamic.api.DynamicController;
import org.gephi.dynamic.api.DynamicModel;
import org.openide.util.Lookup;

/**
 * Configurations dialog for DataTableTopComponent
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public class ConfigurationPanel extends javax.swing.JPanel {

    private DataTableTopComponent dataTableTopComponent;
    private DynamicController dynamicController;

    /** Creates new form ConfigurationPanel */
    public ConfigurationPanel(DataTableTopComponent dataTableTopComponent) {
        this.dataTableTopComponent=dataTableTopComponent;
        dynamicController=Lookup.getDefault().lookup(DynamicController.class);
        initComponents();
        onlyVisibleCheckBox.setSelected(dataTableTopComponent.isShowOnlyVisible());
        useSparklinesCheckBox.setSelected(dataTableTopComponent.isUseSparklines());
        timeIntervalsAsDates.setSelected(dynamicController.getModel().getTimeFormat()!=DynamicModel.TimeFormat.DOUBLE);
        timeIntervalsGraphicsCheckBox.setSelected(dataTableTopComponent.isTimeIntervalGraphics());
        showEdgesNodesLabelsCheckBox.setSelected(dataTableTopComponent.isShowEdgesNodesLabels());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        onlyVisibleCheckBox = new javax.swing.JCheckBox();
        useSparklinesCheckBox = new javax.swing.JCheckBox();
        showEdgesNodesLabelsCheckBox = new javax.swing.JCheckBox();
        timeIntervalsGraphicsCheckBox = new javax.swing.JCheckBox();
        timeIntervalsAsDates = new javax.swing.JCheckBox();

        onlyVisibleCheckBox.setText(org.openide.util.NbBundle.getMessage(ConfigurationPanel.class, "ConfigurationPanel.onlyVisibleCheckBox.text")); // NOI18N
        onlyVisibleCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onlyVisibleCheckBoxActionPerformed(evt);
            }
        });

        useSparklinesCheckBox.setText(org.openide.util.NbBundle.getMessage(ConfigurationPanel.class, "ConfigurationPanel.useSparklinesCheckBox.text")); // NOI18N
        useSparklinesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useSparklinesCheckBoxActionPerformed(evt);
            }
        });

        showEdgesNodesLabelsCheckBox.setText(org.openide.util.NbBundle.getMessage(ConfigurationPanel.class, "ConfigurationPanel.showEdgesNodesLabelsCheckBox.text")); // NOI18N
        showEdgesNodesLabelsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showEdgesNodesLabelsCheckBoxActionPerformed(evt);
            }
        });

        timeIntervalsGraphicsCheckBox.setText(org.openide.util.NbBundle.getMessage(ConfigurationPanel.class, "ConfigurationPanel.timeIntervalsGraphicsCheckBox.text")); // NOI18N
        timeIntervalsGraphicsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeIntervalsGraphicsCheckBoxActionPerformed(evt);
            }
        });

        timeIntervalsAsDates.setText(org.openide.util.NbBundle.getMessage(ConfigurationPanel.class, "ConfigurationPanel.timeIntervalsAsDates.text")); // NOI18N
        timeIntervalsAsDates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeIntervalsAsDatesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(onlyVisibleCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(useSparklinesCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(showEdgesNodesLabelsCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(timeIntervalsGraphicsCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(timeIntervalsAsDates))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(onlyVisibleCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useSparklinesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeIntervalsAsDates)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeIntervalsGraphicsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showEdgesNodesLabelsCheckBox)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void onlyVisibleCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onlyVisibleCheckBoxActionPerformed
        dataTableTopComponent.setShowOnlyVisible(onlyVisibleCheckBox.isSelected());
    }//GEN-LAST:event_onlyVisibleCheckBoxActionPerformed

    private void useSparklinesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useSparklinesCheckBoxActionPerformed
        dataTableTopComponent.setUseSparklines(useSparklinesCheckBox.isSelected());
    }//GEN-LAST:event_useSparklinesCheckBoxActionPerformed

    private void timeIntervalsGraphicsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeIntervalsGraphicsCheckBoxActionPerformed
        dataTableTopComponent.setTimeIntervalGraphics(timeIntervalsGraphicsCheckBox.isSelected());
    }//GEN-LAST:event_timeIntervalsGraphicsCheckBoxActionPerformed

    private void showEdgesNodesLabelsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showEdgesNodesLabelsCheckBoxActionPerformed
        dataTableTopComponent.setShowEdgesNodesLabels(showEdgesNodesLabelsCheckBox.isSelected());
    }//GEN-LAST:event_showEdgesNodesLabelsCheckBoxActionPerformed

    private void timeIntervalsAsDatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeIntervalsAsDatesActionPerformed
        dynamicController.setTimeFormat(timeIntervalsAsDates.isSelected() ? DynamicModel.TimeFormat.DATE : DynamicModel.TimeFormat.DOUBLE);
        dataTableTopComponent.refreshCurrentTable();
    }//GEN-LAST:event_timeIntervalsAsDatesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox onlyVisibleCheckBox;
    private javax.swing.JCheckBox showEdgesNodesLabelsCheckBox;
    private javax.swing.JCheckBox timeIntervalsAsDates;
    private javax.swing.JCheckBox timeIntervalsGraphicsCheckBox;
    private javax.swing.JCheckBox useSparklinesCheckBox;
    // End of variables declaration//GEN-END:variables

}
