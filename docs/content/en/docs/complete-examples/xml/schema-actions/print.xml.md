---
title: "Print"
linkTitle: "Print"
#weight:
date: 2022-05-06
tags: 
  - complete-example
schema_actions:
  - print
description: >
  An example of printing a document.
---

## `<EventDetail>/<Print>`

The following example illustrates a document with the title `Resume - J Coder` being printed on `prn01.luna1.lan.myorg.com`
by user `jc101`.

The document is `2` pages in length and `3410212` bytes in length.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events 
  xmlns="event-logging:3" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xsi:schemaLocation="event-logging:3 file://event-logging-v999.99.9-documentation.xsd" 
  Version="999.99.9">

  <!-- Print event 

    The following example illustrates a document with the title Resume - J Coder 
    being printed on prn01.luna1.lan.myorg.com by user jc101.

    The document is 2 pages in length and 3410212 bytes in length.
  -->
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
