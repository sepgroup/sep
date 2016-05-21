package BasicDesign;

import java.sql.ResultSet;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		project p= new project("project test", "2015-05-23", "2015-06-25", 2500, false);
		System.out.println(p.toString());
		String sql= "SELECT * FROM Project;";
		System.out.println(project.findBySql(sql));

	}

}
