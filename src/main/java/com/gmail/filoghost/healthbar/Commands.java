package com.gmail.filoghost.healthbar;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    public Main instance;
    private static final String PREFIX = "§2[§aHealthBar§2] ";

    public Commands(Main main) {
        instance = main;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendInfo(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sendCommandList(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfigs(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("reloadplayer"))
        {
            if (args.length < 2)
                return false;
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null)
                return false;
            instance.reloadPlayerBar(p);
        }

        sender.sendMessage(PREFIX + "§eUnknown command. Type §a" + label + " §efor help.");
        return true;
    }

    private void reloadConfigs(CommandSender sender) {
        if (!sender.hasPermission("healthbar.reload")) {
            noPermissionMessage(sender);
            return;
        }
        try {
            instance.reloadConfigFromDisk();
            sender.sendMessage("§e>>§6 HealthBar reloaded");
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("§cFailed to reload configs, take a look at the console!");
        }

    }

    private void sendInfo(CommandSender sender) {
        sender.sendMessage(PREFIX);
        sender.sendMessage("§aVersion: §7" + instance.getDescription().getVersion());
        sender.sendMessage("§aDevelopers: §7filoghost§r, §7electronrussia");
        sender.sendMessage("§aCommands: §7/hbr help");
    }

    private void sendCommandList(CommandSender sender) {
        if (!sender.hasPermission("healthbar.help")) {
            noPermissionMessage(sender);
            return;
        }
        sender.sendMessage("§e>>§6 HealthBar commands: ");
        sender.sendMessage("§2/hbr §7- §aDisplays general plugin info");
        sender.sendMessage("§2/hbr reload §7- §aReloads the configs");
    }

    private void noPermissionMessage(CommandSender sender) {
        sender.sendMessage("§cYou don't have permission.");
    }

}
