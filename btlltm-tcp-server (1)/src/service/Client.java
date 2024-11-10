/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import controller.UserController;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import run.ServerRun;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import model.UserModel;

/**
 *
 * @author admin
 */
public class Client implements Runnable {
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    String loginUser;
    Client cCompetitor;
    
//    ArrayList<Client> clients
    Room joinedRoom; // if == null => chua vao phong nao het

    public Client(Socket s) throws IOException {
        this.s = s;

        // obtaining input and output streams 
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
    }

    @Override
    public void run() {

        String received;
        boolean running = true;

        while (!ServerRun.isShutDown) {
            try {
                // receive the request from client
                received = dis.readUTF();

                System.out.println(received);
                String type = received.split(";")[0];
               
                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "GET_LIST_ONLINE":
                        onReceiveGetListOnline();
                        break;
                    case "RANKING":
                        onReceiveRanking(received);
                        break;
                    case "GET_INFO_USER":
                        onReceiveGetInfoUser(received);
                        break;
                    case "LOGOUT":
                        onReceiveLogout();
                        break;  
                    case "CLOSE":
                        onReceiveClose();
                        break; 
                    // chat
                    case "INVITE_TO_CHAT":
                        onReceiveInviteToChat(received);
                        break;
                    case "ACCEPT_MESSAGE":
                        onReceiveAcceptMessage(received);
                        break;
                    case "NOT_ACCEPT_MESSAGE":
                        onReceiveNotAcceptMessage(received);
                        break;
                    case "LEAVE_TO_CHAT":
                        onReceiveLeaveChat(received);
                        break;
                    case "CHAT_MESSAGE":
                        onReceiveChatMessage(received);
                        break;
                    // play
                    case "INVITE_TO_PLAY":
                        onReceiveInviteToPlay(received);
                        break;
                    case "ACCEPT_PLAY":
                        onReceiveAcceptPlay(received);
                        break;
                    case "NOT_ACCEPT_PLAY":
                        onReceiveNotAcceptPlay(received);
                        break;
                    case "LEAVE_TO_GAME":
                        onReceiveLeaveGame(received);
                        break;
                    case "CHECK_STATUS_USER":
                        onReceiveCheckStatusUser(received);
                        break;
                    case "SUBMIT_RESULT":
                        onReceiveSubmitResult(received);
                        break;
                    case "ASK_PLAY_AGAIN":
                        onReceiveAskPlayAgain(received);
                        break;
                        
                    case "EXIT":
                        running = false;
                }

            } catch (IOException ex) {

                // leave room if needed
//                onReceiveLeaveRoom("");
                break;
            } catch (SQLException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }

//        try {
//            // closing resources 
//            this.s.close();
//            this.dis.close();
//            this.dos.close();
//            System.out.println("- Client disconnected: " + s);
//
//            // remove from clientManager
//            ServerRun.clientManager.remove(this);
//
//        } catch (IOException ex) {
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    // send data fucntions
    public String sendData(String data) {
        //data dạng D:\\soud\\chim.mp3;Chim;Gà;Cho;Meo-D:\\soud:\\meo.mp3;Mèo;Gà;Violin;XeMay-D:\\soud:\\violin.mp3;Violin;Cho;Meo;Ga-D:\\soud\\lon.mp3;Lợn;Mèo;Piano;Trống-
        try {
            this.dos.writeUTF(data);
            return "success";
        } catch (IOException e) {
            System.err.println("Send data failed!");
            return "failed;" + e.getMessage();
        }
    }
    
    
   public void sendFile(String data) {
    System.out.println("Send File: " + data);
    
    // Phân tách dữ liệu
    String[] parts = data.split("-");
    List<String> filePaths = new ArrayList<>();
    List<String[]> descriptions = new ArrayList<>();

    for (int i = 1; i < parts.length; i++) {
        String[] fileData = parts[i].split(";");
        filePaths.add(fileData[0]); // Đường dẫn file
        descriptions.add(Arrays.copyOfRange(fileData, 1, fileData.length)); // Mô tả
    }

    // Gửi dữ liệu
    sendFiles(parts[0], filePaths, descriptions);
    System.out.println("Gửi file thành công");
}

public synchronized void sendFiles(String action, List<String> filePaths, List<String[]> descriptions) {
    // Kiểm tra trước khi gửi
    for (int i = 0; i < filePaths.size(); i++) {
        File file = new File(filePaths.get(i));
        if (!file.exists() || !file.canRead()) {
            System.err.println("File không tồn tại hoặc không thể đọc: " + file.getPath());
            return; // Thoát hàm nếu file không tồn tại
        }
    }

    // Dùng try-with-resources để đảm bảo đóng DataOutputStream
    try {
        // Gửi action
        this.dos.writeUTF(action);

        // Gửi số lượng file
        this.dos.writeInt(filePaths.size());

        // Gửi dữ liệu của từng file
        for (int i = 0; i < filePaths.size(); i++) {
            String descriptionsString = String.join(";", descriptions.get(i));
            this.dos.writeUTF(descriptionsString);

            File file = new File(filePaths.get(i));
            String fileName = file.getName();
            this.dos.writeUTF(fileName);
            this.dos.writeLong(file.length());

            // Gửi nội dung file
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    this.dos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                System.err.println("Lỗi khi đọc file: " + file.getPath());
                e.printStackTrace();
                return; // Thoát hàm nếu có lỗi
            }
        }

        // Flush dữ liệu sau khi gửi tất cả file
        this.dos.flush();
        System.out.println("Gửi tất cả file thành công");

    } catch (IOException e) {
        System.err.println("Gửi dữ liệu thất bại!");
        e.printStackTrace();
    }
}
  
    private void onReceiveLogin(String received) {
        System.out.println("received"+received);
        System.out.println("login");
        if (received == null || !received.contains(";") || received.split(";").length < 3) {
        System.out.println("Invalid login data received.");
        return;
    }
        // get email / password from data
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // check login
        String result = new UserController().login(username, password);
        
        if (result.split(";")[0].equals("success")) {
            // set login user
            this.loginUser = username;
        }
        
        // send result
        sendData("LOGIN" + ";" + result);
        onReceiveGetListOnline();
    }
    
    private void onReceiveRegister(String received) {
        // get email / password from data
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // reigster
        String result = new UserController().register(username, password);

        // send result
        sendData("REGISTER" + ";" + result);
    }
    
    private void onReceiveGetListOnline() {
        String result = ServerRun.clientManager.getListUseOnline();
        
        // send result
        String msg = "GET_LIST_ONLINE" + ";" + result;
        ServerRun.clientManager.broadcast(msg);
    }
    
    private void onReceiveRanking(String received) {
        // Kiểm tra nếu dữ liệu nhận được là "RANKING"
        if (!"RANKING".equals(received.trim())) {
            System.out.println("Invalid request for ranking data.");
            return;
        }

        // Lấy danh sách người dùng từ UserController
        List<UserModel> userList = new UserController().getAllUsers();
        if (userList == null || userList.isEmpty()) {
            System.out.println("No users found.");
            sendData("RANKING;No users available.");
            return;
        }
        // Xây dựng chuỗi dữ liệu xếp hạng theo định dạng "RANKING;user1,score1;user2,score2;..."
        StringBuilder rankingData = new StringBuilder("RANKING");
        for (UserModel user : userList) {
            rankingData.append(";").append(user.getUserName()).append(",").append(user.getWin()).append(",").append(user.getScore());
        }
        // Gửi chuỗi dữ liệu xếp hạng về client
        sendData(rankingData.toString());
    }
    
    private void onReceiveGetInfoUser(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        // get info user
        String result = new UserController().getInfoUser(username);
        
        String status = "";
        Client c = ServerRun.clientManager.find(username);
        if (c == null) {
            status = "Offline";
        } else {
            if (c.getJoinedRoom() == null) {
                status = "Online";
            } else {
                status = "In Game";
            }
        }
                
        // send result
        sendData("GET_INFO_USER" + ";" + result + ";" + status);
    }
    
    private void onReceiveLogout() {
        this.loginUser = null;
        // send result
        sendData("LOGOUT" + ";" + "success");
        onReceiveGetListOnline();
    }
    
    private void onReceiveInviteToChat(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // send result
        String msg = "INVITE_TO_CHAT;" + "success;" + userHost + ";" + userInvited;
        ServerRun.clientManager.sendToAClient(userInvited, msg);
    }
    
    private void onReceiveAcceptMessage(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // send result
        String msg = "ACCEPT_MESSAGE;" + "success;" + userHost + ";" + userInvited;
        ServerRun.clientManager.sendToAClient(userHost, msg);
    }      
      
    private void onReceiveNotAcceptMessage(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // send result
        String msg = "NOT_ACCEPT_MESSAGE;" + "success;" + userHost + ";" + userInvited;
        ServerRun.clientManager.sendToAClient(userHost, msg);
    }      
    
    private void onReceiveLeaveChat(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // send result
        String msg = "LEAVE_TO_CHAT;" + "success;" + userHost + ";" + userInvited;
        ServerRun.clientManager.sendToAClient(userInvited, msg);
    }      
    
    private void onReceiveChatMessage(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        String message = splitted[3];
        
        // send result
        String msg = "CHAT_MESSAGE;" + "success;" + userHost + ";" + userInvited + ";" + message;
        ServerRun.clientManager.sendToAClient(userInvited, msg);
    }    
    
    private void onReceiveInviteToPlay(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // create new room
        joinedRoom = ServerRun.roomManager.createRoom();
        // add client
        Client c = ServerRun.clientManager.find(loginUser);
        joinedRoom.addClient(this);
        cCompetitor = ServerRun.clientManager.find(userInvited);
        
        // send result
        String msg = "INVITE_TO_PLAY;" + "success;" + userHost + ";" + userInvited + ";" + joinedRoom.getId();
        ServerRun.clientManager.sendToAClient(userInvited, msg);
    }
    
    private void onReceiveAcceptPlay(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        String roomId = splitted[3];
        
        Room room = ServerRun.roomManager.find(roomId);
        joinedRoom = room;
        joinedRoom.addClient(this);
        
        cCompetitor = ServerRun.clientManager.find(userHost);
        
        // send result
        String msg = "ACCEPT_PLAY;" + "success;" + userHost + ";" + userInvited + ";" + joinedRoom.getId();
        ServerRun.clientManager.sendToAClient(userHost, msg);
//        joinedRoom.startGame(10);
    }      
      
    private void onReceiveNotAcceptPlay(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        String roomId = splitted[3];
        
        // userHost out room
        ServerRun.clientManager.find(userHost).setJoinedRoom(null);
        // Delete competitor of userhost
        ServerRun.clientManager.find(userHost).setcCompetitor(null);
        
        // delete room
        Room room = ServerRun.roomManager.find(roomId);
        ServerRun.roomManager.remove(room);
        
        // send result
        String msg = "NOT_ACCEPT_PLAY;" + "success;" + userHost + ";" + userInvited + ";" + room.getId();
        ServerRun.clientManager.sendToAClient(userHost, msg);
    }      
    
    private void onReceiveLeaveGame(String received) throws SQLException {
        String[] splitted = received.split(";");
        String user1 = splitted[1];
        String user2 = splitted[2];
        String roomId = splitted[3];
        
        joinedRoom.userLeaveGame(user1);
        
        this.cCompetitor = null;
        this.joinedRoom = null;
        
        // delete room
        Room room = ServerRun.roomManager.find(roomId);
        ServerRun.roomManager.remove(room);
        
        // userHost out room
        Client c = ServerRun.clientManager.find(user2);
        c.setJoinedRoom(null);
        // Delete competitor of userhost
        c.setcCompetitor(null);
        
        // send result
        String msg = "LEAVE_TO_GAME;" + "success;" + user1 + ";" + user2;
        ServerRun.clientManager.sendToAClient(user2, msg);
    }      
    
    private void onReceiveCheckStatusUser(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        
        String status = "";
        Client c = ServerRun.clientManager.find(username);
        if (c == null) {
            status = "OFFLINE";
        } else {
            if (c.getJoinedRoom() == null) {
                System.out.println(c.getJoinedRoom());
                status = "ONLINE";
            } else {
                status = "INGAME";
            }
        }
        // send result
        sendData("CHECK_STATUS_USER" + ";" + username + ";" + status);
    }
            
    
    
    private void onReceiveSubmitResult(String received) throws SQLException {
        String[] splitted = received.split(";");
        String user1 = splitted[1];
        String user2 = splitted[2];
        String roomId = splitted[3];
        String isBall = splitted[5];
        String count = splitted[6];
        
        if (user1.equals(joinedRoom.getClient1().getLoginUser())) {
            joinedRoom.setResultClient1(received);
        } else if (user1.equals(joinedRoom.getClient2().getLoginUser())) {
            joinedRoom.setResultClient2(received);
        }
        if(joinedRoom.getResultClient1()!=null && joinedRoom.getResultClient2()!=null) {
//            while (!joinedRoom.getTime().equals("00:00") && joinedRoom.getTime() != null) {
//                System.out.println(joinedRoom.getTime());
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } 
            System.out.println(joinedRoom.getResultClient1());
            String data = "RESULT_GAME;success;" + joinedRoom.handleResultClient() 
                    + ";" + joinedRoom.getClient1().getLoginUser() + ";" + joinedRoom.getClient2().getLoginUser() + ";" + joinedRoom.getId() + ";" 
                    + count + ";" + joinedRoom.getResultClient1().split(";")[4] + ";" + joinedRoom.getResultClient2().split(";")[4]
                    ;
            System.out.println(data);
            joinedRoom.broadcast(data);
    //        ServerRun.clientManager.sendToAClient(user1, data);
            joinedRoom.setResultClient1(null);
            joinedRoom.setResultClient2(null);
            if(count.equals("0")){
    //            joinedRoom.startGame(13);
            }
            else{
                Room room = ServerRun.roomManager.find(joinedRoom.getId());
                ServerRun.roomManager.remove(room);
                this.joinedRoom = null;
                this.cCompetitor = null;
                Client c = ServerRun.clientManager.find(user2);
                c.setJoinedRoom(null);
                c.setcCompetitor(null);
            }
        }
    } 
    
    private void onReceiveAskPlayAgain(String received) throws SQLException {
        String[] splitted = received.split(";");
        String reply = splitted[1];
        String user1 = splitted[2];
        
        System.out.println("client1: " + joinedRoom.getClient1().getLoginUser());
        System.out.println("client2: " + joinedRoom.getClient2().getLoginUser());
        
        if (user1.equals(joinedRoom.getClient1().getLoginUser())) {
            joinedRoom.setPlayAgainC1(reply);
        } else if (user1.equals(joinedRoom.getClient2().getLoginUser())) {
            joinedRoom.setPlayAgainC2(reply);
        }
        
        while (!joinedRoom.getWaitingTime().equals("00:00")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        
        String result = this.joinedRoom.handlePlayAgain();
        if (result.equals("YES")) {
            joinedRoom.broadcast("ASK_PLAY_AGAIN;YES;" + joinedRoom.getClient1().loginUser + ";" + joinedRoom.getClient2().loginUser);
        } else if (result.equals("NO")) {
            joinedRoom.broadcast("ASK_PLAY_AGAIN;NO;");
            
            Room room = ServerRun.roomManager.find(joinedRoom.getId());
            // delete room            
            ServerRun.roomManager.remove(room);
            this.joinedRoom = null;
            this.cCompetitor = null;
        } else if (result == null) {
            System.out.println("da co loi xay ra huhu");
        }
    }
        
    
    // Close app
    private void onReceiveClose() {
        this.loginUser = null;
        ServerRun.clientManager.remove(this);
        onReceiveGetListOnline();
    }
    
    // Get set
    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public Client getcCompetitor() {
        return cCompetitor;
    }

    public void setcCompetitor(Client cCompetitor) {
        this.cCompetitor = cCompetitor;
    }

    public Room getJoinedRoom() {
        return joinedRoom;
    }

    public void setJoinedRoom(Room joinedRoom) {
        this.joinedRoom = joinedRoom;
    }
    
    
}
