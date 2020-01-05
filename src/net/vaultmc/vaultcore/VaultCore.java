package net.vaultmc.vaultcore;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.vaultmc.vaultcore.commands.staff.grant.GrantCommandInv;
import net.vaultmc.vaultcore.runnables.RankPromotions;
import net.vaultmc.vaultutils.database.DBConnection;

public class VaultCore extends JavaPlugin implements Listener {
	public static VaultCore instance;
	private static Chat chat = null;
	private static Permission perms = null;
	private File playerDataFile;
	private FileConfiguration playerData;
	@Getter
	private static DBConnection database;

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		instance = this;

        database = new DBConnection(getConfig().getString("mysql.host"), getConfig().getInt("mysql.port"), getConfig().getString("mysql.database"),
                getConfig().getString("mysql.user"), getConfig().getString("mysql.password"), this);
		
		setupChat();
		setupPermissions();
		Registry.registerCommands();
		Registry.registerListeners();
		createPlayerData();
		GrantCommandInv.initAdmin();
		GrantCommandInv.initMod();
		int minute = (int) 1200L;
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
			RankPromotions.memberPromotion();
			RankPromotions.patreonPromotion();
		}, 0L, minute * 5);
	}

	public FileConfiguration getPlayerData() {
		return this.playerData;
	}

	private void createPlayerData() {
		playerDataFile = new File(getDataFolder(), "data.yml");
		if (!playerDataFile.exists()) {
			playerDataFile.getParentFile().mkdirs();
			saveResource("data.yml", false);
		}
		playerData = new YamlConfiguration();
		try {
			playerData.load(playerDataFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void savePlayerData() {
		try {
			playerData.save(playerDataFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static VaultCore getInstance() {
		return instance;
	}

	private void setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public static Chat getChat() {
		return chat;
	}

	public static Permission getPermissions() {
		return perms;
	}

	@Override
	public void onDisable() {

		/*
		 * TO DO ASAP: FOR ALL PLAYERS ONLINE, TRIGGER PLAYERQUITEVENT WHEN SERVER SHUTDOWN
		 */
		
		this.saveConfig();
		this.savePlayerData();
	}
}