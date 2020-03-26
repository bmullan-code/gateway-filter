package io.pivotal.se;

import org.apache.geode.cache.Declarable;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewaySender;

import java.util.List;

public class GetGatewaySenderQueueStatisticsFunction implements Declarable, Function {
  
  public void execute(FunctionContext context) {
    // Get the GatewaySender id
    String[] arguments = (String[]) context.getArguments();
    String senderId = arguments[0];
    
    GatewaySenderQueueStatistics statistics = null;
    
    // Get the GatewaySender
    GatewaySender sender = context.getCache().getGatewaySender(senderId);
    
    if (sender != null) {
      // Get the TimingGatewayEventFilter
      List<GatewayEventFilter> filters = sender.getGatewayEventFilters();
      GatewayEventFilter filter = filters
        .stream()
        .filter(f -> f instanceof TimingGatewayEventFilter)
        .findAny()
        .orElse(null);
    
      // Get the GatewaySenderQueueStatistics
      if (filter != null) {
        statistics = ((TimingGatewayEventFilter) filter).getGatewaySenderQueueStatistics();
      }
    }
    context.getResultSender().lastResult(statistics);
  }
  
  public String getId() {
    return getClass().getSimpleName();
  }
}
