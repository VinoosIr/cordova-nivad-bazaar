var exec = require('cordova/exec');
module.exports = {
	//Initialize
	Initialize: function(successCallback, BAZAAR_KEY,NIVAD_APP_ID,NIVAD_APP_SECRET,marketName) {
		cordova.exec(successCallback, null, 'NivadPlugin', 'init', [BAZAAR_KEY,NIVAD_APP_ID,NIVAD_APP_SECRET,marketName]);
	},
	//Subscribe
	Subscribe: function(successCallback, subscriptionId) {
		cordova.exec(successCallback, null, 'NivadPlugin', 'subscribe', [subscriptionId]);
	},
	//Buy
	Buy: function(successCallback, productId) {
		cordova.exec(successCallback, null,'NivadPlugin', 'purchase', [productId]);
	},
	//Consume
	Consume: function(successCallback, productId) {
		cordova.exec(successCallback, null, 'NivadPlugin', 'consumePurchase', [productId]);
	},
	//Is Purchased
	IsPurchased: function(successCallback, productId) {
		cordova.exec(successCallback, null, 'NivadPlugin', 'isPurchased', [productId]);
	},
	//Is Subscribed
	IsSubscribed: function(successCallback, productId) {
		cordova.exec(successCallback, null, 'NivadPlugin', 'isSubscribed', [productId]);
	}
};
