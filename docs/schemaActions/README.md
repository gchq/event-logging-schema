# Schema Actions
Schema action elements are the element in `<EventDetail>` that describes the action specific detail of the auditable event, e.g. Import, Copy, Authenticate, etc.

System events could be modelled from various points of view, and although it makes little difference in many cases, it is certainly necessary that a decision is made and then applied consistently.
 
The schema is designed to express events in terms of the *intent of a user* wherever possible. Where there is no user, then the schema expresses events from the point of view of the device/generator creating the event.

For example, a log file from a web proxy may contain events that describe events from its point of view. Perhaps that it has not relayed a web page to an authenticated session ID due to a rule violation. In such a case, the correct way to represent the event is as a known user attempting to access a web page and being denied. Which in XML would be as a `<View>` with `<Outcome>` elements set appropriately.

## Common Structural Elements
While each schema action has a structure that is specific to the auditable event action, there are a number of structural elements that are common to multiple schema actions.

### Multi Object Complex Type
The Multi Object Complex Type is a structure for describing an entity or entities involved in the auditable event, for example a document, file, resource or user. The structure is described in more detail in [Object Types](../objectTypes/README.md).

The `<Unknown>` schema action structure should be the action of last resort, where the auditable action cannot be appropriately described by any of the other schema actions. Heavy use of this structure suggests that the scheme is deficient with respect to a type or types of event and suggestions for how the schema could be improved should be submitted to the developers.

### Outcome
The majority of schema action structures include an `<Outcome>` element. This is used to described whether the action was successful/permitted or not, e.g. a failed logon or a user being denied access to a file.

`<Success>` is used to describe whether something failed or went wrong in some way, e.g. an export to removable media that failed due to lack of space. This element is used in cases of unexpected failure rather than where something has been prevented due to some rule or process, where `<Permitted>` would be more appropriate.

`<Permitted>` is used to describe whether an action was prevented due to some form of authorisation or rule, e.g. a user not being permitted access to a document due to having insufficient permissions.

In both cases a value of true is assumed so the element only needs to be included if the action was unsuccessful.

The `<Description>` element should be used to described the detail around the failure of the action as this will be useful to a human looking at the event.

### Data
This provides a flexible structure for describing aspects of the schema action that cannot be described using the structure in the schema. This structure is described in more detail in [Unstructured Data](../unstructuredData.md)

The majority of the 'schema action' elements include the MultiObjectComplexType structure (or some child of it) to allow for recording various forms of objects that were involved in the event.

## Defined Schema Actions
A number of schema actions are defined that aim to be broadly representative of the kinds of activity that are most likely to be of interest from an audit point of view.

| Schema Action                    | Rough Description                                                                                           |
|----------------------------------|-------------------------------------------------------------------------------------------------------------|
| [Alert](alert.md)                | A potentially concerning situation has been identified *that requires user attention to resolve*.           |
| [AntiMalware](antiMalware.md)    | Events relating to functioning of antimalware softwrae *Will be removed in a future version of the schema*. |
| [Authorise](authorise.md)        | Events relating to authorisation changes, e.g. to group membership within LDAP or AD.                       |
| [Copy](copyMove.md)              | Making copies of data.                                                                                      |
| [Create](createViewDelete.md)    | Creating new items of data.                                                                                 |
| [Delete](createViewDelete.md)    | Destroying data.                                                                                            |
| [Export](importExport.md)        | Moving data out of a controlled area (e.g. database, application or network).                               |
| [Import](importExport.md)        | Moving data into a controlled area (e.g. database, application or network).                                 |
| [Install](installUninstall.md)   | Installing hardware, software or removable media.                                                           |
| [Move](copyMove.md)              | Moving data.                                                                                                |
| [Network](network.md)            | Events that relate to networking between computers.                                                         |
| [Print](printing.md)             | Events relating to printing (i.e. making hard-copies).                                                      |
| [Process](process.md)            | Generic processing events, including starting processes and services on computers.                          |
| [Receive](sendReceive.md)        | Obtaining data over the network.                                                                            |
| [Search](search.md)              | Search operations, e.g. querying a database.                                                                |
| [Send](sendReceive.md)           | Transmitting data over the network.                                                                         |
| [Uninstall](installUninstall.md) | Events relating to removal of hardware, software or removable media.                                        |
| [Unknown](unknown.md)            | A type of event that is dissimilar to any in the schema.                                                    |
| [Update](update.md)              | Modification to data.                                                                                       |
| [View](createViewDelete.md)      | All events relating to accessing data.                                                                      |

## Event Modelling
The schema does not aim to provide an exhaustive list of all possible computer operations.

This is because:

1. It would result in a schema that would be extremely large
1. It would result in a schema that would be constantly changing
1. It would result in a schema that would be very difficult to use
 
Instead, the schema should be used in such a way to describe the operation using the elements of the schema that most closely capture the nature of the kind of operation that was taking place *from the user's point of view*, or where there is no user *from the reporting device's point of view*. 

Judgement is needed to find the best way to model a particular event.

For example, a delete operation could actually just mark a record within the database in such a way as to prevent it from being displayed and so is effectively (at least internally) an update. But from the user's point of view it is a destructive operation - so it should be modelled as a delete.
 
It is important that events with similar effects are modelled similarly, in order that analytics can operate effectively. Examples that illustrate how the schema should be used are described [here](../eventModelling.md). 
