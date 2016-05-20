package com.sepgroup.sep.project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class ProjectModel implements DataBaseFunctions {
//	private static final String tableName="ProjectModel";
	private String name;
//	private int budget;
//	private Date startDate;
//	private Date deadline;
	private boolean done;
//	private static SQLiteDB db = new SQLiteDB();
	private int projectId;
	
	/**
	 * Default constructor
	 */
	public ProjectModel(){
		
	}
	
	/**
	 * Constructor for ProjectModel class
	 * This constructor is used to create objects to insert into the database
	 * id is auto increment and it generates from database so we do not have id in this constructor
	 * @param name Name of ProjectModel
	 * @param sd Start date of ProjectModel
	 * @param dl Deadline of ProjectModel
	 * @param budget Dedicated Budget to the ProjectModel
	 */
	public ProjectModel(String name, String sd, String dl, int budget, boolean done){
		this.name=name;
//		this.budget=budget;
//		this.startDate =this.CastStringToDate(sd);
//		this.deadline =this.CastStringToDate(dl);
		this.done =done;
	}
	/**
	 * This constructor is for internal use to fetch data from database
	 * this is private because we do not want the user assigned id to the ProjectModel
	 * @param id the unique value for ProjectModel
	 * @param name Name of ProjectModel
	 * @param sd Start date of ProjectModel
	 * @param dl Deadline of ProjectModel
	 * @param budget Dedicated Budget to the ProjectModel
	 */
	private ProjectModel(int id, String name, String sd, String dl, int budget, boolean done){
		this.projectId=id;
		this.name=name;
//		this.budget=budget;
//		this.startDate =this.CastStringToDate(sd);
//		this.deadline =this.CastStringToDate(dl);
		this.done =done;
	}

	public int getProjectId() {
        return projectId;
    }

	@Override
	public boolean create() {
		String sql = "INSERT INTO "+tableName+" VALUES ("+ this.name+" ,"+this.budget+" ,"+this.startDate +" ,"+this.deadline +")";
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
	 * and create one object of ProjectModel and returns it
	 * @param id is the ID that we search for that
	 * @return the data inside the row of selected table
	 */
	public static ProjectModel findByID(int id){
		ProjectModel temp=new ProjectModel();
		String sql=("SELECT * FROM " + tableName + " WHERE PID = "+ id+ ";");
		ResultSet rs=db.query(sql);
		try{
		while ( rs.next() ) {
	         int idTemp = rs.getInt("PID");
	         String  PnameTemp = rs.getString("ProjectName");
	         String stDateTemp=rs.getString("startDate");
	         String dlDateTemp=rs.getString("deadline");
	         int budgetTemp = rs.getInt("Budget");
	         Boolean doneTemp=rs.getBoolean("done");
	         temp= new ProjectModel(idTemp, PnameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp);
	      }
		}catch(Exception e){
			e.getMessage();
			temp=null;
		}
		return temp;
	}
	/**
	 * It fetches the data from database and make one linklist of ProjectModel objects
	 * @param sql asked sql statement from database
	 * @return Linklist of ProjectModel objects
	 */
	public static LinkedList findBySql(String sql){
		LinkedList<ProjectModel>temp= new LinkedList<ProjectModel>();
		ResultSet rs= db.query(sql);
		try{
			while ( rs.next() ) {
		         int idTemp = rs.getInt("PID");
		         String  PnameTemp = rs.getString("ProjectName");
		         String stDateTemp=rs.getString("startDate");
		         String dlDateTemp=rs.getString("deadline");
		         int budgetTemp = rs.getInt("Budget");
		         Boolean doneTemp=rs.getBoolean("done");
		         temp.push(new ProjectModel(idTemp, PnameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp));
		      }
			}catch(Exception e){
				e.getMessage();
				temp=null;
			}
		return temp;
	}
	/**
	 * It fetches all data from database and make a LinkedList 
	 * of ProjectModel objects and return it
	 * @return LinkedList of ProjectModel Objects
	 */
	public static LinkedList getAll(){
		LinkedList<ProjectModel>temp= new LinkedList<ProjectModel>();
		String sql="SELECT * FROM " + tableName + ";";
		ResultSet rs= db.query(sql);
		try{
			while ( rs.next() ) {
		         int idTemp = rs.getInt("PID");
		         String  PnameTemp = rs.getString("ProjectName");
		         String stDateTemp=rs.getString("startDate");
		         String dlDateTemp=rs.getString("deadline");
		         int budgetTemp = rs.getInt("Budget");
		         Boolean doneTemp=rs.getBoolean("done");
		         temp.push(new ProjectModel(idTemp, PnameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp));
		      }
			}catch(Exception e){
				e.getMessage();
				temp=null;
			}
		return temp;
	}
	
	public String toString(){
		return ("ProjectModel info is "+this.PID+" ,"+" ProjectModel name is " + this.name + " ,"+this.CastDateToString(startDate)+" ,"+this.CastDateToString(deadline)+" ,"+this.budget);
	}
}
