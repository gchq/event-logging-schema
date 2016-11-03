# Update
The `<Update>` event is different to the previous `<Create>`, `<View>` and `<Delete>` events because it is often desirable to know the before and after state of the data in question if this can be provided. Another reason for providing the before and after states of an object is that it allows for changes to individual properties to be described without the need to include all attributes of the object in question on every update.
 
An example showing updating configuration properties is shown in the following example.

``` xml
<EventDetail>
  <TypeId>UpdateConfiguration</TypeId>
  <Update>
    <Before>
      <Object>
        <Type>Configuration</Type>
        <Data Name="FileAccess" Value="none"/>
      </Object>
    </Before>
    <After>
      <Object>
        <Type>Configuration</Type>
        <Data Name="FileAccess" Value="all"/>
      </Object>
    </After>
  </Update>
</EventDetail>
``` 
