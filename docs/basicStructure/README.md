# Basic Structure
Every XML instance that conforms to the schema must have a root element named `<Events>` and reference the schema, see the following example.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Events xmlns="event-logging:3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="event-logging:3 file://event-logging-v3.0.0.xsd" Version="3.0.0">
    <Event>
    ...
    </Event>
    <Event>
    ...
    </Event>
    ...
</Events>
```

The `<Events>` root element contains zero-to-many `<Event>` elements to describe each event in the set. Events can either be sent individually or in batches.

## Describing an Event
As discussed in the [Introduction](../README.md#describing-an-event) every event must describe when it happened, who was responsible, where it happened and what happened.

The schema is intentionally very permissive with the majority of elements being optional. The reason for this is that not all systems will be able to provide the same breadth of event data and a partially populated event is preferable to no event. Enforcement of mandatory data items can be done outside of the schema.

In the various examples that follow some elements that appear in the example may be optional and are only shown as guidance.

### Describing When?
To describe when an event happened a timestamp must be present on every event, see the following example.

``` xml
<EventTime>
  <TimeCreated>2013-03-11T17:34:14.623Z</TimeCreated>
</EventTime>
```
The date and time must be in the format described in [Data Types](../dataTypes.md#date-and-time-fields).
 
### Describing The Source of the Event?
Each event must describe who or what is responsible for the event occurring.

The `<EventSource>` element describes the event source, e.g. System, Environment, Generator, User, Client, Device, etc. See the following example.

``` xml
<EventSource>
  <System>
    <Name>Some System</Name>
    <Environment>Operational</Environment>
    <Organisation>Some Org</Organisation>
  </System>
  <Generator>Some-Event-Log-Provider</Generator>
  <Device>
    <HostName>someserver.someorg.org</HostName>
    <IPAddress>123.123.123.123</IPAddress>
    <Location>
      <Country>UK</Country>
      <Site>Some Site</Site>
      <TimeZone>UTC</TimeZone>
    </Location>
  </Device>
  <Client>
    <IPAddress>123.123.123.124</IPAddress>
    <Location>
      <Country>UK</Country>
      <Site>Some Site</Site>
      <Room>A1a</Room>
      <Aisle>A1</Aisle>
      <Desk>1</Desk>
      <TimeZone>UTC</TimeZone>
    </Location>
  </Client>
  <User>
    <Id>CN=Some Person (sperson), OU=people, O=Some Org, C=GB</Id>
    <UserDetails>
      <Organisation>Some Org</Organisation>
    </UserDetails>
  </User>
</EventSource>
``` 

#### System

The `<System>` structure provides information about the name of the system that created the event, the deployment environment and the organisation that owns it. It is also possible to provide a description and classification of the system.

#### Generator
The generator is an application, service or specific component that created the event, such as  'Microsoft-Accounting-Service' or 'Apache-HTTPD'. Event type ids (see [Type ID](./README.md#type-id)) are unique to a particular event generator so a generator name must be included.

#### Devices
Devices elements, including the generator, client and server devices, provide a means of recording sufficient details about the devices involved in the generation of the event. The structure allows for the recording of the identity of the device as well as its location physically and on the network.

#### Physical Access (Doors)
Where entry/exit to buildings/rooms is managed by a physical access control system this element can be used to record events at the door. Some form of authentication and authorisation will be required in order for the user to pass through the door, so the outcome of that authorisation can be recorded including failure conditions.

For every door event the door authentication device is the event source. A description of the door may be added to the `<EventSource>` element that is specific to the door authentication device used, see the following example.

``` xml
<EventSource>
  <System>
    <Name>Some System</Name>
    <Environment>Operational</Environment>
    <Organisation>Some Org</Organisation>
  </System>
  <Generator>Door Software</Generator>
  <Door>
    <Name>Main Entrance Door</Name>
    <SingleEntry>true</SingleEntry>
    <OneWay>true</OneWay>
  </Door>
  <User>
    <Id>jbloggs</Id>
  </User>
</EventSource>
``` 

The elements `<SingleEntry>` and `<OneWay>` determine the access control characteristics of the door.

Some doors only allow entry/exit from the side that the authentication device exists on to the other side (one way), and only allow one person to enter/exit at a time (single entry).

Sliding doors or hinged doors potentially allow users to pass in either direction (not one way) and also allow for multiple users to pass through despite only a single authorisation (not single entry).

The identity of the user may be resolved from the pass/access device used to a user id.

The following door events may be recorded:
* **Entry** – User has authenticated and entered through door.
* **Exit** – User has authenticated and exited through door.
* **Reset** – Reset of door entry system to allow passes to be used in either direction. 
* **None** – User has authenticated but not entered or exited.

#### Describing Where?
A device or door typically has some form of physical location.  The location can include details such as the country, site, building, room and desk/rack.  It can also include details of the timezone of that physical location.

#### User
When sharing data within large global organisations it is important that an individual (who may have access to multiple systems across multiple parts of that organisation) can be identified. Individuals may have multiple identities within each part of the organisation due to the scope, purpose and limitations of individual systems. Examples of these many forms of identity range from door access card numbers, email addresses, user names, certificates and user IDs.

Many of these forms of identity may not be unique across all parts of an organisation so it is important to resolve this to an identifier that is unique across the organisation. For example the door systems for each building may be independent and have their own ID scheme. Therefore this local ID should be replaced with a globally unique identifer, keeping the local identifier in a Data element.

In addition to this, user details can be provided to describe which part of the organisation the user works in, e.g. country/group/unit.

Many events may be triggered by a device and not involve a specific user. In these cases it is only necessary to describe the device that the event occurred on.

#### Run As
Some events may be executed by a user assuming the identity of another user, e.g. use of sudo. Where this is the case it is mandatory to describe the user that the event is being executed as using the `<RunAs>` element.

The `<RunAs>` element may also be used when an event is executed by the device on behalf of a user. This can often be the case when executing scheduled tasks, e.g. overnight database clean up jobs.

As with the `<User>` element the `<RunAs>` element must contain a unique user identifier.

#### Interactive
For some events the user may not have been present at the time the event occurred. This can be the case for scheduled tasks or tasks that are executed from a queue, e.g. asynchronous processing. In these cases it is necessary to indicate that the event was not interactive, i.e. not triggered by a user directly. This is done using the `<Interactive>` element.

#### Data
For any event source data that cannot be described using the defined structure, `<Data>` elements can be used, though care should be taken to use these elements in a consistent way to allow for future processing and inspection of events.
 
### Describing What Happened?
Each event will describe what happened within the `<EventDetail>` element. The content of the `<EventDetail>` element will be specific to the type of event that occurred, e.g. a logon event would contain information about the type of logon, see the following example.

``` xml
<EventDetail>
  <TypeId>InteractiveLogon</TypeId>
  <Description>A user has logged on.</Description>
  <Authentication>
    <Action>Logon</Action>
    <LogonType>Interactive</LogonType>
    <User>
      <Id>CN=Some Person (sperson), OU=people, O=Some Org, C=GB</Id>
    </User>
  </Authentication>
</EventDetail>
``` 

#### Description
The `<Description>` element allows for the inclusion of a human readable description of the event type.

#### Type ID
The `<EventDetail>` element includes a mandatory TypeId element. This element identifies the unique event type as known to the generator. For example a generator may distinguish 2 types of logon event just by some id. Use of this element would be the only way to treat the two types differently.  In the case of application logging the Type ID should be unique to a use case within the system, e.g. CreateDocument, DeleteRecord, DocumentSearch, UserSearch, etc.

The form of the Type IDs is specific to the generator and could be strings or numeric codes, as long as they provide a unique identifier for that type of event within that generator.  Where a generator already produces some form of ID for its events, e.g. Microsoft system event codes these can be used directly, however in the absence of predefined code a human readable TypeId is preferable as it is more easily understood by human.

Having sensible Type IDs is of particular benefit when developing processes that depend upon events conforming to the schema as it allows the developer to group events by the Type ID or to have conditional processing based on the ID.

#### Classification
The `<Classification>` element can be used to describe the classification, protective marking or sensitivity of the data in the event.  For example the data may be commercially sensitive or contain sensitive personal data.  The element includes a number of optional elements so can be as simple as just a free text `<Text>` element containing something like `COMMERICAL IN CONFIDENCE` to multiple elements that described the protective marking in complex classification scheme.

The `<Classification>` element appears in a number of places in the schema as it can be used to describe the sensitivity or marking of different entities/objects. For example it can be used to describe the sensitivity of a set of audit events in aggregation, the content of a single audit event or an object that is the subject of an audit event, i.e. a document being viewed by a user.

The following are some examples of populated Classification elements:

``` xml
<Classification>
  <Text>COMMERICAL IN CONFIDENCE</Text>
  <Classification>COMMERICAL IN CONFIDENCE</Classification>
</Classification>
```

``` xml
<Classification>
  <Text>CONFIDENTIAL</Text>
  <Originator>
    <Country>GBR</Country>
    <Organisation>UK Headquarters</Organisation>
  </Originator>
  <Custodian>
    <Country>GBR</Country>
    <Organisation>Group Headquarters</Organisation>
  </Custodian>
  <Classification>CONFIDENTIAL</Classification>
  <Descriptors>
    <Descriptor>PERSONAL</Descriptor>
  </Descriptors>
  <OrGroups>
    <AccessControlGroup>Human Resources</AccessControlGroup>
    <AccessControlGroup>Managers</AccessControlGroup>
    <AccessControlGroup>Auditors</AccessControlGroup>
  </OrGroups>
  <PermittedNationalities>
    <Nationality>GBR</Nationality>
    <Nationality>USA</Nationality>
  </PermittedNationalities>
  <PermittedOrganisations>
    <PermittedOrganisation>UK Headquarters</PermittedOrganisation>
    <PermittedOrganisation>Group Headquarters</PermittedOrganisation>
  </PermittedOrganisations>
  <DisseminationControls>
    <DisseminationControl>ORIGINATOR_CONTROLLED</DisseminationControl>
  </DisseminationControls>
  <Disposition>
    <Date>2020-03-11T00:00:00.000Z</Date>
    <Process>DELETE</Process>
  </Disposition>
</Classification>
```

#### Purpose
Certain auditable events may require users to provide justification for the action they are taking, e.g. viewing a personnel record or processing a high value payment. This element provides the means to record the justification/purpose of the auditable event and possibly any authorisations that were obtained.

#### Schema Action
The action specific detail of the auditble event is recorded in a 'schema action' element, where the structure of each element is tailored to the auditable action.

The schema action element structures are defined in more detail in [Schema Actions](../schemaActions/README.md).

### Event Chain
Where more than one event is related this can be specified using the `<EventChain>` element, see the following example.

``` xml
<EventChain>
  <Activity Id="1234">
    <Parent Id="5678"/>
  </Activity>
</EventChain>
``` 

If a web application session recorded several events, all of the events could output the session id within the `<Activity>` Id attribute. It would then be possible to see what events occurred during that web session.

In some cases an activity may have a parent that could be used to associate events, e.g. the activity id could be the id of the current thread and the parent activity id could be the parent thread id or web session id.

The structure allows for a hierarchy to be constructed if necessary in order to associate activities.

## Schema Versions
It is anticipated that the schema will evole over time to accomodate new types of auditable event and to better describe existing events.  Each version of the schema is marked with a unique version number using the `version` attribute in the `<xs:schema>` element.  All events should be marked with the version of the schema that they are based upon using the `Version` attribute of the `<Events>` element.  This allows systems processing the events to correctly interpret the structure.

### Schema Versioning
The Event Logging Scheme uses the semantic versioning scheme.  The version attribute value takes the form x.y.z where

* **x** = Major version
* **y** = Minor version
* **z** = Patch version

A change to the major version will include 'breaking changes' that are not backwards compatible with previous versions of the schema. 

A change to the minor version will include new structural elements but will be non-breaking and backwards compatible with previous versions of the schame at that major version.

A change to the patch version means very minor non-breaking changes such as additions to enumerated types or annotation changes.

Ideally all new data should conform to the latest version of the schema to reduce the length of time legacy versions have to be supported for.

### Managing Multiple Schema Versions
Systems receiving/processing data conforming to the Event Logging Schema should expect to receive data conforming to multiple historic versions of the schema as it takes time for client systems to update to the latest version.

To resolve the issue of having to process and store multiple versions of the schema, a schema uplift process can be used. This involves having a number of XSLT transformation steps to translate from each iteration of the schema to the next.  For example if v1.2 of the schema has an element called `<Detail>` and this has been renamed ot `<EventDetail>` in v2.0 then the XSLT can rename the elements in the event data to bring the event record up to v2.0. The same uplift operation can be applied to historic events that have already been stored and have become out of date, or they can be uplifted on demand.

Taking this approach will reduce the number of different versions that have to be stored/processed.

