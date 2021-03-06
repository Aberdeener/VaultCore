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

package net.vaultmc.vaultcore.lobby;

import net.md_5.bungee.api.ChatColor;
import net.vaultmc.vaultcore.misc.commands.SecLogCommand;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.ConstructorRegisterListener;
import net.vaultmc.vaultloader.utils.ItemStackBuilder;
import net.vaultmc.vaultloader.utils.player.VLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerHider extends ConstructorRegisterListener {
    private static final ItemStack item = new ItemStackBuilder(Material.LIME_DYE)
            .name(ChatColor.YELLOW + "Player Visibility: " + ChatColor.GREEN + "Visible")
            .build();
    private static final ItemStack inverted = new ItemStackBuilder(Material.GRAY_DYE)
            .name(ChatColor.YELLOW + "Player Visibility: " + ChatColor.GRAY + "Invisible")
            .build();

    private static final Set<UUID> hidingPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (SecLogCommand.getLoggingPlayers().containsKey(e.getPlayer().getUniqueId()) || SecLogCommand.getResetingPlayers().containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND &&
                e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().hasItemMeta() &&
                e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW + "Player Visibility: ")) {
            e.setCancelled(true);
            if (hidingPlayers.contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().getInventory().setItem(6, item);
                hidingPlayers.remove(e.getPlayer().getUniqueId());

                for (Player player : Bukkit.getWorld("Lobby").getPlayers()) {
                    if (!VLPlayer.getPlayer(player).isVanished()) {
                        e.getPlayer().showPlayer(VaultLoader.getInstance(), player);
                    }
                }
                e.getPlayer().sendMessage(VaultLoader.getMessage("lobby.player-hider.shown"));
            } else {
                e.getPlayer().getInventory().setItem(6, inverted);
                hidingPlayers.add(e.getPlayer().getUniqueId());
                for (Player player : Bukkit.getWorld("Lobby").getPlayers()) {
                    e.getPlayer().hidePlayer(VaultLoader.getInstance(), player);
                }
                e.getPlayer().sendMessage(VaultLoader.getMessage("lobby.player-hider.hidden"));
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        if (e.getClickedInventory() instanceof PlayerInventory && e.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW + "Player Visibility: "))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW + "Player Visibility: "))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        hidingPlayers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getWorld().getName().equals("Lobby")) {
            for (UUID uuid : hidingPlayers) {
                VLPlayer player = VLPlayer.getPlayer(uuid);
                if (player.getWorld().getName().equalsIgnoreCase("Lobby")) {
                    player.getPlayer().hidePlayer(VaultLoader.getInstance(), e.getPlayer());
                }
            }
            Bukkit.getScheduler().runTask(VaultLoader.getInstance(), () ->
                    e.getPlayer().getInventory().setItem(6, item));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        if (e.getPlayer().getWorld().getName().equals("Lobby")) {
            Bukkit.getScheduler().runTask(VaultLoader.getInstance(), () -> {
                if (!hidingPlayers.contains(e.getPlayer().getUniqueId())) {
                    e.getPlayer().getInventory().setItem(6, item);
                } else {
                    e.getPlayer().getInventory().setItem(6, inverted);
                }
            });
        } else {
            e.getPlayer().getInventory().clear(6);
        }
    }
}
