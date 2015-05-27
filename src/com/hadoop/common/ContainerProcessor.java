package com.hadoop.common;

import java.io.IOException;
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
	private Application app;
	private HashSet<String> preRunning;
	private HashSet<String> nowRunning;
	private HashMap<String, Container> containers;

	public volatile boolean exit = false;

	public SlaverThread(String url, Application app) {
		this.url = url;
		this.app = app;
		preRunning = new HashSet<String>();
		nowRunning = new HashSet<String>();
		containers = new HashMap<String, Container>();
	}

	public void run() {

		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}
		while (!exit) {
			try {
				Document doc = Jsoup.connect(url).get();
				Elements links = doc.select("a[href*=" + app.getId() + "]");

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");// 设置日期格式
				String now = df.format(new Date());// new Date()为获取当前系统时间
				 //System.out.println(url + "  " + links.size() +" " + app.getId());
				for (Element link : links) {
					if (link.text().contains(app.getId())) {

						if (!preRunning.contains(link.text())) {
							// preRunning.add(link.text()); // add new container
							containers.put(link.text(),
									new Container(app, link.text(), now, now));
						} else {
							preRunning.remove(link.text());
						}
						nowRunning.add(link.text());
					}
				}
				// update end container
				for (String cid : preRunning) {
					Container cont = containers.get(cid);
					cont.setEnd(now);
					System.out.println(cont.toString());
				}
				preRunning = nowRunning;
				nowRunning = new HashSet<String>();
				Thread.sleep(1000);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(url + e.getMessage());
				// e.printStackTrace();
				continue;
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

	public void run() {
		for (String url : slavers) {
			SlaverThread slaverThread = new SlaverThread(url
					+ HadoopAttack.allContainerFix, app);
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
