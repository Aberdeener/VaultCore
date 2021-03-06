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

package net.vaultmc.vaultcore.vanish;

import net.md_5.bungee.api.ChatColor;
import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultcore.VaultCore;
import net.vaultmc.vaultcore.misc.commands.staff.ForceFieldCommand;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.player.VLCommandSender;
import net.vaultmc.vaultloader.utils.player.VLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RootCommand(literal = "vanish", description = "Toggles your vanish state or somebody else's.")
@Permission(Permissions.VanishCommand)
@Aliases("v")
public class VanishCommand extends CommandExecutor {

    public static final Map<UUID, Boolean> vanished = new HashMap<>();

    public VanishCommand() {
        register("vanishSelf", Collections.emptyList());
        register("vanishSelfFake", Collections.singletonList(Arguments.createArgument("fakeLeave", Arguments.boolArgument())));
        register("vanishOthers",
                Collections.singletonList(Arguments.createArgument("player", Arguments.playerArgument())));
    }

    public static void setVanishState(VLPlayer player, boolean vanish) {
        if (vanish) {
            if (ForceFieldCommand.forcefield.containsKey(player.getUniqueId())) {
                player.sendMessage(VaultLoader.getMessage("forcefield.cannot-vanish"));
                return;
            }
            for (VLPlayer i : VLPlayer.getOnlinePlayers()) {
                if (i == player) continue;
                if (i.hasPermission(Permissions.VanishCommand)) continue;
                i.getPlayer().hidePlayer(VaultCore.getInstance().getBukkitPlugin(), player.getPlayer());
            }
            vanished.put(player.getUniqueId(), true);
        } else {
            for (VLPlayer i : VLPlayer.getOnlinePlayers()) {
                if (i == player) continue;
                i.getPlayer().showPlayer(VaultCore.getInstance().getBukkitPlugin(), player.getPlayer());
            }
            vanished.put(player.getUniqueId(), false);
        }
        player.setTemporaryData("vanished", vanish);
    }

    public static void update(VLPlayer player) {
        if (player.hasPermission(Permissions.VanishCommand)) return;
        for (Map.Entry<UUID, Boolean> x : vanished.entrySet()) {
            if (x.getKey().toString().equals(player.getUniqueId().toString())) continue;
            VLPlayer y = VLPlayer.getPlayer(x.getKey());
            if (y != null && x.getValue() && y.hasPermission(Permissions.VanishCommand)) {
                player.getPlayer().hidePlayer(VaultCore.getInstance().getBukkitPlugin(), y.getPlayer());
            }
        }
    }

    @SubCommand("vanishSelf")
    @PlayerOnly
    public void vanishSelf(VLPlayer sender) {
        toggleSelf(sender, false);
    }

    @SubCommand("vanishSelfFake")
    @PlayerOnly
    public void vanishSelfFake(VLPlayer sender, boolean fakeLeave) {
        toggleSelf(sender, fakeLeave);
    }

    private void toggleSelf(VLPlayer sender, boolean fakeLeave) {
        boolean newValue = !vanished.getOrDefault(sender.getUniqueId(), false);
        String state = newValue ? VaultLoader.getMessage("vanish.invisible") : VaultLoader.getMessage("vanish.visible");
        String message = newValue ? ChatColor.RED + "left" : ChatColor.GREEN + "joined";
        sender.sendMessage(VaultLoader.getMessage("vanish.player-state").replace("{STATE}", state));

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players.hasPermission(Permissions.VanishCommand) && players.hasPermission(Permissions.StaffChatCommand)) {
                players.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vanish.staff_message"), sender.getName(), state));
            }
            if (fakeLeave) {
                players.sendMessage(
                        Utilities.formatMessage(VaultLoader.getMessage("vaultcore.listeners.joinquit.event_message"),
                                sender.getFormattedName(), message));
            }
        }
        setVanishState(sender, newValue);
    }

    @SubCommand("vanishOthers")
    public void vanishOthers(VLCommandSender sender, VLPlayer player) {
        boolean newValue = !vanished.getOrDefault(player.getUniqueId(), false);
        String state = newValue ? VaultLoader.getMessage("vanish.invisible") : VaultLoader.getMessage("vanish.visible");
        player.sendMessage(VaultLoader.getMessage("vanish.player-state").replace("{STATE}", state));
        sender.sendMessage(VaultLoader.getMessage("vanish.others-state").replace("{STATE}", state).replace("{PLAYER}", player.getFormattedName()));
        setVanishState(player, newValue);
    }
}
