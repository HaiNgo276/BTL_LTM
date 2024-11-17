package service;

import controller.MatchHistoryController;
import controller.UserController;
import helper.CountDownTimer;
import helper.CustumDateTimeFormatter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import model.MatchHistoryModel;
import model.UserModel;
import run.ServerRun;

public class Room {
    String id;
    String time = "00:00";
    Client client1 = null, client2 = null;
    ArrayList<Client> clients = new ArrayList<>();
    
    boolean gameStarted = false;
    CountDownTimer matchTimer;
    CountDownTimer waitingTimer;
    
    String resultClient1;
    String resultClient2;
    
    String playAgainC1;
    String playAgainC2;
    String waitingTime= "00:00";

    public LocalDateTime startedTime;

    public Room(String id) {
        // room id
        this.id = id;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame(int t) {
        gameStarted = true;
        
        matchTimer = new CountDownTimer(t);
        matchTimer.setTimerCallBack(
            null,
            (Callable) () -> {
                time = "" + CustumDateTimeFormatter.secondsToMinutes(matchTimer.getCurrentTick());
                System.out.println(time);
                if (time.equals("00:00")) {
//                    waitingClientTimer();
                    if (resultClient1 == null && resultClient2 == null) {
                        draw();
                        broadcast("RESULT_GAME;success;DRAW;" + client1.getLoginUser() + ";" + client2.getLoginUser() + ";" + id);
                    } 
                }
                return null;
            },
            1
        );
    }
    public void endGame(String result) {
        Date dateTime = new Date(); // Capture the end time
        
        float player1Score;
        float player2Score;

        if (result.equals("DRAW")) {
            player1Score = (float) 0.5; 
            player2Score = (float) 0.5;  
        } else {
            // Nếu có kết quả thắng/thua, tính điểm cho người thắng (1 điểm) và người thua (0 điểm)
            player1Score = (result.equals(client1.getLoginUser())) ? 1 : 0;
            player2Score = (result.equals(client2.getLoginUser())) ? 1 : 0;
        }
        
        int player1Id = new UserController().getUserIdByUsername(client1.getLoginUser());
        int player2Id = new UserController().getUserIdByUsername(client2.getLoginUser());
        // Create match history with scores and times
        MatchHistoryModel matchHistory = new MatchHistoryModel(
            player1Id, player2Id,
            client1.getLoginUser(), player1Score,
            client2.getLoginUser(), player2Score,
            dateTime
        );

        MatchHistoryController matchHistoryController = new MatchHistoryController();
        boolean saved = matchHistoryController.saveMatchHistory(matchHistory);

        if (saved) {
            System.out.println("Match history saved successfully.");
        } else {
            System.out.println("Failed to save match history.");
        }
    }
    
    public void waitingClientTimer() {
        waitingTimer = new CountDownTimer(12);
        waitingTimer.setTimerCallBack(
            null,
            (Callable) () -> {
                waitingTime = "" + CustumDateTimeFormatter.secondsToMinutes(waitingTimer.getCurrentTick());
                System.out.println("waiting: " + waitingTime);
                if (waitingTime.equals("00:00")) {
                    if (playAgainC1 == null && playAgainC2 == null) {
                        broadcast("ASK_PLAY_AGAIN;NO");
                        deleteRoom();
                    } 
                }
                return null;
            },
            1
        );
    }
    
    public void deleteRoom () {
        client1.setJoinedRoom(null);
        client1.setcCompetitor(null);
        client2.setJoinedRoom(null);
        client2.setcCompetitor(null);
        ServerRun.roomManager.remove(this);
    }
    
    public void resetRoom() {
        gameStarted = false;
        resultClient1 = null;
        resultClient2 = null;
        playAgainC1 = null;
        playAgainC2 = null;
        time = "00:00";
        waitingTime = "00:00";
    }
    
    public String handleResultClient() throws SQLException {
        String result1 = "", result2 = "";
        boolean ball1 = false, ball2 = false;
        String count1 = "0";
        int score1 = 0, score2 = 0;
        if (resultClient1 != null) {
            String[] splitted1 = resultClient1.split(";");
            result1 = splitted1[4];
            if(splitted1[5].equals("true")){
                ball1=true;
            }
            count1 = splitted1[6];
            score1 = Integer.parseInt(splitted1[7]);
        }
        if (resultClient2 != null) {
            String[] splitted2 = resultClient2.split(";");
            result2 = splitted2[4];
            if(splitted2[5].equals("true")){
                ball2=true;
            }
            score2 = Integer.parseInt(splitted2[7]);
        }
        
        if (resultClient1 == null & resultClient2 == null) {
            draw();
            return "DRAW";
        } else if (resultClient1 != null && resultClient2 == null) {
                return client1.getLoginUser();
        } else if (resultClient1 == null && resultClient2 != null) {
                return client2.getLoginUser();
        } else if (resultClient1 != null && resultClient2 != null) {
            if(count1.equals("1")){
                if ((result1.equals(result2) && ball2) || (!result1.equals(result2)) && ball1) {
                    score1++;
                } else {
                    score2++;
                } 
                if(score1 > score2){
                    client1Win();
                    return client1.getLoginUser();
                }
                else if(score1 < score2){
                    client2Win();
                    return client2.getLoginUser();
                }
                else{
                    draw();
                    return "DRAW";
                }
            }
            else{
                if ((result1.equals(result2) && ball2) || (!result1.equals(result2)) && ball1) {
                    return client1.getLoginUser();
                } else {
                    return client2.getLoginUser();
                } 
            }
        }
        return null;
    }
    
    public void draw () throws SQLException {
        UserModel user1 = new UserController().getUser(client1.getLoginUser());
        UserModel user2 = new UserController().getUser(client2.getLoginUser());
        
        user1.setDraw(user1.getDraw() + 1);
        user2.setDraw(user2.getDraw() + 1);
        
        user1.setScore(user1.getScore()+ 0.5f);
        user2.setScore(user2.getScore()+ 0.5f);
        
        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
        
        endGame("DRAW");
    }
    
    public void client1Win() throws SQLException {
        UserModel user1 = new UserController().getUser(client1.getLoginUser());
        UserModel user2 = new UserController().getUser(client2.getLoginUser());
        
        user1.setWin(user1.getWin() + 1);
        user2.setLose(user2.getLose() + 1);
        
        user1.setScore(user1.getScore()+ 1);
        
        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
        
        endGame(client1.getLoginUser());
    }
    
    public void client2Win() throws SQLException {
        UserModel user1 = new UserController().getUser(client1.getLoginUser());
        UserModel user2 = new UserController().getUser(client2.getLoginUser());
        
        user2.setWin(user2.getWin() + 1);
        user1.setLose(user1.getLose() + 1);
        
        user2.setScore(user2.getScore()+ 1);
        
        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
        
        endGame(client2.getLoginUser());
    }
    
    public void userLeaveGame (String username) throws SQLException {
        if (client1.getLoginUser().equals(username)) {
            client2Win();
        } else if (client2.getLoginUser().equals(username)) {
            client1Win();
        }
    }
    
    // add/remove client
    public boolean addClient(Client c) {
        if (!clients.contains(c)) {
            clients.add(c);
            if (client1 == null) {
                client1 = c;
            } else if (client2 == null) {
                client2 = c;
            }
            return true;
        }
        return false;
    }

    public boolean removeClient(Client c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }

    // broadcast messages
    public void broadcast(String msg) {
       
            clients.forEach((c) -> {
            c.sendData(msg);
        });
    }
    
    public void broadcastFile(String msg) {
    clients.forEach((c) -> {
        c.sendFile(msg); // Gửi toàn bộ thông điệp cho từng client
    });
}

    
    public Client find(String username) {
        for (Client c : clients) {
            if (c.getLoginUser()!= null && c.getLoginUser().equals(username)) {
                return c;
            }
        }
        return null;
    }

    // gets sets
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient1() {
        return client1;
    }

    public void setClient1(Client client1) {
        this.client1 = client1;
    }

    public Client getClient2() {
        return client2;
    }

    public void setClient2(Client client2) {
        this.client2 = client2;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }
    
    public int getSizeClient() {
        return clients.size();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResultClient1() {
        return resultClient1;
    }

    public void setResultClient1(String resultClient1) {
        this.resultClient1 = resultClient1;
    }

    public String getResultClient2() {
        return resultClient2;
    }

    public void setResultClient2(String resultClient2) {
        this.resultClient2 = resultClient2;
    }

    public String getPlayAgainC1() {
        return playAgainC1;
    }

    public void setPlayAgainC1(String playAgainC1) {
        this.playAgainC1 = playAgainC1;
    }

    public String getPlayAgainC2() {
        return playAgainC2;
    }

    public void setPlayAgainC2(String playAgainC2) {
        this.playAgainC2 = playAgainC2;
    }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }
    
    
}
