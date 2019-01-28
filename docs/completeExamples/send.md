# Send

The following example illustrates an application being used to transfer a file to from one user to another user.

The action is that the file `samples1.zip` of size `14127312` bytes was sent by the user `jc101` to
the user `C=GB, O=WeDoRocksCo, OU=Space, CN=Tom Thumb(UID=tthumb01)`. 

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
          ...
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

The action is that the file `/appdata/alldata/gooddata/bestdata.xml` of type `text/xml` is being sent from `myhost.mydomain.org` to `yourhost.yourdomain.com`. This file is `12345321` bytes in size and has a digest/checksum/hash of `efd1dffd90296a69a8aecd7ecb1832b7`. N.B. The type of digest used is application specific and not specified in this event.

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
          <Hash Type="MD5">efd1dffd90296a69a8aecd7ecb1832b7</Hash>
          <Path>/appdata/alldata/gooddata/bestdata.xml</Path>
        </File>
      </Payload>
    </Send>
  </EventDetail>
</Event>
```
