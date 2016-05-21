package com.sepgroup.sep.db;

import com.sepgroup.sep.project.ProjectModel;

public class DbTest {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Database db = new Database();
		ProjectModel p = new ProjectModel("ProjectModel test", "2015-05-23", "2015-06-25", 2500, false);
		System.out.println(p.toString());
		String sql = "SELECT * FROM ProjectModel;";
		System.out.println(p.findBySql(sql));
	}

}
