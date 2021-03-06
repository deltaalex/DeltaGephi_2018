package org.gephi.statistics.plugin.builder.social;

import org.gephi.statistics.plugin.social.SocialInfluenceBenchmark;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class SocialInfluenceBenchmarkBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(SocialInfluenceBenchmarkBuilder.class, "SocialInfluenceBenchmark.name");
    }

    public Statistics getStatistics() {
        return new SocialInfluenceBenchmark();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return SocialInfluenceBenchmark.class;
    }
}
