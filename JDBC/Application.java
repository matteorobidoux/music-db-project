package JDBC;

import java.io.Console;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Application {
    
    static Connection con;
    static String username;

    public static void main(String[] args) throws SQLException, ClassNotFoundException, ParseException{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Scanner s = new Scanner(System.in);
        System.out.print("Enter Username: ");
        username = s.nextLine();

        Console console = System.console();
        String password = new String(console.readPassword("Enter Password: "));
        getConnection(username , password );

        System.out.println("Type the number of the choice");
        // System.out.println("1 -- search contributors and roles to a song");
        // System.out.println("2 -- get a breakdown of a compilation");
        // System.out.println("3 -- update fields of a song");
        // System.out.println("4 -- search contributions and roles that an artist has had");
        // System.out.println("5 -- show roles of an artist");
        System.out.println("1 -- UPDATE a table");
        System.out.println("2 -- INSERT a table");
        System.out.println("3 -- DELETE a table");
        System.out.println("4 -- Prepared statements");

        String choice = s.nextLine();
        if(choice.equals("1")){
            showTables();
            choice = s.nextLine();
            if(choice.equals("1")){
                initComponents();
            }
            if(choice.equals("2")){
                initContributors();
            }
            if(choice.equals("3")){
                initDistribution();
            }
        }
        //updateAuditLog();

    }

    public static void getConnection(String username, String password) throws SQLException{
        System.out.println("connecting");
        con = DriverManager.getConnection("jdbc:oracle:thin:@pdbora19c.dawsoncollege.qc.ca:1521/pdbora19c.dawsoncollege.qc.ca", username, password);
        if(con.isValid(1000)){
        System.out.println("connected");
        }
    }
    public static void updateAuditLog() throws SQLException{
        Date date = new Date(System.currentTimeMillis());
        
        String query = "INSERT INTO AUDITLOG(username, dateinfo) VALUES(?,?)";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setDate(2, date);
        //needs to give an explanation too
        stmt.executeUpdate();
    }

    public static void initComponents() throws SQLException{
        String query = "SELECT * FROM components";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        //List<Components> comp = new ArrayList<>();
        int i=0;
        while(rs.next()){
            i++;
            String componentId = rs.getString("componentid");
            String songId = rs.getString("songid");
            int offsetComponent = rs.getInt("offsetcomponent");
            int durationComponent = rs.getInt("durationcomponent");
            String songUsed = rs.getString("songused");
            int offsetSong = rs.getInt("offsetsong");
            int durationSong = rs.getInt("durationsong");
            Components components = new Components(componentId, songId, offsetComponent, durationComponent, songUsed, offsetSong, durationSong);
            //comp.add(components);
            System.out.println(i+" -- "+components);
        }
        stmt.close();
        //return comp;
    }
    public static void initContributors() throws SQLException{
        String query = "SELECT * FROM contributors";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        int i=0;
        while(rs.next()){
            i++;
            String contributorId = rs.getString("contributorId");
            String name = rs.getString("fullname");
            Contributors contributors = new Contributors(contributorId, name);
            System.out.println(i+" -- "+contributors);
        }
        stmt.close();
    }

    public static void initDistribution() throws SQLException{
        String query = "SELECT * FROM distribution";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        int i=0;
        while(rs.next()){
            i++;
            String songID = rs.getString("songid");
            String recordLabelID = rs.getString("recordlabelid");
            String marketID = rs.getString("marketid");
            Date releaseDate = rs.getDate("distributiondate");
            String title = rs.getString("songtitle");
            Distribution dist = new Distribution(songID, recordLabelID, releaseDate, marketID, title);
            System.out.println(i+" -- "+dist);
        }
        stmt.close();
    }

    public static void getConRoles(String song) throws SQLException{
        String query = "SELECT fullname,rolename FROM contributors"
        +" JOIN rolesconsong  rcs USING(contributorid)"
        +" JOIN roles USING(roleid)"
        +" JOIN song USING(songid)"
        +" JOIN distribution USING(songid)"
        +" WHERE songtitle LIKE ? ";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, song);
        ResultSet rs = stmt.executeQuery();
        //List<String> results = new ArrayList<String>();
        while(rs.next()){
            System.out.println("name: "+rs.getString("fullname") + "|| role: "+ rs.getString("rolname") );
        }

    }

    public static void insertSong(String title, String date, String market, String recordLabel) throws SQLException{
        String query = "INSERT INTO distribution(songtitle, distributiondate, marketid) VALUES(?,?,?)";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, title);
        stmt.setString(2, date);
        if(market.equals("USA")){
         stmt.setString(3, "M0004");   
        }else if(market.equals("JAPAN")){
            stmt.setString(3, "M0002");
        }else if(market.equals("UK")){
            stmt.setString(3, "M0004");
        }
        stmt.executeUpdate();
    }

    public static void showComponents(){

    }

    public static void showTables(){
        System.out.println("1 -- COMPONENTS");
        System.out.println("2 -- CONTRIBUTORS");
        System.out.println("3 -- DISTRIBUTION");
        System.out.println("4 -- MARKETS");
        System.out.println("5 -- RECORDLABEL");
        System.out.println("6 -- ROLES");
        //System.out.println("7 -- ROLESCONSONG");
        System.out.println("7 -- SONG");
        System.out.println("SELECT WHICH TABLE TO MODIFY");


    }

}