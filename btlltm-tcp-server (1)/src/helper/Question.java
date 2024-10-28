/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import controller.MediaController;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author admin
 */
public class Question {
    private static final String[] answers = {"Cho", "Meo", "Dan Bau", "Violin", "Guitar", "Lon", "Chim", 
        "Coi Xe", "Ga", "Dan Nhi", "Saxophone", "Xe May", "Con Vit", "Trong", "Cong Nong"};
   

   
    
    
    public static String renQuestion() {
       MediaController mediaController = new MediaController();
       try{
           String media4 = mediaController.getRamdomMedia();
           String[] splited = media4.split(";");
           String data1 = splited[0]; // url-result
           String data2 = splited[1];
           String data3 = splited[2];
           String data4 = splited[3];
           System.out.println(data1 + " " + data2 + " " + data3 + " " + data4);
           String[] answerData1 = data1.split("-");
           String[] answerData2 = data2.split("-");
           String[] answerData3 = data3.split("-");
           String[] answerData4 = data4.split("-");
           String res="";
           res += answerData1[0] + ";" + randomQuestion(answerData1[1]);
           res += answerData2[0] + ";" + randomQuestion(answerData2[1]);
           res += answerData3[0] + ";" + randomQuestion(answerData3[1]);
           res += answerData4[0] + ";" + randomQuestion(answerData4[1]);
           System.out.println(res);
           return res;
           //dạng : D:\\soud\\chim.mp3;chim;cho;meo;xemay;D:\\soud\\cho.mp3;cho;meo;ga;violin;........
       }catch(SQLException e) {
           e.printStackTrace();
       }
        
        // phần render ra câu hỏi
//        List<Integer> listAnswer = new ArrayList<>();
//        
//        int a = randomInt100();
//        int b = randomInt100();
////        System.out.println("a: " + a);
////        System.out.println("b: " + b);
//        int result = a + b;
////        System.out.println("result: " + result);
//        int answer1 = result + randomInt10();
//        int answer2 = result - randomInt10();
//        int answer3 = result + 10;
//        
////        System.out.println("aanswer 1: " + answer1);
////        System.out.println("aanswer 2: " + answer2);
////        System.out.println("aanswer 3: " + answer3);
//        
//        listAnswer.add(result);
//        listAnswer.add(answer1);
//        listAnswer.add(answer2);
//        listAnswer.add(answer3);
////        System.out.println("--------------------------");
//        Collections.shuffle(listAnswer);
//        int i = 1;
//        String msg = "";
//        msg = msg + a + ";" + b + ";";
//        
//        for(Integer x: listAnswer) {
////            System.out.println("dap an " + i + " : " + x);
//            i++;
//            msg += x + ";";
//        }
        
//        System.out.println(msg);
        return null;
    }
    

    static String randomQuestion(String result){
        List<String> options = new ArrayList<>();
        for (String answer : answers) {
            options.add(answer);
        }
        options.remove(result);
        Collections.shuffle(options, new Random()); // trộn ngẫu nhiên
        String res = result + ";";
        for(int i = 0; i < 3; i++){
            res += options.get(i);
            if(i!=2){
                res += ";";
            }
        }
        res +="-";
        System.out.println(res);
        return res;
    }
//    public static void main(String[] args) {
//        renQuestion();
//    }
}
