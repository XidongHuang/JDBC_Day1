package tony.java.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;





/**
 * 操作 JDBC 的工具类，其中封装了一个工具方法
 * 
 * 
 * @return
 * @throws Exception
 */


public class JDBCtools {
	
	
	

	public static void release(ResultSet rs,Statement statement, Connection conn){
		
		if(rs != null){
			try{
				rs.close();
			} catch (Exception e2){
				e2.printStackTrace();
			}
			
			
		}
		
		
		
		
		if(statement != null){
			
			try{
			statement.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			} finally {
				if(conn != null){
					try{
					conn.close();
					} catch (Exception e3){
						e3.printStackTrace();
					}
				}
			}
	}
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 关闭Statement, c
	 * 
	 * @param statement
	 * @param conn
	 */
	
	
	public static void release(Statement statement, Connection conn){
		
		if(statement != null){
			
			try{
			statement.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			} finally {
				if(conn != null){
					try{
					conn.close();
					} catch (Exception e3){
						e3.printStackTrace();
					}
				}
			}
	}
	
	}
	
	
	
	/**
	 * 1. 获取连接的方法
	 * 通过读取配置文件从数据库服务器获取一个连接
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	
	
	public static Connection getConnection() throws Exception{
		//1. 准备连接数据库的4个字符串
		//1） 创建Properties 对象
		Properties properties = new Properties();
		
		
		//2） 获取jdbc.properties 对应的输入流
		InputStream in =
				JDBCtools.class.getClassLoader().getResourceAsStream("jdbc.properties");
		
		//3） 加载 2) 对应的输入流
		properties.load(in);
		
		
		
		//4） 具体决定 user,password 等4个字符串
		String user = properties.getProperty("user");
		String jdbcUrl = properties.getProperty("jdbcUrl");
		String password = properties.getProperty("password");
		String driver = properties.getProperty("driver");
		
		//2.加载数据库驱动(对应的Driver 实现类中有注册驱动的静态代码块)
		Class.forName(driver);
		
		//3. 通过 DriverManager 的getConnection() 方法获取数据库连接
		return (Connection) DriverManager.getConnection(jdbcUrl,user,password);
		
		
		
	}



	
}
