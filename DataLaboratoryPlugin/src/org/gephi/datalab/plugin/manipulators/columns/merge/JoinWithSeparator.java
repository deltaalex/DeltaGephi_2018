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
package org.gephi.datalab.plugin.manipulators.columns.merge;

import javax.swing.Icon;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.api.AttributeColumnsMergeStrategiesController;
import org.gephi.datalab.plugin.manipulators.columns.merge.ui.JoinWithSeparatorUI;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.columns.merge.AttributeColumnsMergeStrategy;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * AttributeColumnsMergeStrategy that joins columns of any type into a new column
 * using the separator string that the user provides.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public class JoinWithSeparator implements AttributeColumnsMergeStrategy {

    public static final String SEPARATOR_SAVED_PREFERENCES = "JoinWithSeparator_Separator";
    private static final String DEFAULT_SEPARATOR = ",";
    private AttributeTable table;
    private AttributeColumn[] columns;
    private String newColumnTitle, separator;

    public void setup(AttributeTable table, AttributeColumn[] columns) {
        this.table = table;
        this.columns = columns;
        separator=NbPreferences.forModule(JoinWithSeparator.class).get(SEPARATOR_SAVED_PREFERENCES, DEFAULT_SEPARATOR);
    }

    public void execute() {
        NbPreferences.forModule(JoinWithSeparator.class).put(SEPARATOR_SAVED_PREFERENCES, separator);
        Lookup.getDefault().lookup(AttributeColumnsMergeStrategiesController.class).joinWithSeparatorMerge(table, columns, null, newColumnTitle, separator);
    }

    public String getName() {
        return NbBundle.getMessage(JoinWithSeparator.class, "JoinWithSeparator.name");
    }

    public String getDescription() {
        return NbBundle.getMessage(JoinWithSeparator.class, "JoinWithSeparator.description");
    }

    public boolean canExecute() {
        return true;
    }

    public ManipulatorUI getUI() {
        return new JoinWithSeparatorUI();
    }

    public int getType() {
        return 0;
    }

    public int getPosition() {
        return 0;
    }

    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/datalab/plugin/manipulators/resources/join.png", true);
    }

    public String getNewColumnTitle() {
        return newColumnTitle;
    }

    public void setNewColumnTitle(String newColumnTitle) {
        this.newColumnTitle = newColumnTitle;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public AttributeTable getTable() {
        return table;
    }
}
