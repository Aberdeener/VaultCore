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
import net.vaultmc.vaultloader.utils.player.VLCommandSender;
import net.vaultmc.vaultloader.utils.player.VLPlayer;

import java.util.Collection;
import java.util.Collections;

@RootCommand(literal = "heal", description = "Heal a player.")
@Permission(Permissions.HealCommand)
public class HealCommand extends CommandExecutor {
    public HealCommand() {
        register("healSelf", Collections.emptyList());
        register("healOthers",
                Collections.singletonList(Arguments.createArgument("target", Arguments.playersArgument())));
    }

    @SubCommand("healSelf")
    @PlayerOnly
    public void healSelf(VLPlayer player) {
        player.sendMessage(
                Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.feed_heal.self"), "healed"));
        player.heal();
    }

    @SubCommand("healOthers")
    @Permission(Permissions.HealCommandOther)
    public void healOthers(VLCommandSender sender, Collection<VLPlayer> targets) {
        for (VLPlayer target : targets) {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.feed_heal.other"),
                    "healed", target.getFormattedName()));
            target.heal();
            target.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.feed_heal.receiver"),
                    "healed", sender.getFormattedName()));
        }
    }
}