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

package net.vaultmc.vaultcore.teleport;

import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.ConstructorRegisterListener;
import net.vaultmc.vaultloader.utils.player.VLPlayer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

public class PlayerTPListener extends ConstructorRegisterListener {
    public static HashMap<UUID, Stack<Location>> teleports = new HashMap<>();

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Stack<Location> stack = teleports.getOrDefault(e.getPlayer().getUniqueId(), new Stack<>());
        stack.push(e.getFrom());
        teleports.put(e.getPlayer().getUniqueId(), stack);
        if (e.getFrom().getWorld().getName().equals("Survival") || (e.getFrom().getWorld().getName().equals("Creative"))) {
            Location from = e.getFrom();
            if (e.getFrom().getWorld().equals(e.getTo().getWorld())) {
                return;
            }
            VLPlayer player = VLPlayer.getPlayer(e.getPlayer());
            if (e.getFrom().getWorld().getName().equals("Survival")) {
                player.getPlayerData().set("locations.sv", Utilities.serializeLocation(from));
            } else if (e.getFrom().getWorld().getName().equals("Creative")) {
                player.getPlayerData().set("locations.cr", Utilities.serializeLocation(from));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Stack<Location> stack = teleports.getOrDefault(e.getEntity().getUniqueId(), new Stack<>());
        stack.push(e.getEntity().getLocation());
        teleports.put(e.getEntity().getUniqueId(), stack);
        e.getEntity().sendMessage(VaultLoader.getMessage("vaultcore.survival.back"));
    }
}