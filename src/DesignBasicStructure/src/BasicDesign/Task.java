package BasicDesign;

import java.util.Date;

public class Task extends project implements DataBaseFunctions{

		private final String tableName="Task";
		private String name;
		public int Tbudget;
		private Date TStartDate;
		private Date TdeadLine;
		private int Pid;
		private int Tid;
		public Task(String name,int budget, int pid, String tstartdate, String tdeadline) {
			this.name=name;
			this.Pid=pid;
			this.Tbudget=budget;
			this.TStartDate=CastStringToDate(tstartdate);
			this.TdeadLine=CastStringToDate(tdeadline);
		}
		//it is not done yet
		@Override
		public boolean Create() {
			// TODO Auto-generated method stub
			return false;
		}
		//it is not done yet
		@Override
		public boolean update() {
			// TODO Auto-generated method stub
			return false;
		}
		//it is not done yet
		@Override
		public boolean delete() {
			// TODO Auto-generated method stub
			return false;
		}

		
		
}
