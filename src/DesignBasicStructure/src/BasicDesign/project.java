package BasicDesign;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

	@Override
	public boolean Create() {
		String sql = "INSERT INTO "+tableName+" VALUES ("+ this.name+" ,"+this.budget+" ,"+this.StartDate+" ,"+this.DeadLine+")";
		 try{
		PreparedStatement ps = db.getConnection().prepareStatement(sql,
		        Statement.RETURN_GENERATED_KEYS);
		 
		ps.execute();
		 
		ResultSet rs = ps.getGeneratedKeys();
		if (rs.next()) {
		    this.PID = rs.getInt(1);
		}
		 return true;
		 }catch(Exception e){
			 e.getMessage();
			 return false;
		 }
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean addTask(String name, int budget, String start, String deadline){
		task temp= new task(name, budget, this.PID, start, deadline);
		return true;
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
	 * @return last inserted id
	 */
	//public static int getLastInsertedId(){
		
	//}
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
	/**
	 * task is inner class because we don't want to instantiate it without project
	 * @author Ali
	 *
	 */
	private class task implements DataBaseFunctions{
		private final String tableName="Task";
		private String name;
		public int Tbudget;
		private Date TStartDate;
		private Date TdeadLine;
		private int Pid;
		private int Tid;
		public task(String name,int budget, int pid, String tstartdate, String tdeadline) {
			this.name=name;
			this.Pid=pid;
			this.Tbudget=budget;
			this.TStartDate=CastStringToDate(tstartdate);
			this.TdeadLine=CastStringToDate(tdeadline);
		}
		@Override
		public boolean Create() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean update() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean delete() {
			// TODO Auto-generated method stub
			return false;
		}

		
		
	}


}
