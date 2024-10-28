/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connection.DatabaseConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import model.UserModel;
/**
 *
 * @author admin
 */
public class UserController {
    //  SQL
    private final String INSERT_USER = "INSERT INTO users (username, password, score, win, draw, lose, avgCompetitor, avgTime) VALUES (?, ?, 0, 0, 0, 0, 0, 0)";
    
    private final String CHECK_USER = "SELECT userId from users WHERE username = ? limit 1";
    
    private final String LOGIN_USER = "SELECT username, password, score FROM users WHERE username=? AND password=?";
    
    private final String GET_INFO_USER = "SELECT username, password, score, win, draw, lose, avgCompetitor, avgTime FROM users WHERE username=?";
    
    private final String UPDATE_USER = "UPDATE users SET score = ?, win = ?, draw = ?, lose = ?, avgCompetitor = ?, avgTime = ? WHERE username=?";
    //  Instance
    private final Connection con;
    
    public UserController() {
        this.con = DatabaseConnection.getInstance().getConnection();
        System.err.println("Connect success");
    }

    public String register(String username, String password) {
    String result = "success;";
    
    try (Connection con = DatabaseConnection.getInstance().getConnection();
         PreparedStatement checkUserStmt = con.prepareStatement(CHECK_USER);
         PreparedStatement insertUserStmt = con.prepareStatement(INSERT_USER)) {

        // Kiểm tra người dùng đã tồn tại chưa
        checkUserStmt.setString(1, username);
        try (ResultSet rs = checkUserStmt.executeQuery()) {
            if (rs.next()) {
                return "failed;User Already Exists";
            }
        }

        // Nếu người dùng chưa tồn tại, thực hiện việc thêm người dùng mới
        insertUserStmt.setString(1, username);
        insertUserStmt.setString(2, password);
        insertUserStmt.executeUpdate();
        
    } catch (SQLException e) {
        e.printStackTrace();
        result = "failed;Error occurred";
    }

    return result;
}

  
    public String login(String username, String password) {
    String result = null;
    
    try (PreparedStatement p = con.prepareStatement(LOGIN_USER)) {
        // Set parameters for the query
        p.setString(1, username);
        p.setString(2, password);
        
        try (ResultSet r = p.executeQuery()) {
            if (r.next()) { // Use r.next() to move to the first row
                float score = r.getFloat("score");
                result = "success;" + username + ";" + score;
            } else {
                result = "failed;Please enter the correct account password!";
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Handle the exception appropriately
    }
    
    return result;
}

    public List<UserModel> getAllUsers() {
        List<UserModel> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UserModel user = new UserModel();
                user.setUserName(resultSet.getString("username"));
                user.setScore(resultSet.getFloat("score"));
                user.setWin(resultSet.getInt("win"));
                user.setLose(resultSet.getInt("lose"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    public String getInfoUser(String username) {
        UserModel user = new UserModel();
        try {
            PreparedStatement p = con.prepareStatement(GET_INFO_USER);
            p.setString(1, username);
            
            ResultSet r = p.executeQuery();
            while(r.next()) {
                user.setUserName(r.getString("username"));
                user.setScore(r.getFloat("score"));
                user.setWin(r.getInt("win"));
                user.setDraw(r.getInt("draw"));
                user.setLose(r.getInt("lose"));
                user.setAvgCompetitor(r.getFloat("avgCompetitor"));
                user.setAvgTime(r.getFloat("avgTime"));
            }
            return "success;" + user.getUserName() + ";" + user.getScore() + ";" + user.getWin() + ";" + user.getDraw() + ";" + user.getLose() + ";" + user.getAvgCompetitor() + ";" + user.getAvgTime() ;
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return null;
    }
    
    public boolean updateUser(UserModel user) throws SQLException {
        boolean rowUpdated;
        PreparedStatement p = con.prepareStatement(UPDATE_USER);
        //  Login User 
        p.setFloat(1, user.getScore());
        p.setInt(2, user.getWin());
        p.setInt(3, user.getDraw());
        p.setInt(4, user.getLose());
        p.setFloat(5, user.getAvgCompetitor());
        p.setFloat(6, user.getAvgTime());
        p.setString(7, user.getUserName());

//            ResultSet r = p.executeQuery();
        rowUpdated = p.executeUpdate() > 0;
        return rowUpdated;
    }

    public UserModel getUser(String username) {
        UserModel user = new UserModel();
        try {
            PreparedStatement p = con.prepareStatement(GET_INFO_USER);
            p.setString(1, username);
            
            ResultSet r = p.executeQuery();
            while(r.next()) {
                user.setUserName(r.getString("username"));
                user.setScore(r.getFloat("score"));
                user.setWin(r.getInt("win"));
                user.setDraw(r.getInt("draw"));
                user.setLose(r.getInt("lose"));
                user.setAvgCompetitor(r.getFloat("avgCompetitor"));
                user.setAvgTime(r.getFloat("avgTime"));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return null;
    }
}
