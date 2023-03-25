package tech.zmario.privatemessages.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;
import tech.zmario.privatemessages.bungee.BungeeCordPlugin;
import tech.zmario.privatemessages.common.commands.interfaces.Command;

public class BungeeCommand extends net.md_5.bungee.api.plugin.Command implements TabExecutor {

    private final BungeeCordPlugin plugin;
    private final Command command;

    public BungeeCommand(BungeeCordPlugin plugin, Command command) {
        super(command.getName(), command.getPermission(), command.getAliases().toArray(new String[0]));
        this.plugin = plugin;
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        command.execute(plugin.getSenderFactory().wrapSender(sender), args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return command.suggest(plugin.getSenderFactory().wrapSender(sender), args);
    }
}
