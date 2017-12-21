# Alert
This schema action should be used to describe events relating to alerts generated from such things as anti-virus/malware monitoring systems, intrusion detection stytems, infrastructure/network monitoring systems or other rule based alerting systems.

The enumerated `<Type>` of the alert must be provided to categorise the alert.  The remainder of the elements are optional to allow for flexibility in describing the event action.

An example alert action for a disk usage monitoring system is as follows:

```xml
<EventDetail>
  <TypeId>err1234</TypeId>
  <Alert>
    <Type>Error</Type>
    <Description>/dev/sda1 is 100% full</Description>
  </Alert>
</EventDetail>
```

The following is an example of an alert from a network device performing packet filtering:
```xml
<EventDetail>
  <TypeId>4921</TypeId>
  <Description>A packet was rejected by filter xyz</Description>
  <Alert>
    <Type>Network</Type>
    <Network>
      <Source>
        <IPAddress>192.168.0.4</IPAddress>
      </Source>
      <Destination>
        <IPAddress>192.168.7.5</IPAddress>
      </Destination>
    </Network>
  </Alert>
</EventDetail>
```
