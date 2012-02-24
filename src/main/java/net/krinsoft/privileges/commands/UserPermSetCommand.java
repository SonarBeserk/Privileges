package net.krinsoft.privileges.commands;

import net.krinsoft.privileges.Privileges;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class UserPermSetCommand extends UserPermCommand {

    public UserPermSetCommand(Privileges plugin) {
        super(plugin);
        this.setName("Privileges: User Perm Set");
        this.setCommandUsage("/privileges user perm set [user] [world:]node [val]");
        this.addCommandExample("/priv user perm set Player example.node true");
        this.addCommandExample("/pups Player world:example.node false");
        this.setArgRange(2, 3);
        this.addKey("privileges user perm set");
        this.addKey("priv user perm set");
        this.addKey("pu perm set");
        this.addKey("pup set");
        this.addKey("pups");
        this.setPermission("privileges.user.perm.set", "Allows this user to set permission nodes.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String user = (plugin.getUsers().getConfigurationSection("users." + args.get(0)) != null ? args.get(0) : null);
        if (user == null) {
            sender.sendMessage("I don't know about that user.");
            return;
        }
        String[] param = validateNode(args.get(1));
        if (param == null) {
            showHelp(sender);
            return;
        }
        boolean val = true;
        if (args.size() == 3) {
            try {
                val = Boolean.parseBoolean(args.get(2));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Value must be a boolean, true or false.");
                return;
            }
        }
        if (param[0].equalsIgnoreCase("privileges.self.edit") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "Only the console can set that node.");
            return;
        }
        List<String> nodes;
        if (param[1] == null) {
            nodes = plugin.getUserNode(user).getStringList("permissions");
            nodes.remove(param[0]);
            nodes.remove("-" + param[0]);
            nodes.add((val ? "" : "-") + param[0]);
            plugin.getUserNode(user).set("permissions", nodes);
        } else {
            nodes = plugin.getUserNode(user).getStringList("worlds." + param[1]);
            nodes.remove(param[0]);
            nodes.remove("-" + param[0]);
            nodes.add((val ? "" : "-") + param[0]);
            plugin.getUserNode(user).set("worlds." + param[1], nodes);
        }
        sender.sendMessage("Node '" + colorize(ChatColor.GREEN, param[0]) + "' is now " + (val ? ChatColor.GREEN : ChatColor.RED) + val + ChatColor.WHITE + " for " + user + (param[1] == null ? "" : " on " + ChatColor.GREEN + param[1]));
        sender.sendMessage("When you're done editing permissions, run: " + ChatColor.GREEN + "/priv reload");
        plugin.log(">> " + sender.getName() + ": " + user + "'s node '" + param[0] + "' is now '" + val + "'");
    }

}
