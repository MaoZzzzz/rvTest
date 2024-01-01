package com.example.rvptest.util;

import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: 清理空闲线程
 * @Author: Mzz
 * @Date: 2023/12/9 22:35
 */
@Component
public class IdleConnectionEvictor extends Thread {

    private HttpClientConnectionManager connMgr;

    private volatile boolean shutdown;

    public IdleConnectionEvictor() {
        super();
        super.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    // 关闭失效的连接
                    connMgr.closeExpiredConnections();
                }
            }
        } catch (InterruptedException ex) {
            // 结束
        }
    }

    // 关闭清理无效连接的线程
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

    @Autowired
    public void setConnMgr(HttpClientConnectionManager connMgr) {
        this.connMgr = connMgr;
    }

}
