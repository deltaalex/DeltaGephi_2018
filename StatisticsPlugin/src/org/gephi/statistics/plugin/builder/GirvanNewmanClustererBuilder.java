package org.gephi.statistics.plugin.builder;

import org.gephi.statistics.plugin.GirvanNewmanClusterer;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = StatisticsBuilder.class)
public class GirvanNewmanClustererBuilder implements StatisticsBuilder {

  @Override
  public String getName() {
    return NbBundle.getMessage(GirvanNewmanClustererBuilder.class, "GirvanNewmanClustererBuilder.name");
  }

  @Override
  public Statistics getStatistics() {
    return new GirvanNewmanClusterer();
  }

  @Override
  public Class<? extends Statistics> getStatisticsClass() {
    return GirvanNewmanClusterer.class;
  }
}