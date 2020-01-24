/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universityapp;
import Services.UniversityDB;
import Data.Departament;
import Data.Lector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rovet
 */
public class UniversityApp {


    public static void main(String[] args) {
    
   UniversityDB univerdb=new UniversityDB();
      try{
    BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
    boolean b=true;
   while(b){
         System.out.println("1 - add data; 2 - Departament info; 3 - exit");
        int k=Integer.parseInt(br.readLine());
  switch(k){
      case 1: univerdb.addDepartamentLectors(); break;
      case 2:{
         System.out.println("Name departament");
        String dep_name=br.readLine();
        univerdb.SearchHeadDep(dep_name);
        univerdb.AvgSalaryDep(dep_name);
        univerdb.CountLector(dep_name);
         univerdb.StatLector(dep_name);
        univerdb.GlobalserchLector("v");
      }break;
       case 3:b=false;
  }  
     }
   }
  catch (IOException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }

  
    }

}
