package br.com.rabbitmessenger.rabbitmessenger.model;

import java.util.Date;

public class Message
{
	private String content;

	private Date date;

	private String sender;

	private String receiver;

	private Boolean read;

	public Message(String content, Date date, String sender)
	{
		this.content = content;
		this.date = date;
		this.sender = sender;
	}

	public Message()
	{
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
