package net.vaultmc.vaultcore.commands.staff;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultcore.VaultCore;
import net.vaultmc.vaultutils.utils.commands.experimental.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@RootCommand(
        literal = "teleport",
        description = "Teleport to a player."
)
@Permission(Permissions.TeleportCommand)
@Aliases("tp")
public class TeleportCommand extends CommandExecutor {
    public TeleportCommand() {
        unregisterExisting();
        register("teleportEntitiesHere", Arrays.asList(
                Arguments.createLiteral("here"),
                Arguments.createArgument("entities", Arguments.entitiesArgument())
        ), "vaultcore");
        register("teleportLocation", Collections.singletonList(Arguments.createArgument("location", Arguments.location3DArgument())), "vaultcore");
        register("teleportLocationWorld", Arrays.asList(
                Arguments.createArgument("location", Arguments.location3DArgument()),
                Arguments.createArgument("world", StringArgumentType.word())
        ), "vaultcore");
        register("teleportToEntity", Collections.singletonList(Arguments.createArgument("target", Arguments.entityArgument())), "vaultcore");
        register("teleportEntityTo", Arrays.asList(
                Arguments.createArgument("target", Arguments.entitiesArgument()),
                Arguments.createArgument("location", Arguments.location3DArgument())
        ), "vaultcore");
        register("teleportEntityToWorld", Arrays.asList(
                Arguments.createArgument("target", Arguments.entitiesArgument()),
                Arguments.createArgument("location", Arguments.location3DArgument()),
                Arguments.createArgument("world", StringArgumentType.word())
        ), "vaultcore");
        register("teleportEntityToEntity", Arrays.asList(
                Arguments.createArgument("target", Arguments.entitiesArgument()),
                Arguments.createArgument("to", Arguments.entityArgument())
        ), "vaultcore");
    }

    private String string = ChatColor.translateAlternateColorCodes('&',
            VaultCore.getInstance().getConfig().getString("string"));
    private String variable1 = ChatColor.translateAlternateColorCodes('&',
            VaultCore.getInstance().getConfig().getString("variable-1"));

    @SubCommand("teleportLocation")
    @PlayerOnly
    public void teleportLocation(CommandSender sender, Location location) {
        ((Player) sender).teleport(location);
        sender.sendMessage(string + "Teleported you to " + variable1 + readLocation(location) + string + ".");
    }

    @SubCommand("teleportLocationWorld")
    @PlayerOnly
    public void teleportLocationWorld(CommandSender sender, Location location, String world) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "This world does not exist!");
            return;
        }
        location.setWorld(w);
        ((Player) sender).teleport(location);
        sender.sendMessage(string + "Teleported you to " + variable1 + readLocation(location) + string + ".");
    }

    @SubCommand("teleportToEntity")
    @PlayerOnly
    @Permission(Permissions.TeleportCommandOther)
    public void teleportToEntity(CommandSender sender, Entity target) {
        ((Player) sender).teleport(target);
        sender.sendMessage(string + "Teleported you to " + variable1 + target.getName() + string + ".");
    }

    @SubCommand("teleportEntityTo")
    @Permission(Permissions.TeleportCommandOther)
    public void teleportEntityTo(CommandSender sender, Collection<Entity> entities, Location location) {
        for (Entity entity : entities) {
            if (location.getWorld() == null) location.setWorld(entity.getWorld());
            entity.teleport(location);
            sender.sendMessage(string + "Teleported " + variable1 + entity.getName() + string + " to " + variable1 + readLocation(location) + string + ".");
        }
    }

    @SubCommand("teleportEntityToWorld")
    @Permission(Permissions.TeleportCommandOther)
    public void teleportEntityToWorld(CommandSender sender, Collection<Entity> entities, Location location, String world) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            sender.sendMessage(ChatColor.RED + "This world does not exist!");
            return;
        }
        location.setWorld(w);
        teleportEntityTo(sender, entities, location);
    }

    @SubCommand("teleportEntityToEntity")
    @Permission(Permissions.TeleportCommandOther)
    public void teleportEntityToEntity(CommandSender sender, Collection<Entity> entities, Entity to) {
        for (Entity entity : entities) {
            entity.teleport(to);
            sender.sendMessage(string + "Teleported " + variable1 + entity.getName() + string + " to " + variable1 + to.getName() + string + ".");
        }
    }

    @SubCommand("teleportEntitiesHere")
    @PlayerOnly
    @Permission(Permissions.TeleportCommandHere)
    public void teleportEntitiesHere(CommandSender sender, Collection<Entity> entities) {
        for (Entity entity : entities) {
            entity.teleport((Player) sender);
            sender.sendMessage(string + "Teleported " + variable1 + entity.getName() + string + " to yourself.");
        }
    }

    private static String readLocation(Location location) {
        return location.getWorld().getName() + ", " + Math.round(location.getX() * 100.0) / 100.0 + ", " +
                Math.round(location.getY() * 100.0) / 100.0 + ", " + Math.round(location.getZ() * 100.0) / 100.0;
    }
}
