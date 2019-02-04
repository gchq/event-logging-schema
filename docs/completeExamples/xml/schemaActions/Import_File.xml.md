# `<EventDetail>/<Import>//<File>`

The following example illustrates how removable media use could be represented within the events-logging schema.

The action is that user `jc101` has read the file `E:/DCIM/Spacecam101/141516.jpg` from a removable USB drive onto the workstation `LUNA/LUNADESK35`.

**N.B.** Although the user might not have actually read the object represented in the EventDetail, all read operations (including file system read/open operations and web page accesses, etc) are represented in the schema as View.  
The nature of the event (type of View) can be indicated within `<EventDetail>/<TypeId>` as we have done in this example, and where possible a more human-readable explanation within `<EventDetail>/<Description>`.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events 
  xmlns="event-logging:3" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xsi:schemaLocation="event-logging:3 file://event-logging-v3.4.0-SNAPSHOT.xsd" 
  Version="3.4.0-SNAPSHOT">

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
      </User>
      <Interactive>true</Interactive>
    </EventSource>

    <EventDetail>
      <TypeId>ReadRM</TypeId>
      <Description>User has read a file from removable media</Description>
      <Import>
        <Source>
          <File>
            <Name>141516.jpg</Name>
            <Description>JPG Image</Description>
            <Permissions>
              <Permission>
                <User>
                  <Id>jc101</Id>
                  <Domain>ACMECoolResearch/Users</Domain>
                </User>
                <Allow>Read</Allow>
              </Permission>
            </Permissions>
            <Path>E:/DCIM/Spacecam101/141516.jpg</Path>
            <Size>5445121</Size>
            <Media>
              <Type>USBMassStorage</Type>
              <Removable>true</Removable>
            </Media>
            <Hash Type="SHA-256">66E0E8221E8B899F08658DA444064E631FA9B8ABE9068A208AFE051BD4E7B960</Hash>
          </File>
        </Source>
        <Outcome>
          <Permitted>true</Permitted>
        </Outcome>
      </Import>
    </EventDetail>

  </Event>

</Events>
```
