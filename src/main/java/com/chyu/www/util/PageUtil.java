package com.chyu.www.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋侃 E-mail:jiangkan@163.com
 * @version 创建时间：2017年5月18日 下午12:49:11
 * 类说明
 * 分页工具类
 */
public class PageUtil {
	
	/**
	 * 当前页号，从1开始
	 */
	private int pageno;
	/**
	 * 下一页页号
	 */
	private int nextpageno;
	/**
	 * 上一页页号

	 */
	private int prevpageno;
	/**
	 * 总页数
	 */
	private int totalpage;
	/**
	 * 总记录数
	 */
	private int totalrecords;
	/**
	 * 每页大小
	 */
	private int pagesize;
	
	protected int startIndex = 0;//起始数据行
	protected int endIndex = 0;//截止数据行
	private List<Integer> pagenoList;
	
	//showPagenoCount:要一次显示的页码列表的个数
	private static int showPagenoCount = 5;

	public PageUtil(){}
	
	
	public void Reset(int pageno,int totalrecords,int pagesize)
	{
		this.pageno = pageno;
		this.totalrecords = totalrecords;
		this.pagesize = pagesize;
		this.totalpage = getToalPage(totalrecords,pagesize);
		this.nextpageno = getNextPageNo();
		this.prevpageno = getPervPageNo();
		
		this.pagenoList= new ArrayList<Integer>();
		//先计算出起始页
		int startPageno = this.pageno - Math.floorDiv(showPagenoCount, 2)<1?1:this.pageno - Math.floorDiv(showPagenoCount, 2);
		//计算结束页
		int endPageno = startPageno + showPagenoCount-1 >this.totalpage?this.totalpage:startPageno + showPagenoCount-1 ;
		for(int i=startPageno;i<=endPageno;i++){
			this.pagenoList.add(i);
		}
	}
	
	/**
	 * 生成界面上要显示的动态页面列表
	 * @return
	 */
	public List<Integer> getPagenoList() {
		return pagenoList;
	}

	public int getPageno() {
		return pageno;
	}

	public int getPagesize() {
		return pagesize;
	}

	public int getTotalpage() {
		return totalpage;
	}

	public int getTotalrecords() {
		return totalrecords;
	}
	
	public int getNextpageno() {
		return nextpageno;
	}

	public int getPrevpageno() {
		return prevpageno;
	}

	/**
	 * 获取总页数
	 * @param records
	 * @param pagesize
	 * @return
	 */
	private int getToalPage(int records,int pagesize){
		if(pagesize==0){
			return 1;
		}
		int totalpage =0;
		if(records % pagesize==0){
			totalpage =records / pagesize;
		}else{
			totalpage =(int) Math.floor(records / pagesize) + 1;
		}
		return totalpage==0?1:totalpage;
	}
	
	/**
	 * 获取下一页
	 * @return
	 */
	private int getNextPageNo(){
		if(pageno+1>=totalpage){
			return totalpage;
		}else{
			return pageno+1;
		}
	}
	
	/**
	 * 获取上一页
	 * @return
	 */
	private int getPervPageNo(){
		if(pageno-1<=1){
			return 1;
		}else{
			return pageno-1;
		}
	}
	
	/**
	 * 返回起始数据行
	 * @return
	 */
	public int getStartIndex() {
		return this.startIndex = pagesize*(pageno - 1 );
	}
	/**
	 * 返回截止数据行


	 * @return
	 */
	public int getEndIndex() {
		return endIndex = pagesize*pageno;
	}
}
