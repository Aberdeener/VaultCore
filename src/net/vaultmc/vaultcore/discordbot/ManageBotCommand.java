package net.vaultmc.vaultcore.discordbot;

import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.player.VLCommandSender;

import java.util.Collections;

@RootCommand(literal = "vaultmcbot", description = "Stop, start and reboot the VaultMCBot.")
@Permission(Permissions.VaultMCBotManage)
public class ManageBotCommand extends CommandExecutor {
    public ManageBotCommand() {
        register("stop",
                Collections.singletonList(Arguments.createLiteral("stop")));
        register("start",
                Collections.singletonList(Arguments.createLiteral("start")));
        register("restart",
                Collections.singletonList(Arguments.createLiteral("restart")));
    }

    @SubCommand("stop")
    public static void stop(VLCommandSender sender) {
        if (VaultMCBot.isStarted()) {
            VaultMCBot.setStarted(false);
            VaultMCBot.getJda().shutdown();
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.managebot.complete"), "stopped"));
        } else {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.managebot.invalid"), "stopped"));
        }
    }

    @SubCommand("start")
    public static void start(VLCommandSender sender) {
        if (VaultMCBot.isStarted()) {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.managebot.invalid"), "started"));
        } else {
            VaultMCBot.startVaultMCBot();
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.managebot.complete"), "stopped"));
        }
    }

    @SubCommand("restart")
    public static void restart(VLCommandSender sender) {
        if (VaultMCBot.isStarted()) {
            VaultMCBot.getJda().shutdown();
            VaultMCBot.setStarted(false);
            VaultMCBot.startVaultMCBot();
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.managebot.complete"), "restarted"));
        } else {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.managebot.invalid"), "stopped"));
        }
    }
}