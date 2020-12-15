package org.gephi.ui.statistics.plugin;

import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.gephi.statistics.plugin.GirvanNewmanClusterer;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = StatisticsUI.class)
public class GirvanNewmanClustererUI implements StatisticsUI {

  private GirvanNewmanClustererPanel panel;
  private GirvanNewmanClusterer clusterer;

  @Override
  public JPanel getSettingsPanel() {
    panel = new GirvanNewmanClustererPanel();
    return panel;
  }

  @Override
  public void setup(Statistics ststcs) {
    this.clusterer = (GirvanNewmanClusterer) ststcs;
    if (panel != null) {
      panel.setClusters(clusterer.getPreferredNumClusters());      
    }
  }

  @Override
  public void unsetup() {
    if (panel != null) {
      clusterer.setPreferredNumClusters(panel.getClusters());      
    }
    clusterer = null;
    panel = null;
  }

  @Override
  public Class<? extends Statistics> getStatisticsClass() {
    return GirvanNewmanClusterer.class;
  }

  @Override
  public String getValue() {
    DecimalFormat df = new DecimalFormat("###.#");    
    return "" + df.format(clusterer.getPreferredNumClusters());
  }

  @Override
  public String getDisplayName() {
    return org.openide.util.NbBundle.getMessage(getClass(), "GirvanNewmanClustererUI.name");
  }

  @Override
  public String getShortDescription() {
    return org.openide.util.NbBundle.getMessage(getClass(), "GirvanNewmanClustererUI.shortDescription");
  }

  @Override
  public String getCategory() {
    return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
  }

  @Override
  public int getPosition() {
    return 601;
  }
}