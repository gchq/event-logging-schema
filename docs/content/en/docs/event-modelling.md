---
title: "Event Modelling"
linkTitle: "Event Modelling"
weight: 20
date: 2022-05-04
tags: 
description: >
  In order for the benefits of event normalisation to be realised, it is necessary for similar events to be represented consistently within the schema.

---

The following table provides a number of examples of how some common event types can be modelled.

|Event Description|Schema Action within `<EventDetail>` and Additional Elements | Notes|
|---|---|---|
|User fails to log onto a workstation due to incorrect password.|`<Authenticate>` with `<Outcome>` subelements `<Success>` set to false and `<Reason>` set to an appropriate value, e.g. `IncorrectUsernameOrPassword`.||
|Network file server reports that a user has authenticated by presenting kerberos token.|`<Authenticate>`||
|Network file server checks that the authenticated user has permission to access a specific folder|This is considered internal system behaviour; nothing has changed as a result of the action. Therefore this is not modelled within the schema.|If there is a requirement to record these events, then `<Unknown>` should be used.|
|Network file server reports that a user has failed to access a file within a specific folder due to insufficient privilege.|`<View>` of type `<File>` with `<Permitted>` element of `<Outcome>` set to false, and `<Description>` element assigned to a suitable string literal.||
|User writes an email and preses the send button.|`<Send>` of type `<Email>`.||
|Device control agent permits user to read a file from USB drive.|`<Import>` of type `<File>`.| An additional event might be created in order to describe the operation requested by the user/enacted by the OS, e.g. `<Copy>`|
|Device control agent permits user to write a file to USB drive.|`<Export>` of type `<File>`.| An additional event might be created in order to describe the operation requested by the user/enacted by the OS, e.g. `<Copy>`|
|Firewall prevents a device talking to network by dropping ICMP Ping request.|`<Network>` with sub-element `<Deny>`.|It is presumed that this is a routine and fully automatic activity.  If this was not the case then `<Alert>` might be more appropriate.|
|Host based, software firewall allows a process to listen on a port.|`<Network>` with sub-element `<Listen>`||
|User enters a building using an access badge.|`<Authenticate>`|*There will be a new schema action for physical access / physical presence events in a future version of the schema*|
|User attempts to badge into a building but is denied as they are not a member of the necessary group.|`<Authenticate>`|*There will be a new schema action for physical access / physical presence events in a future version of the schema*|
|User saves a draft email.|`<Create>` of type `<Email>`||
|User clicks on "Export results to XLS" within an application.|`<Export>` of type `<File>`|This is an `<Export>` as it relates to information being transferred out of a controlled area (the application).|
|User clicks on "Print to PDF" within an application.|`<Export>` of type `<File>`|N.B. not `<Print>`, as it doesn't relate to creating hard-copy, but it is an `<Export>` as it relates to information being transferred out of a controlled area (the application).|
|Web proxy allows an authenticated user access to a website of known category.|`<View>` of type `<Resource>` that has `<Category>` element assigned to that reported by the web proxy|Events are modelled from the point of view of the user, where possible.|
|Someone plugs a USB drive into a workstation while a user session is logged in.|`<Install>` of type `<Media>`.  `<User>` is the user currently logged into the workstation.||
|Someone unplugs a USB drive into a workstation while a user session is not logged in.|`<Uninstall>` of type `<Media>`.  There is no  `<User>`  element.||
|Antivirus product on a workstation with a logged in user detects malware on a file.|`<AntiMalware>` with sub-element `<Quarantine>` |*This will be modelled as `<Alert>` in a future version of schema.*|
|An IPS appliance blocks an IP address that may be attempting an SQL injection attack.|`<Alert>` with sub-element `<IDS>`.  Attacker IP is recorded in `<Source>` and target in `<Destination>`.| (IPS - Intrusion Prevention System, IDS - Intrusion Detection System)|
|A database reports that a processing account logs on and performs a backup.|`<Authenticate>` and then a second event, ideally `<Process>` if the backup is contained within the database and control maintained, otherwise `<Export>`.| It might be unclear that this is a backup operation, e.g. perhaps a query is performed.  In this case `<Criteria>` could be used as the `<Source>` of the `<Export>`.|
|User sends a document to the print queue for immediate printing.|`<Print>` with `<Action>` set to `CreateJob`|Q: Why not `StartPrint`? A: Although user intends this to be printed immediately, it might actually be delayed or never actually be printed, e.g. due to an error.|
|Print server or network printer reports that it has started printing a document.|`<Print>` with `<Action>` set to `StartPrint`.||
|Administrator creates a new user.|`<Create>` of type `<User>`||
|Administrator creates a new role for power-users.|`<Create>` of type `<Group>`||
|Administrator assigns a user to the power-users role.|`<Authorise>` of type `<User>` with `<AddGroups>` element assigned appropriately.||
|A user downloads an executable program from a website (reported by web proxy).|`<View>` of type `<Resource>` with `<MimeType>` assigned appropriately.|It is not possible for the web proxy to accurately infer what a client did with the accessed resource.  Of course, analytics are likely to use the MIME type information in conjunction with other events to identify potential issues. |
|User defines a new product line within the stock control system.|`<Create>` of type `<Object>` with `<Type>` element assigned to an appropriate string literal, e.g. "Product Line"||
|User opens a file from a network share using MSWord.exe.| `<View>` of type `<File>`|*A future update to the schema will allow `<EventSource>` to record details of the opening application.  Currently, this must be recorded within `<Data>` elements of `<EventSource>`*|
|User deletes a file from a network share using the UNIX command line.|`<Delete>` of type `<File>`||
|DHCP service leases an IP address to a device with a certain MAC address.|`<NetworK>` of type `<Bind>`.  Subelements of `<Bind>` define the MAC address of the device within `<Source>` and the assigned IP address within `<Destination>`||
|User initiates an antivirus scan.|`<AntiMalware>` of type `<StartScan>`. | *This will be modelled as `<Process>` in a future version of the schema.*|
|RADIUS server allows an 802.1x authenticated device onto the network.|`<Authenticate>` with the `<Client>` subelement of `<EventSource>` assigned to the authenticated device.||
|User searches for documents containing a particular term using enterprise search tool.| `<Search>` with the term specified within `<Query>` subelement, one of `<Simple>`, `<Advanced>` or `<Raw>`||
|User creates a contact in a messaging application.|`<Create>` of type `<Object>`  with `<Type>` element assigned to an appropriate string literal, e.g. "Contact"||
|User creates a status message in a social media application.|`<Create>` of type `<GroupChat>`  with `<Type>` element assigned to an appropriate string literal, e.g. "Status"|This assumes that other users can comment of the status message and it is therefore a converational medium, and illustrates how analogy can be used to find the most appropriate way to model events.|
|User likes another user's status message in social media application.|`<Create>` of type `<Resource>`  with `<Type>` element assigned to an appropriate string literal, e.g. "Like".  `<URL>` can be used to indicate the actual status message and `<User>` subelement of `<Permissions>` can reflect the user who created the status update message.| *A future version of the schema will allow all Object types to refer to other instances of Object type.  In this way the actual Status message can be included within this event, rather than only indirectly by URL.*|
|User deletes a record via the web interface to a database application.|`<Delete>` of type `<Object>` with `<Type>` element assigned to a string literal that describes the type of record deleted.|Where a generic database tool has been used, then `<Type>` can be assigned to a generic value such as `Record` and `<Description>` assigned to a value that indicates the name of the table (i.e. a rough idea of the type of record).  N.B. `<Name>` should be used only to record information relating to the specific instance, if neeeded (e.g. record number).|
|Email server receives an email from an external party.| `<Receive>` of type `<Email>` | | |
