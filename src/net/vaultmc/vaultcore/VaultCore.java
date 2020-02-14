package net.vaultmc.vaultcore;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.SneakyThrows;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.vaultmc.vaultcore.brand.BrandCommand;
import net.vaultmc.vaultcore.brand.BrandListener;
import net.vaultmc.vaultcore.buggy.Bug;
import net.vaultmc.vaultcore.buggy.BuggyCommand;
import net.vaultmc.vaultcore.buggy.BuggyListener;
import net.vaultmc.vaultcore.chat.ChatUtils;
import net.vaultmc.vaultcore.chat.ClearChatCommand;
import net.vaultmc.vaultcore.chat.ConsoleSay;
import net.vaultmc.vaultcore.chat.MuteChatCommand;
import net.vaultmc.vaultcore.chat.msg.MsgCommand;
import net.vaultmc.vaultcore.chat.msg.ReplyCommand;
import net.vaultmc.vaultcore.chat.msg.SocialSpyCommand;
import net.vaultmc.vaultcore.chat.staff.StaffChatCommand;
import net.vaultmc.vaultcore.connections.DiscordCommand;
import net.vaultmc.vaultcore.connections.TokenCommand;
import net.vaultmc.vaultcore.creative.CycleListener;
import net.vaultmc.vaultcore.creative.SchemCommand;
import net.vaultmc.vaultcore.economy.EconomyCommand;
import net.vaultmc.vaultcore.economy.EconomyImpl;
import net.vaultmc.vaultcore.economy.MoneyCommand;
import net.vaultmc.vaultcore.economy.TransferCommand;
import net.vaultmc.vaultcore.gamemode.GMCreativeCommand;
import net.vaultmc.vaultcore.gamemode.GMSpectatorCommand;
import net.vaultmc.vaultcore.gamemode.GMSurvivalCommand;
import net.vaultmc.vaultcore.gamemode.GameModeCommand;
import net.vaultmc.vaultcore.grant.GrantCommand;
import net.vaultmc.vaultcore.grant.GrantCommandListener;
import net.vaultmc.vaultcore.inventory.InventoryStorageListeners;
import net.vaultmc.vaultcore.misc.commands.*;
import net.vaultmc.vaultcore.misc.commands.staff.*;
import net.vaultmc.vaultcore.misc.listeners.*;
import net.vaultmc.vaultcore.misc.runnables.RankPromotions;
import net.vaultmc.vaultcore.nametags.Nametags;
import net.vaultmc.vaultcore.punishments.PunishmentsDB;
import net.vaultmc.vaultcore.punishments.ban.*;
import net.vaultmc.vaultcore.punishments.kick.KickCommand;
import net.vaultmc.vaultcore.punishments.mute.*;
import net.vaultmc.vaultcore.report.Report;
import net.vaultmc.vaultcore.report.ReportCommand;
import net.vaultmc.vaultcore.report.ReportsCommand;
import net.vaultmc.vaultcore.settings.SettingsCommand;
import net.vaultmc.vaultcore.settings.SettingsListener;
import net.vaultmc.vaultcore.stats.*;
import net.vaultmc.vaultcore.survival.SleepHandler;
import net.vaultmc.vaultcore.survival.claim.ClaimCommand;
import net.vaultmc.vaultcore.survival.claim.UnclaimCommand;
import net.vaultmc.vaultcore.teleport.*;
import net.vaultmc.vaultcore.teleport.worldtp.CRCommand;
import net.vaultmc.vaultcore.teleport.worldtp.SVCommand;
import net.vaultmc.vaultcore.tour.Tour;
import net.vaultmc.vaultcore.tour.TourCommand;
import net.vaultmc.vaultcore.tour.TourMusic;
import net.vaultmc.vaultcore.tour.TourStageCommand;
import net.vaultmc.vaultcore.vanish.VanishCommand;
import net.vaultmc.vaultcore.vanish.VanishListeners;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.components.Component;
import net.vaultmc.vaultloader.components.annotations.ComponentInfo;
import net.vaultmc.vaultloader.components.annotations.Version;
import net.vaultmc.vaultloader.utils.DBConnection;
import net.vaultmc.vaultloader.utils.configuration.Configuration;
import net.vaultmc.vaultloader.utils.configuration.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.text.DecimalFormat;

@ComponentInfo(name = "VaultCore", description = "The suite of tools created for the VaultMC server.", authors = {
        "Aberdeener", "yangyang200", "2xjtn"})
@Version(major = 3, minor = 0, revision = 4)
public final class VaultCore extends Component implements Listener {
    public static final DecimalFormat numberFormat = new DecimalFormat("###,###.###");
    @Getter
    public static VaultCore instance;
    public static boolean isReloaded = false;
    private static Chat chat = null;
    @Getter
    private static DBConnection database;
    @Getter
    private static DBConnection pDatabase;
    private Configuration config;
    private Configuration locations;
    private Configuration data;
    private Configuration inv;

    private static String getServerName() {
        String name = "CraftBukkit";

        try {
            Class.forName("org.spigotmc.event.entity.EntityDismountEvent");
            name = "Spigot";
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("com.destroystokyo.paper.NamespacedTag");
            name = "Paper";
        } catch (ClassNotFoundException ignored) {
        }

        return name;
    }

    public static Chat getChat() {
        return chat;
    }

    @Override
    public void onStartingReloaded() {
        isReloaded = true;
    }

    @Override
    @SneakyThrows
    public void onEnable() {
        instance = this;

        config = ConfigurationManager.loadConfiguration("config.yml", this);
        data = ConfigurationManager.loadConfiguration("data.yml", this);
        inv = ConfigurationManager.loadConfiguration("inventory.yml", this);
        locations = ConfigurationManager.loadConfiguration("locations.yml", this);

        database = new DBConnection(getConfig().getString("mysql.host"), getConfig().getInt("mysql.port"),
                getConfig().getString("mysql.database"), getConfig().getString("mysql.user"),
                getConfig().getString("mysql.password"));
        pDatabase = new DBConnection(getConfig().getString("mysql.host"), getConfig().getInt("mysql.port"),
                "VaultMC_Punishments", getConfig().getString("mysql.user"),
                getConfig().getString("mysql.password"));

        setupChat();
        Bug.load();

        getServer().getScheduler().runTaskLater(this.getBukkitPlugin(), () -> registerEvents(new Nametags()), 1);
        getServer().getServicesManager().register(Economy.class, new EconomyImpl(), this.getBukkitPlugin(), ServicePriority.Highest);
        getServer().getMessenger().registerIncomingPluginChannel(this.getBukkitPlugin(), "minecraft:brand", new BrandListener());
        getServer().getMessenger().registerOutgoingPluginChannel(VaultLoader.getInstance(), "BungeeCord");

        new CRCommand();
        new SVCommand();
        new WildTeleportCommand();
        new TourCommand();
        new TourStageCommand();
        new TourMusic();
        new Tour();
        new SchemCommand();
        registerEvents(new CycleListener());
        registerEvents(new SleepHandler());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.getBukkitPlugin(), () -> {
            RankPromotions.memberPromotion();
            RankPromotions.patreonPromotion();
            Statistics.statistics();
        }, 0L, 2400L);
        new MsgCommand();
        new ReplyCommand();
        new SettingsCommand();
        new TPACommand();
        new TPAcceptCommand();
        new TPAHereCommand();
        new TPHereCommand();
        new TPDenyCommand();
        new WarpCommand();
        new BackCommand();
        new DiscordCommand();
        new PingCommand();
        new PlayTimeCommand();
        new RanksCommand();
        new SpeedCommand();
        new SeenCommand();
        new TokenCommand();
        new GrantCommand();
        new GameModeCommand();
        new GMCreativeCommand();
        new GMSurvivalCommand();
        new GMSpectatorCommand();
        new CheckCommand();
        new ClearChatCommand();
        new ConsoleSay();
        new FeedCommand();
        new FlyCommand();
        new HealCommand();
        new InvseeCommand();
        new MuteChatCommand();
        new StaffChatCommand();
        new TPCommand();
        new ReloadCommand();
        new HasPermCommand();
        new TagCommand();
        new StatsCommand();
        new ListCommand();
        new SocialSpyCommand();
        new ModMode();
        new ReportCommand();
        new ReportsCommand();
        new AFKCommand();
        new ChatUtils();
        new EconomyCommand();
        new MoneyCommand();
        new TimeCommand();
        new TransferCommand();
        new VanishCommand();
        new VanishListeners();
        new GameModeListeners();
        new WeatherCommand();
        new HelpCommand();
        new BrandCommand();
        new KickCommand();
        new BanCommand();
        new MuteCommand();
        new UnbanCommand();
        new IpBanCommand();
        new IpTempBanCommand();
        new IpMuteCommand();
        new IpTempMuteCommand();
        new UnmuteCommand();
        new TempBanCommand();
        new TempMuteCommand();
        new InventoryStorageListeners();
        new ClaimCommand();
        new UnclaimCommand();
        new SchemCommand();
        new ServerNavigator();
        new BuggyCommand();
        new BuggyListener();
        registerEvents(new GrantCommandListener());
        registerEvents(new SignColours());
        registerEvents(new PlayerJoinQuitListener());
        registerEvents(new PlayerTPListener());
        registerEvents(new SettingsListener());
        registerEvents(new ShutDownListener());
        registerEvents(new BannedListener());
        registerEvents(new MutedListener());

        PunishmentsDB.createTables();
        Report.load();

        Bukkit.getServer().getConsoleSender().sendMessage(new String[]{
                ChatColor.YELLOW + "                   _ _     " + ChatColor.GOLD + "___               ",
                ChatColor.YELLOW + " /\\   /\\__ _ _   _| | |_  " + ChatColor.GOLD + "/ __\\___  _ __ ___ ",
                ChatColor.YELLOW + " \\ \\ / / _` | | | | | __|" + ChatColor.GOLD + "/ /  / _ \\| '__/ _ \\",
                ChatColor.YELLOW + "  \\ V / (_| | |_| | | |_" + ChatColor.GOLD + "/ /__| (_) | | |  __/",
                ChatColor.YELLOW + "   \\_/ \\__,_|\\__,_|_|\\__" + ChatColor.GOLD + "\\____/\\___/|_|  \\___|", "",
                ChatColor.GREEN + "Successfully enabled. Maintained by " + ChatColor.YELLOW + "Aberdeener"
                        + ChatColor.GREEN + ", " + "running on " + ChatColor.YELLOW + "Bukkit - " + getServerName()
                        + ChatColor.GREEN + "."});
    }

    public FileConfiguration getConfig() {
        return this.config.getConfig();
    }

    public FileConfiguration getLocationFile() {
        return this.locations.getConfig();
    }

    public FileConfiguration getInventoryData() {
        return inv.getConfig();
    }

    public FileConfiguration getData() {
        return data.getConfig();
    }

    @Override
    public void onServerFinishedLoading() {
        locations = ConfigurationManager.loadConfiguration("locations.yml", this);
    }

    public void saveLocations() {
        locations.save();
    }

    public void saveConfig() {
        config.save();
        data.save();
        inv.save();
        locations.save();
    }

    public void reloadConfig() {
        config.reload();
        data.reload();
        inv.reload();
    }

    @Override
    public void onReload() {
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
    }

    public void sendToBackup() {
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            players.sendMessage(VaultLoader.getMessage("vaultcore.sendingtobackup"));
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("backup");
            players.sendPluginMessage(VaultLoader.getInstance(), "BungeeCord", out.toByteArray());
        }
    }

    @Override
    public void onDisable() {
        database.close();
        pDatabase.close();
        Bug.save();
        inv.save();
        locations.save();
        data.save();
        Report.save();
    }
}