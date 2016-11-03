# Copy and Move
The `<Copy>` and `<Move>` elements are used to describe local copy and move operations. They both contain the same element structure and describe the source and destination for the copy/move event.

When an entity is being sent to a remote recipient then the `<Send>` and `<Receive>` actions should be used instead (see [Send and Receive](./sendReceive.md)).

The following example shows a copy event that has failed due to a lack of necessary permissions.

``` xml
<EventDetail>
  <TypeId>CopyDocument</TypeId>
  <Copy>
    <Source>
      <Document>
        <Title>Some document.</Title>
        <Path>/PathSrc/some document.txt</Path>
      </Document>
    </Source>
    <Destination>
      <Document>
        <Title>Some document.</Title>
        <Path>/PathDest/some document.txt</Path>
      </Document>
    </Destination>
    <Outcome>
      <Permitted>false</Permitted>
      <Description>User does not have write permissions to /PathDest</Description>
    </Outcome>
  </Copy>
</EventDetail>
``` 

As with all data related events, other types of data can be described for the copy or move events, e.g. files, configuration data and messages such as email.
