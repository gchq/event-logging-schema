# `<EventDetail>/<Send>//<File>`

The following example illustrates an application transferring a file to a remote server. 

This reflects an automated service, so attribution of the sender and receiver is to a host, not a user.

The action is that the file `/appdata/alldata/gooddata/bestdata.xml` of type `text/xml` is being sent from `myhost.mydomain.org` to `yourhost.yourdomain.com`. This file is `12345321` bytes in size and has a digest/checksum/hash of `efd1dffd90296a69a8aecd7ecb1832b7`. N.B. The type of digest used is application specific and not specified in this event.

The Outcome of the event is not defined, so we assume that the transfer was successful.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events
  xmlns="event-logging:3"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="event-logging:3 file://event-logging-v3.3.0.xsd"
  Version="3.3.0">

  <Event>
    <EventTime>
      <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
    </EventTime>

    <EventSource>
      <System>
        <Name>File Distribution</Name>
        <Environment>Live</Environment>
        <Organisation>ACMECoolResearch</Organisation>
      </System>
      <Generator>File Distributor 1.5</Generator>
      <Device>
        <IPAddress>123.12.3.123</IPAddress>
      </Device>
    </EventSource>

    <EventDetail>
      <TypeId>File Transfer</TypeId>
      <Send>
        <Source>
          <Device>
            <HostName>myhost.mydomain.org</HostName>
          </Device>
        </Source>
        <Destination>
          <Device>
            <HostName>yourhost.yourdomain.com</HostName>
          </Device>
        </Destination>
        <Payload>
          <File>
            <Type>text/xml</Type>
            <Path>/appdata/alldata/gooddata/bestdata.xml</Path>
            <Size>12345321</Size>
            <Hash Type="MD5">efd1dffd90296a69a8aecd7ecb1832b7</Hash>
          </File>
        </Payload>
      </Send>
    </EventDetail>

  </Event>

</Events>
```
