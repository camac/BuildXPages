package com.gregorbyte.buildxpages.ant;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Task;

import com.gregorbyte.buildxpages.NotesLibrary;

public class HelloWorld extends Task {

	String message;
	List<Message> messages = new ArrayList<Message>();

	public void setMessage(String message) {
		this.message = message;
	}

	public void addText(String text) {
		if (message == null)
			message = getProject().replaceProperties(text);
	}

	public void execute() {

		if (message != null) log(message);
		
		for (Message message : messages) {
			log(message.getMsg());
		}

		NotesLibrary.main(new String[]{});
		
	}

	public Message createMessage() {
		Message msg = new Message();
		messages.add(msg);
		return msg;
	}

	public class Message {
		
		public Message() {
		}

		String msg;

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getMsg() {
			return this.msg;
		}
	}
}
