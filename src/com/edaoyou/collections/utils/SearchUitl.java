package com.edaoyou.collections.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.edaoyou.collections.R;

public class SearchUitl {

	/**
	 * 获取搜索的key在结果的位置
	 * 
	 * @param source
	 *            目标字符串
	 * @param searchKey
	 *            包含的字符串
	 * @return 包含的字符串在目标字符串中的位置集合
	 */
	private static List<Integer> getIndexs(String source, String searchKey) {
		List<Integer> indexs = new ArrayList<Integer>();
		int index = 0;
		for (int i = 0; i < source.length(); i++) {
			index = source.indexOf(searchKey, i);
			if (index >= i) {
				i = index + searchKey.length() - 1;
			} else if (index == -1) {
				return indexs;
			}
			indexs.add(index);
		}
		return indexs;
	}

	/**
	 * 获取变色以后的对象
	 * @param context 上下文
	 * @param text 原字符串
	 * @param searchKey 替换颜色的字符串
	 * @return 包含颜色的builder
	 */
	public static SpannableStringBuilder getBuilder(Context context, String text, String searchKey) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		List<Integer> indexs = getIndexs(text, searchKey);
		for (int i = 0; i < indexs.size(); i++) {
			ForegroundColorSpan greenSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.search_matching_color));
			builder.setSpan(greenSpan, indexs.get(i), indexs.get(i) + searchKey.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return builder;
	}
}
