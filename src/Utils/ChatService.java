/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author hung.tran
 */
public class ChatService {

    ServerSocket socket;

    public ChatService() {
    }

    public void openport(int port) throws Exception {
        socket = new ServerSocket(port);
        System.out.println("Da mo cong " + port);
    }

    public void send(String ip, String data, int port) throws Exception {
        Socket sk = new Socket(ip, port);
        System.out.println("Da noi den server " + ip + " qua cong: " + port);
        //Tạo luồng để gửi dũ liệu lên Server
        DataOutputStream out = new DataOutputStream(sk.getOutputStream());
        out.writeBytes(data); //Gửi thông báo
        out.write(13);
        out.write(10);
        out.close();
    }

    public String Nhan() throws Exception {
        Socket c = socket.accept();

        //Tạo luồng để nhận thông tin client gửi lên
        InputStreamReader is = new InputStreamReader(c.getInputStream());

        //Tạo bộ đệm để lưu dữ liệu nhận về
        BufferedReader r = new BufferedReader(is);
        //đọc dữ liệu từ bộ đệm
        String result = r.readLine();
        return result;
    }

    
}
