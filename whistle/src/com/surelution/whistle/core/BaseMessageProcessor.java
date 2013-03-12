/**
 * 
 */
package com.surelution.whistle.core;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:guangzong.syu@gmail.com">Guangzong</a>
 *
 */
public abstract class BaseMessageProcessor {
	
	public static final String KEY_ToUserName = "ToUserName";
	public static final String KEY_FromUserName = "FromUserName";
	public static final String KEY_CreateTime = "CreateTime";
	public static final String KEY_MsgType = "MsgType";
	public static final String KEY_FuncFlag = "FuncFlag";
	public static final String KEY_Content = "Content";
	public static final String KEY_Location_X = "Location_X";
	public static final String KEY_Location_Y = "Location_Y";
	
	private Map<String, String> params;
	private Map<String, Message> outcomeParams = new HashMap<String, Message>();
	
	final protected void feed(Map<String, String> map) {
		params = map;
		put(KEY_ToUserName, getParam(KEY_FromUserName));
		put(KEY_FromUserName, getParam(KEY_ToUserName));
		put(KEY_CreateTime, String.valueOf(System.currentTimeMillis()), false);
		put(KEY_MsgType, "text");
		put(KEY_FuncFlag, "0", false);
	}
	
	final public String getParam(String key) {
		return params.get(key);
	}

	public abstract boolean accept();
	
	public boolean goOn() {
		return true;
	}

	public abstract void process();

	final public String getXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for(Entry<String, Message> entry : outcomeParams.entrySet()) {
			sb.append(entry.getValue().toXml());
		}
		sb.append("</xml>");
		return sb.toString();
	}

	final protected void put(String key, String message, boolean isCData) {
		outcomeParams.put(key, new Message(key, message, isCData));
	}

	final protected void put(String key, String message) {
		put(key, message, true);
	}

	final protected void put(Message message) {
		outcomeParams.put(message.getKey(), message);
		List<Message> fellows = message.getFellows();
		if(fellows != null) {
			for(Message fellow : fellows) {
				outcomeParams.put(fellow.getKey(), fellow);
			}
		}
	}

	final protected String getMessage(String key, String... args) {
		ResourceBundle messages = ResourceBundle.getBundle(getClass().getPackage().getName() + ".resources", Locale.SIMPLIFIED_CHINESE);
		String pattern;
		if(messages.containsKey(key)) {
			pattern = messages.getString(key);
		} else {
			messages = ResourceBundle.getBundle("resources", Locale.SIMPLIFIED_CHINESE);
			pattern = messages.getString(key);
		}
		MessageFormat mf = new MessageFormat(pattern, Locale.SIMPLIFIED_CHINESE);
		return mf.format(args);
	}
}