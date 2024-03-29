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
package org.gephi.algorithms.shortestpath;

import java.util.HashMap;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.dynamic.DynamicUtilities;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class BellmanFordShortestPathAlgorithm extends AbstractShortestPathAlgorithm {

    protected final DirectedGraph graph;
    protected final HashMap<NodeData, Edge> predecessors;
    protected TimeInterval timeInterval;

    public BellmanFordShortestPathAlgorithm(DirectedGraph graph, Node sourceNode) {
        super(sourceNode);
        this.graph = graph;
        predecessors = new HashMap<NodeData, Edge>();
        DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
        if (dynamicController != null) {
            timeInterval = DynamicUtilities.getVisibleInterval(dynamicController.getModel(graph.getGraphModel().getWorkspace()));
        }
    }

    public void compute() {

        graph.readLock();

        //Initialize
        int nodeCount = 0;
        for (Node node : graph.getNodes()) {
            distances.put(node.getNodeData(), Double.POSITIVE_INFINITY);
            nodeCount++;
        }
        distances.put(sourceNode.getNodeData(), 0d);


        //Relax edges repeatedly
        for (int i = 0; i < nodeCount; i++) {

            boolean relaxed = false;
            for (Edge edge : graph.getEdges()) {
                Node target = edge.getTarget();
                if (relax(edge)) {
                    relaxed = true;
                    predecessors.put(target.getNodeData(), edge);
                }
            }
            if (!relaxed) {
                break;
            }
        }

        //Check for negative-weight cycles
        for (Edge edge : graph.getEdges()) {

            if (distances.get(edge.getSource().getNodeData()) + edgeWeight(edge) < distances.get(edge.getTarget().getNodeData())) {
                graph.readUnlock();
                throw new RuntimeException("The Graph contains a negative-weighted cycle");
            }
        }

        graph.readUnlock();
    }

    @Override
    protected double edgeWeight(Edge edge) {
        if (timeInterval != null) {
            return edge.getWeight(timeInterval.getLow(), timeInterval.getHigh());
        }
        return edge.getWeight();
    }

    public Node getPredecessor(Node node) {
        Edge edge = predecessors.get(node.getNodeData());
        if (edge != null) {
            if (edge.getSource().getNodeData() != node.getNodeData()) {
                return edge.getSource();
            } else {
                return edge.getTarget();
            }
        }
        return null;
    }

    public Edge getPredecessorIncoming(Node node) {
        return predecessors.get(node.getNodeData());
    }
}
