/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.ui.filters.plugin.dynamic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.gephi.desktop.perspective.spi.BottomComponent;
import org.gephi.filters.plugin.dynamic.DynamicRangeBuilder.DynamicRangeFilter;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Mathieu Bastian
 */
public class DynamicRangePanel extends javax.swing.JPanel {

    private final String OPEN;
    private final String CLOSE;

    public DynamicRangePanel() {
        initComponents();
        OPEN = NbBundle.getMessage(DynamicRangePanel.class, "DynamicRangePanel.timelineButton.text");
        CLOSE = NbBundle.getMessage(DynamicRangePanel.class, "DynamicRangePanel.timelineButton.closetext");
    }

    public void setup(final DynamicRangeFilter filter) {
        final BottomComponent bottomComponent = Lookup.getDefault().lookup(BottomComponent.class);
        timelineButton.setText(bottomComponent.isVisible() ? CLOSE : OPEN);
        timelineButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if (!bottomComponent.isVisible()) {
                    bottomComponent.setVisible(true);
                    timelineButton.setText(CLOSE);
                } else {
                    bottomComponent.setVisible(false);
                    timelineButton.setText(OPEN);
                }
            }
        });
        keepEmptyCheckbox.setSelected(filter.isKeepNull());
        keepEmptyCheckbox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (!filter.isKeepNull() == keepEmptyCheckbox.isSelected()) {
                    filter.getProperties()[1].setValue(keepEmptyCheckbox.isSelected());
                }
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        timelineButton = new javax.swing.JButton();
        keepEmptyCheckbox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        timelineButton.setText(org.openide.util.NbBundle.getMessage(DynamicRangePanel.class, "DynamicRangePanel.timelineButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(timelineButton, gridBagConstraints);

        keepEmptyCheckbox.setText(org.openide.util.NbBundle.getMessage(DynamicRangePanel.class, "DynamicRangePanel.keepEmptyCheckbox.text")); // NOI18N
        keepEmptyCheckbox.setToolTipText(org.openide.util.NbBundle.getMessage(DynamicRangePanel.class, "DynamicRangePanel.keepEmptyCheckbox.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(keepEmptyCheckbox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox keepEmptyCheckbox;
    private javax.swing.JButton timelineButton;
    // End of variables declaration//GEN-END:variables
}
