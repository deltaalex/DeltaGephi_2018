/*
 Copyright 2008-2011 Gephi
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
package org.gephi.plugins.example.datalab.column;

import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.spi.DialogControls;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulator;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulatorUI;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.util.NbBundle;

/**
 * UI for ConvertColumnToDynamic
 * Demonstrates Netbeans Validation API
 * @author Eduardo Ramos<eduramiba@gmail.com>
 */
public class ConvertColumnToDynamicUI extends javax.swing.JPanel implements AttributeColumnsManipulatorUI{
    private ConvertColumnToDynamic manipulator;
    private DialogControls dialogControls;
    /**
     * Creates new form ConvertColumnToDynamicUI
     */
    public ConvertColumnToDynamicUI() {
        initComponents();
    }
    
    @Override
    public void setup(AttributeColumnsManipulator m, AttributeTable table, AttributeColumn column, DialogControls dialogControls) {
        this.manipulator=(ConvertColumnToDynamic) m;
        this.dialogControls=dialogControls;
        
        //Get initial start and end:
        startText.setText(manipulator.getStart());
        endText.setText(manipulator.getEnd());
    }

    @Override
    public void unSetup() {
        //Communicate start and end (validated) of the default interval to the manipulator before it is executed:
        manipulator.setStart(startText.getText());
        manipulator.setEnd(endText.getText());
    }

    @Override
    public String getDisplayName() {
        return manipulator.getName();//Use manipulator name as the dialog title
    }

    @Override
    public JPanel getSettingsPanel() {
        //Create a validation panel:
        ValidationPanel validationPanel = new ValidationPanel();
        validationPanel.setInnerComponent(this);

        ValidationGroup group = validationPanel.getValidationGroup();

        group.add(startText,new DoubleValidator());
        group.add(endText,new DoubleValidator());

        return validationPanel;
    }

    @Override
    public boolean isModal() {
        return true;
    }
    
    class DoubleValidator implements Validator<String>{

        @Override
        public boolean validate(Problems problems, String compName, String text) {
            try{
                Double.parseDouble(text);
                dialogControls.setOkButtonEnabled(true);
                return true;
            }catch(NumberFormatException e){
                problems.add(NbBundle.getMessage(ConvertColumnToDynamicUI.class, "ConvertColumnToDynamicUI.validator.error", text));
                dialogControls.setOkButtonEnabled(false);
                return false;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startLabel = new javax.swing.JLabel();
        endLabel = new javax.swing.JLabel();
        startText = new javax.swing.JTextField();
        endText = new javax.swing.JTextField();

        startLabel.setText(org.openide.util.NbBundle.getMessage(ConvertColumnToDynamicUI.class, "ConvertColumnToDynamicUI.startLabel.text")); // NOI18N

        endLabel.setText(org.openide.util.NbBundle.getMessage(ConvertColumnToDynamicUI.class, "ConvertColumnToDynamicUI.endLabel.text")); // NOI18N

        startText.setText(org.openide.util.NbBundle.getMessage(ConvertColumnToDynamicUI.class, "ConvertColumnToDynamicUI.startText.text")); // NOI18N

        endText.setText(org.openide.util.NbBundle.getMessage(ConvertColumnToDynamicUI.class, "ConvertColumnToDynamicUI.endText.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(endLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(endText, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startLabel)
                    .addComponent(startText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endLabel)
                    .addComponent(endText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel endLabel;
    private javax.swing.JTextField endText;
    private javax.swing.JLabel startLabel;
    private javax.swing.JTextField startText;
    // End of variables declaration//GEN-END:variables
}
