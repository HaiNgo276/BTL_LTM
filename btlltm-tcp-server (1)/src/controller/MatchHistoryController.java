package controller;

import connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.MatchHistoryModel;


public class MatchHistoryController {
    
    private final Connection con;
    
    public MatchHistoryController() {
        this.con = DatabaseConnection.getInstance().getConnection();
        System.err.println("Connect success");
    }
    
    
    public boolean saveMatchHistory(MatchHistoryModel matchHistory) {
        String query = "INSERT INTO match_history (player1Id, player2Id, player1Name, player2Name, player1Score, player2Score, dateTime) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            
            PreparedStatement stmt = con.prepareStatement(query);
            
            stmt.setInt(1, matchHistory.getPlayer1Id());
            stmt.setInt(2, matchHistory.getPlayer2Id());
            stmt.setString(3, matchHistory.getPlayer1Name());
            stmt.setString(4, matchHistory.getPlayer2Name());
            stmt.setFloat(5, matchHistory.getPlayer1Score());
            stmt.setFloat(6, matchHistory.getPlayer2Score());
            stmt.setTimestamp(7, new java.sql.Timestamp(matchHistory.getDateTime().getTime()));

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
