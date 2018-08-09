package com.ctg.jmemadmin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctg.jmemadmin.common.Constants;

public class PortsCheck {
	private static final Logger LOG = LoggerFactory.getLogger(PortsCheck.class);

	private Set<Integer> ports = new HashSet<Integer>();
	public PortsCheck() {
		//Do nothing
		ports.add(Integer.valueOf(12301));
		ports.add(Integer.valueOf(12302));
	}
	
	/**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
     * return true，合法
     * */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            		 + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            		 + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            		 + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (text.matches(regex)) {	// 判断ip地址是否与正则表达式匹配
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    /**
	 * 
	 * @param host. it can be IP or domainName
	 * @param port
	 * @return
	 * @throws UnknownHostException
	 */
	public static boolean checkPortBySocket(String host, int port) throws UnknownHostException {
		boolean flag = false;  
        InetAddress Address = InetAddress.getByName(host);  
        try {  
            Socket socket = new Socket(Address,port);  //建立一个Socket连接
            flag = true;  
        } catch (IOException e) {  
        	//
        }  
        return flag;  
    }  

	/**
	 * @param ip 
	 * @param port
	 * @return
	 * @throws InterruptedException
	 */
	//TODO:此函数备用，待回收删除
	@SuppressWarnings("static-access")
	public static boolean checkPortByNc(String ip, int port) throws InterruptedException{
		HostCmdAdmin hostCmdAdmin = new HostCmdAdmin();
		Process process;
		try {
			process = Runtime.getRuntime().exec("nc " + ip + " " + port);
			hostCmdAdmin.processStdout(process.getInputStream(), process.getErrorStream(), Constants.DEFAULT_CHART);
		}catch (IOException e) {
			e.printStackTrace();
			return true;
		}
		Thread.sleep(1000);//默认延时1s
		try {
			process.exitValue();
			return false;
		}catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 验证此行是否为指定的端口，因为 findstr或grep等命令会是把包含的找出来，例如查找80端口，但是会把8099查找出来
	 * @param str
	 * @return
	 */
	// FIXME:暂不能用
	private boolean validPort(String str) {
		Pattern pattern = Pattern.compile("^ *[a-zA-Z]+ +\\S+");
		Matcher matcher = pattern.matcher(str);
		
		matcher.find();
		String find = matcher.group();
		int spstart = find.lastIndexOf(":");
		find = find.substring(spstart + 1);
		int port = 0;
		try {
			port = Integer.parseInt(find);
		}catch (NumberFormatException e) {
			// TODO: handle exception
			System.out.println("查找到错误的端口：" + find);
			return false;
		}
		
		if(this.ports.contains(find)) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 解析脚本执行返回的结果集
	 * @param in
	 * @return
	 * @throws IOException 
	 */
	//FIXME:暂不能用。
	private List<String> processStdoutToList(InputStream in) throws IOException {
        List<String> data = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        String tmp;
        try {
        	while((tmp = bufferedReader.readLine()) != null){
//                boolean validPort = validPort(tmp);
//                if(validPort){
//                    data.add(tmp);
//                }
        		data.add(tmp);
            }
            bufferedReader.close();
        }catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
        return data;
    }
	
	/**
	 * 解析脚本执行返回的结果集
	 * @param in
	 * @param charset   default is "UTF-8"
	 * @return
	 * @throws IOException 
	 */
	//FIXME:暂不能用。
	private List<String> processStdoutToList(InputStream in,String charset) throws IOException {
        List<String> data = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, charset));
        String tmp;
        try {
        	while((tmp = bufferedReader.readLine()) != null){
                boolean validPort = validPort(tmp);
                if(validPort){
                    data.add(tmp);
                }
            }
            bufferedReader.close();
        }catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
        return data;
    }
	
	public List<String> getPidInfos(String port) throws InterruptedException {
		List<String> pidInfos = new ArrayList<String>();
		String[] command = {"/bin/sh","-c", "ps -ax|grep memcached|grep -v grep | grep " + port};
		try {
			//查找进程号
			Process process = Runtime.getRuntime().exec(command);
			LOG.info("command is : ps -ax|grep memcached|grep " + port);
			LOG.info(process.getInputStream().toString());
			pidInfos = processStdoutToList(process.getInputStream());
			
			if(0 == pidInfos.size()) {
				LOG.info("**********找不到该端口的进程！**********");
				try {
					Thread.sleep(3000);
					System.exit(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else {
				LOG.info("找到 [" + pidInfos.size() + "] 个进程，分别如下：");
				for(String pidInfo : pidInfos) {
					LOG.info(pidInfo);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return pidInfos;
	}   
	
	
	//TODO:根据进程ID查询命令返回的内容格式具体调节解析方法。
	public Set<Integer> getPids(List<String> pidInfos){
		Set<Integer> pids = new HashSet<Integer>();
		for(String pidInfo : pidInfos) {
			int offset = pidInfo.lastIndexOf(" ");
			String subPidInfo = pidInfo.substring(offset);
			subPidInfo = subPidInfo.replaceAll(" ", "");
			int pid = 0;
			try {
				pid = Integer.parseInt(subPidInfo);
				LOG.info("***********pid=======" + pid);
			} catch (NumberFormatException e) {
				LOG.info("获取的进程号错误：" + subPidInfo);
			}
			pids.add(pid);
		}
		return pids;
	}
	public static void main(String[] args) throws UnknownHostException {
		boolean flag = false;
		flag = checkPortBySocket(Constants.SINGLE_IP, 12301);
		System.out.println("10.142.90.152的12301端口占用情况：" + flag);
		flag = false;
		flag = checkPortBySocket(Constants.SLAVE_DOMAIN_NAME, 12302);
		System.out.println("10.142.90.154的12302端口占用情况：" + flag);
		
	}
	
}
