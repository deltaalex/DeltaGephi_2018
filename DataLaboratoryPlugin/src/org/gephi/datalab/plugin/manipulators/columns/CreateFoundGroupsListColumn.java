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
package org.gephi.datalab.plugin.manipulators.columns;

import java.awt.Image;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.datalab.plugin.manipulators.columns.ui.GeneralCreateColumnFromRegexUI;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulator;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulatorUI;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * AttributeColumnsManipulator that creates a new string list column from the given column and regular expression with values that are
 * the list of matching groups of the given regular expression.
 * Allows the user to select the title of the new column in the UI
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service = AttributeColumnsManipulator.class)
public class CreateFoundGroupsListColumn extends GeneralCreateColumnFromRegex {

    public void execute(AttributeTable table, AttributeColumn column) {
        if (pattern != null) {
            Lookup.getDefault().lookup(AttributeColumnsController.class).createFoundGroupsListColumn(table, column, title, pattern);
        }
    }

    public String getName() {
        return NbBundle.getMessage(CreateFoundGroupsListColumn.class, "CreateFoundGroupsListColumn.name");
    }

    public String getDescription() {
        return NbBundle.getMessage(CreateFoundGroupsListColumn.class, "CreateFoundGroupsListColumn.description");
    }

    public boolean canManipulateColumn(AttributeTable table, AttributeColumn column) {
        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        return ac.getTableRowsCount(table)>0;//Make sure that there is at least 1 row
    }

    public AttributeColumnsManipulatorUI getUI(AttributeTable table,AttributeColumn column) {
        GeneralCreateColumnFromRegexUI ui = new GeneralCreateColumnFromRegexUI();
        ui.setMode(GeneralCreateColumnFromRegexUI.Mode.MATCHING_GROUPS);
        return ui;
    }

    public int getType() {
        return 200;
    }

    public int getPosition() {
        return 100;
    }

    public Image getIcon() {
        return ImageUtilities.loadImage("org/gephi/datalab/plugin/manipulators/resources/binocular--arrow.png");
    }
}
