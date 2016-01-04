package com.edaoyou.collections.adapter;
//package com.edaoyou.collections.fragment.Adapter;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.edaoyou.collections.MyApplication;
//import com.edaoyou.collections.R;
//import com.edaoyou.collections.bean.Bean.Article;
//import com.edaoyou.collections.bean.Bean.ChatItem;
//import com.edaoyou.collections.bean.Bean.Like;
//import com.edaoyou.collections.bean.Bean.Tag;
//import com.edaoyou.collections.bean.Bean.User;
//import com.edaoyou.collections.utils.CollectionBitmapUtils;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
//
//public class SearchDetailAdapter extends BaseAdapter {
//	private Context mcontext;
//	private Map<Object, Integer> mapDate;
//
//	private final int TYPE_SEPARATOR0 = 0;
//	private final int TYPE_SEPARATOR1 = 1;
//	private final int TYPE_SEPARATOR2 = 2;
//	private final int TYPE_SEPARATOR3 = 3;
//	private ImageLoader imageloader;
//	private ArrayList<Integer> values;
//
//	public SearchDetailAdapter(Context context, Map<Object, Integer> mapData) {
//		this.mcontext = context;
//		this.mapDate = mapData;
//		imageloader = ImageLoader.getInstance();
//		values = new ArrayList<Integer>(mapDate.values());
//	}
//
//	public void addMapData(Map<Object, Integer> mapDate) {
//		this.mapDate.putAll(mapDate);
//		this.notifyDataSetChanged();
//	}
//
//	@Override
//	public int getCount() {
//		return mapDate.size();
//	}
//
//	/** 该方法返回多少个不同的布局 */
//	@Override
//	public int getViewTypeCount() {
//		return 4;
//	}
//
//	/** 根据position返回相应的Item */
//	@Override
////	public int getItemViewType(int position) {
////		Message msg = myList.get(position);  
////        int type = msg.getType();  
////        Log.e("TYPE:", "" + type);  
////        return type;  
////        
//////		if (0 == values.get(position)) {// 标题
//////			return TYPE_SEPARATOR0;
//////		} else if (1 == values.get(position) || 5 == values.get(position)) {
//////			return TYPE_SEPARATOR1;
//////		} else if (2 == values.get(position) || 4 == values.get(position)) {
//////			return TYPE_SEPARATOR2;
//////		} else
//////			// likes,articles
//////			return TYPE_SEPARATOR3;
////	}
//
//	@Override
//	public Object getItem(int position) {
//		return position % getCount();
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position % getCount();
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		int type = getItemViewType(position);
//		Object[] keys = mapDate.keySet().toArray();
//		switch (type) {
//		case TYPE_SEPARATOR0:
////			vh1 = new ViewHolder1();
//			convertView = View.inflate(mcontext, R.layout.searchdetailitem1,
//					null);
//			TextView title = (TextView) convertView
//					.findViewById(R.id.seatchdetail1_title);
//			title.setText((CharSequence) keys[position]);
////			convertView.setTag(vh1);
//			break;
//		case TYPE_SEPARATOR1:
//			// vh2 = new ViewHolder2();
//			 convertView = View.inflate(mcontext, R.layout.searchdetailitem2,
//			 null);
//			// vh2.headImage = (ImageView) convertView
//			// .findViewById(R.id.seatchdetail1_image);
//			// vh2.nick = (TextView) convertView
//			// .findViewById(R.id.seatchdetail1_nick);
//			// String headImageUrl2 = ((User) keys[position]).avatar;
//			// String nick = ((User) keys[position]).nick;
//			// imageloader.loadImage(headImageUrl2, MyApplication.getInstance()
//			// .getOption(), new SimpleImageLoadingListener() {
//			//
//			// @Override
//			// public void onLoadingComplete(String imageUri, View view,
//			// Bitmap loadedImage) {
//			// super.onLoadingComplete(imageUri, view, loadedImage);
//			// Bitmap roundedCornerBitmap = CollectionBitmapUtils
//			// .getRoundedCornerBitmap(loadedImage);// 圆角
//			// vh2.headImage.setImageBitmap(roundedCornerBitmap);
//			// }
//			// });
//			// vh2.nick.setText(nick);
//			// convertView.setTag(vh2);
//			break;
//		case TYPE_SEPARATOR2:
//			// vh3 = new ViewHolder3();
//			// convertView = View.inflate(mcontext,
//			// R.layout.home_userinfodisplayitem, null);
//			// vh3.headImage = (ImageView) convertView
//			// .findViewById(R.id.imageview);
//			// vh3.nick = (TextView) convertView.findViewById(R.id.nick);
//			// vh3.txt = (TextView) convertView.findViewById(R.id.time);
//			//
//			// String headImageUrl3;
//			// String nick3,
//			// txt3;
//			// if (2 == values.get(position)) {
//			// headImageUrl3 = ((ChatItem) keys[position]).avatar;
//			// nick3 = ((ChatItem) keys[position]).nick;
//			// txt3 = ((ChatItem) keys[position]).txt;
//			// } else {
//			// headImageUrl3 = ((Tag) keys[position]).avatar;
//			// nick3 = ((Tag) keys[position]).topic;
//			// txt3 = ((Tag) keys[position]).txt;
//			// }
//			// imageloader.loadImage(headImageUrl3, MyApplication.getInstance()
//			// .getOption(), new SimpleImageLoadingListener() {
//			//
//			// @Override
//			// public void onLoadingComplete(String imageUri, View view,
//			// Bitmap loadedImage) {
//			// super.onLoadingComplete(imageUri, view, loadedImage);
//			// Bitmap roundedCornerBitmap = CollectionBitmapUtils
//			// .getRoundedCornerBitmap(loadedImage);// 圆角
//			// vh3.headImage.setImageBitmap(roundedCornerBitmap);
//			// }
//			// });
//			// vh3.nick.setText(nick3);
//			// vh3.txt.setText(txt3);
//			// convertView.setTag(vh3);
//			break;
//		case TYPE_SEPARATOR3:
//			// vh4 = new ViewHolder4();
//			// convertView = View.inflate(mcontext, R.layout.searchdetailitem3,
//			// null);
//			// vh4.photo = (ImageView) convertView
//			// .findViewById(R.id.lv_searchdetail);
//			// vh4.title = (TextView) convertView.findViewById(R.id.nick);
//			// vh4.txt = (TextView) convertView.findViewById(R.id.comment);
//			// vh4.time = (TextView) convertView.findViewById(R.id.time);
//			// String photoUrl;
//			// String title4,
//			// txt4,
//			// time4;
//			// if (3 == values.get(position)) {
//			// photoUrl = ((Like) keys[position]).avatar;
//			// title4 = ((Like) keys[position]).txt;
//			// time4 = "来自 " + ((Like) keys[position]).nick;
//			// txt4 = "";
//			// } else {
//			// photoUrl = ((Article) keys[position]).photo;
//			// title4 = ((Article) keys[position]).title;
//			// txt4 = ((Article) keys[position]).txt;
//			// time4 = "发布自 " + ((Article) keys[position]).time;
//			// }
//			// vh4.title.setText(title4);
//			// vh4.txt.setText(txt4);
//			// vh4.time.setText(time4);
//			// imageloader.displayImage(photoUrl, vh4.photo, MyApplication
//			// .getInstance().getOption());
//			// convertView.setTag(vh4);
//			break;
//
//		default:
//			break;
//		}
//		return convertView;
//	}
//
//	// 标题
//	static class ViewHolder1 {
//		TextView title;
//	}
//
//	// 我关注的人
//	// 相关用户
//	static class ViewHolder2 {
//		TextView nick;
//		ImageView headImage;
//	}
//
//	// 聊天记录
//	// 相关标签
//	static class ViewHolder3 {
//		TextView nick;
//		ImageView headImage;
//		TextView txt;
//		TextView time;
//	}
//
//	// 我赞过的feed
//	// 相关咨询
//	static class ViewHolder4 {
//		TextView title;
//		ImageView photo;
//		TextView txt;
//		TextView time;
//	}
// }
