# Complete Examples

A number of examples of complete events are provided below, in order to illustrate use of the schema.

**N.B.** All entities including organisations, persons, devices and applications are imaginary. All details (e.g. MAC Address, Name, IP Address) are similarly imaginary and any relationship to any real entities is entirely coincidental!

In some places the `<UserDetails>` element has been abbreviated for clarity using an elipsis (...). Typically the system generating the event will not know anything about a user beyond some identifier so a common practice is to decorate the event with a fully populated `<UserDetails>` element in the system processing the events. This approach means only one system needs to be able to resolve a user identifier into a rich set of data.

In a real-world situation, a design decision would be needed - when to leave the event more concise using only an ID and when to join with additional information. Data such as user details change slowly, but they _do change_, e.g. as people get promoted, change role, etc. Therefore, such a joining process would utilise the values for these additional data fields that were correct at the time the event was generated. Certain tools such as [Stroom](https://github.com/gchq/stroom-docs/blob/master/README.md "Stroom on Github") support this, and are sufficiently flexible to allow this enrichment process to take place during initial normalisation prior to storage, during analysis or at query time / inspection.

The same principle applies to Device details, etc.

Compression such as ZIP can very significantly reduce the overhead associated with highly repetitive data, and are able to allow fully enriched data to be persisted with only minimal overhead.

Event Logging schema does not suggest any level of detail, and so to be more illustrative these examples vary in the level of detail provided, e.g. within `<UserDetails>`.

Organisations may wish to standardise, to maximise consistency and simplify analytic development. However, in most real-world situations, the level of detail is likely to vary due to gaps in the data available to the organisation.  For example, an event that describes the sending of an email from a company employee to an external email address may be expected to provide significantly more detail about the sender than the recipient within `<UserDetails>`.

| Schema Action within `<EventDetail>` |
|:------------------------------------:|
| [Import](import.md)                  |
| [Print](print.md)                    |
| [Search](search.md)                  |
| [Send](send.md)                      |

## XML Document
A version of the examples as a complete XML document is available [here](./completeExamples.xml).
