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
package org.gephi.graph.dhns.utils.avl;

import org.gephi.utils.collection.avl.AVLItemAccessor;
import org.gephi.utils.collection.avl.ParamAVLTree;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.node.AbstractNode;

/**
 * Special type of tree which knows his {@link AbstractNode} owner. The <code>AVLItemAccessor</code> always
 * return the number of the <code>AbstractNode</code> linked to the owner.
 * <p>
 * This type of tree stores {@link AbstractEdge}. These edges can be <b>IN</b> or <b>OUT</b>. The instance
 * of the edge is duplicated in each node, once as <b>IN</b> and once as <b>OUT</b>. In each node, the
 * tree key must be the neigbour's number. So the <code>getNumber()</code> method compare the given
 * item with the owner and returns the neighbour's number.
 * 
 * @author Mathieu Bastian
 */
public class EdgeOppositeTree extends ParamAVLTree<AbstractEdge> {

    private AbstractNode owner;

    public EdgeOppositeTree(AbstractNode owner) {
        super();
        this.owner = owner;
        setAccessor(new EdgeOppositeImplAVLItemAccessor());
    }

    public AbstractNode getOwner() {
        return owner;
    }

    public boolean hasNeighbour(AbstractNode node) {
        return getItem(node.getNumber()) != null;
    }

    private class EdgeOppositeImplAVLItemAccessor implements AVLItemAccessor<AbstractEdge> {

        @Override
        public int getNumber(AbstractEdge item) {
            if (item.getSource().getId() == owner.getId()) {
                return item.getTarget().getNumber();
            } else {
                return item.getSource().getNumber();
            }
        }
    }
}
