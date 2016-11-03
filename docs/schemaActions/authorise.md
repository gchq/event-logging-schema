# Authorise
This schema action describes the event actions relating to the management of group membership or permissions of entities such as documents or users.

Providing a user with access to a group is described as adding groups to a user as shown in the following example.

``` xml
<EventDetail>
  <TypeId>AddGroup</TypeId>
  <Description>Assigning an LDAP group to a user.</Description>
  <Authorise>
    <User>
      <Id>CN=Some Person (sperson), OU=people, O=Some Org, C=GB</Id>
    </User>
    <AddGroups>
      <Group>
        <Type>ACG</Type>
        <Id>ABC</Id>
      </Group>
    </AddGroups>
  </Authorise>
</EventDetail>
``` 

Removing access to a group is done in the same way but uses the `<RemoveGroups>` element.

This structure can also be used to model situations where a user is making an explicit request for access to a particular entity.
