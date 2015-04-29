package com.hadoop.common;

import java.awt.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

public class Tools {
	public static String getHtml(String pageURL, String encoding) {
		StringBuilder pageHTML = new StringBuilder();
		try {

			URL url = new URL(pageURL);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), encoding));

			String line = null;

			while ((line = br.readLine()) != null) {

				pageHTML.append(line);

				pageHTML.append("\r\n");

			}

			connection.disconnect();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return pageHTML.toString();
	}

	public static ArrayList<Application> getMasterData(String source) {
		int index = source.lastIndexOf("var appsTableData");
		ArrayList<Application> app = new ArrayList<Application>();
		if (-1 == index) {
			return app;
		}
		Stack<String> stack = new Stack<String>();
		StringBuilder table = new StringBuilder();
		while (source.charAt(index) != '[')
			index++;
		for (; index < source.length(); index++) {
			char ch = source.charAt(index);
			if (ch == '[') {
				stack.push("[");
			} else if (ch == ']') {
				stack.pop();
				String[] values = table.toString().split(",");
				if (values.length == 11) {
					int k = values[0].lastIndexOf("application_");
					int q = values[0].indexOf("</a>");
					if (k != -1 && q != -1) {
						//System.out.println(values[0].substring(k, q));
						app.add(new Application(values[0].substring(k, q)
								.trim(), values[1].trim(), values[2].trim(),
								values[3].trim()));
					}
				}
				table.delete(0, table.length());
			} else {
				if (ch == '"')
					continue;
				table.append(ch);
			}
			if (stack.isEmpty())
				break;
		}
		return app;
	}
}