package swarm_wars_library.network;

import swarm_wars_library.input.Input;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NetworkClientFunctions {

    private static int interval = 1000/60;
    private static int sleepInterval = 1000/60;
    private static boolean playNetworkGame = false;

    public static void startNewtork() {
        if(!playNetworkGame) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    GameClient.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            GameClient.countDownLatch.await();
            Thread.sleep(sleepInterval);
        } catch (Exception e) {
            System.out.println("FAILED");
            e.printStackTrace();
        }
    }

    public static int getPlayerIdFromUser() {
        if(!playNetworkGame) return 1;
        System.out.println("Enter your id");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    public static void cleanBuffer() {
        if(!playNetworkGame) return;
        MessageHandlerMulti.refreshClientReceiveBuffer();
    }

    public static void sendConnect(int id) {
        if(!playNetworkGame) return;
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(Headers.TYPE, Constants.CONNECT);
        m.put(Headers.PLAYER, id);
        MessageHandlerMulti.putPackage(m);
        System.out.println("Sent CONNECT");
        try{Thread.sleep(interval);}
        catch (Exception e) {
            System.out.println("FAILED");
            e.printStackTrace();
        }
    }

    public static void sendSetup(int id) {
        if(!playNetworkGame) return;
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(Headers.TYPE, Constants.SETUP);
        m.put(Headers.PLAYER, id);
        MessageHandlerMulti.putPackage(m);
        System.out.println("Sent SETUP");
        try{Thread.sleep(interval);}
        catch (Exception e) {
            System.out.println("FAILED");
            e.printStackTrace();
        }
    }

    public static void sendStart(int id) {
        if(!playNetworkGame) return;
        if(id == 0){
             System.out.println("Try to send START");

            while (!MessageHandlerMulti.gameStarted) {
                Map<String, Object> m = new HashMap();
                m.put(Headers.TYPE, Constants.START);
                MessageHandlerMulti.putPackage(m);
                try{Thread.sleep(interval);}
                catch (Exception e) {
                    System.out.println("FAILED");
                    e.printStackTrace();
                }
            }

            System.out.println("Sent START");
        }
    }

    public static void awaitStart() {
        if(!playNetworkGame) return;
         System.out.println("Game not started yet");
        while (!MessageHandlerMulti.gameStarted) {
            try{Thread.sleep(interval);}
            catch (Exception e) {
                 System.out.println("FAILED");
                e.printStackTrace();
            }
        }
        System.out.println("Game started");
    }


    public static void sendOperation(int id, int frame, Input i) {
        if(!playNetworkGame) return;
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(Headers.TYPE, Constants.OPERATION);
        m.put(Headers.PLAYER, id);
//        m.put(Headers.W, i.getMoveUp());
//        m.put(Headers.A, i.getMoveLeft());
//        m.put(Headers.D, i.getMoveRight());
//        m.put(Headers.S, i.getMoveDown());
//        MessageHandlerMulti.putPackage(m);
//        System.out.println("Sent OPERATION - Player:" + id + " Frame:" + frame);
    }

    public static Map getPackage(int id, int frame) {
        Map<String, Object> rev = null;
        if(!playNetworkGame) return rev;
        int getId = Math.abs(id - 1);
//         System.out.println("Get OPERATION - Player " + id + " try to get player:" + getId + " frame:" + frame);
        while (rev == null) {
//            System.out.println("Player " + id + " trying to get player " + Math.abs(id - 1) + "s package with frame number " + frame);
            rev = MessageHandlerMulti.getPackage(getId, frame);
            if (rev == null) {
                // System.out.println("Did not get wanted package, try again, main game waiting");
            }
            try{Thread.sleep(interval);}
            catch (Exception e) {
                // System.out.println("FAILED");
                e.printStackTrace();
            }
        }
//        System.out.println("Get OPERATION - Player " + id + " try to get player:" + getId + " frame:" + frame);
        return rev;
    }

    public static void sendEnd(int id) {
        if(!playNetworkGame) return;
        Map m = new HashMap<String, Object>();
        m.put(Headers.TYPE, Constants.END);
        m.put(Headers.PLAYER, id);
        MessageHandlerMulti.putPackage(m);
        System.out.println("Player " + id + " Game ends");
    }

    public static void threadSleep(){
        if(!playNetworkGame) return;
        try{Thread.sleep(interval);}
        catch (Exception e) {
            System.out.println("FAILED");
            e.printStackTrace();
        }
    }

    public static boolean isPlayNetworkGame() {
        return playNetworkGame;
    }

    public static void setPlayNetworkGame(boolean playNetworkGame) {
        NetworkClientFunctions.playNetworkGame = playNetworkGame;
    }
}
