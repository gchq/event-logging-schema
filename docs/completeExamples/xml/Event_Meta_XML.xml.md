# `<Event>/<Meta>` (XML)

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events
  xmlns="event-logging:3"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="event-logging:3 file://event-logging-v999.99.9-documentation.xsd"
  Version="999.99.9">

  <!-- XML Meta example -->
  <Event>
    <Meta ContentType="XML:MyMeta" Version="1.2">
      <MyMeta xmlns="http://myorg.mydomain.mymeta">
        <ElementA>value A</ElementA>
        <ElementB>value B</ElementB>
      </MyMeta>
    </Meta>
    <EventTime>
      <TimeCreated>2017-01-02T03:04:05.678Z</TimeCreated>
    </EventTime>
    <EventSource>
      <System>
        <Name>SPACEPRINT</Name>
        <Environment>Luna1</Environment>
        <Organisation>ACMECoolResearch</Organisation>
      </System>
      <Generator>ZeroGPrinter</Generator>
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

</Events>
```
