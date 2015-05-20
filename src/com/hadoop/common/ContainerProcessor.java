package com.hadoop.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

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

	public SlaverThread(String url, Application app) {
		this.url = url;
		this.app = app;
		preRunning = new HashSet<String>();
		nowRunning = new HashSet<String>();
		containers = new HashMap<String, Container>();
	}

	public void run() {
		boolean flag = true;
		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}
		while (flag) {
			try {
				Document doc = Jsoup.connect(url).get();
				Elements links = doc.select("a[href*=" + app.getId() + "]");
				flag = false;
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");// 设置日期格式
				String now = df.format(new Date());// new Date()为获取当前系统时间
				for (Element link : links) {
					if (link.text().contains(app.getId())) {
						flag = true;
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
				}
				preRunning = nowRunning;
				nowRunning = new HashSet<String>();
				Thread.sleep(1000);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				//System.out.println(url + e.getMessage());
				e.printStackTrace();
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

		}
		// output
		System.out.println("Slaver: " + url);
		for (Entry<String, Container> cont : containers.entrySet()) {
			System.out.println(cont.getValue().toString());
		}
	}
}

public class ContainerProcessor {
	private Application app;
	private ArrayList<String> slavers;

	public ContainerProcessor(Application app, ArrayList<String> slavers) {
		this.app = app;
		this.slavers = slavers;
	}

	public void run() {
		for (String url : slavers) {
			SlaverThread slaverThread = new SlaverThread(url + HadoopAttack.allContainerFix, app);
			slaverThread.start();
			try {
				slaverThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(app.getName() + " Done!");
	}
}
