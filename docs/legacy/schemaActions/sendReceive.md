# Send and Receive
Events relating to the sending or receiving of some form of payload can be recorded with the `<Send>` and `<Receive>` elements.  These events are at a higher level of abstraction than the low level `<Network>/<Send>` and `<Network>/<Receive>` elements, e.g. sending a file using some form of data transfer service.

``` xml
<EventDetail>
  <TypeId>SendFile</TypeId>
  <Send>
    <Source>
      <User>
        <Id>CN=Some Person (sperson), OU=people, O=Some Org,C=GB</Id>
      </User>
    </Source>
    <Destination>
      <User>
        <Id>CN=Person2 (person2), OU=people, O=Some Org, C=GB</Id>
      </User>
      <Device>
        <HostName>otherbox.someorg.org</HostName>
      </Device>
    </Destination>
    <Payload>
      <File>
        <Name>meetingMinutes.odf</Name>
      </File>
    </Payload>
    <Outcome>
      <Success>true</Success>
    </Outcome>
  </Send>
</EventDetail>
``` 
