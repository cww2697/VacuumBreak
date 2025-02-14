<h1>Vacuum Break</h1>

**Automated server backups for Spigot based servers**

Minecraft Java Edition required. Vacuum Break is compatible with Spigot based Minecraft servers.

## Download Vacuum Break

You can download Vacuum Break from GitHub as either code or as a full function jar file.

## Configuration

Configuration is handled using the configuration file generated after first launch of the server with Vacuum Break installed.

```automated-backups```  Value is currently unused. (Boolean)

```prefer-hours```  Switch between utilizing minutes or hours for automatic backups. (Boolean)

```backup-every-hours```  Number of hours between backups (This values is not used if ```prefer-hours``` is set to false). (Integer)

```backup-every-minutes```  Number of minutes between backups (This value is not used if ```prefer-hours``` is set to true). (Integer)

```snapshot-count```  Maximum number of backups to keep at a time. (Integer)

```include-nether``` Switch backups of Nether on or off. (Boolean)

```include-end``` Switch backups of The End on or off. (Boolean)

```snapshot-dir``` Directory to back up files to. Backup directory should be provided with path from ```plugins/vacuumbreak```. (String)

```silent``` Switch logging on or off. (Boolean)

```config-version``` Version of plugin used to create configuration file. (String)

## Roadmap

1. Initial release (Automated Backups). ✅
2. Introduce command structure for manual backups and hot configuration reload.
3. Introduce permission system allowing for server operators to lock down manual backups of files.
4. Cloud backup of files utilizing S3 object storage.
5. More coming soon!

## Submitting Changes

Vacuum Break is open source (GPL v3), so note that any contributions made will also be open source.