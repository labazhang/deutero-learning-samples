package me.jkong.lock.barrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 双栅栏允许客户端在计算的开始和结束时同步。当足够的进程加入到双栅栏时，进程开始计算， 当计算完成时，离开栅栏。
 *
 * @author JKong
 * @version v0.0.1
 * @date 2020/5/22 11:15.
 */
public class DistributedDoubleBarrierDemo {
    private static final int QTY = 5;
    private static final String PATH = "/examples/barrier";

    public static void main(String[] args) throws Exception {
        try (TestingServer server = new TestingServer()) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();
            ExecutorService service = Executors.newFixedThreadPool(QTY);
            for (int i = 0; i < QTY; ++i) {
                final DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, PATH, QTY);
                final int index = i;
                Callable<Void> task = () -> {

                    Thread.sleep((long) (3 * Math.random()));
                    System.out.println("Client #" + index + " enters");
                    barrier.enter();
                    System.out.println("Client #" + index + " begins");
                    Thread.sleep((long) (3000 * Math.random()));
                    barrier.leave();
                    System.out.println("Client #" + index + " left");
                    return null;
                };
                service.submit(task);
            }

            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            Thread.sleep(Integer.MAX_VALUE);
        }
    }
}