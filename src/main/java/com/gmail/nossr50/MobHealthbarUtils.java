package com.gmail.nossr50;

/*
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.MobHealthDisplayUpdaterTask;
import com.gmail.nossr50.util.player.UserManager;
*/
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

public final class MobHealthbarUtils {
    private MobHealthbarUtils() {};

//    /**
//     * Fix issues with death messages caused by the mob healthbars.
//     *
//     * @param deathMessage The original death message
//     * @param player The player who died
//     * @return the fixed death message
//     */
//    public static String fixDeathMessage(String deathMessage, Player player) {
//        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
//        String replaceString = lastDamageCause instanceof EntityDamageByEntityEvent ? StringUtils.getPrettyEntityTypeString(((EntityDamageByEntityEvent) lastDamageCause).getDamager().getType()) : "a mob";
//
//        return deathMessage.replaceAll("(?:\u00A7(?:[0-9A-FK-ORa-fk-or]){1}(?:[\u2764\u25A0]{1,10})){1,2}", replaceString);
//    }

    /**
     * Handle the creation of mob healthbars.
     *
     * @param target the targetted entity
     * @param damage damage done by the attack triggering this
     */
    public static void handleMobHealthbars(LivingEntity target, double damage) {

        if (isBoss(target)) {
            return;
        }

        /*
        String oldName = target.getCustomName();

        if (oldName == null) {
            oldName = "";
        }

        boolean oldNameVisible = target.isCustomNameVisible();
        */
        String newName = createHealthDisplay(target, damage);

        target.setCustomName(newName);
        target.setCustomNameVisible(true);

        /*
        int displayTime = Config.getInstance().getMobHealthbarTime();

        if (displayTime != -1) {
            boolean updateName = !ChatColor.stripColor(oldName).equalsIgnoreCase(ChatColor.stripColor(newName));

            if (updateName) {
                target.setMetadata(mcMMO.customNameKey, new FixedMetadataValue(mcMMO.p, oldName));
                target.setMetadata(mcMMO.customVisibleKey, new FixedMetadataValue(mcMMO.p, oldNameVisible));
            }
            else if (!target.hasMetadata(mcMMO.customNameKey)) {
                target.setMetadata(mcMMO.customNameKey, new FixedMetadataValue(mcMMO.p, ""));
                target.setMetadata(mcMMO.customVisibleKey, new FixedMetadataValue(mcMMO.p, false));
            }

            new MobHealthDisplayUpdaterTask(target).runTaskLater(mcMMO.p, displayTime * Misc.TICK_CONVERSION_FACTOR); // Clear health display after 3 seconds
        }
        */
    }

    private static String createHealthDisplay(LivingEntity entity, double damage) {
        double maxHealth = entity.getMaxHealth();
        double currentHealth = Math.max(entity.getHealth() - damage, 0);
        double healthPercentage = (currentHealth / maxHealth) * 100.0D;

        int fullDisplay;
        ChatColor color = ChatColor.BLACK;
        String symbol;

                fullDisplay = Math.min((int) (maxHealth / 2), 20);
                symbol = "\u2764"; // ❤

                if (healthPercentage >= 85) {
                    color = ChatColor.DARK_GREEN;
                }
                else if (healthPercentage >= 70) {
                    color = ChatColor.GREEN;
                }
                else if (healthPercentage >= 55) {
                    color = ChatColor.GOLD;
                }
                else if (healthPercentage >= 40) {
                    color = ChatColor.YELLOW;
                }
                else if (healthPercentage >= 25) {
                    color = ChatColor.RED;
                }
                else if (healthPercentage >= 0) {
                    color = ChatColor.DARK_RED;
                }

        int coloredDisplay = (int) Math.ceil(fullDisplay * (healthPercentage / 100.0D));
        int grayDisplay = fullDisplay - coloredDisplay;

        String healthbar = color + "";

        for (int i = 0; i < coloredDisplay; i++) {
            healthbar += symbol;
        }

        healthbar += ChatColor.GRAY;

        for (int i = 0; i < grayDisplay; i++) {
            healthbar += symbol;
        }

        healthbar = "\u00A7r" + healthbar;

        return healthbar;
    }

    /**
     * Check if a given LivingEntity is a boss.
     *
     * @param livingEntity The {@link LivingEntity} of the livingEntity to check
     * @return true if the livingEntity is a boss, false otherwise
     */
    private static boolean isBoss(LivingEntity livingEntity) {
        switch (livingEntity.getType()) {
            case ENDER_DRAGON:
            case WITHER:
                return true;

            default:
                return false;
        }
    }
}
