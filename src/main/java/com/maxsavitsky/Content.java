package com.maxsavitsky;

public class Content {

	public static final String DELIMITER = "~#";

	private final String tag;
	private final String sectionId;
	private final long time;
	private String message;
	private String label;

	public Content(String tag, String sectionId, String message) {
		this(tag, sectionId, message, null);
	}

	public Content(String tag, String sectionId, String message, String label) {
		this.tag = tag;
		this.sectionId = sectionId;
		this.message = message == null ? null : message.trim();
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String format() {
		String s = "";
		if (tag != null)
			s += "tag=" + tag + DELIMITER;
		if (label != null)
			s += "lbl=" + label + DELIMITER;
		if (message != null)
			s += "msg=" + message + DELIMITER;
		if (sectionId != null)
			s += "sec=" + sectionId + DELIMITER;
		s = s.substring(0, s.length() - DELIMITER.length());
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
