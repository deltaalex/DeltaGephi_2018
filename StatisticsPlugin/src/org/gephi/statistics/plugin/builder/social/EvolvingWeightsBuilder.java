package org.gephi.statistics.plugin.builder.social;

import org.gephi.statistics.plugin.social.EvolvingWeights;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class EvolvingWeightsBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(EvolvingWeightsBuilder.class, "EvolvingWeights.name");
    }

    public Statistics getStatistics() {
        return new EvolvingWeights();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return EvolvingWeights.class;
    }
}
