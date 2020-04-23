package net.vaultmc.vaultcore.settings;

import lombok.Getter;

public enum Settings {

    ALLOW_MESSAGES("Allow Messages", "msg", true),
    CYCLE("Cycle Hotbar", "cycle", true),
    ALLOW_TPA("Allow TPA's", "tpa", true),
    AUTO_TPA("Auto Accept TPA's", "autotpa", true),
    MINIMAL_MESSAGES("Minimal Messages", "minimal_messages", true),
    MINIMAL_CAPS("Minimal Caps", "minimal_caps", true),
    CR_ITEM_DROPS("Item Drops", "item_drops", true),
    GRAMMARLY("Grammarly", "grammarly", true);

    @Getter
    public String name;
    @Getter
    public String vc_name;
    @Getter
    public boolean toggleable;

    Settings(String name, String vc_name, boolean toggleable) {
        this.name = name;
        this.vc_name = vc_name;
        this.toggleable = toggleable;
    }
}
