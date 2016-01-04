package com.edaoyou.collections.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edaoyou.collections.R;

public class PersonalDataButton extends LinearLayout{
	private TextView tv_num;
	private Button btn_content;

	public PersonalDataButton(Context context) {
		super(context);
	}

	public PersonalDataButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		View inflate = LayoutInflater.from(context).inflate(
				R.layout.zdy_personaldata, this, true);
		tv_num = (TextView) inflate.findViewById(R.id.num);
		btn_content = (Button) inflate.findViewById(R.id.content);
	}
	
    /** 
     * 设置TextView显示的文字 
     */  
    public void setTvText(String text) {  
    	tv_num.setText(text);    
    }  
  
    /** 
     * 设置Button显示的文字 
     */  
    public void setBtnText(String text) {  
    	btn_content.setText(text);  
    }  

	/**
	 * 定义一个接口
	 */
	public interface ICallBack{  
        public void onClickButton(String s);  
    } 
	/** 
     * 初始化接口变量 
     */  
    ICallBack icallBack = null;  
      
    /** 
     * 自定义控件的自定义事件 
     * @param iBack 接口类型 
     */  
    public void setonClick(ICallBack iBack){  
        icallBack = iBack;  
    } 

}
