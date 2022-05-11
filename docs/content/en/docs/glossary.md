---
title: "Glossary"
linkTitle: "Glossary"
weight: 100
date: 2022-02-16
tags: 
description: >
  Glossary of common words, terms and abreviations used in Stroom.

---

## ACL

Access Control List.
A list or rules specifying which users, groups or systems are allowed (or deined) access to a particular object or resource.


## API

Application Programming Interface.
A software interface used with two applications or systems communicate.


## CRUD

Create, Read, Update, Delete.
Often used to describe the typical actions a user can perform in a system, e.g. creating a record, deleting a record, etc.


## DN

[LDAP]({{< relref "#ldap" >}}) Distinguished Name.


## Decoration

The process of augmenting an event record with additional information obtained from other systems using key identifiers in the event.
For example resolving an {{< glossary "FQDN" >}} in an asset management database to provide location details.


## FQDN

Fully Qualified Domain Name or Absolute Domain Name.


## GMT

Greenwich Mean Time was the former international time standard, now replaced by {{< glossary "UTC" >}}.


## HTML

Hyper Text Markup Language.


## IM

Instant Message or Online Chat.
A form of real time messaging between users or groups of users.


## IP

Internet Protocol.
The network protocol that backs the Internet.


## ISO-8601

The international standard for representing dates and times.
ISO-8601 has a number of valid formats however the Schema uses only the full date and time format in {{< glossary "UTC" >}} with fractional seconds, i.e.:

`yyyy-MM-ddThh:mm:ss.mmmZ`

See [Date and Time Fields]({{< relref "data-types#date-and-time-fields" >}}) for more detail on the format used in the Schema.

For more general information on ISO-8601 see {{< external-link "Wikipedia" "https://en.wikipedia.org/wiki/ISO_8601" >}}.


## LDAP

Lightweight Directory Access Protocol.
An open industry standard for accessing and maintaining distributed directory information services over a network.


## MAC

Machine Access Control.
The MAC address is a unique identifier assigned to a network interface controller.
It allows a network attached device to be uniquely identified by the network adapter/controller that connects it to the network.


## NTP

Network Time protocol.
A protocol for clock synchronisation between computer systems.
Typically a computer will synchronise its clock using a trusted NTP server.


## Object Type

Within an event, this is the type of the object/entity that is the subject of the event.
Examples of Object Type are [`<File>`]({{< relref "/docs/object-types/file" >}}), [`<User>`]({{< relref "/docs/object-types/user" >}}), etc.

See [Object Types]({{< relref "/docs/object-types" >}})


## PKI

Public Key Infrastructure.
A set of roles, policies, software and hardware used to use and manage digital certificates and public key encryption.
Public key infrastructure will typically involve public keys, private keys and trusted certificate authorities.


## Schema Action

This is essentially the verb or action of the event, e.g. [`<Create>`]({{< relref "/docs/schema-actions/create-view-delete" >}}), [`<Search>`]({{< relref "/docs/schema-actions/search" >}}), etc.
Every event must have a schema action that describes what the user is doing.

See [Schema Actions]({{< relref "/docs/schema-actions" >}})


## UTC

Coordinated Universal Time.
The international standard for regulating clocks with a time-zone offset of 0.
The successor to {{< glossary "GMT" >}}
Sometimes referred to as _Zulu_ time.

For more detail on UTC see {{< external-link "UTC" "https://en.wikipedia.org/wiki/Coordinated_Universal_Time" >}}.


## VOIP

Voice Over Internet Protocol or {{< glossary "IP" >}} Telephony.
The method of delivering voice communications over the internet or a local intranet rather than a traditional Public Switched Telephone Network.
