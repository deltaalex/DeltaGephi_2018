/*
Copyright 2008-2011 Gephi
Authors : Sébastien Heymann <sebastien.heymann@gephi.org>
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
package org.gephi.ui.statistics.plugin.dynamic;

import javax.swing.JPanel;
import org.gephi.statistics.plugin.dynamic.DynamicNbEdges;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Sébastien Heymann
 */
@ServiceProvider(service = StatisticsUI.class)
public class DynamicNbEdgesUI implements StatisticsUI {

    private final StatSettings settings = new StatSettings();
    private DynamicNbEdges nbEdges;
    private DynamicNbEdgesPanel panel;

    public JPanel getSettingsPanel() {
        panel = new DynamicNbEdgesPanel();
        return panel;
    }

    public void setup(Statistics statistics) {
        this.nbEdges = (DynamicNbEdges) statistics;
        if (panel != null) {
            settings.load(nbEdges);
        }
    }

    public void unsetup() {
        if (panel != null) {
            settings.save(nbEdges);
        }
        nbEdges = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return DynamicNbEdges.class;
    }

    public String getValue() {
        return "";
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "DynamicNbEdgesUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_DYNAMIC;
    }

    public int getPosition() {
        return 200;
    }

    public String getShortDescription() {
        return NbBundle.getMessage(getClass(), "DynamicNbEdgesUI.shortDescription");
    }

    private static class StatSettings {

        private double window = 0.0;
        private double tick = 0.0;

        private void save(DynamicNbEdges stat) {
            this.window = stat.getWindow();
            this.tick = stat.getTick();
        }

        private void load(DynamicNbEdges stat) {
            stat.setWindow(window);
            stat.setTick(tick);
        }
    }
}
