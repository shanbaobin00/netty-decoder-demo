package org.eric;

import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        int threads = 50;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.execute(()->{
                try{
                    barrier.await();
                    String[] msgs = {
                            "02ab0",
                            "2ab02",
                            "ab",
                            "0",
                            "2ab02ab",
                            "02ab0",
                            "2cd03",
                            "ef",
                            "g",
                            "02uh02jk",
                    };

                    Socket socket = new Socket("127.0.0.1", 7777);
                    OutputStream outputStream = socket.getOutputStream();

                    for (String msg : msgs) {
                        System.out.println(msg);
                        outputStream.write(msg.getBytes());
                        outputStream.flush();
                    }
                    Thread.sleep(10000);
                    outputStream.close();
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(10000);
        executorService.shutdown();
    }

}
