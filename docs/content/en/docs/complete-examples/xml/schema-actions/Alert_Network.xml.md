---
title: "Alert (Network)"
linkTitle: "Alert (Network)"
#weight:
date: 2022-05-04
tags: 
description: >
  
---

## `<EventDetail>/<Alert>/<Network>`

This example illustrates an alert being fired by from a network device performing packet filtering:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Events 
  xmlns="event-logging:3" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xsi:schemaLocation="event-logging:3 file://event-logging-v999.99.9-documentation.xsd" 
  Version="999.99.9">

  <!-- Alert Network event -->
  <Event>
    <EventTime>
      <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
    </EventTime>

    <EventSource>
      <System>
        <Name>Geology Image Database</Name>
        <Environment>Live</Environment>
        <Organisation>ACMECoolResearch</Organisation>
      </System>
      <Generator>geoimg v4.1</Generator>
      <Device>
        <HostName>geodb.servers.mycloud.myorg</HostName>
        <IPAddress>104.105.106.107</IPAddress>
        <MACAddress>AB:CB:BC:DE:EE:FF</MACAddress>
      </Device>
      <User>
        <Id>jc101</Id>
      </User>
    </EventSource>

    <EventDetail>
      <TypeId>4921</TypeId>
      <Description>A packet was rejected by filter xyz</Description>
      <Alert>
        <Type>Network</Type>
        <Severity>Minor</Severity>
        <Priority>High</Priority>
        <Subject>Filter XYZ</Subject>
        <Network>
          <Source>
            <Device>
              <IPAddress>192.168.0.4</IPAddress>
              <Port>56123</Port>
            </Device>
          </Source>
          <Destination>
            <Device>
              <IPAddress>192.168.7.5</IPAddress>
              <Port>53</Port>
            </Device>
          </Destination>
        </Network>
      </Alert>
    </EventDetail>

  </Event>

</Events>
```
