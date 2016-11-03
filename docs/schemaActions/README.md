# Schema Actions
Schema action elements are the element in `<EventDetail>` that describes the action specific detail of the auditable event, e.g. Import, Copy, Authenticate, etc.

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

