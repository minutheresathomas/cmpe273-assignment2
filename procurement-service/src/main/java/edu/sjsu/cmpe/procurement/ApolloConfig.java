package edu.sjsu.cmpe.procurement;

public class ApolloConfig {
	private static String user;
	private static String password;
	private static String host;
	private static String port;
	private static String queue;
	private static String topic;
	
	public ApolloConfig(String user, String passowrd, String host, String port, String queue, String topic)
	{
		this.user= user;
		this.password = passowrd;
		this.host = host;
		this.port = port;
		this.queue = queue;
		this.topic = topic;
	}

	public ApolloConfig() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @return the queue
	 */
	public String getQueue() {
		return queue;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}
	
}
