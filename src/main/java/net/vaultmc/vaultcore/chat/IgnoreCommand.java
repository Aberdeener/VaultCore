/*
 * VaultCore contains the basic functionalities for VaultMC.
 * Copyright (C) 2020 VaultMC
 *
 * VaultCore is a proprietary software: you may not redistribute/use it
 * without prior permission from its owner, however you may contribute
 * to the code. by contributing to VaultCore, you grant to VaultMC a
 * perpetual, nonexclusive, transferable, royalty-free and worldwide
 * license to use, host, reproduce, modify, adapt, publish, translate,
 * create derivative works from, distribute, perform, and display your
 * contribution.
 */

package net.vaultmc.vaultcore.chat;

import net.md_5.bungee.api.ChatColor;
import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.configuration.SQLPlayerData;
import net.vaultmc.vaultloader.utils.player.VLOfflinePlayer;
import net.vaultmc.vaultloader.utils.player.VLPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RootCommand(literal = "ignore", description = "Stop seeing messages from a player.")
@Permission(Permissions.IgnoreCommand)
@PlayerOnly
public class IgnoreCommand extends CommandExecutor {
    // TODO: Caching of some sort...

    public IgnoreCommand() {
        register("ignoreList", Collections.singletonList(Arguments.createLiteral("list")));
        register("ignore", Collections.singletonList(Arguments.createArgument("target", Arguments.offlinePlayerArgument())));
    }

    /**
     * Check if ignorer is ignoring ignoredPlayer
     *
     * @param ignorer       The player receiving the message/event
     * @param ignoredPlayer The player executing the event
     */
    public static boolean isIgnoring(VLOfflinePlayer ignorer, VLPlayer ignoredPlayer) {
        SQLPlayerData data = ignorer.getPlayerData();
        String csvIgnored = data.getString("ignored");
        if (csvIgnored != null) {
            if (csvIgnored.isEmpty()) return false;
            List<String> ignored = Arrays.asList(csvIgnored.split(", "));
            return ignored.contains(ignoredPlayer.getUniqueId().toString());
        }
        return false;
    }

    @SubCommand("ignore")
    public void ignore(VLPlayer sender, VLOfflinePlayer target) {
        if (sender == target) {
            sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.ignore.self_error"));
            return;
        }
        if (target.getFirstPlayed() == 0L) {
            sender.sendMessage(VaultLoader.getMessage("vaultcore.player_never_joined"));
            return;
        }
        SQLPlayerData data = sender.getPlayerData();
        String csvIgnored = data.getString("ignored");
        if (csvIgnored != null) {
            csvIgnored = csvIgnored.replaceAll("\\s", "");
            List<String> ignored = Arrays.asList(csvIgnored.split(","));
            if (csvIgnored.startsWith(",")) csvIgnored = csvIgnored.substring(1);
            if (ignored.contains(target.getUniqueId().toString())) {
                sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.ignore.already_ignored"), target.getFormattedName()));
            } else {
                data.set("ignored", csvIgnored + (ignored.size() < 1 ? "" : ", ") + target.getUniqueId());
                sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.ignore.toggle_ignored"), ChatColor.GREEN + "started", target.getFormattedName()));
            }
        } else {
            data.set("ignored", target.getUniqueId());
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.ignore.toggle_ignored"), ChatColor.GREEN + "started", target.getFormattedName()));
        }
    }

    @SubCommand("ignoreList")
    public void ignoreList(VLPlayer sender) {
        SQLPlayerData data = sender.getPlayerData();
        String csvIgnored = data.getString("ignored");
        sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.ignore.header"));
        if (csvIgnored != null) {
            csvIgnored = csvIgnored.replaceAll("\\s", "");
            if (csvIgnored.isEmpty()) {
                sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.ignore.not_ignoring_anyone"));
                return;
            }
            if (csvIgnored.startsWith(",")) csvIgnored = csvIgnored.substring(1);
            List<String> ignored = Arrays.asList(csvIgnored.split(","));
            if (ignored.size() > 0) {
                int count = 1;
                for (String uuid : ignored) {
                    sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.ignore.list"), count, VLOfflinePlayer.getOfflinePlayer(UUID.fromString(uuid)).getFormattedName()));
                    count++;
                }
            } else sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.ignore.not_ignoring_anyone"));
        } else sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.ignore.not_ignoring_anyone"));
    }
}
