package com.ljqiii;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/*
 * 用户界面
 * */
public class ChatClient extends JFrame {

    Socket s;
    DataInputStream in = null;//用于接收消息
    DataOutputStream out = null;//发送消息

    TextArea msgarea;//显示聊天记录文本area
    TextField msginput;//输入消息文本框
    JButton submitmsg;//发送消息
    JLabel havaunreadmsg;//有新的未读消息提示
    JButton closechat;//关闭聊天窗口

    JLabel iplabel;//ip提示文字
    JTextField ip;//ip输入框
    JLabel portlabel;//端口提示文字
    JTextField port;//端口输入框
    JLabel nicknamelabel;//昵称提示
    JTextField nickname;//昵称输入框
    JButton loginbtn;//登陆按钮
    JLabel connecterror;//错误提示


    JLabel tittle;//显示在昵称列表上边
    JList<String> nicknamejlist;//昵称列表
    private JScrollPane nicknameListscrollPane;//包含昵称列表Jlist,滚动
    ListSelectionModel listSelectionModel;//list选择模式

    String nowchatwith;//当前聊天的人
    ArrayList<MsgLog> msglog;//聊天记录保存
    String nicknamelist[];//昵称列表
    boolean isdisplayuneradmsg;//保存当前是否显示含有未读消息
    boolean isInchatpanl;//记录是否在聊天界面
    String nicknamethis;//当前窗口的昵称


    public TextArea getMsgarea() {
        return msgarea;
    }

    public TextField getMsginput() {
        return msginput;
    }

    public JButton getSubmitmsg() {
        return submitmsg;
    }


    public JButton getClosechat() {
        return closechat;
    }

    public JLabel getTittle() {
        return tittle;
    }

    public JScrollPane getNicknameListscrollPane() {
        return nicknameListscrollPane;
    }


    public boolean isInchatpanl() {
        return isInchatpanl;
    }

    public void setInchatpanl(boolean inchatpanl) {
        isInchatpanl = inchatpanl;
    }


    public boolean isIsdisplayuneradmsg() {
        return isdisplayuneradmsg;
    }


    public String getNicknamethis() {
        return nicknamethis;
    }

    public void setNicknamethis(String nicknamethis) {
        this.nicknamethis = nicknamethis;
    }

    public String[] getNicknamelist() {
        return nicknamelist;
    }

    public void setNicknamelist(String[] nicknamelist) {
        this.nicknamelist = nicknamelist;
    }


    public Socket getSocket() {
        return s;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }


    //设置当前聊聊天的人
    public void setNowchatwith(String nowchatwith) {
        this.nowchatwith = nowchatwith;
    }


    //查询是否有未读消息
    public boolean isHaveUnreadMsg() {
        for (int i = 0; i < msglog.size(); i++) {
            if (msglog.get(i).getUnread() != 0) {
                return true;
            }
        }
        return false;
    }

    //所有未读消息的昵称
    public ArrayList<String> getAllUnreadNickname() {
        ArrayList<String> tempallunreadnickname = new ArrayList<>();

        for (int i = 0; i < msglog.size(); i++) {
            if (msglog.get(i).getUnread() != 0) {
                tempallunreadnickname.add(msglog.get(i).getNickname());
            }
        }
        return tempallunreadnickname;
    }

    //显示有消息
    public void displayHaveUnreadmsg() {
        add(this.havaunreadmsg);
        this.isdisplayuneradmsg = true;
        validate();
        repaint();
    }


    //取消显示有未读消息
    public void unDisplayHaveUnreadmsg() {
        remove(this.havaunreadmsg);
        this.isdisplayuneradmsg = false;
        validate();
        repaint();
    }

    //从昵称列表进入聊天界面
    public void displayChatUI() {
        remove(nicknameListscrollPane);
        remove(tittle);

        add(this.msgarea);
        add(this.msginput);
        add(this.submitmsg);
        add(this.closechat);

        validate();
        repaint();

        this.msgarea.setText(getmsglog(nowchatwith).getallmsg());
    }

    //取消聊天界面,回到列表
    public void UnDisplayChatUI() {
        this.msgarea.setText("");
        remove(this.msgarea);
        remove(this.msginput);
        remove(this.submitmsg);
        remove(this.closechat);
        add(tittle);
        add(getnicknamepanel());

        this.nowchatwith = "";

        validate();
        repaint();
    }


    //判断昵称是否在msglog里面
    public boolean isInmsglog(String nicknamein) {
        for (int i = 0; i < msglog.size(); i++) {
            if (msglog.get(i).getNickname().equals(nicknamein)) {
                return true;
            }
        }
        return false;
    }

    //判断字符串是否在字符串数组里面
    public boolean isStringInStringlist(String s, String[] ss) {
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    //添加一个MsgLog到msglog
    public void appendToMsglog(String nicknamein) {
        MsgLog tempmsglog = new MsgLog(nicknamein);
        msglog.add(tempmsglog);
    }

    //添加聊天记录
    public boolean newmsgunread(String nickname, String from, String msg) {
        for (int i = 0; i < msglog.size(); i++) {
            if (msglog.get(i).getNickname().equals(nickname)) {
                msglog.get(i).addUnread();
                msglog.get(i).appendmsg(from + ":" + msg + "\n");
                return true;
            }
        }
        return false;
    }

    //得到MsgLog对象
    public MsgLog getmsglog(String nickname) {
        for (int i = 0; i < msglog.size(); i++) {
            if (msglog.get(i).getNickname().equals(nickname)) {
                return msglog.get(i);
            }
        }
        return null;
    }


    //发送字符串
    public static void writeString(DataOutputStream out, String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.writeChar(s.charAt(i));
        }
    }


    //设置昵称
    public int setNickname(String nickname) {
        setNicknamethis(nickname);

        String nicknametemp = "setnickname:" + nickname + "\n";
        System.out.println(nicknametemp);

        try {
            ChatClient.writeString(out, nicknametemp);
        } catch (IOException e) {
            e.printStackTrace();

            return 0;
        }
        return 1;
    }

    //获取在线昵称列表GUI
    public JScrollPane getnicknamepanel() {

        nicknamejlist = new JList<String>();

        String namelist[] = null;
        namelist = getNicknamelist().clone();

        //如果含有未读消息,在你昵称前面添加未读
        if (isHaveUnreadMsg() == true) {
            ArrayList<String> unreadname = getAllUnreadNickname();
            for (int i = 0; i < unreadname.size(); i++) {
                if (isStringInStringlist(unreadname.get(i), namelist)) {
                    for (int ii = 0; ii < namelist.length; ii++) {
                        if (unreadname.get(i).equals(namelist[ii])) {
                            namelist[ii] = "未读_" + namelist[ii];
                            break;
                        }
                    }
                }
            }
        }
        nicknamejlist.setListData(namelist);
        //设置选择模式
        listSelectionModel = nicknamejlist.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel aa = (ListSelectionModel) e.getSource();
                String selectedvalue = nicknamejlist.getSelectedValue();
                //System.out.println(e.getValueIsAdjusting());
                if (e.getValueIsAdjusting() == false) {
                    System.out.println("selectedvalue: " + selectedvalue);
                    if (selectedvalue != null) {

                        //把选择的项取消未读_
                        if (selectedvalue.contains("未读_")) {
                            selectedvalue = selectedvalue.substring(3, selectedvalue.length());
                        }
                        getmsglog(selectedvalue).setUnread0();//设置未读数量0
                        //取消右上角有未读消息的提示
                        if (isIsdisplayuneradmsg() == true) {
                            if (isHaveUnreadMsg() == false) {
                                unDisplayHaveUnreadmsg();
                            }
                        }
                        //记录当前界面为聊天界面
                        setInchatpanl(true);
                        setNowchatwith(selectedvalue);
                        displayChatUI();
                    }
                }

            }
        });

        nicknameListscrollPane = new JScrollPane();
        nicknameListscrollPane.setBounds(0, 20, 285, 430);
        nicknameListscrollPane.setViewportView(nicknamejlist);
        nicknameListscrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI());
        return nicknameListscrollPane;
    }


    //解析消息
    //消息以 命令:内容 为格式
    public void parseMsg(String msg) {
        String response = msg.split(":")[0];
        String content = msg.split(":")[1];
        System.out.println("response: " + response);
        System.out.println("content: " + content);

        //返回的昵称列表
        if (response.equals("allnickname")) {
            int beforemsglognum = msglog.size();
            if (!content.contains(",")) {
                String temp[] = new String[]{content};
                setNicknamelist(temp);

                if (beforemsglognum > 1) {
                    for (int i = 0; i < beforemsglognum; i++) {
                        if (!msglog.get(i).getNickname().equals(content)) {
                            msglog.remove(i);
                        }
                    }
                    //System.out.println("msglog size:" + String.valueOf(msglog.size()));

                } else if (beforemsglognum < 1) {
                    appendToMsglog(content);
                    //System.out.println("msglog size:" + String.valueOf(msglog.size()));

                }


            } else {
                String nicknamelisttemp[];
                nicknamelisttemp = content.split(",");

                if (beforemsglognum < nicknamelisttemp.length) {
                    for (int i = 0; i < nicknamelisttemp.length; i++) {
                        if (!isInmsglog(nicknamelisttemp[i])) {
                            appendToMsglog(nicknamelisttemp[i]);
                        }
                    }
                    //System.out.println("msglog size:" + String.valueOf(msglog.size()));

                } else if (beforemsglognum > nicknamelisttemp.length) {
                    for (int ii = 0; ii < msglog.size(); ii++) {
                        String tempname = msglog.get(ii).getNickname();
                        if (!isStringInStringlist(tempname, nicknamelisttemp)) {
                            msglog.remove(ii);
                        }
                    }
                    //System.out.println("msglog size:" + String.valueOf(msglog.size()));

                }

                setNicknamelist(nicknamelisttemp);
            }
            if (nicknamejlist != null) {
                nicknamejlist.setListData(getNicknamelist());
            }

        //接收到消息
        } else if (response.equals("receive")) {
            String from = content.split("\\|")[0];
            String frommsg = content.split("\\|")[1];
            System.out.println("receive from" + " " + from + " :" + frommsg);
            getmsglog(from).appendmsg(from + ": " + frommsg + "\n");

            if (!from.equals(nowchatwith)) {
                getmsglog(from).addUnread();
                displayHaveUnreadmsg();

                if (isInchatpanl() == false) {
                    remove(nicknameListscrollPane);
                    add(getnicknamepanel());
                    validate();// 重构内容面板
                    repaint();// 重绘内容面板

                }
            } else {
                msgarea.setText(getmsglog(from).getallmsg());
            }


        }
    }

    //发送字符串
    public void send(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            try {
                this.out.writeChar(msg.charAt(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //登陆到服务器,成功返回1,失败返回0
    int loginfunc(String ipadress, int port) {
        try {
            s = new Socket(ipadress, port);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            getMsg msg = new getMsg(this);
            msg.start();
            //连接不到服务器错误处理

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return 0;
        } catch (ConnectException e) {
            return 0;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    //移除登陆界面组件
    public void removelogincomponents() {
        remove(iplabel);
        remove(ip);
        remove(portlabel);
        remove(port);
        remove(nickname);
        remove(nicknamelabel);
        remove(loginbtn);
    }

    //连接服务器失败错误GUI
    public void showconnecterror() {
        //removeAll();
        add(connecterror);
        validate();// 重构内容面板
        repaint();// 重绘内容面板
    }

    //构造函数
    public ChatClient() {

        tittle = new JLabel("聊天客户端");
        tittle.setBounds(5, 0, 100, 20);

        this.isInchatpanl = false;
        this.isdisplayuneradmsg = false;

        this.closechat = new JButton("←");//返回按钮
        this.closechat.setBounds(0, 0, 50, 20);

        this.closechat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setInchatpanl(false);
                UnDisplayChatUI();
                setNowchatwith("");
            }
        });


        this.havaunreadmsg = new JLabel("您有未读消息");
        this.havaunreadmsg.setBounds(200, 0, 80, 20);
        this.havaunreadmsg.setForeground(Color.RED);
        //add(this.havaunreadmsg);

        this.msgarea = new TextArea();
        this.msgarea.setBounds(0, 20, 285, 400);


        this.msginput = new TextField();
        this.msginput.setBounds(2, 427, 210, 20);

        this.submitmsg = new JButton("发送");
        this.submitmsg.setBounds(215, 427, 60, 20);
        submitmsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sendstringtemp = "sendmsg:" + nowchatwith + "|" + msginput.getText() + "\n";
                try {
                    ChatClient.writeString(out, sendstringtemp);
                    getmsglog(nowchatwith).appendmsg(getNicknamethis() + ":" + msginput.getText() + "\n");//添加聊天记录
                    msginput.setText("");//清空输入框
                    msgarea.setText(getmsglog(nowchatwith).getallmsg());
                } catch (Exception ee) {
                    ee.printStackTrace();
                    //发送失败,服务器出现问题,
                    remove(msginput);
                    remove(msgarea);
                    remove(submitmsg);
                    showconnecterror();
                    connecterror.setText("ta下线了");
                }
            }
        });


        msglog = new ArrayList<MsgLog>();
        nowchatwith = "";
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        iplabel = new JLabel("服务器IP");
        iplabel.setBounds(30, 117, 60, 20);

        ip = new JTextField();
        ip.setBounds(85, 120, 65, 20);
        ip.setText("127.0.0.1");

        portlabel = new JLabel("端口号");
        portlabel.setBounds(160, 117, 40, 20);

        port = new JTextField();
        port.setBounds(208, 120, 40, 20);
        port.setText("1020");

        nicknamelabel = new JLabel();
        nicknamelabel.setText("昵称");
        nicknamelabel.setBounds(70, 170, 60, 20);

        nickname = new JTextField();
        nickname.setBounds(115, 170, 80, 20);
        nickname.setText("DafaultNickname");

        loginbtn = new JButton("登陆");
        loginbtn.setBounds(120, 300, 60, 20);

        connecterror = new JLabel("登陆失败");
        connecterror.setBounds(115, 210, 60, 20);


        //测试按钮
        JButton testbtn = new JButton("testbtn");
        testbtn.setBounds(200, 0, 75, 20);
        ChatClient that = this;
        testbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }
        });
        //TODO 如果测试,添加testbtn
        //add(testbtn);


        loginbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String ipadress = "";
                int portnum = 0;
                String nicknametemp = "";

                try {
                    ipadress = ip.getText();
                    portnum = Integer.parseInt(port.getText());
                    nicknametemp = nickname.getText();

                } catch (Exception ee) {
                    removelogincomponents();
                    showconnecterror();


                }


                int loginresult = loginfunc(ipadress, portnum);
                if (loginresult == 0) {
                    //System.out.println("连接服务器失败");
                    removelogincomponents();
                    showconnecterror();


                } else if (loginresult == 1) {
                    //System.out.println("连接服务器成功");

                    setNickname(nicknametemp);

                    //等待子线程解析的昵称列表消息
                    while (that.nicknamelist == null) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }

                    setTitle(getNicknamethis());
                    removelogincomponents();

                    add(tittle);
                    add(getnicknamepanel());

                    validate();// 重构内容面板
                    repaint();// 重绘内容面板

                }
            }
        });


        setLayout(null);//设置绝对布局
        setTitle("聊天客户端");
        setVisible(true);
        setResizable(false);


        add(iplabel);
        add(ip);
        add(portlabel);
        add(port);
        add(nickname);
        add(nicknamelabel);
        add(loginbtn);

        setBounds(600, 200, 290, 500);


    }
}


