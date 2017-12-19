# Print

The following example illustrates a document with the title `Resume - J Coder` being printed on `prn01.luna1.lan.myorg.com`
by user `jc101`.

The document is `2` pages in length and `3410212` bytes in length.


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
