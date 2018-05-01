package com;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {
	private String driver;
	private String url;
	private String username;
	private String password;
	private Connection con;
	private PreparedStatement pstmt;
	 
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	//构造方法，定义驱动程序连接用户名和密码信息
    public DBUtil(){
	  driver="com.mysql.jdbc.Driver";
	  url="jdbc:mysql://localhost:3306/bookstore";
	  username="root";
	  password="root";
    }
    
	//加载驱动程序，得到连接对象
	private void init(){
		try {
			Class.forName(driver);
			con=DriverManager.getConnection(url,username,password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//关闭操作
	private void close(){
		if(pstmt!=null){
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
    //给SQL语句设置参数
    private void setParams(String sql,String[] params){
    	if(params!=null){
    		for(int i=0;i<params.length;i++){
    			try {
					pstmt.setString(i+1, params[i]);
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}
    		
    	}
    }
    
    //执行更新
    public int update(String sql,String[] params){
    	int result=0;
    	init();
    	try {
			pstmt=con.prepareStatement(sql);
			setParams(sql,params);
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			close();
		}
    	return result;
    }
    
    public int update(String sql){
    	return update(sql, null);
    }
    
   //查询获取List对象
 	public List<Map<String,String>> getList(String sql, String[] params) {
 		// 定义保存查询结果的集合对象
 		List<Map<String,String>> list = null;
 		init();
 		try {
 			pstmt=con.prepareStatement(sql);
 			setParams(sql, params);
 			ResultSet rs = pstmt.executeQuery();
 			// 根据RS得到list
 			list = getListFromRS(rs);
 			rs.close();
 		} catch (Exception e) {
 			e.printStackTrace();
 		} finally {
 			close();
 		}
 		return list;
 	}
 	
 	public List<Map<String,String>> getList(String sql){
 		return getList(sql, null);
 	}
 	
 	//查询获取Map对象
 	public Map<String,String> getMap(String sql, String[] params) {
 		Map<String,String> m = null;
 		List<Map<String,String>> l = getList(sql, params);
 		if (l != null && l.size()!=0){
 			m = (Map<String,String>) (l.get(0));
 		}
 		return m;
 	}
   
 	public Map<String,String> getMap(String sql){
 		return getMap(sql, null);
 	}
 	
 	//将结果集封装成一个List
	private List<Map<String,String>> getListFromRS(ResultSet rs) throws SQLException {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		// 获取元数据
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			Map<String,String> m = new HashMap<String,String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				// 获取当前行第i列的列名
				String colName = rsmd.getColumnLabel(i);
				String s = rs.getString(colName);
				if (s != null) {
						m.put(colName, s);
				}
			}
			list.add(m);
		}
		return list;
	}
}