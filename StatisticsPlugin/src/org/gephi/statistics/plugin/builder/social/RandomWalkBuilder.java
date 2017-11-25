package org.gephi.statistics.plugin.builder.social;

import org.gephi.statistics.plugin.social.RandomWalk;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class RandomWalkBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(RandomWalkBuilder.class, "RandomWalk.name");
    }

    public Statistics getStatistics() {
        return new RandomWalk();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return RandomWalk.class;
    }
}
