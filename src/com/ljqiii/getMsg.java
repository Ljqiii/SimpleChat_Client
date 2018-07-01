package com.ljqiii;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


/*
 * getMsg类
 * 用于从服务器接收新的消息
 * */


public class getMsg extends Thread {

    ChatClient cc;
    DataInputStream in;//接收数据,ChatClient对象
    Socket socket = null;


    public getMsg(DataInputStream in) {
        this.in = in;
    }

    public getMsg(Socket s) {
        this.socket = s;
        try {
            this.in = new DataInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public getMsg(ChatClient cc) {
        this.cc = cc;
        this.socket = cc.getSocket();
        try {
            this.in = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void run() {
        //System.out.println("getmsg thread start");

        String receivetemp = "";//用于临时存储服务器发送的消息
        //接收消息,服务器会发送以 : 为分隔符号以 \n 结尾的指令
        while (true) {
            char a = 0;
            try {
                a = in.readChar();
                if (a != '\n') {
                    receivetemp = receivetemp + a;
                }

                if (a == '\n') {
                    System.out.println(receivetemp);
                    if (receivetemp.contains(":")) {

                        cc.parseMsg(receivetemp);//把服务器接收到的消息传到ChatClient去解析
                    }
                    receivetemp = "";
                }


            } catch (Exception e) {
                break;

            }

        }

        System.out.println("子线程运行结束");//正常运行除了关闭窗口不会出现
    }

}
