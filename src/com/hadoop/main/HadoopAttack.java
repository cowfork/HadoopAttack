package com.hadoop.main;

import java.util.List;

import com.hadoop.common.Application;
import com.hadoop.common.Tools;

public class HadoopAttack {
	private static String masterUrl = "http://1.brainoverflow.sinaapp.com/master.html";
	private static String encoding = "UTF-8";

	public static void main(String[] args) {
		String user = "hadoop";// 默认
		if (args.length >= 1) {
			user = args[0];
		}
		String masterHtml = Tools.getHtml(masterUrl, encoding);
		List<Application> appList = Tools.getMasterData(masterHtml);
		for (Application app : appList) {
			if (app.getUser().equals(user)) {
				System.out.println(app.getId());
			}
		}

	}
}
