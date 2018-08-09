package com.ctg.jmemadmin.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctg.jmemadmin.common.Constants;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class HostCmdAdmin {
	private static final Logger LOG = LoggerFactory.getLogger(HostCmdAdmin.class);
	
	private static Connection conn;
	private String ip;
	private String userName;
	private String password;
	
	public HostCmdAdmin() {
		//Do Nothing
	}
	
	public HostCmdAdmin(String ip, String userName, String password) {
		this.ip = ip;
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * 远程登陆Linux主机
	 * @return
	 * 登陆成功返回true，否则返回false
	 */
	private boolean login() {
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
	 * 执行命令的子进程的工作目录默认和当前主进程工作目录相同
	 * @param cmd
	 * @return
	 */
	public String executeLocalCmd(String cmd){
		String[] command = {"/bin/sh","-c", cmd};
		String result = "";
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			//process.waitFor(); // 方法阻塞, 等待命令执行完成（成功会返回0）
			result = processStdout(process.getInputStream(), process.getErrorStream(), Constants.DEFAULT_CHART);
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//	public String executeLocalCmd(String cmd) {
	//		String[] command = {"/bin/sh","-c", cmd};
	//		Runtime runtime = Runtime.getRuntime();
	//		StringBuffer result = new StringBuffer();
	//		try {
	//			Process process = runtime.exec(command);
	//			InputStream inputStream = process.getInputStream();
	//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));			
	//			String tmp = null;
	//			while((tmp = bufferedReader.readLine()) != null) {
	//				result.append(tmp);
	//			}
	//			LOG.info("job result [" + result.toString() + "]");
	//			inputStream.close();
	//			//process.waitFor();
	//			process.destroy();
	//		}catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//		return result.toString();
	//	}
		
		/**
	     * 执行系统命令, 返回执行结果
	     *
	     * @param cmd 需要执行的命令
	     * @param dir 执行命令的子进程的工作目录, null 表示和当前主进程工作目录相同
	     */
	    public String executeLocalCmd(String cmd, File dir) throws Exception {
	    	String[] command = {"/bin/sh","-c", cmd};
	        StringBuilder result = new StringBuilder();
	        Process process = null;
	        BufferedReader bufrIn = null;
	        BufferedReader bufrError = null;
	
	        try {
	            process = Runtime.getRuntime().exec(command, null, dir);// 执行命令, 返回一个子进程对象（命令在子进程中执行）
	            //process.waitFor();// 方法阻塞, 等待命令执行完成（成功会返回0）
	
	            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
	            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
	            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
	            // 读取输出
	            String line = null;
	            while ((line = bufrIn.readLine()) != null) {
	                result.append(line).append('\n');
	            }
	            while ((line = bufrError.readLine()) != null) {
	                result.append(line).append('\n');
	            }
//	            LOG.info("********** result **********");
//	            LOG.info(result.toString());
	            System.out.println("********** result **********");
	            System.out.println(result.toString());
	        } finally {
	        	bufrIn.close();
	        	bufrError.close();
	            // 销毁子进程
	            if (process != null) {
	                process.destroy();
	            }
	        }
	        return result.toString();
	    }
	    

	public String executeRemoteCmd(String cmd) {
		String result = "";
		try {
			if(login()) {
				Session session = conn.openSession();//打开一个会话
				session.execCommand(cmd);//执行命令
				result = processStdout(session.getStdout(), session.getStderr(), Constants.DEFAULT_CHART);
				//TODO:这里输出为空，待解决。。。
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

		
	/**
	 * 解析脚本执行返回的结果集
	 * @param in
	 * @param charset   default is "UTF-8"
	 * @return
	 * @throws IOException 
	 */
	public static String processStdout(InputStream in, InputStream error, String charset) throws IOException {
		StringBuilder result = new StringBuilder();
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        try {
        	bufrIn = new BufferedReader(new InputStreamReader(in, charset));
            bufrError = new BufferedReader(new InputStreamReader(error, charset));
            // 读取输出
            String line = null;
            while ((line = bufrIn.readLine()) != null) {
                result.append(line).append('\n');
            }
            while ((line = bufrError.readLine()) != null) {
                result.append(line).append('\n');
            }
            LOG.info("********** result **********");
            LOG.info(result.toString());
        }catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
        	bufrIn.close();
        	bufrError.close();
        }
        return result.toString();
    }

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		HostCmdAdmin.conn = conn;
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

	public static void main(String[] args) throws Exception {
		HostCmdAdmin hostCmdAdmin = new HostCmdAdmin(Constants.SLAVE_IP, Constants.SLAVE_USERNAME, Constants.SLAVE_PASSWORD);
		hostCmdAdmin.executeRemoteCmd(Constants.CREATE_SLAVE_MC_INSTANCE_CMD);
		hostCmdAdmin.executeLocalCmd("ls", null);
		hostCmdAdmin.executeLocalCmd("java -version");
		hostCmdAdmin.executeLocalCmd("ps -ax|grep memcached|grep 12301", null);
		hostCmdAdmin.executeLocalCmd("ps -ax|grep memcached|grep 12301");
		hostCmdAdmin.executeRemoteCmd("ps -ef|grep memcached|grep 12301|grep -v grep");
	}
}
