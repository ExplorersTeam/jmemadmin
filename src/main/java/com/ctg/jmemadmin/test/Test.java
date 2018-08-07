package com.ctg.jmemadmin.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctg.jmemadmin.common.Constants;
import com.ctg.jmemadmin.utils.FileMonitor;
import com.ctg.jmemadmin.utils.MemCachedAdmin;
import com.ctg.jmemadmin.utils.RemoteExecuteCmd;

public class Test {
	private static final Logger LOG = LoggerFactory.getLogger(Test.class);
	//MemCachedAdmin memCachedAdmin = MemCachedAdmin.getInstance();
	
	public static void main(String[] args) throws NumberFormatException, UnsupportedEncodingException {
//		boolean flag = false;
//		flag = MemCachedAdmin.add("name2", new String("zhangqingliang"), new Date(1800));
//		LOG.info("添加数据成功！！！");
//		String result = (String)MemCachedAdmin.get("name");
//		LOG.info("运行结果为：name = " + result);
//		MemCachedAdmin.getKeysForMap();
//		MemCachedAdmin.stats(Constants.SERVERS);
//		
//		try {
//			// 构造观察类主要提供要观察的文件或目录，当然还有详细信息的filter
//			FileAlterationObserver observer = new FileAlterationObserver(new File("D:\\"));
//			// 构造收听类
//			FileMonitor listener = new FileMonitor();
//			// 为观察对象添加收听对象
//			observer.addListener(listener);
//			// 配置Monitor，第一个参数单位是毫秒，是监听的间隔；第二个参数就是绑定我们之前的观察对象
//			FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(10000, new FileAlterationObserver[] {observer});
//			//启动开始监听
//			fileAlterationMonitor.start();
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		MemCachedAdmin.executeLinuxCmd(Constants.CREATE_MASTER_MC_INSTANCE_CMD);
		
		RemoteExecuteCmd remoteExecuteCmd = new RemoteExecuteCmd(Constants.SLAVE_IP, Constants.SLAVE_USERNAME, Constants.SLAVE_PASSWORD);
		remoteExecuteCmd.executeCmd(Constants.CREATE_SLAVE_MC_INSTANCE_CMD);
	}
}
