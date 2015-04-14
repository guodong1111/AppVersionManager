package tw.guodong.tools.appversionmanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AppVersionManager {	//版本控制及下載(可從google play及自定義下載點)
	private String apkLink;			//自定義下載連結網址
	private int leastVersion;		//要求最低執行之版本號
	private String message;			//要求更新的Message內容
	private boolean updateAPKAuto;	//當開發者呼叫checkVersion,當版本低於最低要求,是否要直接更新
	private OnDialogCancelListener mOnDialogCancelListener;	//當使用者關閉Dialog時監聽器(拿來製作是否要強制更新,ex:直接activity.finish())
	public AppVersionManager(){
		apkLink ="";
		leastVersion = 0;
		message = "New version is available.\nPlease update to the latest version.\nUnexpected problems might occur in older versions!!";
		updateAPKAuto = true;
	}
	public String getApkLink() {
		return apkLink;
	}
	public boolean isUpdateAPKAuto() {
		return updateAPKAuto;
	}
	public void setUpdateAPKAuto(boolean updateAPKAuto) {
		this.updateAPKAuto = updateAPKAuto;
	}
	public void setApkLink(String apkLink) {
		this.apkLink = apkLink;
	}
	public int getLeastVersion() {
		return leastVersion;
	}
	public void setLeastVersion(int leastVersion) {
		this.leastVersion = leastVersion;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public OnDialogCancelListener getOnDialogCancelListener() {
		return mOnDialogCancelListener;
	}
	public void setOnDialogCancelListener(
			OnDialogCancelListener onDialogCancelListener) {
		mOnDialogCancelListener = onDialogCancelListener;
	}
	//檢查版本是否低於最低要求
	public boolean checkVersion(Context context){
		int nowVersion = getVersion(context);
		if(leastVersion>nowVersion){
			if(updateAPKAuto){
				updateAKP(context);
			}
			return true;
		}else{
			return false;
		}
	}
	
	//取得該app的versionCode
    public int getVersion(Context context){
		try {
			return getPackageInfo(context).versionCode;
		} catch (NameNotFoundException e) {
		}
		return 0;
    }

    //取得該app的PackageInfo
    private PackageInfo getPackageInfo(Context context) throws NameNotFoundException{
		PackageManager manager = context.getPackageManager();
		PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
    	return info;
    }

    //取得該app的packageName
    private String getPackageName(Context context){
    	try {
			return getPackageInfo(context).packageName;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "";
    }

    //取得該app的Google Play的market網址位置
    private String getGooglePlayMarketURL(Context context){
		return "market://details?id="+getPackageName(context);
    }

    //取得該app的Google Play的https網址位置
    private String getGooglePlayURL(Context context){
		return "https://play.google.com/store/apps/details?id="+getPackageName(context);
    }
    
    //跳出要求更新訊息的Dialog
    public void updateAKP(final Context context){
		final Dialog dialog=new Dialog(context);
		View dialog_update=LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
		dialog.setContentView(dialog_update, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		((TextView)dialog_update.findViewById(R.id.dialog_update_content)).setText(message);
		dialog.setTitle(R.string.update);
		dialog_update.findViewById(R.id.dialog_update_enter).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(context, R.string.start_download, Toast.LENGTH_LONG).show();
				new Thread(new Runnable() {
					public void run() {
						String url = getGooglePlayURL(context);
						String data = HttpUtils.getUrlData(url);
						if(TextUtils.isEmpty(data)){
							downloadAPKFromURL(context);
						}else{
							downloadAPKFromGooglePlay(context);
						}
						dialog.cancel();
					}
				}).start();
			}
		});
		dialog_update.findViewById(R.id.dialog_update_cancel).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				if(mOnDialogCancelListener!=null){
					mOnDialogCancelListener.onDialogCancel();
				}
			}
		});
		dialog.show();
    }

    //跳轉頁面到該app的Google Play
    private void downloadAPKFromGooglePlay(Context context){
		Intent ie = new Intent(Intent.ACTION_VIEW,Uri.parse(getGooglePlayMarketURL(context)));
		context.startActivity(ie);
    }

    //從自定義網址下載app(使用Server)
    private void downloadAPKFromURL(Context context){
    	if(TextUtils.isEmpty(apkLink)){
    		Toast.makeText(context, R.string.sorry_can_not_update, Toast.LENGTH_LONG).show();
    	}else{
	    	Intent intent = new Intent(context, DownloadService.class);
	    	intent.putExtra("url", apkLink);
	    	context.startService(intent);
    	}
    }
    
    public interface OnDialogCancelListener{
    	public void onDialogCancel();
    }
	
}
