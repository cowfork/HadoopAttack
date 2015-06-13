package com.hadoop.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hadoop.main.HadoopAttack;

/**
 * 
 * @author wanxiang.wx
 *
 */
class SlaverThread extends Thread {
	private String url;
	private int loopTime;
	private Application app;
	private HashSet<String> preRunning;
	private HashSet<String> nowRunning;
	private HashMap<String, Container> containers;

	public volatile boolean exit = false;

	public SlaverThread(String url, int loopTime, Application app) {
		this.url = url;
		this.app = app;
		this.loopTime = loopTime;
		preRunning = new HashSet<String>();
		nowRunning = new HashSet<String>();
		containers = new HashMap<String, Container>();
	}

	public void run() {
		String node = url;
		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}
		while (true) {
			try {
				String html = Tools.getHtml(url, "UTF-8");
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");// 设置日期格式
				String now = df.format(new Date());// new Date()为获取当前系统时间
				Document doc = Jsoup.parse(html);
				Element table = doc.getElementById("containers");
				Elements tabletr = table.select("tr");
				for (Element trinfo : tabletr) {
					Elements tds = trinfo.select("td");
					if (tds.size() == 3
							&& tds.get(1).html().contains("RUNNING")) {
						Elements links = tds.get(0).select("a[href]");
						if (links.size() == 1
								&& links.first().text().contains(app.getId())) {
							String link = links.first().text();
							if (!preRunning.contains(link)) {
								// preRunning.add(link.text()); // add new
								// container
								containers.put(link, new Container(app, link,
										now, now, node));
							} else {
								preRunning.remove(link);
							}
							nowRunning.add(link);
						}

					}
				}
				// update end container
				for (String cid : preRunning) {
					Container cont = containers.get(cid);
					cont.setEnd(now);
					System.out.println(cont.toString());
				}
				if (exit) {
					for (String cid : nowRunning) {
						Container cont = containers.get(cid);
						cont.setEnd(now);
						System.out.println(cont.toString());
					}
					break;
				}
				preRunning = nowRunning;
				nowRunning = new HashSet<String>();
				Thread.sleep(loopTime);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println(url + e.getMessage());
				// e.printStackTrace();
				continue;
			}

		}
	}
}

public class ContainerProcessor {
	private Application app;
	private ArrayList<String> slavers;
	private ArrayList<SlaverThread> threadList;

	public ContainerProcessor(Application app, ArrayList<String> slavers) {
		this.app = app;
		this.slavers = slavers;
		this.threadList = new ArrayList<SlaverThread>();
	}

	public void run(int loopTime) {
		for (String url : slavers) {
			SlaverThread slaverThread = new SlaverThread(url
					+ HadoopAttack.allContainerFix, loopTime, app);
			threadList.add(slaverThread);
			slaverThread.start();
		}
	}

	public void kill() {
		for (SlaverThread thread : threadList) {
			thread.exit = true;
		}
	}
}
