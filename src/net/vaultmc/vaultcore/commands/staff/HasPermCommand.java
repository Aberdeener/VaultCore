package net.vaultmc.vaultcore.commands.staff;

import net.vaultmc.vaultcore.*;
import net.vaultmc.vaultloader.utils.DBConnection;
import net.vaultmc.vaultloader.utils.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

@RootCommand(literal = "hasperm", description = "Check if a player has a permission.")
@Permission(Permissions.HasPermCommand)
public class HasPermCommand extends CommandExecutor {

	String string = Utilities.string;
	String variable1 = Utilities.variable1;

	public HasPermCommand() {
		this.register("hasPermSelf", Arrays.asList(Arguments.createArgument("permission", Arguments.string())));
		this.register("hasPermOther", Arrays.asList(Arguments.createArgument("permission", Arguments.string()),
				Arguments.createArgument("target", Arguments.offlinePlayerArgument())));
	}

	@SubCommand("hasPermSelf")
	@PlayerOnly
	public void hasPermSelf(CommandSender sender, String permission) {

		if (VaultCore.getPermissions().has(sender, permission)) {
			sender.sendMessage(string + "You " + ChatColor.GREEN + "have" + string + " the permission " + variable1
					+ permission + string + ".");
		} else {
			sender.sendMessage(string + "You " + ChatColor.RED + "don't have" + string + " the permission " + variable1
					+ permission + string + ".");
		}
	}

	@SubCommand("hasPermOther")
	@Permission(Permissions.HasPermCommandOther)
	public void hasPermOther(CommandSender sender, String permission, OfflinePlayer target) {
		if (target.isOnline()) {
			hasPermOtherOnline(target.getPlayer(), sender, permission);
			return;
		}
		hasPermOtherOffline(sender, target, permission);
	}

	private void hasPermOtherOnline(Player target, CommandSender sender, String permission) {
		if (VaultCore.getPermissions().has(target, permission)) {
			sender.sendMessage(variable1 + VaultCoreAPI.getName(target) + ChatColor.GREEN + " has " + string
					+ "the permission " + variable1 + permission + string + ".");
		} else {
			sender.sendMessage(variable1 + VaultCoreAPI.getName(target) + ChatColor.RED + " doesn't have "
					+ string + "the permission " + variable1 + permission + string + ".");
		}
	}

	private void hasPermOtherOffline(CommandSender sender, OfflinePlayer target, String permission) {

		DBConnection database = VaultCore.getDatabase();

		try {
			ResultSet rs = database.executeQueryStatement("SELECT username FROM players WHERE username=?",
					target.getName());
			if (!rs.next()) {
				sender.sendMessage(ChatColor.RED + "That player has never joined before!");
				return;
			}

			if (VaultCore.getPermissions().playerHas(Bukkit.getWorlds().get(0).getName(), target, permission)) {
				sender.sendMessage(variable1 + VaultCoreAPI.getName(target) + ChatColor.GREEN + " has "
						+ string + "the permission " + variable1 + permission + string + ".");
			} else {
				sender.sendMessage(variable1 + VaultCoreAPI.getName(target) + ChatColor.RED
						+ " doesn't have " + string + "the permission " + variable1 + permission + string + ".");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}