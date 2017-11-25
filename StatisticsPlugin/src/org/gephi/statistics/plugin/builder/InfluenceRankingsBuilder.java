package org.gephi.statistics.plugin.builder;

import org.gephi.statistics.plugin.InfluenceRankings;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexander
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class InfluenceRankingsBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(InfluenceRankingsBuilder.class, "InfluenceRankings.name");
    }

    public Statistics getStatistics() {
        return new InfluenceRankings();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return InfluenceRankings.class;
    }
}
