package com.gentcent.wechat.zzk.util;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisposeXMLUtil {
	public static Element a(Element iVar, String str) {
		List<Element> elements = iVar.elements();
		for (Element iVar2 : elements) {
			if (iVar2.getName().equals(str)) {
				return iVar2;
			}
		}
		return null;
	}
	
	public static String a(String str) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			stringBuffer.append(a(a(a(DocumentHelper.parseText(str).getRootElement(), "sysmsgtemplate"), "content_template"), "template").getText());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
	
	public static String a(String str, String str2) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			Iterator elementIterator = a(a(a(DocumentHelper.parseText(str).getRootElement(), "sysmsgtemplate"), "content_template"), "link_list").elementIterator("link");
			while (elementIterator.hasNext()) {
				Element iVar = (Element) elementIterator.next();
				String attributeValue = iVar.attributeValue("name");
				if (str2.contains("$")) {
					str2 = str2.replaceAll("\\$", "");
				}
				if (attributeValue.equals(str2)) {
					if (!attributeValue.equals("remark")) {
						if (!attributeValue.equals("others")) {
							XLog.d("Xposed getNickName" + str2);
							Element a = a(iVar, "memberlist");
							if (a != null) {
								Iterator elementIterator2 = a.elementIterator("member");
								while (true) {
									if (!elementIterator2.hasNext()) {
										break;
									}
									Element iVar2 = (Element) elementIterator2.next();
									if (iVar2 == null) {
										break;
									}
									Element a2 = a(iVar2, "nickname");
									StringBuilder sb = new StringBuilder();
									sb.append("getNickName nickname");
									sb.append(a2.getText());
									XLog.d(sb.toString());
									StringBuilder sb2 = new StringBuilder();
									sb2.append(a2.getText());
									sb2.append("、");
									stringBuffer.append(sb2.toString());
								}
							} else {
								return "";
							}
						} else {
							Element a3 = a(iVar, "plain");
							StringBuilder sb3 = new StringBuilder();
							sb3.append(a3.getText());
							sb3.append("");
							return sb3.toString();
						}
					} else {
						Element a4 = a(iVar, "plain");
						StringBuilder sb4 = new StringBuilder();
						sb4.append(a4.getText());
						sb4.append("");
						return sb4.toString();
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		if (stringBuffer.toString().contains("、")) {
			return stringBuffer.toString().substring(0, stringBuffer.lastIndexOf("、"));
		}
		return stringBuffer.toString();
	}
	
	public static String b(String str) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			return a(a(DocumentHelper.parseText(str).getRootElement(), "invokeMessage"), "text").getText();
		} catch (DocumentException e) {
			e.printStackTrace();
			return stringBuffer.toString().substring(0, stringBuffer.lastIndexOf("、"));
		}
	}
	
	public static ArrayList<String> c(String str) {
		ArrayList<String> arrayList = new ArrayList<>();
		Matcher matcher = Pattern.compile("\\$.*?\\$").matcher(str);
		while (matcher.find()) {
			arrayList.add(matcher.group());
		}
		return arrayList;
	}
}
