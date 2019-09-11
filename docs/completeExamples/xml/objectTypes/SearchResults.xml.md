# `<SearchResults>`

User is viewing a set of search results independently of the search event that generated them.
The Query/Id element can be used to link the two events together.

This object type can be used when the results of the query are not know at the time the search is executed and the Search event is created.

A View event is used here simply to illustrate the use of SearchResults.
SearchResults can be used within other schema actions.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events
  xmlns="event-logging:3"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="event-logging:3 file://event-logging-v999.99.9-documentation.xsd"
  Version="999.99.9">

  <!-- View/SearchResults event -->

  <Event>

    <EventTime>
      <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
    </EventTime>

    <EventSource>
      <!-- The source system specific unique ID for this event -->
      <EventId>1024</EventId>
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
          <SearchResults>
            <Query>
              <!-- 
              Provides a link back to the Search event generated when the 
              query was executed. The link can also be acheived using EventLinks below.
              -->
              <Id>query-538393</Id>
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
          </SearchResults>
        </View>
    </EventDetail>

    <!-- This event is linked back to its parent (the initial search) -->
    <EventLinks>
      <EventParent>
        <!-- Links this event to the one with EventSource/EventId[text()='1023'] -->
        <EventId>1023</EventId>
      </EventParent>
    </EventLinks>

  </Event>

</Events>

```
