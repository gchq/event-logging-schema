# Complete Examples
A number of examples of complete events are provided below, in order to illustrate use of the schema.

N.B. All entities including organisations, persons, devices and applications are imaginary.
All details (e.g. MAC Address, Name, IP Address) are similarly imaginary and any relationship to any 
real entities is entirely coincidental!

In some places UserDetails have been abreviated for clarity using an elipsis (...).

In a real-world situation, a design decision would be needed - when to leave the event more concise using
only an ID and when to join with additional information.  Data such as user details
change slowly, but they _do change_, e.g. as people get promoted, change role, etc.  Therefore, such a joining 
process would utilise the values for these additional data fields that were correct at the time the event
was generated.  Certain tools such as [Stroom](https://github.com/gchq/stroom-docs/blob/master/README.md 
"Stroom on Github") support this, and are sufficiently flexible to allow this enrichment process to take place
during initial normalisation prior to storage, during analysis or at query time / inspection.
 
The same principle applies to Device details, etc.  

Compression such as ZIP can very significantly reduce the overhead associated with highly repetitive
data, and are able to allow fully enriched data to be persisted with only minimal overhead.
 
A version of the examples as a complete XML document is available [here](./completeExamples.xml) .
## Send




The following example illustrates an application being used to transfer a file to from one user to another user.

The action is that the file ```samples1.zip``` of size ```14127312``` bytes was sent by the user ```jc101``` to
the user ```C=GB, O=WeDoRocksCo, OU=Space, CN=Tom Thumb(UID=tthumb01)```. 

The file was transferred successfully.
```xml
      <Event>
         <EventTime>
            <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
         </EventTime>
         <EventSource>
            <System>
               <Name>FileSharer21</Name>
               <Description>Interactive File Sharing</Description>
               <Environment>Operational</Environment>
               <Organisation>ACMECoolResearch</Organisation>
            </System>
            <Generator>fs-21-v2.2</Generator>
            <Device>
               <HostName>fs04.fs.myorg.com</HostName>
               <IPAddress>131.141.151.161</IPAddress>
               <MACAddress>A1:B1:C1:D1:E1:F1</MACAddress>
            </Device>
            <Client>
               <IPAddress>121.121.121.121</IPAddress>
            </Client>
            <User>
               <Id>jc101</Id>
               <UserDetails>
                  <Id>jc101</Id>
                  <StaffNumber>101</StaffNumber>
                  <Surname>Coder</Surname>
                  <Initials>JD</Initials>
                  <Title>Mr</Title>
                  <KnownAs>Jolly</KnownAs>
                  <PersonType>Employee</PersonType>
                  <Group>Technology</Group>
                  <Unit>Geology</Unit>
                  <Position>T.SD/42</Position>
                  <Role>Geologist</Role>
                  <GradeOfPost>E.5</GradeOfPost>
                  <EmploymentType>Employee</EmploymentType>
                  <EmploymentStatus>Active</EmploymentStatus>
                  <Nationality>GB</Nationality>
                  <Location>Moon Base Alpha</Location>
                  <RoomNumber>4/31</RoomNumber>
                  <Phone>11223</Phone>
                  <SupervisorStaffNumber>421</SupervisorStaffNumber>
                  <Organisation>ACMECoolResearch</Organisation>
                  <HostOrganisation>MoonCo</HostOrganisation>
               </UserDetails>
            </User>
         </EventSource>
         <EventDetail>
            <TypeId>NormalSend</TypeId>
            <Description>A user sends a file to another user</Description>
            <Purpose>
               <Justification>Rock sample spectrum for analysis</Justification>
            </Purpose>
            <Send>
               <Source>
                  <User>
                     <Id>jc101</Id>
                     <UserDetails>
                        ...
                     </UserDetails>
                  </User>
               </Source>
               <Destination>
                  <User>
                     <Id>C=GB, O=WeDoRocksCo, OU=Space, CN=Tom Thumb(UID=tthumb01)</Id>
                     <UserDetails>
                        <Id>tthumb01</Id>
                        <Surname>Thumb</Surname>
                        <Initials>T</Initials>
                        <Group>Space</Group>
                        <Nationality>GB</Nationality>
                        <Organisation>WeDoRocksCo</Organisation>
                     </UserDetails>
                  </User>
               </Destination>
               <Payload>
                  <Document>
                     <Name>samples1.zip</Name>
                     <Size>14127312</Size>
                  </Document>
               </Payload>
               <Outcome>
                  <Success>true</Success>
                  <Description>Transfer Succeeded</Description>
               </Outcome>
            </Send>
         </EventDetail>
      </Event>
```

The following example illustrates an application transferring a file to a remote server. 

This reflects an automated service, so attribution of the sender and receiver is to a host, not a user.

The action is that the file ```/appdata/alldata/gooddata/bestdata.xml``` of type ```text/xml``` is being
sent from ```myhost.mydomain.org``` to ```yourhost.yourdomain.com```. This file is ```12345321``` bytes in size
and has a digest/checksum/hash of ```efd1dffd90296a69a8aecd7ecb1832b7```.  N.B. The type of digest used is application
specific and not specified in this event.

The Outcome of the event is not defined, so we assume that the transfer was successful.

```xml
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
                  <Size>12345321</Size>
                     <Type>text/xml</Type>
                     <Hash>efd1dffd90296a69a8aecd7ecb1832b7</Hash>
                     <Path>/appdata/alldata/gooddata/bestdata.xml</Path>
                  </File>
               </Payload>
            </Send>
         </EventDetail>
      </Event>
```
##Search
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

##Read
The following example illustrates how removable media use could be represented within the events-logging schema.

The action is that user ```jc101``` has read the file ```E:/DCIM/Spacecam101/141516.jpg``` from a removable 
USB drive onto the workstation ```LUNA/LUNADESK35```.

```xml
      <Event>
         <EventTime>
            <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
         </EventTime>
         <EventSource>
            <System>
               <Name>Space Desk</Name>
               <Environment>LunaDomain</Environment>
               <Organisation>ACMECoolResearch</Organisation>
            </System>
            <Generator>RMMaster2000 RM Control</Generator>
            <Device>
               <HostName>rmmaster.luna.mydomain.org</HostName>
            </Device>
            <Client>
               <Name>LUNA/LUNADESK35</Name>
               <HostName>lunadesk35.lunadesk.luna.mydomain.org</HostName>
            </Client>
            <User>
               <Id>jc101</Id>
               <Domain>ACMECoolResearch/Users</Domain>
               <UserDetails>
                  <Id>jc101</Id>
                  ...
               </UserDetails>
            </User>
            <Interactive>true</Interactive>
         </EventSource>
         <EventDetail>
            <TypeId>ReadRM</TypeId>
            <Description>User has read a file from removable media</Description>
            <Read>
              <File>
                 <Name>141516.jpg</Name>
                 <Description>JPG Image</Description>
                 <Permissions>
                    <Permission>
                       <User>
                          <Id>jc101</Id>
                          <Domain>ACMECoolResearch/Users</Domain>
                          <UserDetails>
                             ...
                          </UserDetails>
                       </User>
                       <Allow>Read</Allow>
                    </Permission>
                 </Permissions>
                 <Path>E:/DCIM/Spacecam101/141516.jpg</Path>
                 <Size>5445121</Size>
                 <Hash>f4d3a2b1ffac</Hash>
                 <Media>
                    <Type>USBMassStorage</Type>
                    <Removable>true</Removable>
                 </Media>
              </File>
               <Outcome>
                  <Permitted>true</Permitted>
               </Outcome>
            </Read>
         </EventDetail>
      </Event>
```

#Import

The following example illustrates user ```jc101``` importing some data into an application 
called ```Geology Image Database```.  The application is based on ```geoimg v4.1``` and is running on the server ```geodb.servers.mycloud.myorg```.

The Object is of Type ```Image Archive``` and has an id ```14131A```.  It has a Classification of ```Geology```.

There is no Success element on ```<Outcome>```, so it is assumed that the action completed successfully.

```xml
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
               <UserDetails>
                  ...
               </UserDetails>
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
```

The following example illustrates how a user initiated antivirus scan could be modelled.

The user ```jc101``` has run an antimalware scan on ```LUNA/LUNADESK35``` using the antivirus product ```viricide-av```.


```xml
      <Event>
         <EventTime>
            <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
         </EventTime>
         <EventSource>
            <System>
               <Name>VIRICIDE</Name>
               <Environment>Op</Environment>
               <Organisation>ACMECoolResearch</Organisation>
            </System>
            <Generator>viricide-av</Generator>
            <Device>
               <Name>LUNA/LUNADESK35</Name>
               <HostName>lunadesk35.lunadesk.luna.mydomain.org</HostName>
               <IPAddress>101.101.101.101</IPAddress>
               <MACAddress>AA:BB:CC:DD:EE:FF</MACAddress>
            </Device>
            <User>
               <Id>jc101</Id>
               <UserDetails>
                  ...
               </UserDetails>
            </User>
         </EventSource>
         <EventDetail>
            <TypeId>Manual-Scan</TypeId>
            <Description>User initiated a scan</Description>
            <AntiMalware>
               <StartScan>
                  <Product>
                     <Id>viricide7</Id>
                     <Name>Viricide Antivirus</Name>
                     <Version>7.2</Version>
                  </Product>
                  <Signature>
                     <Version>78.1.3-1</Version>
                  </Signature>
               </StartScan>
            </AntiMalware>
         </EventDetail>
      </Event>
```

#Print

The following example illustrates a document with the title ```Resume - J Coder``` being printed on ```prn01.luna1.lan.myorg.com```.

The document is ```2``` pages in length and ```3410212``` bytes in length.


```xml
      <Event>
          <EventTime>
              <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
          </EventTime>
          <EventSource>
              <System>
                  <Name>SPACEPRINT</Name>
                  <Environment>Luna1</Environment>
                  <Organisation>ACMECoolResearch</Organisation>
              </System>
              <Generator>ZeroGPrinter v2.2</Generator>
              <Device>
                  <HostName>prn01.luna1.lan.myorg.com</HostName>
              </Device>
              <User>
                  <Id>jc101</Id>
              </User>
              <Interactive>true</Interactive>
          </EventSource>
          <EventDetail>
              <TypeId>BWPrint</TypeId>
              <Description>System has finished printing</Description>
              <Print>
                  <Action>FinishPrint</Action>
                  <PrintJob>
                      <Document>
                          <Title>Resume - J Coder</Title>
                      </Document>
                      <Pages>2</Pages>
                      <Size>3410212</Size>
                      <Submitted>2017-01-02T03:03:51.234Z</Submitted>
                  </PrintJob>
                  <Outcome>
                      <Success>true</Success>
                  </Outcome>
              </Print>
          </EventDetail>
      </Event>
```

