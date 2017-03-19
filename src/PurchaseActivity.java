package io.nivad.miladesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import io.nivad.miladesign.NivadPlugin;

public class PurchaseActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState)  {
	    super.onCreate(savedInstanceState);
	    
	    int ot = getIntent().getIntExtra("orientation", -1);
		setRequestedOrientation(ot);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    String type = getIntent().getStringExtra("type");
	    String webhookPayload = getIntent().hasExtra("webhook_payload") ? getIntent().getStringExtra("webhook_payload") : "";
	    if ("inapp".equals(type)) {
	    	String pid = getIntent().getStringExtra("product_id");
	    	if (!NivadPlugin.bp.purchase(this, pid, webhookPayload)) {
	    		Log.e("yoyo", "Purchase method could not run properly");
	    		finish();
	    	}
	    } else if ("subs".equals(type)) {
	    	String sid = getIntent().getStringExtra("subscription_id");
	    	if (!NivadPlugin.bp.subscribe(this, sid, webhookPayload)) {
	    		Log.e("yoyo", "Subscribe method could not run properly");
	    		finish();
	    	}
	    } else {
	    	Log.e("yoyo", "Invalid type");
	    	finish();
	    }
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("yoyo", "Got activity result");
	    if (!NivadPlugin.bp.handleActivityResult(requestCode, resultCode, data)) {
	    	super.onActivityResult(requestCode, resultCode, data);
	    } else {
	    	finish();
	    }
	}
}