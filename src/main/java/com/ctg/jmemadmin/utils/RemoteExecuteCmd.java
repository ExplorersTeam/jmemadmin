package com.ctg.jmemadmin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctg.jmemadmin.common.Constants;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class RemoteExecuteCmd {
	private static final Logger LOG = LoggerFactory.getLogger(RemoteExecuteCmd.class);
	
	private static String DEFAULT_CHART = "UTF-8";
	private static Connection conn;
	private String ip;
	private String userName;
	private String password;
	
	public RemoteExecuteCmd() {
		//Do Nothing
	}
	
	public RemoteExecuteCmd(String ip, String userName, String password) {
		this.ip = ip;
		this.userName = userName;
		this.password = password;
	}
	
	public static void setCharset(String charset) {
		DEFAULT_CHART = charset;
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		RemoteExecuteCmd.conn = conn;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 远程登陆Linux主机
	 * @return
	 * 登陆成功返回true，否则返回false
	 */
	public boolean login() {
		boolean flag = false;
		try {
			conn = new Connection(ip);
			conn.connect();
			flag = conn.authenticateWithPassword(userName, password);//认证登陆信息
			if(flag) {
				LOG.info("认证登陆信息成功！");
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 解析脚本执行返回的结果集
	 * @param in
	 * @param charset
	 * @return
	 */
	public static String processStdout(InputStream in, String charset) {
		InputStream stdout = new StreamGobbler(in);
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdout, charset));
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				buffer.append(line + "\n");
			}
			bufferedReader.close();
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	
	public String executeCmd(String cmd) {
		String result = "";
		try {
			if(login()) {
				Session session = conn.openSession();//打开一个会话
				session.execCommand(cmd);//执行命令
				result = processStdout(session.getStdout(), DEFAULT_CHART);
				LOG.info("命令执行结果：[" + result +"]");
//				if(StringUtils.isBlank(result)) {
//					result = processStdout(session.getStderr(), DEFAULT_CHART);
//				}
				conn.close();
				session.close();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		RemoteExecuteCmd remoteExecuteCmd = new RemoteExecuteCmd(Constants.SLAVE_IP, Constants.SLAVE_USERNAME, Constants.SLAVE_PASSWORD);
		remoteExecuteCmd.executeCmd(Constants.CREATE_SLAVE_MC_INSTANCE_CMD);
	}
}
