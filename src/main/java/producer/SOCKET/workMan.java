package producer.SOCKET;

import java.io.*;
import java.net.Socket;

import producer.demo.myProducer;


public class workMan implements Runnable{

    private Socket socket;
    private String topic = "webTopic";
    private myProducer producer;
    private StringBuilder sb = new StringBuilder();

    public workMan(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handlerSocket();
            sendToKafka();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handlerSocket() throws Exception {
        clientRequest();
        response();
        socket.close();
    }

    /**
     * 获取客户端发送过来的数据
     */
    private void clientRequest(){
        // 跟客户端建立好连接之后，我们就可以获取socket的InputStream，并从中读取客户端发过来的信息了
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到数据后对客户端进行响应
     */
    private void response(){
        try {
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write("SUCCESS");
            writer.flush();
//            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将消息丢给producer
     */
    private void sendToKafka(){
        if (producer==null)
            producer = new myProducer();
        producer.sendMsg(topic,sb);
    }
}