package fr.golemhealth;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Set;

public final class GolemHealthPlugin extends JavaPlugin implements Listener {

    private double health;
    private final Set<SpawnReason> reasons = EnumSet.noneOf(SpawnReason.class);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadSettings() {
        reloadConfig();
        health = Math.max(1.0, getConfig().getDouble("health", 20.0));
        reasons.clear();
        for (String name : getConfig().getStringList("spawn-reasons")) {
            try {
                reasons.add(SpawnReason.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                getLogger().warning("SpawnReason inconnue ignorée : " + name);
            }
        }
    }

    // MONITOR + ignoreCancelled : on ne touche que les spawns réellement validés
    // (après RoseStacker et les autres plugins de protection).
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.IRON_GOLEM) return;
        if (!reasons.contains(event.getSpawnReason())) return;

        IronGolem golem = (IronGolem) event.getEntity();
        AttributeInstance attr = golem.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return;

        attr.setBaseValue(health);
        golem.setHealth(Math.min(health, attr.getValue()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            loadSettings();
            sender.sendMessage("§aGolemHealth rechargé (vie : " + health + ")");
            return true;
        }
        return false;
    }
}
