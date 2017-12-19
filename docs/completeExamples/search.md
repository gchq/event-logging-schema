#Search
The following example illustrates a query against a database application.
The action is that user ```jc101``` execute a query ```type=r, size=large, colour=white``` which returned 
2 results. The Interactive field is set to ```false```, so it may be inferred that this was an automated operation of
some kind.

The results were two Objects. Both were of type ```Rock```. The first with an id of ```7811``` and a name of
```Surpisingly Heavy Chunk``` and the second with an id of ```11418``` and a name of ```Possible Gold Ore```.

```xml
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
               <UserDetails>
                  <Id>jc101</Id>
                  ...
               </UserDetails>
            </User>
            <Interactive>false</Interactive>
         </EventSource>
         <EventDetail>
            <TypeId>findByConstraint</TypeId>
            <Description>User has queried database using specified constraints</Description>
            <Search>
               <Query>
                  <Raw>type=r, size=large, colour=white</Raw>
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
```
