package com.watabou.pixeldungeon.Network;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.utils.Random;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

class ClientThread extends Thread {

    public ObjectInputStream readStream;
    public ObjectOutputStream writeStream;
    public int threadID;
    protected Socket clientSocket = null;

    public ClientThread(int ThreadID, Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            readStream = new ObjectInputStream(clientSocket.getInputStream());
            writeStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.threadID = ThreadID;
            this.start(); //auto start
        } catch (IOException e) {
            GLog.n(e.getMessage());
            disconnect();
        }
    }

    public void run() {
        //socket read
        try {
            int code = (int) readStream.readObject();
            switch (code) {
                //Level block
                case (Codes.LEVEL_MAP): {SendData.sendLevelMap(Dungeon.level,threadID); break;}
                case (Codes.LEVEL_VISITED): {SendData.sendLevelVisited(Dungeon.level,threadID);break;}
                case (Codes.LEVEL_MAPPED):{SendData.sendLevelMapped(Dungeon.level,threadID);break;}
                //hero block
                case (Codes.HERO_CLASS):{ //create new Hero
                    int classID =(int) readStream.readObject();
                    HeroClass curClass;
                    if (classID < 1 || classID > 4) {
                        if (classID != 0) {
                            GLog.w("Incorrect classID:{0}; threadID:{1}", classID, threadID);
                        }
                        classID = Random.Int(1, 4);
                    }
                    switch (classID){
                        /*case (1)*/default: GLog.h("ClassID incorrect:{0}; threadID:{1}",classID,threadID);
                        case (1): curClass=HeroClass.WARRIOR;break;
                        case (2): curClass=HeroClass.MAGE;break;
                        case (3): curClass=HeroClass.ROGUE;break;
                        case (4): curClass=HeroClass.HUNTRESS;break;
                    }
                    Hero newHero=new Hero();
                    newHero.live();
                    curClass.initHero(newHero);
                    //todo PLACE HERO TO THE LEVEL
                }

                //control block
                case (Codes.CELL_SELECT):{}//todo
                default: {
                    GLog.n("BadCode:{0}", code);
                    break;
                }
            }
        } catch (Exception e) {
            disconnect();//  need?
        }

    }

    public <T> void  send(int code, T ...  data) {
        try {
            writeStream.writeObject(code);
            for (int i=0; i<data.length;i++){
                writeStream.writeObject(data[i]);
            };
            writeStream.flush();
        } catch (Exception e) {
            disconnect();
        }
    }

    public static <T> void sendAll(int code, T ...  data){
        for  (int i=0;i<Server.clients.length;i++){
            Server.clients[i].send(code, data);
        }
    }

    public void disconnect() {
        try {
            clientSocket.close(); //it creates exception when we will wait client data
        } catch (Exception e) {
        }
        readStream = null;
        writeStream = null;
        clientSocket = null;
        Server.clients[threadID] = null;

    }
}
