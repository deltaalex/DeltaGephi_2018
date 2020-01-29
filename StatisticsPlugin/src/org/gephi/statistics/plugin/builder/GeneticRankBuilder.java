package org.gephi.statistics.plugin.builder;

import org.gephi.statistics.plugin.GeneticRank;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexander
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class GeneticRankBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(GeneticRankBuilder.class, "GeneticRank.name");
    }

    public Statistics getStatistics() {
        return new GeneticRank();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return GeneticRank.class;
    }
}
