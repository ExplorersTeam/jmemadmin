package org.exp.jmemadmin.utils;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ZhangQingliang
 *
 */
public class HostCatalogMonitor extends FileAlterationListenerAdaptor {
    private static final Logger LOG = LoggerFactory.getLogger(HostCatalogMonitor.class);

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
        LOG.info("文件系统观察者开始检查事件");
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
        LOG.info("文件系统观察者完成检查事件");
    }

    @Override
    public void onDirectoryCreate(File directory) {
        super.onDirectoryCreate(directory);
        LOG.info("目录创建事件");
    }

    @Override
    public void onDirectoryChange(File directory) {
        // TODO Auto-generated method stub
        super.onDirectoryChange(directory);
        LOG.info("目录改变事件");
    }

    @Override
    public void onDirectoryDelete(File directory) {
        super.onDirectoryDelete(directory);
        LOG.info("目录删除事件");
    }

    @Override
    public void onFileCreate(File file) {
        super.onFileCreate(file);
        LOG.info("文件创建事件,文件名称为：" + file.getName());

    }

    @Override
    public void onFileChange(File file) {
        super.onFileChange(file);
        LOG.info("文件改变事件");
    }

    @Override
    public void onFileDelete(File file) {
        super.onFileDelete(file);
        LOG.info("文件删除事件，文件名称为：" + file.getName());
    }

    static final class FileFilterImpl implements FileFilter {
        @Override
        public boolean accept(File file) {
            LOG.info("文件路径为：" + file + ";最后修改时间为：" + file.lastModified());
            return true;
        }
    }

    public static void monitor(File filePath, int timeSpanMs) {
        try {
            FileAlterationObserver observer = new FileAlterationObserver(filePath);// 构造观察类主要提供要观察的文件或目录，当然还有详细信息的filter
            HostCatalogMonitor listener = new HostCatalogMonitor();// 构造收听类
            observer.addListener(listener);// 为观察对象添加收听对象
            // 配置Monitor，第一个参数单位是毫秒，是监听的间隔；第二个参数就是绑定我们之前的观察对象
            FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(timeSpanMs, new FileAlterationObserver[] { observer });
            fileAlterationMonitor.start();// 启动开始监听
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        monitor(new File("D:\\"), 10000);
    }
}
