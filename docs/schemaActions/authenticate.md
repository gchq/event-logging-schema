# Authenticate
This schema action describe authentication events such as operating system logon or logon to an application or website.  It can also be used to describe the authentication that happens when a user passes through an access controlled door.

## Operating System and Application Authentication
Operating systems and applications that require user authentication before they can be accessed should record the following actions:

* **Logon** – Logon to OS or application requiring authentication.
* **Logoff** – Logoff of OS or application.
* **Lock** – Lock/suspend an OS or application session.
* **Unlock** – Unlock/resume an OS or application session.

Details of the device in question shall be contained within the `<EventSource>` element. The above actions can be recorded in the `<Authentication><Action>` element, see the following example.

``` xml
<EventDetail>
  <TypeId>InteractiveLogon</TypeId>
  <Description>A user has logged on.</Description>
  <Authentication>
    <Action>Logon</Action>
    <LogonType>Interactive</LogonType>
    <User>
      <Id>CN=Some Person (sperson), OU=people, O=Some Org, C=GB</Id>
    </User>
  </Authentication>
</EventDetail>
``` 

There are multiple session types related to authentication events that describe local logon, remote logon etc. The logon type may be included in the `<LogonType>` element.
 
If an authentication action fails the optional `<Outcome>` element can be included in the description with a Success attribute set to false. The `<Description>` and `<Reason>` elements within `<Outcome>` may also be included to indicate why the action failed, see the following example.

``` xml
<EventDetail>
  <TypeId>InteractiveLogon</TypeId>
  <Description>A user has logged on.</Description>
      <Authentication>
        <Action>Logon</Action>
        <LogonType>Interactive</LogonType>
        <User>
          <Id>CN=Some Person (sperson), OU=people, O=Some Org, C=GB</Id>
        </User>
        <Outcome>
          <Success>false</Success>
          <Description>Logon failure. Incorrect password.</Description>
          <Reason>IncorrectPassword</Reason>
        </Outcome>
      </Authentication>
    </EventDetail>
``` 

In addition to logon and logoff, there are additional authentication related events supported by the schema. These include the following:

* **ChangePassword** – An account password is changed.
* **AccountLock** – The action of locking an account, either by an administrator or as a result of multiple logon failures.
* **AccountUnlock** – The action of unlocking an account by an administrator.

## Physical Access
This information is described as an authentication event using the schema, as shown in the following example.

``` xml
<EventDetail>
  <TypeId>DoorEntry</TypeId>
  <Description>A person has entered the building.</Description>
  <Authentication>
    <Action>Logon</Action>
    <User>
      <Id>CN=Some Person (sperson), OU=people, O=Some Org, C=GB</Id>
    </User>
  </Authentication>
</EventDetail>
``` 
