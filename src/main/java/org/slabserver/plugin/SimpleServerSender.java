package org.slabserver.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SimpleServerSender extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ConfigurationSection servers = getConfig().getConfigurationSection("servers");
        if (servers == null) {
            getLogger().warning("No servers configured - no commands registered");
            return;
        }

        // Register a /<name> command and transfer.<name> permission for every server in the config
        for (String name : servers.getKeys(false)) {
            String commandName = name.toLowerCase();
            String permission = "transfer." + commandName;

            if (getServer().getPluginManager().getPermission(permission) == null) {
                getServer().getPluginManager().addPermission(
                        new Permission(permission, "Allows use of /" + commandName, PermissionDefault.TRUE));
            }

            String description = servers.getString(name + ".description", "Transfer to the " + name + " server");
            getServer().getCommandMap().register(getName().toLowerCase(), new TransferCommand(commandName, description, permission));
        }

        getLogger().info("SimpleServerSender enabled! Ready to send to servers, simply");
    }

    private void transfer(Player player, String commandName) {
        String basePath = "servers." + commandName;

        String host = getConfig().getString(basePath + ".host");
        int port = getConfig().getInt(basePath + ".port");

        if (host == null || host.isEmpty()) {
            player.sendMessage("§cSimpleServerSender config error. Please contact the staff team.");
            getLogger().warning("Missing host for " + commandName);
            return;
        }

        player.transfer(host, port);
    }

    private final class TransferCommand extends Command {

        TransferCommand(String name, String description, String permission) {
            super(name, description, "/" + name, List.of());
            setPermission(permission);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!testPermission(sender)) {
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command - how did you even do this?!?");
                return true;
            }

            transfer(player, getName());
            return true;
        }
    }
}
