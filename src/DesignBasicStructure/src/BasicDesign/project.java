package BasicDesign;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class project implements DataBaseFunctions{
	private static final String tableName="Project";
	private String name;
	private int budget;
	private Date StartDate;
	private Date DeadLine;
	private boolean Done;
	private static Database db= new Database();
	private int PID;
	
	/**
	 * Default costructor
	 */
	public project(){
		
	}
	
	/**
	 * Constructor for project class
	 * This constructor is used to create objects to insert into the database
	 * id is auto increment and it generates from database so we do not have id in this constructor
	 * @param name Name of project
	 * @param sd Start date of project
	 * @param dl Deadline of project
	 * @param budget Dedicated Budget to the project
	 */
	public project(String name, String sd, String dl, int budget, boolean done){
		this.name=name;
		this.budget=budget;
		this.StartDate=this.CastStringToDate(sd);
		this.DeadLine=this.CastStringToDate(dl);
		this.Done=done;
	}
	/**
	 * This constructor is for internal use to fetch data from database
	 * this is private because we do not want the user assigned id to the project
	 * @param id the unique value for project
	 * @param name Name of project
	 * @param sd Start date of project
	 * @param dl Deadline of project
	 * @param budget Dedicated Budget to the project
	 */
	private project(int id, String name, String sd, String dl, int budget, boolean done){
		this.PID=id;
		this.name=name;
		this.budget=budget;
		this.StartDate=this.CastStringToDate(sd);
		this.DeadLine=this.CastStringToDate(dl);
		this.Done=done;
	}
	/**
	 * Setter for name of project which is used in update method
	 * @param name updated name of project
	 */
	public void setName(String name){
		this.name=name;
	}
	
	/**
	 * Setter for budget of project which is used in update method
	 * @param budget updated budget of project
	 */
	public void setBudget(int budget){
		this.budget=budget;
	}
	/**
	 * Setter for Start date of project which is used in update method
	 * @param date updated date of project
	 */
	public void setStartDate(String date){
		this.StartDate=this.CastStringToDate(date);
	}
	/**
	 * Setter for Dead Line of project which is used in update method
	 * @param deadline updated date of deadline
	 */
	public void setDeadLine(String deadline){
		this.DeadLine=this.CastStringToDate(deadline);
	}
	
	/**
	 * Setter for done attribute of project which is used in update method
	 * @param done updated status of project
	 */
	public void setDone(boolean done){
		this.Done=done;
	}
	/**
	 * This Method insert the information of new project inside database
	 * if fetches the Id from inserted row and assign it to the PID
	 */
	@Override
	public boolean Create() {
		String sql = "INSERT INTO "+tableName + " (ProjectName, StartDate, DeadLine, Budget, Done";
		sql+=") VALUES ('"+ this.name + "' ,'" + this.CastDateToString(this.StartDate) + "' ,'" + this.CastDateToString(this.DeadLine) + "' ," + this.budget + " , 0);";
		
		if(this.db.commitQuery(sql)){
			String sqlTemp="SELECT last_insert_rowid() AS tempID;";
			ResultSet rs=this.db.query(sqlTemp);
			try {
				this.PID=rs.getInt("tempID");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}else{
			return false;
		}

	}
	
	/**
	 * This method update an object which is fetched from database
	 */
	@Override
	public boolean update() {
		String sql="UPDATE "+ tableName + " SET";
		sql+=" ProjectName = "+ this.name;
		sql+=" StartDate = "+ this.StartDate;
		sql+=" DeadLine = "+ this.DeadLine;
		sql+=" Budget = "+ this.budget;
		sql+=" Done = "+ this.Done;
		sql+=" WHERE ID = " + this.PID + " ;";
		return this.db.commitQuery(sql);
	}

	/**
	 * This method delete an object from database
	 */
	@Override
	public boolean delete() {
		String sql="DELETE FROM "+ tableName;
		sql+=" WHERE ID = "+this.PID  + " ;";
		return this.db.commitQuery(sql);
	}
	/**
	 * This is the function to create task for project object
	 * @param name the name of the task
	 * @param budget the dedicated budget for the task
	 * @param start the starting date for the task
	 * @param deadline the deadline for the task
	 * @return true if it registers in database and false if there is an error
	 */
	public boolean addTask(String name, int budget, String start, String deadline){
		Task temp= new Task(name, budget, this.PID, start, deadline);
		if(temp.Create()){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Makes Date object to the String format which is easier to put in database
	 * @param input Date object that we want to cast to String
	 * @return String format of Date
	 */
	public String CastDateToString(Date input){
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
		String date = temp.format(input);
		return date;
	}
	/**
	 * Makes String to Date object
	 * @param input this is a String format variable
	 * @return Date object
	 */
	public Date CastStringToDate(String input){
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date=temp.parse(input);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method finds the last inserted id in the table.
	 * ID in the table is auto increment
	 * @return if there is no error last inserted id, if there is any error -1
	 */
	public static int getLastInsertedId(){
		String sqlTemp="SELECT last_insert_rowid() AS tempID;";
		ResultSet rs=db.query(sqlTemp);
		try {
			return (rs.getInt("tempID"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	/**
	 * It fetches the data from database which has the given ID 
	 * and create one object of project and returns it
	 * @param id is the ID that we search for that
	 * @return the data inside the row of selected table
	 */
	public static project findByID(int id){
		project temp=new project();
		String sql=("SELECT * FROM " + tableName + " WHERE PID = "+ id+ ";");
		ResultSet rs=db.query(sql);
		try{
		while ( rs.next() ) {
	         int idTemp = rs.getInt("PID");
	         String  PnameTemp = rs.getString("ProjectName");
	         String stDateTemp=rs.getString("StartDate");
	         String dlDateTemp=rs.getString("DeadLine");
	         int budgetTemp = rs.getInt("Budget");
	         Boolean doneTemp=rs.getBoolean("Done");
	         temp= new project(idTemp, PnameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp);
	      }
		}catch(Exception e){
			e.getMessage();
			temp=null;
		}
		return temp;
	}
	/**
	 * It fetches the data from database and make one linklist of project objects
	 * @param sql asked sql statement from database
	 * @return Linklist of project objects
	 */
	public static LinkedList findBySql(String sql){
		LinkedList<project>temp= new LinkedList<project>();
		ResultSet rs= db.query(sql);
		try{
			while ( rs.next() ) {
		         int idTemp = rs.getInt("PID");
		         String  PnameTemp = rs.getString("ProjectName");
		         String stDateTemp=rs.getString("StartDate");
		         String dlDateTemp=rs.getString("DeadLine");
		         int budgetTemp = rs.getInt("Budget");
		         Boolean doneTemp=rs.getBoolean("Done");
		         temp.push(new project(idTemp, PnameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp));
		      }
			}catch(Exception e){
				e.getMessage();
				temp=null;
			}
		return temp;
	}
	/**
	 * It fetches all data from database and make a LinkedList 
	 * of project objects and return it
	 * @return LinkedList of Project Objects
	 */
	public static LinkedList findAll(){
		LinkedList<project>temp= new LinkedList<project>();
		String sql="SELECT * FROM " + tableName + ";";
		ResultSet rs= db.query(sql);
		try{
			while ( rs.next() ) {
		         int idTemp = rs.getInt("PID");
		         String  PnameTemp = rs.getString("ProjectName");
		         String stDateTemp=rs.getString("StartDate");
		         String dlDateTemp=rs.getString("DeadLine");
		         int budgetTemp = rs.getInt("Budget");
		         Boolean doneTemp=rs.getBoolean("Done");
		         temp.push(new project(idTemp, PnameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp));
		      }
			}catch(Exception e){
				e.getMessage();
				temp=null;
			}
		return temp;
	}
	
	public String toString(){
		return ("Project info is "+this.PID+" ,"+" Project name is " + this.name + " ,"+this.CastDateToString(StartDate)+" ,"+this.CastDateToString(DeadLine)+" ,"+this.budget);
	}

}
