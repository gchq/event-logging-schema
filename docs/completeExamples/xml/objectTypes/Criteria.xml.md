# `<Criteria>`

User is viewing a Criteria ojbect that represents the definition of a search/query that can be executed.

A View event is used here simply to illustrate the use of Criteria.
Criteria can be used within other schema actions.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events
  xmlns="event-logging:3"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="event-logging:3 file://event-logging-v3.4.1.xsd"
  Version="3.4.1">

  <!-- View/Criteria event -->

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
      <TypeId>viewSearchResults</TypeId>
      <Description>User is viewing a set of stored search results</Description>
      <View>
        <Criteria>
          <DataSources>
            <DataSource>geology-db</DataSource>
          </DataSources>
          <Query>
            <!-- 
            Provides a link back to the Search event generated when 
            the query was executed 
            -->
            <Id>query-538393</Id>
            <!-- Tree of terms and operators to describe the query -->
            <Advanced>
              <And>
                <Term>
                  <Name>size</Name>
                  <Condition>Equals</Condition>
                  <Value>large</Value>
                </Term>
                <Term>
                  <Name>colour</Name>
                  <Condition>Equals</Condition>
                  <Value>white</Value>
                </Term>
                <Term>
                  <Name>type</Name>
                  <Condition>Equals</Condition>
                  <Value>r</Value>
                </Term>
              </And>
            </Advanced>
          </Query>
        </Criteria>
      </View>
    </EventDetail>

  </Event>

</Events>

```
