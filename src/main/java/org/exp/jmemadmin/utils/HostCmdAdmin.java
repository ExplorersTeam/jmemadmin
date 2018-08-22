package org.exp.jmemadmin.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.exp.jmemadmin.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

/**
 *
 * @author ZhangQingliang
 *
 */
public class HostCmdAdmin {
    private static final Logger LOG = LoggerFactory.getLogger(HostCmdAdmin.class);
    private static Connection conn;
    private static String ip;
    private static String userName;
    private static String password;
    static {
        ip = Constants.IP;
        userName = Constants.SLAVE_USERNAME;
        password = Constants.SLAVE_PASSWORD;
    }

    private HostCmdAdmin() {
        // Do Nothing
    }

    /**
     * 远程登陆Linux主机
     *
     * @return 登陆成功返回true，否则返回false
     */
    private static boolean login() {
        boolean flag = false;
        try {
            conn = new Connection(ip);
            conn.connect();
            flag = conn.authenticateWithPassword(userName, password);// 认证登陆信息
            if (flag) {
                LOG.info("认证登陆信息成功！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 执行命令的子进程的工作目录默认和当前主进程工作目录相同
     *
     * @param cmd
     * @return
     */
    public static String executeLocalCmd(String cmd) {
        String[] command = { "/bin/sh", "-c", cmd };
        String result = "";
        Process process = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(command);
            // process.waitFor(); // 方法阻塞, 等待命令执行完成（成功会返回0）
            result = processStdout(process.getInputStream(), process.getErrorStream(), Constants.DEFAULT_CHART);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }

        return result;
    }

    /**
     * 执行系统命令, 返回执行结果
     *
     * @param cmd
     *            需要执行的命令
     * @param dir
     *            执行命令的子进程的工作目录, null 表示和当前主进程工作目录相同
     */
    public static String executeLocalCmd(String cmd, File dir) throws Exception {
        String[] command = { "/bin/sh", "-c", cmd };
        String result = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command, null, dir);// 执行命令,
                                                                    // 返回一个子进程对象（命令在子进程中执行）
            // process.waitFor();// 方法阻塞, 等待命令执行完成（成功会返回0）
            result = processStdout(process.getInputStream(), process.getErrorStream(), Constants.DEFAULT_CHART);

        } finally {
            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }
        return result.toString();
    }

    public static String executeRemoteCmd(String cmd) {
        String result = "";
        try {
            if (login()) {
                Session session = conn.openSession();// 打开一个会话
                session.execCommand(cmd);// 执行命令
                result = processStdout(session.getStdout(), session.getStderr(), Constants.DEFAULT_CHART);
                // TODO:这里输出为空，待解决。。。
                LOG.info("命令执行结果：[" + result + "]");
                // if(StringUtils.isBlank(result)) {
                // result = processStdout(session.getStderr(), DEFAULT_CHART);
                // }
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析脚本执行返回的结果集
     *
     * @param in
     * @param charset
     *            default is "UTF-8"
     * @return
     * @throws IOException
     */
    public static String processStdout(InputStream in, InputStream error, String charset) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        try {
            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bufrIn.close();
            bufrError.close();
        }
        return result.toString();
    }

    /**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断 return true，合法
     */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (text.matches(regex)) { // 判断ip地址是否与正则表达式匹配
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * check port's using state.
     *
     * @param host.
     *            it can be IP or domainName
     * @param port
     * @return using:false not using:true
     * @throws UnknownHostException
     */
    public static boolean checkPortBySocket(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress Address = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(Address, port); // 建立一个Socket连接
            flag = true;
        } catch (IOException e) {
            //
        }
        return flag;
    }

    public static Connection getConn() {
        return conn;
    }

    public static void setConn(Connection conn) {
        HostCmdAdmin.conn = conn;
    }

    // TODO:Delete codes below. Just for test.
    public static void main(String[] args) throws Exception {
        HostCmdAdmin.executeLocalCmd("ls", null);
        HostCmdAdmin.executeLocalCmd("java -version");
        HostCmdAdmin.executeLocalCmd("ps -ax|grep memcached|grep 12301", null);
        HostCmdAdmin.executeLocalCmd("ps -ax|grep memcached|grep 12301");
        HostCmdAdmin.executeRemoteCmd("ps -ef|grep memcached|grep 12301|grep -v grep");
    }
}
