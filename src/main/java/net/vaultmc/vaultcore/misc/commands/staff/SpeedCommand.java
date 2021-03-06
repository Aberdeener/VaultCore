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

package net.vaultmc.vaultcore.misc.commands.staff;

import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.commands.wrappers.WrappedSuggestion;
import net.vaultmc.vaultloader.utils.player.VLCommandSender;
import net.vaultmc.vaultloader.utils.player.VLPlayer;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RootCommand(literal = "speed", description = "Change the speed of yourself or another player.")
@Permission(Permissions.SpeedCommand)
public class SpeedCommand extends CommandExecutor {

    List<String> movements = Arrays.asList("fly", "walk");

    public SpeedCommand() {
        register("speedFind", Collections.emptyList());
        register("speedCurrent", Collections.singletonList(Arguments.createArgument("speed", Arguments.integerArgument(1, 10))));
        register("speedSelf", Arrays.asList(Arguments.createArgument("speed", Arguments.integerArgument(1, 10)),
                Arguments.createArgument("movement", Arguments.word())));
        register("speedOther",
                Arrays.asList(Arguments.createArgument("target", Arguments.playerArgument()),
                        Arguments.createArgument("speed", Arguments.integerArgument(1, 10)),
                        Arguments.createArgument("movement", Arguments.word())));
    }

    @SubCommand("speedFind")
    @PlayerOnly
    public static void speedFind(VLPlayer sender) {
        if (sender.isFlying()) {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.speed.find_self"), "flying", Math.round(Bukkit.getPlayer(sender.getUniqueId()).getFlySpeed() * 10)));
        } else {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.speed.find_self"), "walking", Math.round(Bukkit.getPlayer(sender.getUniqueId()).getWalkSpeed() * 10)));
        }
    }

    @SubCommand("speedCurrent")
    @PlayerOnly
    public void speedCurrent(VLPlayer sender, int speed) {
        setSpeed(sender, sender, speed, sender.isFlying() ? "fly" : "walk");
    }

    @SubCommand("speedSelf")
    @PlayerOnly
    public void speedSelf(VLPlayer sender, int speed, String movement) {
        setSpeed(sender, sender, speed, movement);
    }

    @SubCommand("speedOther")
    @Permission(Permissions.SpeedCommandOther)
    public void speedOther(VLCommandSender sender, VLPlayer target, int speed, String movement) {
        setSpeed(sender, target, speed, movement);
    }

    private void setSpeed(VLCommandSender sender, VLPlayer target, int speed, String movement) {
        float newSpeed;
        if (speed / 10D > 1.0 || speed / 10D < -1.0) {
            sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.speed.invalid_speed"));
            return;
        } else {
            newSpeed = (float) speed / 10;
        }
        switch (movement.toLowerCase()) {
            case "walk":
                if (sender == target) {
                    ((VLPlayer) sender).getPlayer().setWalkSpeed(newSpeed);
                    sender.sendMessage(Utilities.formatMessage(
                            VaultLoader.getMessage("vaultcore.commands.speed.sender_set_to"), "walk", speed));
                } else {
                    target.getPlayer().setWalkSpeed(speed);
                    sender.sendMessage(
                            Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.speed.sender_target"),
                                    target.getFormattedName(), "walk", speed));
                    target.sendMessage(
                            Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.speed.target_set_to"),
                                    "walk", speed, sender.getFormattedName()));
                }
                break;
            case "fly":
                if (sender == target) {
                    ((VLPlayer) sender).getPlayer().setFlySpeed(newSpeed);
                    sender.sendMessage(Utilities.formatMessage(
                            VaultLoader.getMessage("vaultcore.commands.speed.sender_set_to"), "fly", speed));
                } else {
                    target.getPlayer().setWalkSpeed(newSpeed);
                    sender.sendMessage(
                            Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.speed.sender_target"),
                                    target.getFormattedName(), "fly", speed));
                    target.sendMessage(
                            Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.speed.target_set_to"),
                                    "fly", speed, sender.getFormattedName()));
                }
                break;
            default:
                sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.speed.invalid_movement"));
                break;
        }
    }

    @TabCompleter(
            subCommand = "speedCurrent|speedSelf|speedOther",
            argument = "movement"
    )
    public List<WrappedSuggestion> suggestWarp(VLPlayer sender, String remaining) {
        return movements.stream().map(WrappedSuggestion::new).collect(Collectors.toList());
    }
}
