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

/**
 * 
 * @author wanxiang.wx
 *
 */
class SlaverThread extends Thread {
	private String url;
	private String appId;
	private HashSet<String> preRunning;
	private HashSet<String> nowRunning;
	private HashMap<String, Container> containers;

	public SlaverThread(String url, String appId) {
		this.url = url;
		this.appId = appId;
		preRunning = new HashSet<String>();
		nowRunning = new HashSet<String>();
		containers = new HashMap<String, Container>();
	}

	public void run() {
		while (true) {
			try {
				Document doc = Jsoup.connect(url).get();
				Elements links = doc.select("a[href^=" + appId + "]");
				if (links.isEmpty())
					return;
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");// 设置日期格式
				String now = df.format(new Date());// new Date()为获取当前系统时间
				for (Element link : links) {
					if (link.text().startsWith(appId)) {
						if (!preRunning.contains(link.text())) {
							preRunning.add(link.text()); // add new container
							containers.put(link.text(), new Container(appId,
									now, now));
						} else {
							preRunning.remove(link.text());
						}
						nowRunning.add(link.text());
					}
				}
				//update end container
				for(String cid : preRunning){
					Container cont = containers.get(cid);
					cont.setEnd(now);
				}
				preRunning = nowRunning;
				nowRunning = new HashSet<String>();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
}
