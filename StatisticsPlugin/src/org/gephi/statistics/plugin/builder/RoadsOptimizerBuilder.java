package org.gephi.statistics.plugin.builder;

import org.gephi.statistics.plugin.RoadsOptimizer;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class RoadsOptimizerBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(RoadsOptimizerBuilder.class, "RoadsOptimizer.name");
    }

    public Statistics getStatistics() {
        return new RoadsOptimizer();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return RoadsOptimizer.class;
    }
}
