package com.maxsavitsky;

public class Line {

	public static final String delimiter = "~#";

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

	public String format(){
		String s = "";
		if(tag != null)
			s += "tag=" + tag + delimiter;
		if(label != null)
			s += "lbl=" + label + delimiter;
		if(message != null)
			s += "msg=" + message + delimiter;
		if(sectionId != null)
			s += "sec=" + sectionId + delimiter;
		s = s.substring(0, s.length() - delimiter.length());
		return s;
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
