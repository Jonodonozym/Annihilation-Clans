package org.guildcraft.annihilation.clans.util;

import org.guildcraft.annihilation.clans.Clans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class SQLArray {

    public static List<String> getFromString(String str) {
        List<String> list = new ArrayList<>();
        Arrays.stream(str.split(",")).filter(s -> !s.equals("")).forEach(list::add);
        return list;
    }

    public static List<String> getFromStringLS(String str) {
        List<String> list = new ArrayList<>();
        Arrays.stream(str.split(",")).filter(s -> !s.equals("")).map(String::toLowerCase).forEach(list::add);
        return list;
    }

    public static String convertToString(List<String> list) {
        return String.join(",", list);
    }


    public static String convertToStringView(List<String> list) {
        List<String> list2 = new ArrayList<>();

        for (String s : list)
            list2.add(Clans.getInstance().getClansManager().getRealPlayerName(s));

        return String.join(", ", list2);
    }
}