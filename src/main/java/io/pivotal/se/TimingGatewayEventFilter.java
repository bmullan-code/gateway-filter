package io.pivotal.se;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.logging.FileHandler;
//import java.util.logging.Logger;
//import java.util.logging.SimpleFormatter;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayQueueEvent;
import org.apache.geode.internal.cache.wan.GatewaySenderEventImpl;

public class TimingGatewayEventFilter implements GatewayEventFilter, Declarable {

//  Logger logger = Logger.getLogger("statistics");  
//  FileHandler fh;
	
  private GatewaySenderQueueStatistics queueStatistics;
  
  private Map<Long,Long> transmitStartTimes;
  
  public TimingGatewayEventFilter() throws Exception {
	  
//	  fh = new FileHandler("statistics.log");  
//      logger.addHandler(fh);
//      SimpleFormatter formatter = new SimpleFormatter();  
//      fh.setFormatter(formatter);  
//      logger.info("Setting up statistics logging");
      
    this.queueStatistics = new GatewaySenderQueueStatistics();
    this.transmitStartTimes = new ConcurrentHashMap<>();
    launchDumpQueueStatisticsThread();
  }

  public boolean beforeEnqueue(GatewayQueueEvent event) {
    // Increment received events
    this.queueStatistics.incrementReceivedEvents();

    return true;
  }

  public boolean beforeTransmit(GatewayQueueEvent event) {
    // This method can be called multiple times for the same batch if the remote site is
    // not connected.
    GatewaySenderEventImpl gsei = (GatewaySenderEventImpl) event;
    //if (this.transmitStartTimes.containsKey(gsei.getShadowKey())) {
    if (this.transmitStartTimes.containsKey(gsei.getEventId().getSequenceId())) {
      // This case means the batch is being re-attempted.
      // @TODO Decrement the previous time from the stats and add the new time.
      //System.out.println(Thread.currentThread().getName() + ": Reattempting transmission event=" + event.getKey());
    } else {
			// Increment transmitted events
			this.queueStatistics.incrementTransmittedEvents();
		
			// Calculate and save queue time for this event
			long currentTime = System.currentTimeMillis();
			long queueTime = currentTime - gsei.getCreationTime();
			this.queueStatistics.addQueueTime(queueTime);
		
			// Set the transmit start time for this event
			//this.transmitStartTimes.put(gsei.getShadowKey(), currentTime);
      this.transmitStartTimes.put(gsei.getEventId().getSequenceId(), currentTime);
		
			// Log the current event
			//logTime(gsei, "queueTime", currentTime, gsei.getCreationTime(), queueTime);
		}
    return true;
  }

  public void afterAcknowledgement(GatewayQueueEvent event) {
    // Get transmit start time for this event
    GatewaySenderEventImpl gsei = (GatewaySenderEventImpl) event;
    // Long transmitStartTime = this.transmitStartTimes.remove(gsei.getShadowKey());
    Long transmitStartTime = this.transmitStartTimes.remove(gsei.getEventId().getSequenceId());
    
    // If the event was not transmitted by this member, ignore it.
    // Only handle primary events.
    if (transmitStartTime != null) {
      // Increment acknowledged events
      this.queueStatistics.incrementAcknowledgedEvents();
      
      // Calculate and save transmit time for this event
		  long currentTime = System.currentTimeMillis();
			long transmitTime = currentTime - transmitStartTime;
			this.queueStatistics.addTransmitTime(transmitTime);

      // Log the current event
			//logTime(gsei, "transmitTime", currentTime, transmitStartTime, transmitTime);
		}
  }

  private GatewaySenderQueueStatistics getQueueStatistics() {
    return this.queueStatistics;
  }
  
  private void logTime(GatewaySenderEventImpl event, String activity, long currentTime, long startTime, long time) {
    StringBuilder builder = new StringBuilder();
    builder
    	.append(getClass().getSimpleName())
    	.append(" ")
    	.append(activity)
    	.append(" for event ")
    	// .append("shadowKey=")
    	// .append(event.getShadowKey())
      .append("eventId=")
      .append(event.getEventId().getSequenceId())
    	.append("; key=")
    	.append(event.getKey())
    	.append("; currentTime=")
    	.append(currentTime)
    	.append("; startTime=")
    	.append(startTime)
    	.append("; time=")
    	.append(time);
    System.out.println(builder.toString());
    System.err.println(builder.toString());
  }
  
  private void launchDumpQueueStatisticsThread() {
	  
    Thread thread = new Thread(
      new Runnable() {
        public void run() {
          long previousReceivedEvents=0, previousTransmittedEvents=0, previousAcknowledgedEvents=0;
          GatewaySenderQueueStatistics queueStatistics = getQueueStatistics();
          while (true) {
            long currentReceivedEvents = queueStatistics.getReceivedEvents();
            long currentTransmittedEvents = queueStatistics.getTransmittedEvents();
            long currentAcknowledgedEvents = queueStatistics.getAcknowledgedEvents();
            if (currentReceivedEvents != previousReceivedEvents
              || currentTransmittedEvents != previousTransmittedEvents
            	|| currentAcknowledgedEvents != previousAcknowledgedEvents) {
              System.out.println(queueStatistics);
//              System.err.println(queueStatistics);
//              logger.info(queueStatistics.toString());  
              previousReceivedEvents = currentReceivedEvents;
              previousTransmittedEvents = currentTransmittedEvents;
              previousAcknowledgedEvents = currentAcknowledgedEvents;
            }

            try {Thread.sleep(2000);} catch (InterruptedException e) {}
          }
        }
      });
    thread.setDaemon(true);
    thread.start();
  }
  
  public void close() {
  }
  
  public void init(Properties properties) {
  }
}
