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

import java.util.Collections;

@RootCommand(literal = "clear", description = "Clear a players inventory.")
@Permission(Permissions.ClearCommand)
@Aliases({"ci", "clearinventory"})
public class ClearCommand extends CommandExecutor {

    public ClearCommand() {
        unregisterExisting();
        register("clearSelf", Collections.emptyList());
        register("clearOthers",
                Collections.singletonList(Arguments.createArgument("target", Arguments.playerArgument())));
    }

    @SubCommand("clearSelf")
    @PlayerOnly
    public static void clearSelf(VLPlayer sender) {
        sender.getInventory().clear();
        sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.clear.self"));
    }

    @SubCommand("clearOthers")
    @Permission(Permissions.ClearCommandOther)
    public static void clearOther(VLCommandSender sender, VLPlayer target) {
        target.getInventory().clear();
        sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.clear.other_sender"), target.getFormattedName()));
        target.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.clear.other_target"), sender.getFormattedName()));
    }
}
