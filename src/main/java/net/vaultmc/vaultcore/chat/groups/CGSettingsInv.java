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

package net.vaultmc.vaultcore.chat.groups;

import net.md_5.bungee.api.ChatColor;
import net.vaultmc.vaultloader.utils.ItemStackBuilder;
import net.vaultmc.vaultloader.utils.player.VLOfflinePlayer;
import net.vaultmc.vaultloader.utils.player.VLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class CGSettingsInv {
    public void openMainMenu(VLPlayer sender) {
        ChatGroup chatGroup = ChatGroup.getChatGroup(sender);

        Inventory mainMenu = Bukkit.createInventory(null, 27, ChatColor.RESET + "Chat Group Settings: " + chatGroup.name);
        mainMenu.setItem(11, new ItemStackBuilder(Material.PAPER)
                .name(ChatColor.YELLOW + "Public/Private")
                .lore(Arrays.asList(
                        ChatColor.GRAY + "Toggle the visibility of your chat group.",
                        "",
                        chatGroup.open ? ChatColor.GREEN + "Public" : ChatColor.RED + "Private"
                ))
                .build());
        mainMenu.setItem(15, new ItemStackBuilder(Material.PLAYER_HEAD)
                .name(ChatColor.YELLOW + "Manage Group Members")
                .skullOwner("eyJ0aW1lc3RhbXAiOjE1ODY5MjQ5OTg3OTAsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMz" +
                        "MyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6I" +
                        "mh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZTA2M2NhZmI0NjdhNWM4ZGU0M2VjNz" +
                        "g2MTkzOTlmMzY5ZjRhNTI0MzRkYTgwMTdhOTgzY2RkOTI1MTZhMCJ9fX0")
                .lore(Collections.singletonList(
                        ChatColor.GRAY + "Edit the members of your ChatGroup."
                ))
                .build());
        sender.openInventory(mainMenu);
    }

    public void openMembersMenu(VLPlayer sender) {
        ChatGroup chatGroup = ChatGroup.getChatGroup(sender);
        Set<VLOfflinePlayer> members = ChatGroup.getChatGroupMembers(chatGroup);

        int memberCount = members.size();
        int slotCount;

        if (memberCount < 27) slotCount = 27;
        else if (memberCount > 27 && memberCount < 54) slotCount = 54;
        else return; // TODO: Handle chatgroups with > 54 players

        int currentSlot = 0;

        Inventory membersMenu = Bukkit.createInventory(null, slotCount, ChatColor.RESET + "Chat Group Members: " + chatGroup.name);

        for (VLOfflinePlayer player : members) {
            membersMenu.setItem(currentSlot, new ItemStackBuilder(Material.PLAYER_HEAD)
                    .name(ChatColor.YELLOW + "Edit: " + player.getFormattedName())
                    .skullOwner(player)
                    .lore(Arrays.asList(
                            ChatColor.GRAY + "Click to kick, promote or demote " + player.getName() + ".",
                            "",
                            ChatColor.DARK_GRAY + "UUID: " + player.getUniqueId().toString(),
                            "",
                            ChatColor.GRAY + "Role: " + ChatColor.GOLD + ChatGroup.getRole(player, chatGroup).getName()
                    ))
                    .build());
            currentSlot++;
        }
        sender.openInventory(membersMenu);
    }

    public void openMemberSettingsMenu(VLPlayer sender, VLOfflinePlayer target) {
        Inventory memberSettingMenu = Bukkit.createInventory(null, 27, ChatColor.RESET + "Edit: " + target.getName());

        memberSettingMenu.setItem(1, new ItemStackBuilder(Material.TNT)
                .name(ChatColor.YELLOW + "Kick: " + target.getFormattedName())
                .lore(Collections.singletonList(
                        ChatColor.GRAY + "Click to kick " + target.getName() + "."
                ))
                .build());
        memberSettingMenu.setItem(4, new ItemStackBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.YELLOW + "Promote: " + target.getFormattedName())
                .lore(Collections.singletonList(
                        ChatColor.GRAY + "Click to promote: " + target.getName() + "."
                ))
                .build());
        memberSettingMenu.setItem(7, new ItemStackBuilder(Material.COAL)
                .name(ChatColor.YELLOW + "Demote: " + target.getFormattedName())
                .lore(Collections.singletonList(
                        ChatColor.GRAY + "Click demote " + target.getName() + "."
                ))
                .build());
        memberSettingMenu.setItem(22, new ItemStackBuilder(Material.BOOK)
                .name(ChatColor.YELLOW + "Go Back... ")
                .build());
        sender.openInventory(memberSettingMenu);
    }
}
