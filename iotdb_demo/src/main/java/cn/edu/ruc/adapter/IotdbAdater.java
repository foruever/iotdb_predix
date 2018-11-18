package cn.edu.ruc.adapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;




public class IotdbAdater {

	public static void main(String[] args) throws Exception {
	    predixInsertTest();
//	    predixQueryTest();
	}
	public static Object predixInsertTest() throws Exception {
		
		Random r=new Random();
		for(int index=0;index<10;index++) {
			List<String> sqls=new ArrayList<>();
			long startTime=1541019600000L+3600*1000*3*index;
			long endTime=1541030400000L+3600*1000*3*index;
			while(startTime<endTime) {
				String sql="insert into root.perform.d1 (timestamp,s1) values ("+startTime+","+r.nextDouble()*1000+")";
				sqls.add(sql);
//				System.out.println(sql);
				startTime+=1000;
			}
			@SuppressWarnings("unchecked")
			Connection connection = getConnection();
			Statement statement = null;
			Long costTime=0L;
			try {
				statement = connection.createStatement();
				for(String sql:sqls) {
					statement.addBatch(sql);
				}
				long startMs=System.nanoTime();
				statement.executeBatch();
				long endMS=System.nanoTime();
				costTime=endTime-startTime;
				statement.clearBatch();
				System.out.println("index:"+index+",size:"+sqls.size()+",cost time:"+(endMS-startMs)+" ms");
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				closeStatement(statement);
				closeConnection(connection);
			}
			
		}
		return "";
	}
	public static Object predixQueryTest() throws Exception {
		String sql1="select s1 from root.perform.d1 where time>=1541019600000 and time <=1541120400000";
		String sql2="select mean(s1) from root.perform.d1 where time>=1541019600000 and time <=1541120400000 group by (1h,[1541019600000,1541120400000])";
		return "";
	}
    private static  Connection getConnection(){
		Connection connection=null;
		 try {
			 //数据源管理
			 connection = DriverManager.getConnection("jdbc:tsfile://10.77.110.226:6667", "root", "root");
//			 connection=getDataSource().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return connection;
	}
	private static void closeConnection(Connection conn){
		try {
			if(conn!=null){
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void closeStatement(Statement statement){
		try {
			if(statement!=null){
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
