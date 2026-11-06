package fr.golemhealth;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Set;

public final class GolemHealthPlugin extends JavaPlugin implements Listener {

    private NamespacedKey markKey;
    private double health;
    private boolean debug;
    private boolean applyToExisting;
    private final Set<SpawnReason> excluded = EnumSet.noneOf(SpawnReason.class);

    @Override
    public void onEnable() {
        markKey = new NamespacedKey(this, "processed");
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("GolemHealth actif : golems de fer à " + health + " PV, raisons exclues = " + excluded);
    }

    private void loadSettings() {
        reloadConfig();
        health = Math.max(1.0, getConfig().getDouble("health", 20.0));
        debug = getConfig().getBoolean("debug", false);
        applyToExisting = getConfig().getBoolean("apply-to-existing", false);
        excluded.clear();
        for (String name : getConfig().getStringList("excluded-spawn-reasons")) {
            try {
                excluded.add(SpawnReason.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                getLogger().warning("SpawnReason inconnue ignorée : " + name);
            }
        }
    }

    /** fresh = nouveau golem (pleine vie), sinon on ne dépasse pas la vie actuelle. */
    private void apply(IronGolem golem, boolean fresh) {
        AttributeInstance attr = golem.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return;
        attr.setBaseValue(health);
        double max = attr.getValue();
        golem.setHealth(fresh ? max : Math.min(golem.getHealth(), max));
        golem.getPersistentDataContainer().set(markKey, PersistentDataType.BYTE, (byte) 1);
        if (debug) {
            getLogger().info("[debug] Golem " + golem.getUniqueId() + " -> " + golem.getHealth() + "/" + max);
        }
    }

    private void applyWithRetry(IronGolem golem) {
        apply(golem, true);
        // Les plugins de stack (RoseStacker) retouchent parfois l'entité juste après : on réapplique.
        getServer().getScheduler().runTask(this, () -> {
            if (golem.isValid()) apply(golem, true);
        });
        getServer().getScheduler().runTaskLater(this, () -> {
            if (golem.isValid()) apply(golem, true);
        }, 10L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof IronGolem golem)) return;

        SpawnReason reason = event.getSpawnReason();
        boolean skip = excluded.contains(reason);
        if (debug) {
            getLogger().info("[debug] CreatureSpawnEvent golem, raison = " + reason + (skip ? " (exclu)" : " (traité)"));
        }
        if (skip) return;

        applyWithRetry(golem);
    }

    // Filet de sécurité : golems ajoutés au monde sans passer par l'event ci-dessus
    // (œufs gérés par un plugin de stack, dispensers, chargement de chunk...).
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdd(EntityAddToWorldEvent event) {
        if (!(event.getEntity() instanceof IronGolem golem)) return;
        if (golem.getPersistentDataContainer().has(markKey, PersistentDataType.BYTE)) return;

        boolean isNew = golem.getTicksLived() < 20;
        if (!isNew && !applyToExisting) return;

        SpawnReason reason = golem.getEntitySpawnReason();
        boolean skip = excluded.contains(reason);
        if (debug) {
            getLogger().info("[debug] EntityAddToWorldEvent golem, raison = " + reason
                    + ", nouveau = " + isNew + (skip ? " (exclu)" : " (traité)"));
        }
        if (skip) return;

        if (isNew) applyWithRetry(golem); else apply(golem, false);
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
