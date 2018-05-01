package com;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Record
 */
@WebServlet("/Record")
public class Record extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Record() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		if(null == id ){
			return;
		}
		//to record
		DBUtil db = new DBUtil();
		String insertSql = "insert into juli(juli) values("+id+")";
		int successNum = db.update(insertSql);
		if(successNum >0){
			// get id 
			String getIdSql = "select * from juli order by id desc LIMIT 1,1";
			Map<String, String> idMap  = db.getMap(getIdSql);
			String lastId = idMap.get("id");
			String lastJuLi = idMap.get("juli");
			if(id.equals(lastJuLi)){
				return ;
			}
			System.out.println("Record:"+id+"/M");			
			if(Float.valueOf(lastJuLi) < 1.0){
				String getThisTimeRecordSql = "select * from juli where id = " + (Integer.valueOf(lastId)  +  1);
				Map<String, String> thisTimeReocrd  = db.getMap(getThisTimeRecordSql);
				if(Float.valueOf(thisTimeReocrd.get("juli")) > 1.5){
					// 设置状态 车位1 状态 开锁
					String setKaiSuoSql = "update chewei set tingche = 1 where id = 1";
					int kaisuonum = db.update(setKaiSuoSql);
					System.out.println("上上上上上上上锁成功："+ kaisuonum);
					try {
						new Thread().sleep(8 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String setPingShiSql = "update chewei set tingche = '' where id = 1";
					int pingshiNum = db.update(setPingShiSql);
					System.out.println("更改成平时状态："+ pingshiNum);

					jiezhang(db);

				}
			}else{
				String getThisTimeRecordSql = "select * from juli where id = " + (Integer.valueOf(lastId)  +  1);
				Map<String, String> thisTimeReocrd  = db.getMap(getThisTimeRecordSql);
				if(Float.valueOf(thisTimeReocrd.get("juli")) < 1){
					// 设置状态 车位1 状态 开锁
					String setKaiSuoSql = "update chewei set tingche = 0 where id = 1";
					int kaisuonum = db.update(setKaiSuoSql);
					System.out.println("解解解解解解锁成功个数："+ kaisuonum);
					try {
						new Thread().sleep(8 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String setPingShiSql = "update chewei set tingche = '' where id = 1";
					int pingshiNum = db.update(setPingShiSql);
					System.out.println("更改成平时状态："+ pingshiNum);
					firstSteopToRecord(db);
				}
			}

			// success
			// if last long < 1    this time > 1.5   car is gone.
			//			String lastTime = db.getMap(sql);


		}


		//		PrintWriter out = response.getWriter();
		//		// 判断是否有该条数据
		//		String judgeSql = "select * from goods where id = " + id;
		//		List<Map<String, String>> list = db.getList(judgeSql);
		//		int result ;
		//		if(list != null  && list.size()>0) {
		//			String currentNum = list.get(0).get("num"); 
		//			String updateSql = "";
		//			
		//			if("1".equals(num)) {
		//				updateSql = "update goods set num = "+ (Integer.valueOf(currentNum).intValue() +1)+" where id = "+id;
		//				result = Integer.valueOf(currentNum).intValue() +1;
		//			}else {
		//				updateSql = "update goods set num = "+ (Integer.valueOf(currentNum).intValue() -1) +" where id = "+id;
		//				result = Integer.valueOf(currentNum).intValue() -1;
		//			}
		//			int num2 = db.update(updateSql);
		//			if (num2>0) {
		//				out.println(result);
		//			}else {
		//				out.println("error");
		//			}
		//		}else {
		//			// insert num = 1 id = id;
		//			String insertSql = "insert into goods(id,num) values('"+ id +"',1)";
		//			int num2 = db.update(insertSql);
		//			if (num2>0) {
		//				out.println("1");
		//			}else {
		//				out.println("error");
		//			}
		//		}

	}

	private void firstSteopToRecord(DBUtil db) {
		// 生成停车记录

		String chengshengtingchejiluSql = "insert into tingchejilu(userid,riqi,kaishidate) values(1,now(),now())";
		db.update(chengshengtingchejiluSql);
		// 停车成功
		System.out.println("生成停车记录第一步骤成功");
	}

	private void jiezhang(DBUtil db) {
		// 停车结束  结账
		String jiezhangSql = "select * from tingchejilu where shijian is null order by id desc LIMIT 0,1 ";
		Map<String,String> jiezhangMap = db.getMap(jiezhangSql);
		String jiezhangId = jiezhangMap.get("id");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date b = new Date();
		Date a = null;
		try {
			String str = jiezhangMap.get("kaishidate").substring(0,jiezhangMap.get("kaishidate").length()-2);
			System.out.println("str:"+jiezhangMap.get("kaishidate").substring(0,jiezhangMap.get("kaishidate").length()-2));
			a = sdf.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int shichang = 0;
		System.out.println("b.getTime():"+b.getTime());
		System.out.println("a.getTime():"+a.getTime());
		
		long interval = (b.getTime() - a.getTime())/1000;
		if(interval <= 60*60){
			shichang = 1;
		}else{
			shichang = (int) (interval / (60*60));
		}

		System.out.println("两个时间相差"+interval+"秒");//会打印出相差3秒
		System.out.println("停车时长：" + shichang+"小时");
		String jiezhangUpdateSql = "update tingchejilu set shijian = '"+ shichang+"',jieshudate = now() where id = " + jiezhangId;
		int updatenummm = db.update(jiezhangUpdateSql);
		System.out.println("停车成功。"+updatenummm);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
