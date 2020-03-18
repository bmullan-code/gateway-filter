package io.pivotal.se;

import java.util.concurrent.atomic.AtomicLong;

public class GatewaySenderQueueStatistics {
	
	private AtomicLong receivedEvents;

	private AtomicLong transmittedEvents;

	private AtomicLong acknowledgedEvents;

	private AtomicLong totalQueueTime;

	private AtomicLong totalTransmitTime;

	private AtomicLong minimumQueueTime;

	private AtomicLong minimumTransmitTime;

	private AtomicLong maximumQueueTime;

	private AtomicLong maximumTransmitTime;
	
	public GatewaySenderQueueStatistics() {
		this.receivedEvents = new AtomicLong();
		this.transmittedEvents = new AtomicLong();
		this.acknowledgedEvents = new AtomicLong();
		this.totalQueueTime = new AtomicLong();
		this.totalTransmitTime = new AtomicLong();
		this.minimumQueueTime = new AtomicLong(Long.MAX_VALUE);
		this.minimumTransmitTime = new AtomicLong(Long.MAX_VALUE);
		this.maximumQueueTime = new AtomicLong();
		this.maximumTransmitTime = new AtomicLong();
	}
	
	protected void incrementReceivedEvents() {
		this.receivedEvents.incrementAndGet();
	}
	
	protected void incrementTransmittedEvents() {
		this.transmittedEvents.incrementAndGet();
	}

	protected void incrementAcknowledgedEvents() {
		this.acknowledgedEvents.incrementAndGet();
	}

	protected void addQueueTime(long time) {
		this.totalQueueTime.addAndGet(time);
		setMinimumQueueTime(time);
		setMaximumQueueTime(time);
	}

	protected void addTransmitTime(long time) {
		this.totalTransmitTime.addAndGet(time);
		setMinimumTransmitTime(time);
		setMaximumTransmitTime(time);
	}

	public long getReceivedEvents() {
		return this.receivedEvents.get();
	}

	public long getTransmittedEvents() {
		return this.transmittedEvents.get();
	}

	public long getAcknowledgedEvents() {
		return this.acknowledgedEvents.get();
	}

	public long getTotalQueueTime() {
		return this.totalQueueTime.get();
	}

	public long getTotalTransmitTime() {
		return this.totalTransmitTime.get();
	}

  public long getQueueTimePerEvent() {
    return getTransmittedEvents() == 0 ? 0 : getTotalQueueTime() / getTransmittedEvents();
  }

  public long getTransmitTimePerEvent() {
    return getAcknowledgedEvents() == 0 ? 0 : getTotalTransmitTime() / getAcknowledgedEvents();
  }
    
  public void setMinimumQueueTime(long time) {
    while (true) {
      long currentMinimumQueueTime = this.minimumQueueTime.get();
      if (time < currentMinimumQueueTime) {
        if (this.minimumQueueTime.compareAndSet(currentMinimumQueueTime, time)) {
          return;
        }
      } else {
        return;
      }
    }
  }

	public long getMinimumQueueTime() {
		return this.minimumQueueTime.get();
	}

  public void setMaximumQueueTime(long time) {
    while (true) {
      long currentMaximumQueueTime = this.maximumQueueTime.get();
      if (time > currentMaximumQueueTime) {
        if (this.maximumQueueTime.compareAndSet(currentMaximumQueueTime, time)) {
          return;
        }
      } else {
        return;
      }
    }
  }

	public long getMaximumQueueTime() {
		return this.maximumQueueTime.get();
	}
  
  public void setMinimumTransmitTime(long time) {
    while (true) {
      long currentMinimumTransmitTime = this.minimumTransmitTime.get();
      if (time < currentMinimumTransmitTime) {
        if (this.minimumTransmitTime.compareAndSet(currentMinimumTransmitTime, time)) {
          return;
        }
      } else {
        return;
      }
    }
  }

	public long getMinimumTransmitTime() {
		return this.minimumTransmitTime.get();
	}
  
  public void setMaximumTransmitTime(long time) {
    while (true) {
      long currentMaximumTransmitTime = this.maximumTransmitTime.get();
      if (time > currentMaximumTransmitTime) {
        if (this.maximumTransmitTime.compareAndSet(currentMaximumTransmitTime, time)) {
          return;
        }
      } else {
        return;
      }
    }
  }

	public long getMaximumTransmitTime() {
		return this.maximumTransmitTime.get();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder
			.append(getClass().getSimpleName())
			.append("[")
			.append("receivedEvents=")
			.append(getReceivedEvents())
			.append("; transmittedEvents=")
			.append(getTransmittedEvents())
			.append("; acknowledgedEvents=")
			.append(getAcknowledgedEvents())
			.append("; totalQueueTime=")
			.append(getTotalQueueTime())
			.append("; minimumQueueTime=")
			.append(getMinimumQueueTime())
			.append("; maximumQueueTime=")
			.append(getMaximumQueueTime())
			.append("; queueTimePerEvent=")
			.append(getQueueTimePerEvent())
			.append("; totalTransmitTime=")
			.append(getTotalTransmitTime())
			.append("; minimumTransmitTime=")
			.append(getMinimumTransmitTime())
			.append("; maximumTransmitTime=")
			.append(getMaximumTransmitTime())
			.append("; transmitTimePerEvent=")
			.append(getTransmitTimePerEvent())
			.append("]");
		return builder.toString();
	}
}
