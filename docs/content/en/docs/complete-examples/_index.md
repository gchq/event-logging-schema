---
title: "Complete Examples"
linkTitle: "Complete Examples"
weight: 90
date: 2022-05-04
tags: 
description: >
  A number of examples of complete events are provided below, in order to illustrate use of the schema.

---

{{% note %}}
All entities including organisations, persons, devices and applications are imaginary.
All details (e.g. MAC Address, Name, IP Address) are similarly imaginary and any relationship to any real entities is entirely coincidental!
{{% /note %}}

In some places the `<UserDetails>` element has been abbreviated for clarity using an ellipsis (...).
Typically the system generating the event will not know anything about a user beyond some identifier so a common practice is to decorate the event with a fully populated `<UserDetails>` element in the system processing the events.
This approach means only one system needs to be able to resolve a user identifier into a rich set of data.

In a real-world situation, a design decision would be needed - when to leave the event more concise using only an ID and when to join with additional information.
Data such as user details change slowly, but they _do change_, e.g. as people get promoted, change role, etc.
Therefore, such a joining process would utilise the values for these additional data fields that were correct at the time the event was generated.
Certain tools such as {{< external-link "Stroom" "https://github.com/gchq/stroom-docs/blob/master/README.md "Stroom on Github"" >}} support this, and are sufficiently flexible to allow this enrichment process to take place during initial normalisation prior to storage, during analysis or at query time / inspection.

The same principle applies to Device details, etc.

Compression such as ZIP can very significantly reduce the overhead associated with highly repetitive data, and are able to allow fully enriched data to be persisted with only minimal overhead.

Event Logging schema does not suggest any level of detail, and so to be more illustrative these examples vary in the level of detail provided, e.g. within `<UserDetails>`.

Organisations may wish to standardise, to maximise consistency and simplify analytic development.
However, in most real-world situations, the level of detail is likely to vary due to gaps in the data available to the organisation. 
For example, an event that describes the sending of an email from a company employee to an external email address may be expected to provide significantly more detail about the sender than the recipient within `<UserDetails>`.

## Example files

The following are links to annotated example `<Events>` XML documents that are valid against the Event Logging Schema.

* **Schema Actions** (within `<EventDetail>`)
  * **`<Alert>`**
    * [`<Alert>/<Network>` XML]({{< relref "xml/schema-actions/alert-network.xml.md" >}})
  * **`<Import>`**
    * [`<Import>/<File>` XML]({{< relref "xml/schema-actions/import-file.xml.md" >}})
    * [`<Import>/<Object>` XML]({{< relref "xml/schema-actions/import-object.xml.md" >}})
  * **`<Network>`**
    * [`<Network>/<Close>` XML]({{< relref "xml/schema-actions/network-close.xml.md" >}})
  * **`<Print>`**
    * [`<Print>` XML]({{< relref "xml/schema-actions/print.xml.md" >}})
  * **`<Search>`**
    * [`<Search>` XML]({{< relref "xml/schema-actions/search.xml.md" >}})
  * **`<Send>`**
    * [`<Send>/<Document>` XML]({{< relref "xml/schema-actions/send-document.xml.md" >}})
    * [`<Send>/<File>` XML]({{< relref "xml/schema-actions/send-file.xml.md" >}})
* **Object Types**
  * [`<SearchResults> XML`]({{< relref "xml/object-types/search-results.xml.md" >}})
  * [`<Criteria> XML`]({{< relref "xml/object-types/criteria.xml.md" >}})
* **Other**
  * **`<Event>/<Meta>`**
    * [`<Event>/<Meta>` (JSON) XML]({{< relref "xml/event-meta-json.xml.md" >}})
    * [`<Event>/<Meta>` (XML) XML]({{< relref "xml/event-meta-xml.xml.md" >}})
