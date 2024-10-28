/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import model.MediaModel;

/**
 *
 * @author ASUS
 */
public class MediaController {
    private final String GET_4_MEDIA_RANDOM = "SELECT url, result, type, urlImage FROM media ORDER BY RAND() LIMIT 4";
    private final Connection con;

    public MediaController() {
        this.con = DatabaseConnection.getInstance().getConnection();
        System.err.println("Connect success");
    }
    
    
    public String getRamdomMedia() throws SQLException{
        List<MediaModel> mediaList = new ArrayList<>();
        try {
            PreparedStatement p = con.prepareStatement(GET_4_MEDIA_RANDOM);
            ResultSet rs = p.executeQuery();
            
            while(rs.next()) {
                MediaModel media = new MediaModel();
                media.setUrl(rs.getString("url"));
                media.setResutl(rs.getString("result"));
                media.setType(rs.getString("type"));
                media.setUrlImage(rs.getString("urlImage"));
                mediaList.add(media);
            }
            StringJoiner joiner = new StringJoiner(";");
            for(MediaModel media : mediaList) {
                String mediaString = media.getUrl() + "-" + media.getResutl();
                joiner.add(mediaString);
            }
            System.err.println(joiner.toString());
            return joiner.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return null;
       
    }
}