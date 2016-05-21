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
	
	/**
	 * Make query from Database according to asked sql
	 * @param sql asked to fetch some data
	 * @return ResultSet object which contains the fetched data from database
	 */
	public ResultSet query(String sql){
		ResultSet rs;
		try{
		Statement stmt=this.c.createStatement();
		rs=stmt.executeQuery(sql);
		}catch(Exception e){
			e.getMessage();
			rs= null;
		}
		return rs;
	}
	/**
	 * 
	 * @param sql sent sql to update, delete, create
	 * @return
	 */
	public boolean commitQuery(String sql){
		try{
			Statement stmt= this.c.createStatement();
			stmt.executeUpdate(sql);
			this.c.commit();
			return true;
		}catch(Exception e){
			e.getMessage();
			return false;
		}
	}
	
	/**
	 * Close the opened connection
	 */
	public void closeConnection(){
		try {
			this.c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Getter of connection object which connect to database
	 * @return the connection object
	 */
	public Connection getConnection(){
		return this.c;
	}
}
