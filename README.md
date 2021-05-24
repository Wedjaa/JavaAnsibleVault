# Java Ansible Vault Utility Library

This library allows you to handle Ansible encrypted vaults.

## Quick examples

Here are a couple of examples of how you could use this library

### Easy peasy

JavaAnsibleVault uses https://github.com/EsotericSoftware/yamlbeans for serializing/deserializing
objects to and from Ansible Vaults. This gives you very convenient ways of handling the vaults from
Java.

#### Create an Ansible vault from a Java object

```java
import net.wedjaa.ansible.vault.Manager;

public void createVault(Object someObject, String vaultPassword)
{
        try
        {
            String objectVault = manager.writeToVault(someObject, vaultPassword);
            // objectVault contains now an encrypted YML vault with the
            // object properties... do what you please with it....
        }
        catch (IOException ex)
        {
           // Something went wrong in creating the vault
        }
}
```

#### Deserialize a vault into a Java Object

```java
import net.wedjaa.ansible.vault.Manager;

public void readVault(String vault, Class theObjectClass, String vaultPassword)
{
        try
        {
            Object deserializedObject = manager.getFromVault(theObjectClass, vault, vaultPassword);
            // deserializedObject is the object coming out from the vault - cast away!
        }
        catch (IOException ex)
        {
           // Something went wrong in opening and parsing the vault
        }
}
```

### Full control

#### Create a vault from a buffer or a stream

```java
import net.wedjaa.ansible.vault.crypto;

public void encryptVault(String data, String vaultPassword)
{
        try
        {
            // Get a byte array out of a byte array in
            byte [] encryptedVault = VaultHandler.encrypt(data.getBytes(), vaultPassword);
            // Or use streams
            VaultHandler.encrypt(inputClearStream, outputVaultStream, vaultPassword);
        } catch(Exception ex) {
            ex.printStackTrace();
            logger.warn("Failed to create vault: " + ex.getMessage());
        }
}
```

#### Read a vault from a buffer or a stream

```java
import net.wedjaa.ansible.vault.crypto;

public void decryptVault(String vault, String vaultPassword)
{
        try
        {
            // Get a byte array out of a byte array in
            byte [] decryptedVault = VaultHandler.decrypt(vault.getBytes(), vaultPassword);
            // Or use streams
            VaultHandler.decrypt(inputVaultStream, outputCleartextStream, vaultPassword);
        } catch(Exception ex) {
            ex.printStackTrace();
            logger.warn("Failed to decrypt vault: " + ex.getMessage());
        }
}
```

## Maven and Gradle

You can use this library in your Maven and Gradle projects via [JitPack](https://jitpack.io/#com.github.Wedjaa/JavaAnsibleVault)

Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Wedjaa</groupId>
    <artifactId>JavaAnsibleVault</artifactId>
    <version>git hash or tag</version>
</dependency>
```

Gradle

```groovy
repositories {
    jcenter()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "com.github.Wedjaa:JavaAnsibleVault:<git hash or tag>"
}
```

## Notices and Limitations
The library handles only the newest (version 1.1) format of the vaults. It will cry and crash and burn with any previous versions of the vaults.

_**Ansible uses 256 bits keys to handle encryption and decryption of the vaults, this means that
in order to handle these vaults you will need to install the unrestricted policy files from Oracle.
You have been warned.**_
