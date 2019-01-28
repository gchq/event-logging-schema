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

There are multiple session types related to authentication events that describe local logon, remote logon etc. The logon type may be included in the `<LogonType>` element. Possible values for `<LogonType>` are defined in the `AuthenticateLogonTypeSimpleType` and are explained below:

* **Interactive** - A logon to a domain by a user.

* **Network** - A logon to a computer via a network, e.g. accessing a shared folder.

* **Batch** - A background scheduled task running as the user.

* **Service** - A background service running as a user.

* **Unlock** - A user un-locking a password protected screen lock or screen saver.

* **NetworkCleartext** - A network logon similar to **Network** except that the password is sent of the network in plain text.

* **NewCredentials** - When a program is run with `RunAs` and using `/netonly` any connections to other computers on the network will be as the `RunAs` user so will result in a **NewCredentials** logon to that computer.

* **RemoteInteractive** - A user logging on interactively to a computer over the network using Remote Desktop Protocol (RDP) or similar.

* **CachedInteractive** - When the user's computer is disconnected from the network (and domain controller) a cache of their hashed credentials is used to authenticate them.

* **CachedRemoteInteractive** - When cached hashed credentials are used to logon to a computer over the network using RDP or similar.

* **CachedUnlock** - When cached hashed credentials are used to unlock a password protected screen lock or screen saver. 

* **Proxy** - A proxy type logon.

* **Other** - When some other logon type is used. Use the `<Data>` element to provide more detail.

  ```xml
  <Data Name="OtherLogonType" Value="Whatever"/>
  ```

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
