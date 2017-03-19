var exec = require('cordova/exec');
module.exports = {
	//Initialize
	Initialize: function(BAZAAR_KEY,NIVAD_APP_ID,NIVAD_APP_SECRET,marketName) {
		cordova.exec(function (result) {
				var event = result["event"];
				if (event == "onBillingInitialized") {
					console.log("Event: "+event);
				}
			},null, 'NivadPlugin', 'init', [BAZAAR_KEY,NIVAD_APP_ID,NIVAD_APP_SECRET,marketName]);
	},
	//Subscribe
	Subscribe: function(successCallback, failureCallback, subscriptionId) {
		cordova.exec(successCallback, failureCallback, 'NivadPlugin', 'subscribe', [subscriptionId]);
	},
	//Buy
	Buy: function(successCallback, failureCallback, productId) {
		cordova.exec(successCallback, failureCallback, 'NivadPlugin', 'purchase', [productId]);
	},
	//Consume
	Consume: function(successCallback, failureCallback, productId) {
		cordova.exec(successCallback, failureCallback, 'NivadPlugin', 'consumePurchase', [productId]);
	}
};
