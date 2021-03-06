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

package net.vaultmc.vaultcore.survival.home;

import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.commands.wrappers.WrappedSuggestion;
import net.vaultmc.vaultloader.utils.player.VLPlayer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RootCommand(
        literal = "delhome",
        description = "Remove a home you have created."
)
@Permission(Permissions.Home)
@PlayerOnly
public class DelHomeCommand extends CommandExecutor {
    public DelHomeCommand() {
        register("delHome", Collections.emptyList());
        register("delHomeHome", Collections.singletonList(Arguments.createArgument("name", Arguments.word())));
    }

    @TabCompleter(
            subCommand = "delHomeHome",
            argument = "name"
    )
    public List<WrappedSuggestion> suggestions(VLPlayer sender, String remaining) {
        return sender.getPlayerData().getKeys().stream().filter(p -> p.startsWith("home.")).map(s ->
                new WrappedSuggestion(s.replaceFirst("home.", ""))).collect(Collectors.toList());
    }

    @SubCommand("delHome")
    public void delHome(VLPlayer sender) {
        delHomeHome(sender, "home");
    }

    @SubCommand("delHomeHome")
    public void delHomeHome(VLPlayer sender, String home) {
        if (sender.getPlayerData().contains("home." + home)) {
            sender.getPlayerData().remove("home." + home);
            sender.sendMessage(VaultLoader.getMessage("home.deleted").replace("{HOME}", home));
        } else {
            sender.sendMessage(VaultLoader.getMessage("home.no-home").replace("{HOME}", home));
        }
    }
}
