---
title: "Data Types"
linkTitle: "Data Types"
weight: 70
date: 2022-05-04
tags: 
description: >
  Various common data types used in the schema.

---

## Date and Time Fields

All date and time fields (e.g. Event time, see section 3.1) must be supplied in ISO-8601 format in the {{< glossary "UTC" >}} time zone with millisecond precision, e.g. `2008-11-18T09:47:12.261Z`.
This ensures all events are in the same time zone and can be correctly ordered irrespective of the local time.
The time source should be synchronised with a reliable {{< glossary "NTP" >}} server.

Date time fields must be zero filled where data does not provide millisecond precision, e.g. `2008-11-18T00:00:00.000Z`.

The ISO-8601 date time format is `yyyy-MM-ddThh:mm:ss.sssZ` where:

| Char   | Description                                        |
| ---    | ---                                                |
| `yyyy` | 4 digit year                                       |
| `MM`   | 2 digit month                                      |
| `dd`   | 2 digit day                                        |
| `T`    | indicates that the rest of the string is a time    |
| `hh`   | 2 digit hour                                       |
| `mm`   | 2 digit minute                                     |
| `ss`   | 2 digit second                                     |
| `sss`  | 3 digit millisecond                                |
| `:`    | literal separator                                  |
| `.`    | literal separator                                  |
| `Z`    | literal value denoting the time is Zulu time (UTC) |


## IP Addresses

IP Addresses can be provided in IPv4 or IPv6 forms.

* **IPv4** - Must be specified as four sets of digits separated by full stops (e.g. `255.255.255.0` and `147.80.1.1`).
* **IPv6** - Must be specified as eight sets of hexadecimal digits separated by commas (e.g. `2001:cdba:0000:0000:0000:0000:3257:9652`.
    Though it is permitted for IPv6 addresses to be surrounded by square brackets when used in a URL, these brackets must be omitted when an IPv6 address is used in isolation.

The acceptable range for each number is 0 – 255.


## Port Numbers

All port numbers specified must fall within the range 0 – 65535 inclusive.


## Users

Data provided within `<User><Id>` elements is expected to be a PKI DN.
