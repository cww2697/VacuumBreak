package net.canyonwolf.commands;

import net.canyonwolf.constants.Worlds;
import net.canyonwolf.service.BackupService;
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

    public Snapshot(Logger logger, FileConfiguration config, String sourceDirectory, String targetDirectory) {
        this.config = config;
        this.logger = logger;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
    }

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

        BackupService.createSnapshots(sourceDirectory, targetDirectory, includeNether, includeEnd, includeOverworld);

        return true;

    }
}
