package model;

import java.util.Date;


public class MatchHistoryModel {
    private int player1Id;
    private int player2Id;
    private String player1Name;
    private float player1Score;
    private String player2Name;
    private float player2Score;
    private Date dateTime;

    // Constructor
    public MatchHistoryModel(int player1Id, int player2Id, String player1Name, float player1Score, String player2Name, float player2Score, Date dateTime) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player1Name = player1Name;
        this.player1Score = player1Score;
        this.player2Name = player2Name;
        this.player2Score = player2Score;
        this.dateTime = dateTime;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public float getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(float player1Score) {
        this.player1Score = player1Score;
    }

    public float getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(float player2Score) {
        this.player2Score = player2Score;
    }
      
}
