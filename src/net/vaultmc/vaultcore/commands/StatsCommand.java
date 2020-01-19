package net.vaultmc.vaultcore.commands;

import java.sql.ResultSet;
import java.util.Collections;

import lombok.SneakyThrows;
import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultcore.VaultCore;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.DBConnection;
import net.vaultmc.vaultloader.utils.commands.Arguments;
import net.vaultmc.vaultloader.utils.commands.CommandExecutor;
import net.vaultmc.vaultloader.utils.commands.Permission;
import net.vaultmc.vaultloader.utils.commands.PlayerOnly;
import net.vaultmc.vaultloader.utils.commands.RootCommand;
import net.vaultmc.vaultloader.utils.commands.SubCommand;
import net.vaultmc.vaultloader.utils.player.VLCommandSender;
import net.vaultmc.vaultloader.utils.player.VLOfflinePlayer;
import net.vaultmc.vaultloader.utils.player.VLPlayer;

@RootCommand(literal = "stats", description = "Check statistics of yourself or other players.")
@Permission(Permissions.StatsCommand)
public class StatsCommand extends CommandExecutor {

	private String string = Utilities.string;
	private String variable2 = Utilities.variable2;

	public StatsCommand() {
		register("statsSelf", Collections.emptyList());
		register("statsOthers",
				Collections.singletonList(Arguments.createArgument("target", Arguments.offlinePlayerArgument())));
	}

	@SubCommand("statsSelf")
	@PlayerOnly
	public void statsSelf(VLPlayer player) {
		viewStats(player, player);
	}

	@SubCommand("statsOthers")
	@Permission(Permissions.StatsCommandOther)
	public void statsOthers(VLCommandSender sender, VLOfflinePlayer target) {
		viewStats(sender, target);
	}

	@SneakyThrows
	private void viewStats(VLCommandSender sender, VLOfflinePlayer target) {
		DBConnection database = VaultCore.getDatabase();

		ResultSet check = database.executeQueryStatement("SELECT username FROM players WHERE username=?",
				target.getName());
		if (!check.next()) {
			sender.sendMessage(VaultLoader.getMessage("vaultcore.player_never_joined"));
			return;
		}
		ResultSet stats = database.executeQueryStatement(
				"SELECT COUNT(session_id) AS sessions, AVG(duration) AS duration FROM sessions WHERE username=?",
				target.getName());
		if (!stats.next()) {
			sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.stats.error"));
			return;
		}
		long[] time = Utilities.millisToTime(stats.getInt("duration"));
		sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.stats.header"));
		sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.stats.player_joined"),
				target.getDisplayName(), stats.getString("sessions")));
		String message = String.format(target.getFormattedName() + string + "'s average session length is " + variable2
				+ "%d" + string + " days, " + variable2 + "%d" + string + " hours and " + variable2 + "%d" + string
				+ "  minutes.", time[0], time[1], time[2]);
		sender.sendMessage(message);
	}
}