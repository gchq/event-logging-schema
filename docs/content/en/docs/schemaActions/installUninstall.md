# Install and Uninstall
This action can be used for recording the installation or removal of software. Also, some systems are able to monitor for addition or removal of hardware/media. Where this is possible install/uninstall events can be used for this purpose.

The following example shows a typical event that could be recorded when hardware addition is detected. Note that the addition of hardware in this instance is not permitted due to restrictions applied to the device in question.

``` xml
<EventDetail>
  <TypeId>InstallHardware</TypeId>
  <Install>
    <Hardware>
      <Type>USBMassStorage</Type>
      <Manufacturer>Some Manufacturer</Manufacturer>
      <Capacity>8589934592</Capacity>
    </Hardware>
    <Outcome>
      <Permitted>false</Permitted>
      <Description>No hardware is allowed to be added.</Description>
    </Outcome>
  </Install>
</EventDetail>
```  
 
Whenever read only or recordable media are inserted into a device this may be recorded for audit purposes. The media type may be described (e.g. DVD, CD) as well as whether or not the media is writeable. The capacity can also be specified in bytes, see the following example.

``` xml
<EventDetail>
  <TypeId>InsertRemovableMedia</TypeId>
  <Install>
    <Media>
      <Type>CD</Type>
      <ReadWrite>true</ReadWrite>
      <Capacity>671088640</Capacity>
    </Media>
  </Install>
</EventDetail>
``` 

The following is an example of an event recording the installation of a piece of software:
``` xml
<EventDetail>
  <TypeId>yumInstall</TypeId>
  <Install>
    <Software>
      <Name>vim</Name>
      <Version>7.4</Version>
    </Software>
  </Install>
</EventDetail>
``` 
