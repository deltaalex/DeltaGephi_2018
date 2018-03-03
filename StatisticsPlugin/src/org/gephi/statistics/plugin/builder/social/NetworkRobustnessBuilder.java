package org.gephi.statistics.plugin.builder.social;

import org.gephi.statistics.plugin.social.NetworkRobustness;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class NetworkRobustnessBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(NetworkRobustnessBuilder.class, "NetworkRobustness.name");
    }

    public Statistics getStatistics() {
        return new NetworkRobustness();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return NetworkRobustness.class;
    }
}
