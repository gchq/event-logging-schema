# `<EventDetail>/<Send>//<Document>`

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events
  xmlns="event-logging:3"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="event-logging:3 file://event-logging-v3.3.0.xsd"
  Version="3.3.0">

  <!-- Send Document event 
    The following example illustrates an application being used to transfer a file to from one user to another user.

    The action is that the file `samples1.zip` of size `14127312` bytes was sent by the user `jc101` to
    the user `C=GB, O=WeDoRocksCo, OU=Space, CN=Tom Thumb(UID=tthumb01)`. 

    The file was transferred successfully.
  -->
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
          <!-- Fully populated here to illustrate a decorated event-->
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
              <!-- User belongs to the organisation generating the event so only an Id is required -->
          </User>
        </Source>
        <Destination>
          <User>
            <Id>C=GB, O=WeDoRocksCo, OU=Space, CN=Tom Thumb (UID=tthumb01)</Id>
              <!-- User is external to the organisation generating the event so a rich UserDetails element is required-->
            <UserDetails>
              <Id>tthumb011</Id>
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
</Events>
```
