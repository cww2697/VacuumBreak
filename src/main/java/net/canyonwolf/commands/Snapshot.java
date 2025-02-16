package net.canyonwolf.commands;

import net.canyonwolf.constants.Worlds;
import net.canyonwolf.service.SnapshotService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class Snapshot implements CommandExecutor {

    Logger logger;
    FileConfiguration config;
    String sourceDirectory;
    String targetDirectory;

    /**
     * @param logger Java logger
     * @param config Plugin configuration
     * @param sourceDirectory Directory to snapshot
     * @param targetDirectory Snapshot directory
     */
    public Snapshot(Logger logger, FileConfiguration config, String sourceDirectory, String targetDirectory) {
        this.config = config;
        this.logger = logger;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
    }

    /**
     * @param sender Command sender
     * @param cmd Command
     * @param label Command Label
     * @param args Command Arguements
     * @return Success state
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command cmd,
            @NotNull String label,
            @NotNull String[] args
    ) {

        boolean includeOverworld = false;
        boolean includeNether = false;
        boolean includeEnd = false;

        if (args.length == 0) {
            SnapshotService.createSnapshots(sourceDirectory, targetDirectory, true, true, true);
            return true;
        }

        for (String arg : args) {
            switch (arg) {
                case Worlds.overworldName:
                case Worlds.overworldPath:
                    includeOverworld = true;
                    continue;
                case Worlds.netherName:
                case Worlds.netherPath:
                    includeNether = true;
                    continue;
                case Worlds.endName:
                case Worlds.endPath:
                    includeEnd = true;
                    continue;
                case "all":
                    includeOverworld = true;
                    includeNether = true;
                    includeEnd = true;
                    break;
                default:
                    logger.warning("Invalid argument: " + arg);
                    return false;
            }
        }

        SnapshotService.createSnapshots(sourceDirectory, targetDirectory, includeNether, includeEnd, includeOverworld);

        return true;

    }
}
