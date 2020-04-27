package net.vaultmc.vaultcore.chat.groups;

import net.milkbowl.vault.chat.Chat;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultcore.VaultCore;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.player.VLOfflinePlayer;
import net.vaultmc.vaultloader.utils.player.VLPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CGSettingsInvListener implements Listener {

    HashMap<VLPlayer, VLOfflinePlayer> editors = new HashMap<>();

    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        VLPlayer sender = VLPlayer.getPlayer((Player) e.getWhoClicked());
        ChatGroup chatGroup = ChatGroup.getChatGroup(sender);

        CGSettingsInv cgSettingsInv = new CGSettingsInv();
        String title = e.getView().getTitle();
        if (title.contains("ChatGroup Settings:")) {
            if (e.getSlot() == 11) {
                e.setCancelled(true);
                chatGroup.open = !chatGroup.open;
                sender.closeInventory();
                cgSettingsInv.openMainMenu(sender);
            } else if (e.getSlot() == 15) {
                e.setCancelled(true);
                cgSettingsInv.openMembersMenu(sender);
            }
        } else if (title.contains("ChatGroup Members:")) {
            ItemStack item = e.getCurrentItem();
            e.setCancelled(true);
            VLOfflinePlayer member = null;
            try {
                member = VLOfflinePlayer.getOfflinePlayer(UUID.fromString(item.getItemMeta().getLore().get(2).replace(ChatColor.DARK_GRAY + "UUID: ", "")));
            } catch (NullPointerException ignored) {
                return;
            }
            if (sender == member) {
                sender.closeInventory();
                sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.self_error"));
                return;
            }
            // Check if the sender has permission to edit this player.
            ChatGroupRole senderRole = ChatGroup.getRole(sender, chatGroup);
            ChatGroupRole targetRole = ChatGroup.getRole(member, chatGroup);
            // Owner editing admin, or admin editing member - ALLOWED
            if (senderRole.getLevel() > targetRole.getLevel()) {
                cgSettingsInv.openMemberSettingsMenu(sender, member);
                editors.put(sender, member);
            }
            // Admin editing Admin, or Admin editing Owner - NOT ALLOWED
            else if (senderRole.getLevel() == targetRole.getLevel() || targetRole.getLevel() > senderRole.getLevel()) {
                sender.closeInventory();
                sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.permission_error"));
            }
        } else if (title.contains("Edit:")) {
            VLOfflinePlayer target = editors.get(sender);
            e.setCancelled(true);
            if (e.getSlot() == 22) {
                cgSettingsInv.openMembersMenu(sender);
            } else if (e.getSlot() == 1) {
                // Kick
                ChatGroup.removeFromGroup(ChatGroup.getChatGroup(target), target);
                sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.kick.success"), target.getFormattedName()));
                target.sendOrScheduleMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.kick.kicked_target")));
            } else if (e.getSlot() == 4) {
                // Promote
                if (ChatGroup.makeAdmin(ChatGroup.getChatGroup(target), target)) {
                    sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.roles.promote"), target.getFormattedName()));
                    target.sendOrScheduleMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.roles.promoted_target"), sender.getFormattedName()));
                } else {
                    sender.closeInventory();
                    sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.roles.already_role"), target.getFormattedName(), "admin"));
                }
            } else if (e.getSlot() == 7) {
                // Demote
                if (ChatGroup.makeMember(ChatGroup.getChatGroup(target), target)) {
                    sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.roles.demote"), target.getFormattedName()));
                    target.sendOrScheduleMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.roles.demoted_target"), sender.getFormattedName()));
                } else {
                    sender.closeInventory();
                    sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.chatgroups.roles.already_role"), target.getFormattedName(), "member"));
                }
            }
        }
    }
}
