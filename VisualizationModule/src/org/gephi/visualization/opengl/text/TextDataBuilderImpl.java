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
package org.gephi.visualization.opengl.text;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.Estimator;
import org.gephi.data.attributes.type.DynamicType;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.graph.api.EdgeData;
import org.gephi.graph.api.NodeData;
import org.gephi.graph.api.TextData;
import org.gephi.graph.spi.TextDataFactory;
import org.gephi.visualization.impl.TextDataImpl;
import org.gephi.visualization.opengl.text.TextModel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
public class TextDataBuilderImpl {

    private Estimator defaultEstimator;
    private Estimator numberEstimator;

    public void buildNodeText(NodeData nodeData, TextDataImpl textDataImpl, TextModel model, TimeInterval timeInterval) {
        if (model.getNodeTextColumns() != null) {
            String str = "";
            int i = 0;
            for (AttributeColumn c : model.getNodeTextColumns()) {
                if (i++ > 0) {
                    str += " - ";
                }
                Object val = nodeData.getAttributes().getValue(c.getIndex());
                if (val instanceof DynamicType) {
                    DynamicType dynamicType = (DynamicType) val;
                    Estimator estimator = defaultEstimator;
                    if (Number.class.isAssignableFrom(dynamicType.getUnderlyingType())) {
                        estimator = numberEstimator;
                    }
                    if (timeInterval != null) {
                        val = dynamicType.getValue(timeInterval.getLow(), timeInterval.getHigh(), estimator);
                    } else {
                        val = dynamicType.getValue(estimator);
                    }
                }
                str += val != null ? val : "";
            }
            textDataImpl.setText(str);
        }
    }

    public void buildEdgeText(EdgeData edgeData, TextDataImpl textDataImpl, TextModel model, TimeInterval timeInterval) {
        if (model.getEdgeTextColumns() != null) {
            String str = "";
            int i = 0;
            for (AttributeColumn c : model.getEdgeTextColumns()) {
                if (i++ > 0) {
                    str += " - ";
                }
                Object val = edgeData.getAttributes().getValue(c.getIndex());
                if (val instanceof DynamicType) {
                    DynamicType dynamicType = (DynamicType) val;
                    Estimator estimator = defaultEstimator;
                    if (Number.class.isAssignableFrom(dynamicType.getUnderlyingType())) {
                        estimator = numberEstimator;
                    }
                    if (timeInterval != null) {
                        val = dynamicType.getValue(timeInterval.getLow(), timeInterval.getHigh(), estimator);
                    } else {
                        val = dynamicType.getValue(estimator);
                    }
                }
                str += val != null ? val : "";
            }
            textDataImpl.setText(str);
        }
    }

    public void setDefaultEstimator(Estimator defaultEstimator) {
        this.defaultEstimator = defaultEstimator;
    }

    public void setNumberEstimator(Estimator numberEstimator) {
        this.numberEstimator = numberEstimator;
    }
}
