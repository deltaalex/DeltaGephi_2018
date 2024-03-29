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
package org.gephi.visualization.api.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.gephi.visualization.VizController;
import org.gephi.visualization.api.initializer.Modeler;
import org.gephi.visualization.apiimpl.ModelImpl;
import org.gephi.visualization.api.initializer.CompatibilityModeler;

/**
 *
 * @author Mathieu Bastian
 */
public class CompatibilityModelClass extends ModelClass {

    //Initializer
    private CompatibilityModeler currentModeler;
    private List<CompatibilityModeler> modelers;
    private CompatibilityModeler newModeler;

    public CompatibilityModelClass(String name, boolean lod, boolean selectable, boolean clickable, boolean glSelection, boolean aloneSelection) {
        super(name, lod, selectable, clickable, glSelection, aloneSelection);
        modelers = new ArrayList<CompatibilityModeler>();
    }

    public void lod(Iterator<ModelImpl> iterator) {
        for (; iterator.hasNext();) {
            ModelImpl obj = iterator.next();
            currentModeler.chooseModel(obj);
        }
    }

    public void beforeDisplay(GL gl, GLU glu) {
        currentModeler.beforeDisplay(gl, glu);
    }

    public void afterDisplay(GL gl, GLU glu) {
        currentModeler.afterDisplay(gl, glu);
    }

    public void addModeler(Modeler modeler) {
        modelers.add((CompatibilityModeler) modeler);
    }

    @Override
    public void setCurrentModeler(Modeler modeler) {
        if (currentModeler == null) {
            currentModeler = (CompatibilityModeler) modeler;
        }
        if (modeler != currentModeler) {
            newModeler = (CompatibilityModeler) modeler;
            VizController.getInstance().getVizModel().setNodeModeler(newModeler.getClass().getSimpleName());
        }
    }

    public void setCurrentModeler(String className) {
        for (CompatibilityModeler mod : modelers) {
            if (mod.getClass().getSimpleName().equals(className)) {
                setCurrentModeler(mod);
            }
        }
    }

    @Override
    public CompatibilityModeler getCurrentModeler() {
        return currentModeler;
    }

    @Override
    public List<CompatibilityModeler> getModelers() {
        return modelers;
    }

    public void swapModelers() {
        if (newModeler != null) {
            currentModeler = newModeler;
            newModeler = null;
            VizController.getInstance().getVizModel().setNodeModeler(currentModeler.getClass().getSimpleName());
        }
    }
}
