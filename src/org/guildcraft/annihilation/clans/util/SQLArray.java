package org.guildcraft.annihilation.clans.util;

import org.guildcraft.annihilation.clans.manager.ClansDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class SQLArray {

	public static List<String> getFromString(String str) {
		ArrayList<String> list = new ArrayList<>();
		
		for (String s : str.split(","))
			if (!s.equals(""))
				list.add(s);

		return list;
	}

	public static List<String> getFromStringLS(String str) {

		List<String> list = new ArrayList<>();

		for (String s : str.split(","))
			if (!s.equals(""))
				list.add(s.toLowerCase());

		return list;
	}

	public static String convertToString(List<String> list) {
		String result = String.join(",", list);
		return result;
	}

	public static String convertToStringView(List<String> list) {
		List<String> list2 = new ArrayList<>();
		for (String s : list)
			list2.add(ClansDatabase.getInstance().getRealPlayerName(s));
		
		String result = String.join(", ", list2);
		return result;
	}
}
