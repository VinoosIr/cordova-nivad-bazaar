package io.nivad.miladesign;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import io.nivad.iab.BillingProcessor;
import io.nivad.iab.MarketName;
import io.nivad.iab.TransactionDetails;
import io.nivad.miladesign.PurchaseActivity;

public class NivadPlugin extends CordovaPlugin {
	private static final String LOG_TAG = "MilaDesign";
	private static CallbackContext callbackContextKeepCallback = null;
	private static Activity mActivity = null;
	public CordovaInterface cordova = null;
	static BillingProcessor bp;
	static int ot = -1;
	
	// This is to prevent an issue where if two Javascript calls are made to OneSignal expecting a callback then only one would fire.
	private static void callbackSuccess(CallbackContext CallbackContext, JSONObject jsonObject) {
	    if(jsonObject == null){ // in case there are no data
	    	jsonObject = new JSONObject();
	    }
	    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
	    pluginResult.setKeepCallback(true);
	    CallbackContext.sendPluginResult(pluginResult);
	}
	  
	@Override
	public void initialize (CordovaInterface initCordova, CordovaWebView webView) {
		 Log.e (LOG_TAG, "initialize");
		  cordova = initCordova;
		  super.initialize (cordova, webView);
	}
	

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext CallbackContext) throws JSONException {
		if (action.equals("init")) {
			init(args, CallbackContext);
		    return true;
		}
		if (action.equals("purchase")) {
			String productId = args.getString(0);
			purchase(productId, CallbackContext);
		    return true;
		}
		if (action.equals("subscribe")) {
			String productId = args.getString(0);
			subscribe(productId, CallbackContext);
		    return true;
		}
		if (action.equals("consumePurchase")) {
			String productId = args.getString(0);
			consumePurchase(productId, CallbackContext);
		    return true;
		}
	    return false;
	}
	
	public void init(JSONArray args, CallbackContext callbackContext) throws JSONException {
		mActivity = cordova.getActivity();
		callbackContextKeepCallback = callbackContext;
		final String BAZAAR_KEY = args.getString(0);
		final String NIVAD_APP_ID = args.getString(1);
		final String NIVAD_APP_SECRET = args.getString(2);
		final String marketName = args.getString(3);
		final MarketName marketNameEnum;
	    if ("bazaar".equalsIgnoreCase(marketName)) {
	      marketNameEnum = MarketName.CAFE_BAZAAR;
	    } else {
	      if ("myket".equalsIgnoreCase(marketName))
	        marketNameEnum = MarketName.MYKET;
	      else
	        throw new IllegalArgumentException(marketName + " is not a valid market name. Acceptable values are \"cafe-bazaar\" and \"myket\"");
	    }
	    
		bp = new BillingProcessor(mActivity, BAZAAR_KEY, NIVAD_APP_ID, NIVAD_APP_SECRET,marketNameEnum, createHandler());
	}
	
	private BillingProcessor.IBillingHandler createHandler() {
		return new BillingProcessor.IBillingHandler() {
			@Override
			public void onBillingInitialized() {
				JSONObject outerObject = new JSONObject();
		    	try {
		            outerObject.put("event", "onBillingInitialized");
		            callbackSuccess(callbackContextKeepCallback, outerObject);
		    	} catch (Throwable t) {
		            t.printStackTrace();
		        }
			}

		    @Override
		    public void onProductPurchased(String sku, TransactionDetails details) {
		    	JSONObject outerObject = new JSONObject();
		    	try {
		            outerObject.put("event", "onProductPurchased");
		            outerObject.put("sku", sku);
		            outerObject.put("token", details.purchaseToken);
		            outerObject.put("time", details.purchaseTime);
		            callbackSuccess(callbackContextKeepCallback, outerObject);
		    	} catch (Throwable t) {
		            t.printStackTrace();
		        }
		    }

		    @Override
		    public void onBillingError(int code, Throwable error) {
		    	JSONObject outerObject = new JSONObject();
		    	try {
		            outerObject.put("event", "onBillingError");
		            outerObject.put("code", code);
		            callbackSuccess(callbackContextKeepCallback, outerObject);
		    	} catch (Throwable t) {
		            t.printStackTrace();
		        }
		    }

		    @Override
		    public void onPurchaseHistoryRestored() {
		    	JSONObject outerObject = new JSONObject();
		    	try {
		            outerObject.put("event", "onPurchaseHistoryRestored");
		            callbackSuccess(callbackContextKeepCallback, outerObject);
		    	} catch (Throwable t) {
		            t.printStackTrace();
		        }
		    }

		};
	}

	public boolean isPurchased(String productId){
	    return bp.isPurchased(productId);
	}

	public boolean isSubscribed(String productId){
	    return bp.isSubscribed(productId);
	}

	public void purchase(String productId, CallbackContext callbackContext) {
		mActivity = cordova.getActivity();
		ot = mActivity.getResources().getConfiguration().orientation;
		callbackContextKeepCallback = callbackContext;
		Intent i = new Intent(mActivity, PurchaseActivity.class).putExtra("type", "inapp").putExtra("orientation", ot).putExtra("product_id", productId);
		mActivity.startActivity(i);
	}
	
	public void subscribe(String subscriptionId, CallbackContext callbackContext) {
		mActivity = cordova.getActivity();
		ot = mActivity.getResources().getConfiguration().orientation;
		callbackContextKeepCallback = callbackContext;
		Intent i = new Intent(mActivity, PurchaseActivity.class).putExtra("type", "subs").putExtra("orientation", ot).putExtra("subscription_id", subscriptionId);
		mActivity.startActivity(i);
	}
	
	public void consumePurchase(String productId, CallbackContext callbackContext) {
		mActivity = cordova.getActivity();
		callbackContextKeepCallback = callbackContext;
		boolean Result;
    	JSONObject outerObject = new JSONObject();
    	try {
    		Result = bp.consumePurchase(productId);
            outerObject.put("event", "consumePurchase");
            outerObject.put("consume", Result);
            callbackSuccess(callbackContextKeepCallback, outerObject);
    	} catch (Throwable t) {
    		callbackContextKeepCallback.error(0);
        }
	}
}
