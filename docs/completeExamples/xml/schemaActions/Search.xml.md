# `<EventDetail>/<Search>`

The following example illustrates a query against a database application.
The action is that user `jc101` execute a query `select r.* from ROCK r where r.TYPE = 'r' and r.SIZE = 'large' and r.COLOUR = 'white'` which returned 2 results.
The Interactive field is set to `false`, so it may be inferred that this was an automated operation of some kind.

The results were two Objects. Both were of type `Rock`.
The first with an id of `7811` and a name of `Surpisingly Heavy Chunk` and the second with an id of `11418` and a name of `Possible Gold Ore`.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events 
  xmlns="event-logging:3"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="event-logging:3 file://event-logging-v999.99.9-documentation.xsd"
  Version="999.99.9">

  <!-- Search event -->
  <Event>
    <EventTime>
      <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
    </EventTime>

    <EventSource>
      <System>
        <Name>Rock Sample Database</Name>
        <Environment>Space</Environment>
        <Organisation>ACMECoolResearch</Organisation>
        <Version>R8.1</Version>
      </System>
      <Generator>db-query</Generator>
      <Device>
        <HostName>db56.serverfarm.mydomain.org</HostName>
        <IPAddress>191.181.171.161</IPAddress>
      </Device>
      <Client>
        <HostName>desktop4.moonbase-a.mydomain.org</HostName>
        <IPAddress>111.101.101.111</IPAddress>
      </Client>
      <User>
        <Id>jc101</Id>
      </User>
      <Interactive>true</Interactive>
    </EventSource>

    <EventDetail>
      <TypeId>findByConstraint</TypeId>
      <Description>User has queried database using specified constraints</Description>
      <Search>
        <Query>
          <Id>query-538393</Id>
          <Description>Large, white, type 'r'</Description>
          <Raw>select r.* from ROCK r where r.TYPE = 'r' and r.SIZE = 'large' and r.COLOUR = 'white'</Raw>
        </Query>
        <TotalResults>2</TotalResults>
        <Results>
          <Object>
            <Type>Rock</Type>
            <Id>78121</Id>
            <Name>Surpisingly Heavy Chunk</Name>
          </Object>
          <Object>
            <Type>Rock</Type>
            <Id>11418</Id>
            <Name>Possible Gold Ore</Name>
          </Object>
        </Results>
      </Search>
    </EventDetail>
  </Event>

</Events>
```
