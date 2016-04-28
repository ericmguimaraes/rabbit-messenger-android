package com.chatt.demo.model;

import java.util.Date;

/**
 * The Class Message is a Java Bean class that represents a single chat
 * conversation message.
 */
public class Message
{

	/** The Constant STATUS_SENDING. */
	public static final int STATUS_SENDING = 0;

	/** The Constant STATUS_SENT. */
	public static final int STATUS_SENT = 1;

	/** The Constant STATUS_FAILED. */
	public static final int STATUS_FAILED = 2;
	/** The content. */
	private String content;

	/** The status. */
	private int status = STATUS_SENT;

	/** The date. */
	private Date date;

	/** The sender. */
	private String sender;

	private String receiver;

	/**
	 * Instantiates a new conversation.
	 * 
	 * @param content
	 *            the content
	 * @param date
	 *            the date
	 * @param sender
	 *            the sender
	 */
	public Message(String content, Date date, String sender)
	{
		this.content = content;
		this.date = date;
		this.sender = sender;
	}

	/**
	 * Instantiates a new conversation.
	 */
	public Message()
	{
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Sets the content.
	 * 
	 * @param content
	 *            the new content
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * Checks if is sent.
	 * 
	 * @return true, if is sent
	 */
	public boolean isSent()
	{
		return false;//UserList.user.getUsername().equals(sender);
	}

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * Sets the date.
	 * 
	 * @param date
	 *            the new date
	 */
	public void setDate(Date date)
	{
		this.date = date;
	}

	/**
	 * Gets the sender.
	 * 
	 * @return the sender
	 */
	public String getSender()
	{
		return sender;
	}

	/**
	 * Sets the sender.
	 * 
	 * @param sender
	 *            the new sender
	 */
	public void setSender(String sender)
	{
		this.sender = sender;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }
}
