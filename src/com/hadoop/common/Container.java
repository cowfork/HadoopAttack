package com.hadoop.common;

public class Container {
	private String id;
	private String start;
	private String end;
	private Application app;

	public Container(Application app,String id,String start, String end) {
		this.app = app;
		this.id = id;
		this.start = start;
		this.end = end;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "Container [id=" + id + ", name=" + app.getName() + ", start=" + start + ", end=" + end
				+ "]";
	}

}
