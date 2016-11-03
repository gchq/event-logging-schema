# Network
Some network devices are able to record auditable events. Typically these events relate to network connections being made between devices and data being transferred between them.

There are several types of network device that may be capable of this. They include:

* **Firewalls** – Allow or block traffic between certain devices depending on configured rules. Details about what connections are blocked and allowed can be recorded.
* **Message boundary filters** – Allow or block transfer of messages between devices depending on configured rules. Types of messages could include emails, IM etc.

For each event, details about the source and destination device may be recorded. Where connections/transfers are blocked the rule name and associated message may also be recorded, see the following example.

``` xml
<EventDetail>
  <TypeId>ABC123</TypeId>
  <Network>
    <Close>
      <Source>
        <Device>
          <IPAddress>192.168.1.2</IPAddress>
          <Port>56123</Port>
        </Device>
        <TransportProtocol>UDP</TransportProtocol>
      </Source>
      <Destination>
        <Device>
          <IPAddress>192.168.1.3</IPAddress>
          <Port>53</Port>
        </Device>
        <Application>Outlook</Application>
        <TransportProtocol>TCP/IP</TransportProtocol>
        <ApplicationProtocol>IMAP</ApplicationProtocol>
        <Port>80</Port>
      </Destination>
    </Close>
  </Network>
</EventDetail>
``` 
The Send and Receive sub-elements should be used judiciously as there is some cross-over with the Send and Receive top level schema actions described below.  Network/Send and Network/Receive should be limited to low level network events only and not higher level abstractions such as a data transfer service.
