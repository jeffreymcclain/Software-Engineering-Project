var blacklist = ["*://*.pornhub.com/*", "*://*.yahoo.com/*","*://*.bing.com/*"];

/*function enforceBlacklist(blacklist) { //blocks and logs attempted access to urls on blacklist
  console.log("blacklist url blocked: " + blacklist.url);
  console.log("blacklist time blocked: " + new Date(blacklist.timeStamp));
  //return {redirectUrl: "https://www.google.com/"};
  return {cancel: true}; 
}

chrome.webRequest.onHeadersReceived.addListener(enforceBlacklist, {urls: blacklist}, ['blocking']);
*/

var continuous = setInterval(function() { //removes tabs on blacklist immediately
	chrome.tabs.query({
		//active: true,
		//currentWindow: true,
		//windowType: "normal",
		url: blacklist
	}, function(tabs) {
		//var tabURL = tabs[0].url;
		//console.log(tabURL);
		for (var i = 0; i < tabs.length; i++) {
			console.log(tabs[i].id);
			chrome.tabs.remove(tabs[i].id);
		}
	});
}, 5000);

/*setTimeout(function(){
			startSession(tab);
		}, 3000);*/

/*var continuous = setInterval(function() { //removes tab on blacklist
		chrome.tabs.query({
			active: true,
			currentWindow: true,
			windowType: "normal",
			url: blacklist
		}, function(tabs) {
			var tabURL = tabs[0].url;
			console.log(tabURL);
			console.log(tabs[0].id);
			chrome.tabs.remove(tabs[0].id);
		});
}, 5000);*/

/*var testSocket = new WebSocket("ws://localhost:4649/test");
testSocket.onopen = function (event) {
	testSocket.send("testing");
};

window.addEventListener("beforeunload", function (e) {
	var confirmationMessage = "\o/";
	console.log("testSend");
	testSocket.send("testSend");

	e.returnValue = confirmationMessage;     // Gecko, Trident, Chrome 34+
	return confirmationMessage;              // Gecko, WebKit, Chrome <34
});

var number = 0;*/
/*chrome.windows.onRemoved.addListener(function (windowId) {
	number--;
	console.log(number);
	testSocket.send("initial test, window number: " + number);
	if (number == -1 && (mode == "funmode" || mode == "studymode")) {
		testSocket.send("testing1");
		const whitelist = wList.split(',');
		for (var i = 0; i < whitelist.length; i++) {
			if (session.url.indexOf(whitelist[i]) != -1) { //sends final end time when window closed and url is on whitelist
				testSocket.send("testing2");
				var time = new Date();
				testSocket.send(name + ":logEndTime:" + count + ":<" + session.url + ">:<" + formatTime(time) + ">");
				console.log(name + ":logEndTime:" + count + ":<" + session.url + ">:<" + formatTime(time) + ">");
			}
		}
	}
});*/


	
	window.onbeforeunload = function (e) {
		number--;
		console.log(number);
		testSocket.send("2initial test, window number: " + number);
		if (number == -1 && (mode == "funmode" || mode == "studymode")) {
			testSocket.send("2testing1");
			const whitelist = wList.split(',');
			for (var i = 0; i < whitelist.length; i++) {
				if (session.url.indexOf(whitelist[i]) != -1) { //sends final end time when window closed and url is on whitelist
					testSocket.send("2testing2");
					var time = new Date();
					testSocket.send(name + ":logEndTime:" + count + ":<" + session.url + ">:<" + formatTime(time) + ">");
					console.log(name + ":logEndTime:" + count + ":<" + session.url + ">:<" + formatTime(time) + ">");
				}
			}
		}
	}

/*var socket = new WebSocket('ws://localhost:4649/test');
socket.addEventListener('open', function (event) {
	console.log("hello server");
    socket.send('Hello Server!');
});

socket.onclose = function(event) {
  console.log("socket closed");
  test();
};

function test() {
	var refreshInterval = setInterval(function() { 
		socket = new WebSocket("ws://localhost:4649/test");
		socket.onopen = function (event) { //if websocket is open
			console.log("connection open");
		};
	}, 15 * 1000);
}*/

/*var testingtime = new Date();
console.log("testing time format");
console.log(testingtime.getHours() + ":" + testingtime.getMinutes() + ":" + testingtime.getSeconds());*/

/*chrome.history.onVisited.addListener(function (stuff) {
	console.log("url visited: " + stuff.url);
	console.log("time visited: " + new Date(stuff.lastVisitTime));
});

 chrome.webRequest.onHeadersReceived.addListener(
	function(details) { 
		console.log("url blocked: " + details.url);
		console.log("time blocked: " + new Date(details.timeStamp));
		return {cancel: true}; 
		//return {redirectUrl: "https://www.google.com/"};
	},
	{urls: ["*://www.reddit.com/*"]},
	["blocking"]
);*/

/*chrome.tabs.onCreated.addListener(
	function(tab) {
		console.log(tab.id);
		console.log(tab.url);
	}
);

chrome.tabs.onRemoved.addListener(function(tabId, info) {
		console.log(tabId);
		if (info.isWindowClosing == true)
			console.log("the window closed");
	}
);*/


/*function handleCreated(tab) {
  console.log(tab.id);
}

chrome.tabs.onCreated.addListener(handleCreated);*/

