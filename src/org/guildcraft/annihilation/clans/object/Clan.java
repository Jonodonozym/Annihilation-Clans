package org.guildcraft.annihilation.clans.object;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arjen on 20/04/2016.
 */
public class Clan {

    public static Map<String, Clan> clans = new HashMap<>();
    @Getter
    private String name;
    @Getter
    private String owner;
    @Getter
    private List<String> officers;
    @Getter
    private List<String> members;
    @Getter
    private String tag;
    @Getter
    private String motd;
    @Getter
    @Setter
    private int coins;
    @Getter
    @Setter
    private int points;
    @Getter
    private List<String> invited;
    @Getter
    private Long updated;


    public Clan(String name, String owner, List<String> officers, List<String> members, String tag,
                String motd, int coins, int points, List<String> invited, Long updated) {
        this.name = name;
        this.owner = owner;
        this.officers = officers;
        this.members = members;
        this.tag = tag;
        this.motd = motd;
        this.coins = coins;
        this.points = points;
        this.invited = invited;
        this.updated = updated;
    }

    public static void addClan(String name, String owner, List<String> officers, List<String> members,
                               String tag, String motd, int coins, int points, List<String> invited, Long updated) {
        clans.put(name, new Clan(name, owner, officers, members, tag, motd, coins, points, invited, updated));
    }

    public static Clan getClan(String name) {
        return clans.getOrDefault(name, null);
    }
}
