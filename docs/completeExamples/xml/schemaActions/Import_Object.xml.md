# `<EventDetail>/<Import>//<Object>`

The following example illustrates user `jc101` importing some data into an application 
called `Geology Image Database`.  The application is based on `geoimg v4.1` and is running on the server `geodb.servers.mycloud.myorg`.

The Object is of Type `Image Archive` and has an id `14131A`.  It has a Classification of `Geology`.

There is no Success element in `<Outcome>`, so it is assumed that the action completed successfully.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events 
  xmlns="event-logging:3" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xsi:schemaLocation="event-logging:3 file://event-logging-v3.4.0.xsd" 
  Version="3.4.0">

  <!-- Import Object event -->
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
      <TypeId>IMAGE-IMPORT-ARCHIVE</TypeId>
      <Description>User has imported an image archive</Description>
      <Import>
        <Destination>
          <Object>
            <Type>Image Archive</Type>
            <Id>14131A</Id>
            <Description>Crater images</Description>
            <Classification>
              <Text>Geology</Text>
            </Classification>
          </Object>
        </Destination>
        <Outcome>
          <Description>Image archive successfully imported</Description>
        </Outcome>
      </Import>
    </EventDetail>

  </Event>

</Events>
```
