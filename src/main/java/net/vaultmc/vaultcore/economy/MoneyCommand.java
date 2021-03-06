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

package net.vaultmc.vaultcore.economy;

import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.VaultCore;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.player.VLPlayer;

import java.util.Collections;

@RootCommand(
        literal = "money",
        description = "Checks for your current balance."
)
@Permission(Permissions.BalanceCommand)
@PlayerOnly
@Aliases({"b", "balance", "bal"})
public class MoneyCommand extends CommandExecutor {
    public MoneyCommand() {
        register("checkBalance", Collections.emptyList());
    }

    @SubCommand("checkBalance")
    public void execute(VLPlayer sender) {
        sender.sendMessage(VaultLoader.getMessage("economy.balance").replace("{AMOUNT}",
                VaultCore.numberFormat.format(sender.getBalance(sender.getWorld()))));
    }
}
