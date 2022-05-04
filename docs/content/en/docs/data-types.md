---
title: "Data Types"
linkTitle: "Data Types"
#weight:
date: 2022-05-04
tags: 
description: >
  Various data types used in the schema.

---

## Date and Time Fields

All date and time fields (e.g. Event time, see section 3.1) must be supplied in ISO-8601 format in the UTC time zone with millisecond precision, e.g. `2008-11-18T09:47:12.261Z`.

Date time fields must be zero filled where data does not provide millisecond precision, e.g. `2008-11-18T00:00:00.000Z`.

The ISO-8601 date time format is `yyyy-MM-ddThh:mm:ss.sssZ` where:

| Char   | Description                                       |
| ---    | ---                                               |
| `yyyy` | 4 digit year                                      |
| `MM`   | 2 digit month                                     |
| `dd`   | 2 digit day                                       |
| `T`    | indicates that the rest of the string is a time   |
| `hh`   | 2 digit hour                                      |
| `mm`   | 2 digit minute                                    |
| `ss`   | 2 digit second                                    |
| `sss`  | 3 digit millisecond                               |
| `:`    | literal spearator                                 |
| `.`    | literal spearator                                 |
| `Z`    | literal value denoting the time is Zul time (UTC) |


## IP Addresses

IP Addresses must be specified as four sets of digits separated by full stops (e.g. `255.255.255.0` and `147.80.1.1`) for IPV4 addresses, or eight sets of hexadecimal digits separated by commas for IPV6 addresses (e.g. `2001:cdba:0000:0000:0000:0000:3257:9652`.

The acceptable range for each number is 0 – 255.


## Port Numbers

All port numbers specified must fall within the range 0 – 65535 inclusive.


## Users

Data provided within `<User><Id>` elements is expected to be a PKI DN.
