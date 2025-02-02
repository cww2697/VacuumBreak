package net.canyonwolf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class CommandBackup implements CommandExecutor {

    Logger logger;
    FileConfiguration config;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return false;
    }
}
