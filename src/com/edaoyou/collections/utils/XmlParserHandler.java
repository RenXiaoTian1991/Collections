package com.edaoyou.collections.utils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.edaoyou.collections.bean.CityBean;
import com.edaoyou.collections.bean.ProvinceBean;

public class XmlParserHandler extends DefaultHandler {

	/**
	 * 存储所有的解析对象
	 */
	private List<ProvinceBean> provinceList = new ArrayList<ProvinceBean>();
	 	  
	public XmlParserHandler() {
		
	}

	public List<ProvinceBean> getDataList() {
		return provinceList;
	}

	@Override
	public void startDocument() throws SAXException {
		// 当读到第一个开始标签的时候，会触发这个方法
	}

	ProvinceBean provinceModel = new ProvinceBean();
	CityBean cityModel = new CityBean();
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// 当遇到开始标记的时候，调用这个方法
		if (qName.equals("province")) {
			provinceModel = new ProvinceBean();
			provinceModel.setName(attributes.getValue(0));
			provinceModel.setCityList(new ArrayList<CityBean>());
		} else if (qName.equals("city")) {
			cityModel = new CityBean();
			cityModel.setName(attributes.getValue(0));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// 遇到结束标记的时候，会调用这个方法
		if (qName.equals("city")) {
        	provinceModel.getCityList().add(cityModel);
        } else if (qName.equals("province")) {
        	provinceList.add(provinceModel);
        }
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

}
