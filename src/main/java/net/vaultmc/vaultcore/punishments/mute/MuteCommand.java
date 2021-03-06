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

package net.vaultmc.vaultcore.punishments.mute;

import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.punishments.PunishmentsDB;
import net.vaultmc.vaultcore.punishments.ban.BanCommand;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.player.VLCommandSender;
import net.vaultmc.vaultloader.utils.player.VLOfflinePlayer;
import net.vaultmc.vaultloader.utils.player.VLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

@RootCommand(
        literal = "mute",
        description = "Disallows a player from chatting, using signs and executing some commands permanently."
)
@Permission(Permissions.MuteCommand)
public class MuteCommand extends CommandExecutor {
    public MuteCommand() {
        register("mute", Collections.singletonList(
                Arguments.createArgument("player", Arguments.offlinePlayerArgument())
        ));
        register("muteSilent", Arrays.asList(
                Arguments.createArgument("player", Arguments.offlinePlayerArgument()),
                Arguments.createArgument("silent", Arguments.boolArgument())
        ));
        register("muteReasonSilent", Arrays.asList(
                Arguments.createArgument("player", Arguments.offlinePlayerArgument()),
                Arguments.createArgument("silent", Arguments.boolArgument()),
                Arguments.createArgument("reason", Arguments.greedyString())
        ));
    }

    @SubCommand("mute")
    public void mute(VLCommandSender sender, VLOfflinePlayer victim) {
        mutePlayer(sender, victim, VaultLoader.getMessage("punishments.default-reason"), false);
    }

    @SubCommand("muteSilent")
    public void muteSilent(VLCommandSender sender, VLOfflinePlayer victim, boolean silent) {
        mutePlayer(sender, victim, VaultLoader.getMessage("punishments.default-reason"), silent);
    }

    @SubCommand("muteReasonSilent")
    public void muteReasonSilent(VLCommandSender sender, VLOfflinePlayer victim, boolean silent, String reason) {
        mutePlayer(sender, victim, reason, silent);
    }

    private void mutePlayer(VLCommandSender actor, VLOfflinePlayer victim, String reason, boolean silent) {
        if (Bukkit.getPlayer(victim.getUniqueId()) != null) {
            Bukkit.getPlayer(victim.getUniqueId()).sendMessage(VaultLoader.getMessage("punishments.mute.message")
                    .replace("{ACTOR}", actor.getFormattedName())
                    .replace("{REASON}", reason));
        }

        PunishmentsDB.registerData("mutes", new PunishmentsDB.PunishmentData(victim.getUniqueId().toString(), true, reason, -1,
                (actor instanceof VLPlayer) ? ((VLPlayer) actor).getUniqueId() : BanCommand.console));

        actor.sendMessage(VaultLoader.getMessage("punishments.mute.sent").replace("{PLAYER}", victim.getFormattedName()));

        if (silent) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(Permissions.PunishmentNotify)) {
                    player.sendMessage(VaultLoader.getMessage("punishments.silent-flag") +
                            VaultLoader.getMessage("punishments.mute.announcement")
                                    .replace("{ACTOR}", actor.getFormattedName())
                                    .replace("{PLAYER}", victim.getFormattedName())
                                    .replace("{REASON}", reason));
                }
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(
                        VaultLoader.getMessage("punishments.mute.announcement")
                                .replace("{ACTOR}", actor.getFormattedName())
                                .replace("{PLAYER}", victim.getFormattedName())
                                .replace("{REASON}", reason));
            }
        }
    }
}
