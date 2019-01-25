# Import

Import is used to model events in which data has been placed into a controlled environment of some kind.

Examples:

* Data being imported into an application, such that the application now controls that data.
* Data being imported onto a host (and thereby the host's network) from removable media.

---

The following example illustrates user `jc101` importing some data into an application 
called `Geology Image Database`.  The application is based on `geoimg v4.1` and is running on the server `geodb.servers.mycloud.myorg`.

The Object is of Type `Image Archive` and has an id `14131A`.  It has a Classification of `Geology`.

There is no Success element in `<Outcome>`, so it is assumed that the action completed successfully.

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

---

## Removable Media

The following example illustrates how removable media use could be represented within the events-logging schema.

The action is that user `jc101` has read the file `E:/DCIM/Spacecam101/141516.jpg` from a removable USB drive onto the workstation `LUNA/LUNADESK35`.

**N.B.** Although the user might not have actually read the object represented in the EventDetail, all read operations (including file system read/open operations and web page accesses, etc) are represented in the schema as View.  The nature of the event (type of View) can be indicated within EventDetail/TypeId as we have done in this example, and where possible a more human-readable explanation within EventDetail/Description.

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
      <UserDetails><Id>jc101</Id>
          ...
      </UserDetails>
    </User>
    <Interactive>true</Interactive>
  </EventSource>
  <EventDetail>
    <TypeId>ReadRM</TypeId>
    <Description>User has read a file from removable media</Description>
    <Import>
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
        <Hash Type="SHA-256">66E0E8221E8B899F08658DA444064E631FA9B8ABE9068A208AFE051BD4E7B960</Hash>
        <Media>
          <Type>USBMassStorage</Type>
          <Removable>true</Removable>
        </Media>
      </File>
      <Outcome>
        <Permitted>true</Permitted>
      </Outcome>
    </Import>
  </EventDetail>
</Event>
```
