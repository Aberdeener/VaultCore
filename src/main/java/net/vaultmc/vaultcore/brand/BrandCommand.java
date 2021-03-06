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

package net.vaultmc.vaultcore.brand;

import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.player.VLCommandSender;
import net.vaultmc.vaultloader.utils.player.VLPlayer;

import java.util.Collections;

@RootCommand(
        literal = "brand",
        description = "Checks for the brand reported by the client for the player."
)
@Permission(Permissions.BrandCommand)
public class BrandCommand extends CommandExecutor {
    public BrandCommand() {
        register("checkBrand", Collections.singletonList(
                Arguments.createArgument("player", Arguments.playerArgument())));
    }

    @SubCommand("checkBrand")
    public void execute(VLCommandSender sender, VLPlayer player) {
        if (!BrandListener.getBrands().containsKey(player)) {
            sender.sendMessage(VaultLoader.getMessage("brand-not-ready"));
            return;
        }

        sender.sendMessage(VaultLoader.getMessage("brand").replace("{PLAYER}", player.getFormattedName())
                .replace("{BRAND}", BrandListener.getBrands().get(player)));
    }
}
