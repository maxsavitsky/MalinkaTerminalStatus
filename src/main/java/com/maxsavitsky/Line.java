package com.maxsavitsky;

public class Line {

	private final String tag, sectionId;
	private String message, label;
	private final long time;

	public Line(String tag, String sectionId) {
		this(tag, sectionId, null, null);
	}

	public Line(String tag, String sectionId, String message){
		this(tag, sectionId, message, null);
	}

	public Line(String tag, String sectionId, String message, String label) {
		this.tag = tag;
		this.sectionId = sectionId;
		this.message = message;
		this.label = label;
		time = System.currentTimeMillis();
	}

	public String getTag() {
		return tag;
	}

	public String getSectionId() {
		return sectionId;
	}

	public long getTime() {
		return time;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return "Line{" +
				"tag='" + tag + '\'' +
				", sectionId='" + sectionId + '\'' +
				", message='" + message + '\'' +
				", label='" + label + '\'' +
				", time=" + time +
				'}';
	}
}
