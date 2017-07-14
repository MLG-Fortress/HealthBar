package com.gmail.filoghost.healthbar;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.gmail.filoghost.healthbar.api.BarHideEvent;
import com.gmail.filoghost.healthbar.utils.PlayerBarUtils;
import com.gmail.filoghost.healthbar.utils.Utils;

public class PlayerBar {

    private final static Plugin INSTANCE;
    private static Scoreboard sb;

    private static boolean playerEnabled;
    private static boolean textMode;
    private static boolean useBelow;
    private static boolean belowUseProportion;
    private static int belowNameProportion;
    private static boolean belowUseRawAmountOfHearts;
    private static Objective belowObj;

    private static boolean useCustomBar;

    static {
        INSTANCE = Main.plugin;
        sb = INSTANCE.getServer().getScoreboardManager().getMainScoreboard();
    }

    /* enforce non-instantiability with a private constructor */
    private PlayerBar() {
        throw new RuntimeException();
    }

    public static void setupBelow() {

        //remove previous objectives under the name
        removeBelowObj();

        if (playerEnabled && useBelow) {
            //create the objective
            belowObj = sb.registerNewObjective("healthbarbelow", "dummy");
            belowObj.setDisplayName(Utils.replaceSymbols(INSTANCE.getConfig().getString(Configuration.Nodes.PLAYERS_BELOW_TEXT.getNode())));
            belowObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

    }

    public static void removeBelowObj() {
        if (sb.getObjective(DisplaySlot.BELOW_NAME) != null) {
            sb.getObjective(DisplaySlot.BELOW_NAME).unregister();
        }
        if (sb.getObjective("healthbarbelow") != null) {
            sb.getObjective("healthbarbelow").unregister();
        }
    }

    public static boolean hasHealthDisplayed(Player player) {
        Team team = sb.getPlayerTeam((OfflinePlayer) player);
        if (team == null) {
            return false;
        }
        if (sb.getPlayerTeam((OfflinePlayer) player).getName().contains("hbr")) {
            return true;
        }
        return false;
    }

    public static void hideHealthBar(Player player) {
        Team team = sb.getTeam("hbr0");
        if (team == null) {
            team = sb.registerNewTeam("hbr0");
            team.setCanSeeFriendlyInvisibles(false);
        }
        OfflinePlayer offPlayer = (OfflinePlayer) player;
        team.addPlayer(offPlayer);

        //api - call the custom event after hiding the bar
        INSTANCE.getServer().getPluginManager().callEvent(new BarHideEvent(offPlayer));
    }

    public static void updateHealthBelow(final Player player) {
        if (useBelow && playerEnabled) {
            int score = 0;

            //higher priority
            if (belowUseRawAmountOfHearts) {
                score = getRawAmountOfHearts(player);
            } else if (belowUseProportion) {
                score = Utils.roundUpPositive((player.getHealth()) * ((double) belowNameProportion) / (player.getMaxHealth()));
            } else {
                score = Utils.roundUpPositive(player.getHealth());
            }

            belowObj.getScore(player).setScore(score);
        }
    }

    //Used in PlayerQuitEvent
    public static void removeTeam(Player player)
    {
        OfflinePlayer op = player;
        if (op == null) //shouldn't happen
            return;

        Team team = sb.getTeam(op.getName());
        if (team == null)
            team.unregister();
    }

    public static void setHealthSuffix(Player player, double health, double max) {

        OfflinePlayer op = (OfflinePlayer) player;
        if (op == null)
            return;

        if (useCustomBar || (!textMode))
        {
            Team team = sb.getTeam(op.getName());
            if (team == null)
            {
                team = sb.registerNewTeam(op.getName());
            }

            //int healthOn10 = Utils.roundUpPositiveWithMax(((health * 10.0) / max), 10);

            String color = getColor(health, max);

            StringBuilder healthbarSuffix = new StringBuilder(" " + color);

            int healthInt = Utils.roundUpPositive(health / 5);
            switch (healthInt)
            {
                case 0:
                case 1:
                case 2:
                    healthbarSuffix.append("\u258c"); //▌
                    break;
                case 3:
                    healthbarSuffix.append("\u258c\u258c");
                    break;
                case 4:
                    healthbarSuffix.append("\u258c\u258c\u258c");
                    break;
                case 5:
                    healthbarSuffix.append("\u258c\u258c\u258c\u258c");
                    break;
                case 6:
                    healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c");
                    break;
                case 7:
                    healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c\u258c");
                    break;
                case 8:
                    healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c\u258c\u258c");
                    break;
                case 9:
                    healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c");
                    break;
                case 10:
                default:
                    healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c");
                    break;
            }


            if (INSTANCE.getServer().getPluginManager().getPlugin("AborptionShields") != null)
            {
                ShieldManager shieldManager = ((AbsorptionShields)INSTANCE.getServer().getPluginManager().getPlugin("AborptionShields")).getShieldManager();
                int shieldHealth = Utils.roundUpPositive(shieldManager.getShieldHealth(player) / 5);
                if (shieldHealth > 0)
                    healthbarSuffix.append("\u00a76");
                switch (shieldHealth)
                {
                    case 0:
                        break;
                    case 1:
                    case 2:
                        healthbarSuffix.append("\u258c"); //▌
                        break;
                    case 3:
                        healthbarSuffix.append("\u258c\u258c");
                        break;
                    case 4:
                        healthbarSuffix.append("\u258c\u258c\u258c");
                        break;
                    case 5:
                        healthbarSuffix.append("\u258c\u258c\u258c\u258c");
                        break;
                    case 6:
                        healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c");
                        break;
                    case 7:
                        healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c\u258c");
                        break;
                    case 8:
                        healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c\u258c\u258c");
                        break;
                    case 9:
                        healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c");
                        break;
                    case 10:
                    default:
                        healthbarSuffix.append("\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c");
                        break;
                }
            }

            healthbarSuffix.setLength(16);
            team.setSuffix(healthbarSuffix.toString());
            team.addPlayer(op);
        } else {

            int intHealth = Utils.roundUpPositive(health);
            int intMax = Utils.roundUpPositive(max);

            String color = getColor(health, max);
            Team team = sb.getTeam("hbr" + intHealth + "-" + intMax);
            if (team == null) {
                team = sb.registerNewTeam("hbr" + intHealth + "-" + intMax);
                team.setSuffix(" - " + color + intHealth + "§7/§a" + intMax);
                team.setCanSeeFriendlyInvisibles(false);
            }
            team.addPlayer(op);
        }
    }

    public static String getColor(double health, double max) {
        double ratio = health / max;
        if (ratio > 0.5) {
            return "\u00a7a"; //more than half health -> green
        }
        if (ratio > 0.25) {
            return "\u00a7e"; //more than quarter health -> yellow
        }
        return "\u00a7c"; //critical health -> red
    }

    public static void loadConfiguration() {

        //remove all teams
        sb = INSTANCE.getServer().getScoreboardManager().getMainScoreboard();
        PlayerBarUtils.removeAllHealthbarTeams(sb);

        FileConfiguration config = INSTANCE.getConfig();

        playerEnabled = config.getBoolean(Configuration.Nodes.PLAYERS_ENABLE.getNode());
        textMode = config.getBoolean(Configuration.Nodes.PLAYERS_AFTER_TEXT_MODE.getNode());
        useCustomBar = config.getBoolean(Configuration.Nodes.PLAYERS_AFTER_USE_CUSTOM.getNode());
        useBelow = config.getBoolean(Configuration.Nodes.PLAYERS_BELOW_ENABLE.getNode());
        belowUseProportion = config.getBoolean(Configuration.Nodes.PLAYERS_BELOW_USE_PROPORTION.getNode());
        belowNameProportion = config.getInt(Configuration.Nodes.PLAYERS_BELOW_PROPORTIONAL_TO.getNode());
        belowUseRawAmountOfHearts = config.getBoolean(Configuration.Nodes.PLAYERS_BELOW_DISPLAY_RAW_HEARTS.getNode());

        setupBelow();

        if (useCustomBar) {
            PlayerBarUtils.create10CustomTeams(sb, Utils.loadFile("custom-player-bar.yml", INSTANCE));
        }
        //else if (!textMode) {
        //    PlayerBarUtils.create10DefaultTeams(sb, config.getInt(Configuration.Nodes.PLAYERS_AFTER_STYLE.getNode()));
        //}
        //else creates the teams at the moment

        PlayerBarUtils.setAllTeamsInvisibility(sb);
    }

    public static int getRawAmountOfHearts(Player player) {
        if (player.isHealthScaled()) {
            return Utils.round(player.getHealth() * 10.0 / player.getMaxHealth());
        } else {
            return Utils.roundUpPositive(player.getHealth() / 2);
        }
    }

}
