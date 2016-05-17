package BasicDesign;
import java.sql.*;
public class Database {
	/**
	 * Connection for connect to database
	 */
	private Connection c = null;
	public Database(){
	    try {
	      Class.forName("org.sqlite.JDBC");
	      this.c = DriverManager.getConnection("jdbc:sqlite:src/Resources/ProjectManagement.db");
	      c.setAutoCommit(false);
	      if(c !=null){
	      System.out.println("Opened database successfully");
	      }
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	  }
	public ResultSet query(String sql){
		try{
		Statement stmt=this.c.createStatement();
		ResultSet rs=stmt.executeQuery(sql);
		return rs;
		}catch(Exception e){
			e.getMessage();
			return null;
		}
	}
	
	public void closeConnection(){
		try {
			this.c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection getConnection(){
		return this.c;
	}
}
