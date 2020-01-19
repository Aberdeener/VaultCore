package net.vaultmc.vaultcore.listeners;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.SneakyThrows;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultcore.VaultCore;
import net.vaultmc.vaultloader.utils.DBConnection;
import net.vaultmc.vaultloader.utils.player.VLPlayer;

public class PlayerJoinQuitListener implements Listener {
	static DBConnection database = VaultCore.getDatabase();


	String string = Utilities.string;
	String variable2 = Utilities.variable2;

	@SneakyThrows
	public static String count() {
		String total_players = null;
		ResultSet rs = database.executeQueryStatement("SELECT COUNT(uuid) FROM players");
		while (rs.next()) {
			total_players = rs.getString(1);
		}
		return total_players;
	}

	@EventHandler
	@SneakyThrows
	public void onJoin(PlayerJoinEvent join) {
		VLPlayer player = VLPlayer.getPlayer(join.getPlayer());
		String uuid = player.getUniqueId().toString();
		String username = player.getName();
		long firstseen = player.getFirstPlayed();
		long lastseen = System.currentTimeMillis();
		long playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
		String rank = player.getGroup();
		String ip = player.getAddress().getAddress().getHostAddress();

		String prefix = ChatColor.translateAlternateColorCodes('&', player.getPrefix());

		player.getPlayer().setDisplayName(prefix + username);
		player.getPlayer().setPlayerListName(player.getDisplayName());

		playerDataQuery(uuid, username, firstseen, lastseen, playtime, rank, ip);


		if (!player.getDataConfig().contains("settings")) {
			player.getDataConfig().set("settings.msg", true);
			player.getDataConfig().set("settings.tpa", true);
			player.getDataConfig().set("settings.autotpa", false);
			player.getDataConfig().set("settings.cycle", false);
			player.getDataConfig().set("settings.swearfilter", true);
			player.saveData();

			for (Player players : Bukkit.getOnlinePlayers()) { 
				players.sendMessage(string + "Welcome " + player.getName() + " to VaultMC! (" + variable2 + "#"
						+ count() + string + ")");
			}
		}
		join.setJoinMessage(player.getFormattedName() + string + " has " + ChatColor.GREEN + "joined" + string + ".");
		player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				VaultCore.getInstance().getConfig().getString("welcome-message")));
	}

	@EventHandler
	@SneakyThrows
	public void onQuit(PlayerQuitEvent quit) {
		Player player = quit.getPlayer();
		String uuid = player.getUniqueId().toString();
		long lastseen = System.currentTimeMillis();
		long playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
		String rank = VaultCore.getChat().getPrimaryGroup(player);

		quit.setQuitMessage(player.getDisplayName() + string + " has " + ChatColor.RED + "left" + string + ".");
		playerDataQuery(uuid, "", 0, lastseen, playtime, rank, "");
	}

	@SneakyThrows
	private void playerDataQuery(String uuid, String username, long firstseen, long lastseen, long playtime,
			String rank, String ip) {
		database.executeUpdateStatement(
				"INSERT INTO players (uuid, username, firstseen, lastseen, playtime, rank, ip) VALUES ("
						+ "?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uuid=?, lastseen=?, playtime=?, rank=?",
				uuid, username, firstseen, lastseen, playtime, rank, ip, uuid, lastseen, playtime, rank);
	}


}
