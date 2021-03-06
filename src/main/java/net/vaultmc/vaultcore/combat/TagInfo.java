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

package net.vaultmc.vaultcore.combat;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@AllArgsConstructor
@Data
public class TagInfo {
    private UUID attacker;
    private BukkitTask task;
}
