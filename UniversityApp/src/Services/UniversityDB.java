/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import universityapp.UniversityApp;

/**
 *
 * @author rovet
 */
public class UniversityDB {
    
    private  static Connection con=null;
    PreparedStatement ps;   
    ResultSet rs=null;
    
static{
    ResultSet rs=null;
     try {
        con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "root");
         System.out.println("connect server true");
 rs=con.prepareStatement("Select datname from pg_database").executeQuery();
 boolean b=false;
 while (rs.next())    
 {
 //  System.out.println(rs.getString("datname"));
if (rs.getString("datname").equals("university")) b=true;
 }
 if (!b){
 con.prepareStatement("create database university").executeUpdate();
           System.out.println("create database");
 }
        con= DriverManager.getConnection("jdbc:postgresql://localhost:5432/university", "postgres", "root");
               System.out.println("connect database true");
          con.prepareStatement(
                  //"create sequence if not exists  public.lectors_id_seq;"
              "create table if not exists public.lectors("
                    + "lec_id serial  not null,"
                  + "surname character varying not null,"
                    + "name character varying not null,"
                    + "position character varying not null,"
                  + "salary real not null,"
                    + "primary key(lec_id)"
                  + ");"
                //  +"create sequence if not exists public.dep_id_seq;"
                 + "create table if not exists public.departament("
                 + "dep_id serial not null primary key,"
                 + "dep_name character varying not null unique,"
                 + "head_name character varying not null"
                  + ");"
                 + "create table if not exists public.lec_dep("
                  + "lec_id int not null,"
                  + "dep_id int not null,"
                  + "primary key (lec_id,dep_id),"
                  + "foreign key (lec_id) references public.lectors(lec_id) ,"
                  + "foreign key (dep_id) references public.departament(dep_id) "
                  + ")").executeUpdate();
         
            System.out.println("schema create");  
       
     } catch (SQLException ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
    
     finally{
        try {
            rs.close();  
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

    private   Lector addLectors(){
   Lector lector= new Lector();  
    try  {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Surname lector");
         lector.setSurname(br.readLine());
         System.out.println("Name lector");
         lector.setName(br.readLine());
         System.out.println("Position lector");
         lector.setPosition(br.readLine());
         System.out.println("Salary lector");
         lector.setSalary(Double.parseDouble(br.readLine()));
     } catch (IOException ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
     insertLectors(lector);
    return lector;
}

public  Departament searchDepartament(){
  
     Departament d=new Departament();
    try { 
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Name departament");
        String dep_name=br.readLine();
        d.setName(dep_name);
          boolean b=readDepartament(dep_name);
         if (!b){
         System.out.println("Name head departament ");
         d.setHead(br.readLine());
         insertDepartament(d);
         }        
    } catch (IOException ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
    return d;
}

public   void addDepartamentLectors() {  
  String text=null;
   try{
    BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
  do{
       con.setAutoCommit(false);
      Departament d=searchDepartament();
         Lector l=addLectors();
             insertLecDep(l, d);
          System.out.println("y - continue; end - exit");
           text=br.readLine();   
          con.commit();
  }while(!text.equals("end"));

   }catch (SQLException | IOException ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
        
    
}
    
    
    
  public void insertDepartament(Departament d){
  
    try{    
     ps= con.prepareStatement("Insert into public.departament (dep_name, head_name) values (?,?)");
     ps.setString(1, d.getName());
     ps.setString(2, d.getHead());
     ps.executeUpdate(); 
     } catch (SQLException ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
     finally{
        try {
            ps.close();  
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public  void insertLectors(Lector lector){  
    try{
     ps=con.prepareStatement("Insert into public.lectors (surname, name, position, salary) values (?,?,?,?)",1);
     ps.setString(1, lector.getSurname());
     ps.setString(2, lector.getName());
     ps.setString(3, lector.getPosition());
     ps.setDouble(4, lector.getSalary());
     ps.executeUpdate();
     rs=ps.getGeneratedKeys();
     if(rs.next())
         lector.setId(rs.getInt("lec_id"));
     } catch (SQLException ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
     finally{
        try {
            ps.close();  
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public void insertLecDep(Lector l, Departament d){   
    try{
        ps=con.prepareStatement("Insert into lec_dep(lec_id, dep_id) "
                      + "SELECT l.lec_id, d.dep_id FROM lectors l, departament d "
             + "where l.lec_id=? and d.dep_name=?");
    ps.setInt(1, l.getId());
     ps.setString(2, d.getName());
     ps.executeUpdate();
     } catch (SQLException ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     } 
        finally{
        try {
            ps.close();  
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}




public boolean readDepartament(String dep_name){
    boolean b=false;
    try { 
    ps=con.prepareStatement("Select dep_name from public.departament where dep_name=?");
          ps.setString(1, dep_name); 
          rs=ps.executeQuery();
          while(rs.next())
              if (rs.getString("dep_name").equals(dep_name)) b=true;                
          } catch (SQLException  ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
    finally{
        try {
            ps.close();  
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        return b;  
          
} 

public void AvgSalaryDep(String dep_name){
     try {
         ps=con.prepareStatement("Select AVG(lectors.salary) as average_salary  from "
                 +"(departament join lec_dep using(dep_id)) join lectors using(lec_id) "
                 +"where departament.dep_name=?");
         ps.setString(1, dep_name);
         rs=ps.executeQuery();
         double avg=0;
         while(rs.next())
          avg=rs.getDouble("average_salary");
         System.out.println("The average salary of " + dep_name + "is "+ avg);  
         
     } catch (SQLException  ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
     finally{
        try {
            ps.close();  
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
}

public void CountLector(String dep_name){
     try {     
         ps=con.prepareStatement("Select COUNT(lectors.lec_id) as count_lectors  from "
                 +"(departament join lec_dep using(dep_id)) join lectors using(lec_id) "
                 +"where departament.dep_name=?");
         ps.setString(1, dep_name);
         rs=ps.executeQuery();
         int count=0;
         while(rs.next())
          count=rs.getInt("count_lectors");
         System.out.println("employee_count= " + count);  
     } catch (SQLException  ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
     finally{
        try {
            ps.close();  
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public void StatLector(String dep_name){
     try {
        
        String[]position=new String[]{"assistent","associate professor","professor"};
        int count=0;
        for (int i=0; i<position.length; i++){
         ps=con.prepareStatement("Select COUNT(lectors.lec_id) as count_lectors  from "
                 +"(departament join lec_dep using(dep_id)) join lectors using(lec_id) "
                 +"where departament.dep_name=? and lectors.position=?");
         ps.setString(1, dep_name);
         ps.setString(2, position[i]);
         rs=ps.executeQuery();
         while(rs.next())
          count=rs.getInt("count_lectors");
           System.out.println(position[i] +" - "+ count);
        }
    
     } catch (SQLException  ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
     finally{
        try {
            ps.close();  
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public void GlobalserchLector(String p){
     try {
         ps=con.prepareStatement("Select lectors.surname, lectors.name from lectors where name like ? ");
        ps.setString(1, "%"+p+"%");
         rs=ps.executeQuery();
         while(rs.next()){
        System.out.println (rs.getString("surname")+" "+rs.getString("name"));  
         }   
         
     } catch (SQLException ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
    finally{
        try {
            ps.close();  
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
public void SearchHeadDep(String dep_name){
    String head=null;
    try {
    ps=con.prepareStatement("Select head_name from public.departament where dep_name=?");
          ps.setString(1, dep_name); 
          rs=ps.executeQuery();
          while(rs.next())   
          head=rs.getString("head_name");
             if (head!=null)
                 System.out.println("Head of "+dep_name+ " department is "+ head);
                 else
               System.out.println("Departament not exist");  
          } catch (SQLException   ex) {
         Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
     }
    finally{
        try {
            ps.close();  
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(UniversityApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}




}
