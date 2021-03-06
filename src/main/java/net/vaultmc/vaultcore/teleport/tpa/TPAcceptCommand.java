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

package net.vaultmc.vaultcore.teleport.tpa;

import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.player.VLPlayer;

import java.util.Collections;

@RootCommand(literal = "tpaccept", description = "Accept a teleport request.")
@Permission(Permissions.TPACommand)
@PlayerOnly
public class TPAcceptCommand extends CommandExecutor {

    public TPAcceptCommand() {
        unregisterExisting();
        register("tpaccept", Collections.emptyList());
    }

    @SubCommand("tpaccept")
    public void tpaccept(VLPlayer target) {
        if (TPACommand.getTpaRequests().containsKey(target)) {
            VLPlayer requester = TPACommand.getTpaRequests().get(target);
            requester.teleport(target.getLocation());
            TPACommand.getTpaRequests().remove(target);
            requester.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.tpa.requests.response_sender"), target.getFormattedName(), "accepted"));
            target.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.tpa.requests.response_target"), "accepted", requester.getFormattedName()));

        } else if (TPAHereCommand.getTpaRequestsHere().containsKey(target)) {
            VLPlayer requester = TPAHereCommand.getTpaRequestsHere().get(target);
            target.teleport(requester.getLocation());
            TPAHereCommand.getTpaRequestsHere().remove(target);
            requester.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.tpa.requests.response_sender"), target.getFormattedName(), "accepted"));
            target.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.tpa.requests.response_target"), "accepted", requester.getFormattedName()));

        } else target.sendMessage(VaultLoader.getMessage("vaultcore.commands.tpa.requests.no_request_error"));
    }
}