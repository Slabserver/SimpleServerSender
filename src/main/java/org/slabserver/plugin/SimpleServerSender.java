package org.slabserver.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleServerSender extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("SimpleServerSender enabled! Ready to send to servers, simply");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command - how did you even do this?");
            return false;
        }

        String commandName = command.getName().toLowerCase();

        String basePath = "servers." + commandName;

        String host = getConfig().getString(basePath + ".host");
        int port = getConfig().getInt(basePath + ".port");

        if (host == null || host.isEmpty()) {
            player.sendMessage("§cSimpleServerSender config error. Please contact the staff team.");
            getLogger().warning("Missing host for " + commandName);
            return true;
        }

        player.transfer(host, port);
        return true;
    }
}
