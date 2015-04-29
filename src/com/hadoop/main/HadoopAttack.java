package com.hadoop.main;

import java.util.List;

import com.hadoop.common.Application;
import com.hadoop.common.Tools;

public class HadoopAttack {
	private static String masterUrl = "http://1.brainoverflow.sinaapp.com/master.html";
	private static String clusterUrl = "http://1.brainoverflow.sinaapp.com/cluster.html";
	private static String encoding = "UTF-8";
	private static String user = "hadoop";
	public static void main(String[] args) {
		if (args.length >= 1) {
			user = args[0];
		}
		String masterHtml = Tools.getHtml(masterUrl, encoding);
		List<Application> appList = Tools.getMasterData(masterHtml);
		for (Application app : appList) {
			if (app.getUser().equals(user)) {
				List<String> slavers = Tools.getSlaverList(Tools.getHtml(
						clusterUrl, encoding));
				System.out.println(app.getId());
				for (String s : slavers) {
					System.out.println(s);
				}
			}
		}

	}
}
