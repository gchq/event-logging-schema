# Introduction
This documentation describes the Event Logging XML Schema herein referred to as the 'schema'. This is a guide for anybody interested in using the schema for recording auditable events and includes details of the structure, features and common usage.

The schema aims to provide a structure for describing all forms of auditable events in a structured and consistent way.  The benefit of the schema is that events translated into it are all in a consistent normalised form enabling common processing of the translated events.

## Describing an event
Each action performed by a user, system or application component during the course of operation produces one or more events. Some of these events need to be recorded for the purposes of audit.

In order for event data to be useful each event must contain the following information:

* **When did it happen?** – What was the time when the event occurred?
* **Who is responsible?** – Who was the user and/or device that caused the event to occur?
* **Where did it happen?** – Where was the user and/or device located when the event happened?
* **What happened?** – What data was created, updated, deleted etc?

### When did it happen?
It is important for analysts to be able to understand the order in which events occurred and what events are related. To do this it is vitally important that all events are recorded with an accurate time, ideally down to millisecond precision.

A single task performed by an end user may result in many events being raised by several devices. Because each device records events separately, clocks must be synchronised between devices so that it is possible for the analysts to determine the order in which events occurred across devices.

Some non-interactive events are attributable to a user but may run as scheduled jobs or as asynchronous tasks (see [Run As](basicStructure/README.md#run-as)). As a result the time of the event will not be the same as when the user interacted with the system.  Where this is the case analysts may not be able to determine the exact order of events.

In some cases devices being used by an end user may be located in different time zones, e.g. a user may be interacting with a client machine in one country while the server is in a different country. For this reason all times must be recorded in UTC (a.k.a. Zulu Time denoted Z). The DeviceComplexType structure provides a means for recording the time zone of the devices involved in the event.

For consistency times should be zero filled where the source devices do not record times to the nearest millisecond, e.g. if a time is only recorded to the nearest minute then the end of the time string would be 00.000Z.

### Who or what caused it?
When recording any events it is important for analysts to know the source of the event.

Most events will be raised as a result of user interaction with a device, however some occur as a result of a device process (e.g. OS start-up) or scheduled task (e.g. cron job) and therefore have no attributable user or are attributable to a processing user account.

In many cases an event may be the result of a user performing an action on a server via a client terminal.

Where an event has been raised in this way the event log should record information about the user, client terminal and device that recorded the event.

In most cases the server will identify the user via PKI or username & password authentication. The server will also be aware of the IP Address of the client computer that they have connected from.

Where an event is raised as a result of direct user interaction the user must always be recorded.

In some instances a user may be performing an action using elevated privileges or through another user account, e.g. when using sudo in Linux.  In these instances it is important to capture both the user that initiated the event and the account that they performed the event as (the [Run As](basicStructure/README.md#run-as) user).

### Where did it happen?
Analysis of event data often relies on knowledge about the geographic location of the user and associated devices used when the event log is generated. Because of this the schema allows for the location to be provided for each device including the client terminal in use when the event was raised.

In addition to location information other attributes of the devices involved in the generation of an event can be added including the IP address, FQDN, MAC address and Port.

### What happened? 
There are many different types of event that are recorded for analysis. These include user logon, log off, search, delete, view etc.

Each type of event needs to include specific data that is relevant to the event, e.g. logon would need to contain details of the user who logged on.

The schema includes a number of 'schema actions' that encompass most auditable events, e.g. Create, Update, Export, Send, etc.  Where an event has to be recorded that doesn’t fit into one of these predefined categories then the Unknown 'schema action' can be used, but this should be a last resort as its content is unstructured and reduces the normalisation benefits.

### Putting it all together
It can be hard to imagine what complete events look like, so a number of [illustrative examples](completeExamples/completeExamples.md)
are available.