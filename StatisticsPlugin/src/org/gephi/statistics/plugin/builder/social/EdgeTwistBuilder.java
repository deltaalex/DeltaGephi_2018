package org.gephi.statistics.plugin.builder.social;

import org.gephi.statistics.plugin.social.EdgeTwist;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class EdgeTwistBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(EdgeTwistBuilder.class, "EdgeTwist.name");
    }

    public Statistics getStatistics() {
        return new EdgeTwist();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return EdgeTwist.class;
    }
}
