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
package org.gephi.ranking.spi;

import org.gephi.ranking.api.Transformer;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.ranking.api.Ranking;

/**
 * Transformer user interface. Implement this interface to create panels associated
 * to a particular transformer.
 * <p>
 * The icon and display name are used to create the transformer button in the UI.
 * <p>
 * Implementors should add the <code>@ServiceProvider</code> annotation to be
 * registered by the system.
 * 
 * @see Transformer
 * @author Mathieu Bastian
 */
public interface TransformerUI {

    /**
     * Returns the transformer's icon
     * @return the icon of this transformer
     */
    public Icon getIcon();

    /**
     * Returns the display name of the transformer
     * @return the display name of this transformer
     */
    public String getDisplayName();

    /**
     * Returns the panel associated to this transformer.
     * @param transformer the transformer to build the panel for
     * @param ranking the ranking to be used by the transformer
     * @return the panel of this transformer
     */
    public JPanel getPanel(Transformer transformer, Ranking ranking);
    
    /**
     * Returns <code>true</code> if this UI is built for <code>transformer</code>.
     * @param transformer the transformer to test ownership
     * @return <code>true</code> if this UI is associated to <code>transformer</code>,
     * <code>false</code> otherwise
     */
    public boolean isUIForTransformer(Transformer transformer);
}
