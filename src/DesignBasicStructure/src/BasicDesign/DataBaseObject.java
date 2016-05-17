package BasicDesign;

import java.sql.ResultSet;

/**
 * This class is the parent of different classes
 * @author Ali
 *
 */
public class DataBaseObject {
	/**
	 * this is the name of our table
	 */
	private String tableName;
	private String name;
	
	public DataBaseObject(String tableName, String name){
		this.tableName=tableName;
		this.name=name;
	}
	public String getTableName(){
		return this.tableName;
	}
	public String getName(){
		return this.name;
	}
}
