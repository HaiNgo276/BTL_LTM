/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 *
 * @author ASUS
 */
public class MediaModel implements Serializable{
    private static final long serialVersionUID = 2L;
    
    private String url;
    private String type;
    private String resutl;
    private String urlImage;

    public MediaModel() {
    }

    public MediaModel(String url, String type, String resutl) {
        super();
        this.url = url;
        this.type = type;
        this.resutl = resutl;
    }

    public MediaModel(String url, String type, String resutl, String urlImage) {
        super();
        this.url = url;
        this.type = type;
        this.resutl = resutl;
        this.urlImage = urlImage;
    }

    public MediaModel(String url, String resutl) {
        super();
        this.url = url;
        this.resutl = resutl;
    }

    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResutl() {
        return resutl;
    }

    public void setResutl(String resutl) {
        this.resutl = resutl;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
    
    
}
