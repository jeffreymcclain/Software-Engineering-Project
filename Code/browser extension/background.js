var name = ""; //String, stores name of the browser
var count = 0; //integer, stores how many server requests have been sent
var bList = ""; //String, unformatted blacklist (bing.com,yahoo.com,facebook.com)
var wList = ""; //String, unformatted whitelist (google,reddit)
var unformattedWList = "";
var mode = ""; //String, security level (freereign, funmode, or studymode)

if (navigator.userAgent.indexOf("Chrome") != -1) //sets name based on useragent
	name = "chrome";
else if (navigator.userAgent.indexOf("Firefox") != -1)
	name = "firefox";
else 
	name = "unknown";

chrome.webRequest.onBeforeRequest.addListener( //redirects fox news to cnn
	function(details) { return {redirectUrl: "https://www.cnn.com/"}; },
	{urls: ["*://www.foxnews.com/*"]},
	["blocking"]
);

function enforceBlacklist(blacklist) { //blocks and logs attempted access to urls on blacklist
  console.log("blacklist url blocked: " + blacklist.url);
  console.log("blacklist time blocked: " + new Date(blacklist.timeStamp));
  //return {redirectUrl: "https://www.google.com/"};
  return {cancel: true}; 
}

function studyMode(details) { //blocks all urls except those on whitelist
	const whitelist = wList.split(','); //formats whitelist into array
	for (var i = 0; i < whitelist.length; i++) {
		if (details.url.indexOf(whitelist[i]) != -1) { //if on whitelist, don't block
			//console.log(details.url); 
			return {cancel: false};
		}
	} 
	//logs attempted website visits that aren't on whitelist
	//console.log("whitelist url blocked: " + details.url); 
	//console.log("whitelist time blocked: " + new Date(details.timeStamp));
	return {cancel: true};
}

chrome.storage.local.get('urls', data => { // loads previous blacklist on startup
  if (data.urls && data.urls[0]) {
    chrome.webRequest.onHeadersReceived.addListener(enforceBlacklist, data, ['blocking']);
  }
});

var testSocket = new WebSocket("ws://localhost:4649/test");
testSocket.onerror = function(error) {
	console.log("1connection failed - try polling");
	refreshing(); //call refreshing function if the websocket isn't open
};
testSocket.onopen = function (event) { 
	console.log("1connection open");
	testSocket.close(); 
	mainProgram(); //begin main program if websocket is already open
};

function refreshing() {
	var refreshInterval = setInterval(function() { 
		testSocket = new WebSocket("ws://localhost:4649/test");
		testSocket.onerror = function(error) { //check if websocket is not open
			console.log("2connection failed - try polling");
		};
		testSocket.onopen = function (event) { //if websocket is open
			console.log("2connection open");
			testSocket.close(); //close temporary websocket
			mainProgram(); //begin main program
			clearInterval(refreshInterval); //break loop
		};
	}, 15 * 1000); //attempt reconnection to websocket every 15 seconds
}

function mainProgram() {
	testSocket = new WebSocket("ws://localhost:4649/test");
	/*testSocket.onclose = function(event) {
		console.log("socket closed");
		refreshing();
	};*/
	
	testSocket.onopen = function (event) { //when testsocket connection is opened
		testSocket.send(name + ":request_blacklistWebsites:" + count);
		count++;
		testSocket.send(name + ":request_whitelistWebsites:" + count);
		count++;
		testSocket.send(name + ":request_securityLevel:" + count);
		count++;
		var continuous = setInterval(function() { //query blacklist, whitelist, and securityLevel
			if (count >= 99) //keeps count from going over 100
				count = 0;
			if (testSocket.readyState == 3) { //retries connection if websocket closes while extension is running.
				console.log("connection lost");
				testSocket.close(); //close socket
				refreshing(); //call function to attempt to reconnect to socket every 15 seconds
				clearInterval(continuous); //clear this interval until the connection is reestablished
			} else {
				testSocket.send(name + ":request_blacklistWebsites:" + count);
				count++;
				testSocket.send(name + ":request_whitelistWebsites:" + count);
				count++;
				testSocket.send(name + ":request_securityLevel:" + count);
				count++;
			}
		}, 100); //repeat 10 times a second!
	};


	testSocket.onmessage = function (e) { //receives messages from server
	  //console.log(typeof e); //displays "object" in console
	  e = e.data.toString().replace(/\s/g,''); //removes whitespaces, converts to String
	  var msg = e.split(':'); //splits String by colons
	  //console.log("1st element: " + msg[0]);
	  //console.log("2nd element: " + msg[1]);
	  //console.log("3rd element: " + msg[2]);

	  if (msg[0] == name && msg[1] == "request_blacklistWebsites" && msg[3] != bList) { 
		bList = msg[3];
		msg.splice(0,3); //removes name, action, and count leaving just the list of urls
		const blacklist = msg[0].split(','); //creates an array of strings, storing urls
		for (var i = 0; i < blacklist.length; i++) { //changes urls into correct format
			blacklist[i] = "*://*." + blacklist[i] + "/*";
		}
		console.log(typeof blacklist[0]); //should display "String" in console
		console.log("1st url: " + blacklist[0]); 
		console.log("2nd url: " + blacklist[1]); 
		chrome.storage.local.set({urls: blacklist});
		chrome.webRequest.onHeadersReceived.removeListener(enforceBlacklist);
		if (blacklist[0]) {
		   chrome.webRequest.onHeadersReceived.addListener(enforceBlacklist, {urls: blacklist}, ['blocking']); //sets blacklist
		   chrome.tabs.query({
				url: blacklist
			}, function(tabs) {
				for (var i = 0; i < tabs.length; i++) { //removes tabs with blacklisted urls immediately, when blacklist is updated
					chrome.tabs.remove(tabs[i].id);
					console.log(tabs[i].id);
					console.log(tabs[i].url);
				}
			});
		}
	  }
	  
	  if (msg[0] == name && msg[1] == "request_whitelistWebsites" && msg[3] != unformattedWList) {
		msg.splice(0,3);
		unformattedWList = msg[0];
		var tempWlist = unformattedWList.split(','); //splits String into array of domains
		for (var i = 0; i < tempWlist.length; i++) {  
			var temp = tempWlist[i].split(".")
			temp.pop()
			tempWlist[i] = temp.join('.');
		}
		wList = tempWlist.join(','); //joins array back together into a String, separating domains by commas
		console.log(wList);
	  }
	  
	  if (msg[0] == name && msg[1] == "request_securityLevel" && msg[3] != mode) {
		chrome.webRequest.onHeadersReceived.removeListener(studyMode);
		msg.splice(0,3);
		mode = msg.toString(); 
		console.log(mode);
		if (mode == "studymode") { //if StudyMode, block everything not on whitelist
			console.log("studymode");
			chrome.webRequest.onHeadersReceived.addListener(studyMode, {urls: ["<all_urls>"]}, ["blocking"]);
		}
	  }
	};
}

