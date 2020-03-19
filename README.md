# gateway-filter
A cloud cache gateway sender filter to log cache statistics


### Build

```
mvn package
```


### Deploy to cloud cache

- Start a `gfsh` session with the cloud cache service. (https://docs.pivotal.io/p-cloud-cache/1-7/accessing-instance.html)

- Create the gateway sender with filter

```
create gateway-sender --id=remoteB --parallel=false --remote-distributed-system-id="2" 
--gateway-event-filter=io.pivotal.se.TimingGatewayEventFilter 
```

### Statistics
* receivedEvents
* transmittedEvents
* acknowledgedEvents
* totalQueueTime
* minimumQueueTime
* maximumQueueTime
* queueTimePerEvent
* totalTransmitTime
* minimumTransmitTime
* maximumTransmitTime
* transmitTimePerEvent


### Log Format
```
INFO: GatewaySenderQueueStatistics[receivedEvents=125; transmittedEvents=1; acknowledgedEvents=0; 
totalQueueTime=134; minimumQueueTime=134; maximumQueueTime=134; queueTimePerEvent=134; 
totalTransmitTime=0; minimumTransmitTime=9223372036854775807; maximumTransmitTime=0; transmitTimePerEvent=0]
```



