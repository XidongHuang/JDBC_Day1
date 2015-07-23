package tony.java.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Test;

import com.mysql.jdbc.Driver;

public class JDBCTest {
	
	/**
	 * ResultSet: 结果集。封装了使用JDBC 进行查询的结果
	 * 1. 调用 Statement 对象的 executeQuery(sql) 了可以得到结果集 
	 * 2. ResultSet 返回的实际上就是一张数据表。 有一个指针指向数据表的第一行的前面
	 * 可以调用 next() 方法检测下一行是否有效。若有效该方法返回true，且指针下移，相当于
	 * Iterator 对象的 hasNext(), next() 方法的结合体
	 * 3. 当指针对位到一行时，可以调用调用getXxx(index) 或 getXxx(columnName)
	 * 获取每一列的值。 例如: getInt(1), getString("name")
	 * 4. ResultSet 当然也需要进行关闭。
	 * 
	 */
	
	@Test
	public void testResultSet(){
		//获取 id = 2 的customer 数据表的记录，并打印
		
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try{
		//1. 获取Connection
		conn = JDBCtools.getConnection();
		
		//2. 获取 Statement
		statement = conn.createStatement();
		
		//3. 准备sql
		String sql = "Select * from customer";
		  
		
		
		//4. 进行查询，得到ResultSet
		rs= statement.executeQuery(sql);
		
		
		
		//5. 处理ResulstSet
		while(rs.next()){
			int id = rs.getInt(1);
			String name = rs.getString(2);
			String email = rs.getString(3);
			Date birthday = rs.getDate(4);
			
			System.out.println(id);
			System.out.println(name);
			System.out.println(email);
			System.out.println(birthday);
		}
		
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//6. 关闭数据库资源 
			JDBCtools.release(rs, statement, conn);
		}
		
		
	}
	
	
	
	/**
	 * 通用的更新的方法: 包括INSERT,UPDATE,DELETE
	 * 版本1.
	 * @throws SQLException 
	 * 
	 * 
	 */
	
	
	
	public void update(String sql){
		
		Connection conn = null;
		Statement statement = null;
		
		try{
			conn = JDBCtools.getConnection();
			statement = conn.createStatement();
			statement.execute(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCtools.release(statement, conn);
			
		}
		
	}
	
	
	
	
	
	
	/**
	 * 通过JDBC指定的数据表中插入一条记录
	 * @throws Exception 
	 * 
	 * 1. Statement: 用于执行 SQL 语句的对象
	 * 1.1 通过Connection 的 createStatement() 方法来获取
	 * 1.2 通过executeUpdate(sql) 可以执行sql语句
	 * 1.3 传入的sql 可以是insert, update 或 delete. 但不能是 select
	 * 
	 * 2. Connection, statement 都是应用程序和数据库服务器的连接资源。使用后一定要关闭。
	 * 需要在finally 中关闭
	 * 
	 * 
	 * 3. 关闭的顺序： 先关闭后获取的。即先关闭Statement 后关闭 Connection
	 * 
	 * 
	 */
	
	
	
	@Test
	public void testStatement() throws Exception{
		//1. 获取数据库连接
		Connection conn = null;
		Statement statement = null;		
				
				
		try {
			conn = getConnection2(); 

			
			//3. 准备插入的sql语句
			
			String sql = "insert into yidi.customer(name,email,birthday)"
					+ "values('XYZ','XYZ@GUI.com','1990-05-01')";
			
			
			
			//4. 执行插入
			//4.1 获取操作 sql 语句的 statement 对象：
			//调用Connection 的 createStatement() 方法来获取
			statement = conn.createStatement();
			
			
			//4.2 调用statement 对象的executeUpdate(sql) 执行sql语句进行插入
			statement.execute(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		
			try {
				//5. 关闭Statement 对象
				if(statement != null)
				statement.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			
			//2. 关闭连接
			if(conn != null)
			conn.close();
			
			}
		}
		
	}
	
	
	
	
	@Test
	public void testGetConnection2() throws Exception{
		System.out.println(getConnection2());
		
	}
	
	
	
	
	public Connection getConnection2() throws Exception{
		//1. 准备连接数据库的4个字符串
		//1） 创建Properties 对象
		Properties properties = new Properties();
		
		
		//2） 获取jdbc.properties 对应的输入流
		InputStream in =
				this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		
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
	
	
	
	
	
	
	/**
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * DriverManager 是驱动的管理类
	 * 1） 可以通过重载的getConnection() 方法获取数据库连接，较为方便
	 * 2） 可以同时管理多个驱动程序：若注册了多个数据库连接，
	 * 则调用getCnnection()方法时传入的参数不同，即返回数据库连接
	 * 
	 * 
	 * 
	 */
	
	@Test
	public void testDriverManager() throws SQLException, IOException, ClassNotFoundException{
		//1. 准备连接数据库的4个字符串
		//驱动的全类名
		String driverClass = null;
		
		String jdbcUrl = null;
		
		String user = null;
		
		String password = null;
		
		//读取类路径下的jdbc.properties文件
		InputStream in =
				getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		Properties properties = new Properties();
		properties.load(in);
		driverClass = properties.getProperty("driver");
		jdbcUrl = properties.getProperty("jdbcUrl");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		
		
		//2.加载数据库驱动程序；（对应的Driver 实现类中有注册驱动的静态代码块。）
		Class.forName(driverClass);
		
		//3. 通过DriverManager的getConnection() 方法获取数据库连接。
		Connection connection =
				(Connection) DriverManager.getConnection(jdbcUrl,user,password);
		System.out.println(connection);
		
	}
	
	
	
	
	/**
	 * Driver 是一个接口：数据库厂商必须提供实现的接口，能从其中获取数据库连接
	 * @throws SQLException 
	 * 
	 * 
	 * 
	 * 
	 */
	@Test
	public void testDriver() throws SQLException{
		//1. 创建一个Driver 实现类的对象
		Driver driver = new Driver();
		
		//2. 准备连接数据库的基本信息: url, user, password
		String url = "jdbc:mysql://127.0.0.1:3306/test";
		Properties info = new Properties();
		info.put("user", "root");
		info.put("password", "gzinkkk51");
		
		//3. 调用Driver 接口的connect(url,info) 获取数据库连接
		Connection connection = (Connection) driver.connect(url, info);
		System.out.println(connection);
		
		
				
	}
	
	/**
	 * 编写一个通用的方法，在不修改源程序的情况下，可以获取任何数据库的是连接
	 * 解决方案： 把数据库驱动 Driver 实现类的全名, url, user, password 放入一个
	 * 配置文件中，通过修改配置文件的方式实现和具体的数据库解耦
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	
	public Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		String driverClass = null;
		String jdbcUrl = null;
		String user = null;
		String password = null;
		
		InputStream in =
				getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		Properties properties = new Properties();
		properties.load(in);
		driverClass = properties.getProperty("driver");
		jdbcUrl = properties.getProperty("jdbcUrl");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		
		//通过反射创建Driver 对象
		Driver driver = (Driver) Class.forName(driverClass).newInstance();
		Properties info = new Properties();
		info.put("user", user);
		info.put("password", password);
		
		//通过Driver 的connect 方法获取连接数据库
		Connection connection = (Connection) driver.connect(jdbcUrl, info);
		
		
		return null;

		
	}
	
	@Test
	public void testGetConnection() throws Exception{
		
		System.out.println(getConnection());
		
		
	}
	
}
