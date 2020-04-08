package net.vaultmc.vaultcore.survival.item;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.vaultmc.vaultcore.survival.item.recipe.Recipe;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class Item {
    @Getter
    private static final Map<String, Item> items = new LinkedHashMap<>();
    @Getter
    private ItemStack item;
    @Getter
    private String id;
    @Getter
    private Recipe recipe;

    public Item(ItemStack item, String id) {
        this(item, id, null);
    }

    public Item(ItemStack item, String id, Recipe recipe) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(item);
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = nmsItem.getOrCreateTag();
        tag.set("Survival.ItemKey", StringTag.valueOf(id));
        nmsItem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsItem);
        this.id = id;
        this.recipe = recipe;
        items.put(id, this);
    }

    public static Item getBy(ItemStack item) {
        Preconditions.checkNotNull(item);
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = nmsItem.getOrCreateTag();
        if (tag.contains("Survival.ItemKey")) {
            return getBy(tag.getString("Survival.ItemKey"));
        }
        return null;
    }

    public static Item getBy(String id) {
        Preconditions.checkNotNull(id);
        return items.get(id);
    }

    public static String getId(ItemStack item) {
        if (item == null) return null;
        return getBy(item) != null ? getBy(item).getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item)) return false;
        return ((Item) o).getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode() * 5;
    }

    @Override
    public String toString() {
        return getClass().getName() + " { id = " + id + " }";
    }
}