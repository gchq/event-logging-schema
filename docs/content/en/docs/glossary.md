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


## CRUD

Create, Read, Update, Delete.
Often used to describe the typical actions a user can perform in a system, e.g. creating a record, deleting a record, etc.


## DN

[LDAP]({{< relref "#ldap" >}}) Distinguished Name.


## FQDN

Fully Qualified Domain Name or Absolute Domain Name.


## GMT

Greenwich Mean Time was the former international time standard, now replaced by [UTC]({{< relref "#utc" >}}).


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
Examples of ISO-8601 format dates and times are 

* `2022-05-09T09:43:35+00:00`
* `2022-05-09T09:43:35Z`
* `20220509T094335Z`


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
The international standard for regulating clocks.
The successor to [GMT]({{< relref "#gmt" >}}).


## VOIP

Voice Over Internet Protocol or {{< glossary "IP" >}} Telephony.
The method of delivering voice communications over the internet or a local intranet rather than a traditional Public Switched Telephone Network.
