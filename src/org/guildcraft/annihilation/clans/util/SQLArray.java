package org.guildcraft.annihilation.clans.util;

import org.guildcraft.annihilation.clans.Clans;

import java.util.ArrayList;

/**
 * Created by Arjenpro on 6/01/2017.
 */
public class SQLArray {

	public static ArrayList<String> getFromString(String str) {

		ArrayList<String> list = new ArrayList<>();

		for (String s : str.split(",")) {
			if (!s.equals(""))
				list.add(s);
		}

		return list;
	}

	public static ArrayList<String> getFromStringLS(String str) {

		ArrayList<String> list = new ArrayList<>();

		for (String s : str.split(",")) {
			if (!s.equals(""))
				list.add(s.toLowerCase());
		}

		return list;
	}

	public static String convertToString(ArrayList<String> list) {
		String result = String.join(",", list);
		return result;
	}



	public static String convertToStringView(ArrayList<String> list) {
		ArrayList<String> list2 = new ArrayList<>();
		for (String s : list) {
			list2.add(Clans.instance.getClansManager().getRealPlayerName(s));
		}
		String result = String.join(", ", list2);
		return result;
	}


}
