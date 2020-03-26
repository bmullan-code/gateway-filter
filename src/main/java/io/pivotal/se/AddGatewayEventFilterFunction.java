package io.pivotal.se;


import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewaySender;

public class AddGatewayEventFilterFunction implements Function {
  
  public void execute(FunctionContext context)  {
    // Get the Gateway Sender id
    String[] arguments = (String[]) context.getArguments();
    String senderId = arguments[0];
    
    // Get the Gateway Sender
    GatewaySender sender = context.getCache().getGatewaySender(senderId);
    System.out.println("Retrieved " + sender);
    
    // Add the GatewayEventFilter
    GatewayEventFilter filter = new TimingGatewayEventFilter();
    sender.addGatewayEventFilter(filter);
    System.out.println("Added " + filter);

    context.getResultSender().lastResult(true);
  }
  
  public String getId() {
    return getClass().getSimpleName();
  }
}
