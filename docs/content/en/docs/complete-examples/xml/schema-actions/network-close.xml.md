---
title: "Network (Close)"
linkTitle: "Network (Close)"
#weight:
date: 2022-05-06
tags: 
  - complete-example
schema_actions:
  - network
object_types:
description: >
  An example of closing a network connection.
---

## `<EventDetail>/<Network>/<Close>`

This example illustrates the closing of a network connection between two devices.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Events 
  xmlns="event-logging:3" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xsi:schemaLocation="event-logging:3 file://event-logging-v999.99.9-documentation.xsd" 
  Version="999.99.9">

  <!-- Netowrk Close event -->
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
      <TypeId>ABC123</TypeId>
      <Network>
        <Close>
          <Source>
            <Device>
              <IPAddress>192.168.1.2</IPAddress>
              <Port>56123</Port>
            </Device>
            <TransportProtocol>UDP</TransportProtocol>
          </Source>
          <Destination>
            <Device>
              <IPAddress>192.168.1.3</IPAddress>
              <Port>53</Port>
            </Device>
            <Application>Outlook</Application>
            <TransportProtocol>TCP</TransportProtocol>
            <ApplicationProtocol>IMAP</ApplicationProtocol>
            <Port>80</Port>
          </Destination>
        </Close>
      </Network>
    </EventDetail>

  </Event>

</Events>
```

