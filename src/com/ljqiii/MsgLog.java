package com.ljqiii;

import java.util.ArrayList;


/*
 * MsgLog类
 * 用于保存聊天记录
 * 有一个人上线,就会产生该类的一个对象,下线删除
 * */


public class MsgLog {

    String nickname;//聊天人的昵称
    ArrayList<String> msglist;//聊天信息列表
    int unread;//未读消息数量


    public MsgLog(String nicknamein) {
        this.nickname = nicknamein;
        msglist = new ArrayList<String>();
        this.unread = 0;
    }

    //添加消息
    public void appendmsg(String msgin) {
        this.msglist.add(msgin);
    }


    //获取所有聊天记录
    public String getallmsg() {
        String msgtemp = "";
        for (int i = 0; i < msglist.size(); i++) {
            msgtemp = msgtemp + msglist.get(i) + "\n";
        }
        return msgtemp;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ArrayList<String> getMsglist() {
        return msglist;
    }

    public void setMsglist(ArrayList<String> msglist) {
        this.msglist = msglist;
    }

    public int getUnread() {
        return unread;
    }

    public void addUnread() {
        this.unread++;
    }

    //设置未读消息数量为0
    public void setUnread0() {
        this.unread = 0;
    }
}
