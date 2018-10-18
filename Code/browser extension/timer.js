/*chrome.history.onVisited.addListener(function (stuff) {
	console.log("url visited: " + stuff.url);
	console.log("time visited: " + new Date(stuff.lastVisitTime));
});

 /*chrome.webRequest.onHeadersReceived.addListener(
	function(details) { 
		console.log("url blocked: " + details.url);
		console.log("time blocked: " + new Date(details.timeStamp));
		return {cancel: true}; 
		//return {redirectUrl: "https://www.google.com/"};
	},
	{urls: ["*://www.reddit.com/*"]},
	["blocking"]
);*/

//based on https://www.npmjs.com/package/chrome-track-activity
startTrackingActivity(onSessionStart, onSessionEnd);
//var session = { tabId: -1, url:"www.fake.com",  endTime:"00:00:00"};
function startTrackingActivity(onSessionStart, onSessionEnd) {
  console.log("function started");
  var session = { tabId: -1 };
  //console.log(typeof session);

  function endSession() {
    if (session.tabId != -1) {
      session.endTime = Date.now();
      onSessionEnd && onSessionEnd(session);
      session = { tabId: -1 };
    }
  }

  function startSession(tab) {
	console.log("the session has started");
    endSession();
    session = {
      tabId: tab.id,
	  url: tab.url,
      startTime: Date.now()
    };
    onSessionStart && onSessionStart({
        tabId: session.tabId,
        url: session.url,
        startTime: session.startTime
    });
  }
  
  //starts a new session when user changes windows, if windows have different domains
  function trackWindowFocus(windowId) {
	if (windowId !== -1) {
	  chrome.windows.getCurrent({ populate: true }, function(window) {
		var activeTab = window.tabs.filter(function(tab) {
		  return tab.active;
		})[0];
		activeTab.url = activeTab.url.split('/')[2]; //formats tab url to "www.reddit.com"
		console.log(activeTab.url);
		console.log(session.url);
		if (typeof activeTab.url == "undefined") { //Firefox newtab page has undefined url
			activeTab.url = "newtab";
			console.log(activeTab.url);
		}
		if (activeTab && activeTab.id != session.tabId && activeTab.url != session.url) {
		  startSession(activeTab);
		}
	  });
	}
  }

  //starts new session when user changes tabs, if tabs have different domains
  function trackActiveTab(activeInfo) { 
    chrome.tabs.get(activeInfo.tabId, function(tab) {
		tab.url = tab.url.split('/')[2]; //formats tab url to "www.reddit.com"
		if (!chrome.runtime.lastError && tab.id != session.tabId && tab.url != session.url) {
			console.log("tab.url: " + tab.url);
			if (typeof tab.url == "undefined") { //Firefox newtab page has undefined url
				tab.url = "newtab";
				console.log(tab.url);
			}
			console.log("session.url: " + session.url);
			startSession(tab);	
		}
    });
  }

  //starts a new session when current tab changes domains
  function trackTabUpdates(tabId, changeInfo, tab) { 
    tab.url = tab.url.split('/')[2]; //formats tab url to "www.reddit.com"
	if (tab.active && changeInfo.status == "loading") {
		if (Object.keys(session).length == 1 || tab.url != session.url) {
			console.log(tab.url);
			if (typeof tab.url == "undefined") { //Firefox newtab page has undefined url
				tab.url = "newtab";
				console.log(tab.url);
			}
			console.log(session.url);
			chrome.windows.get(tab.windowId, function(window) {
				if (!chrome.runtime.lastError && window.focused) {
					startSession(tab);
					//console.log(session.url);
				}
			});
		}
    }
  }
  
	var number = 0; //keeps track of number of windows
	chrome.windows.onCreated.addListener(function (window) {
		number++;
		console.log(number);
	});

	chrome.windows.onRemoved.addListener(function (windowId) { //doesn't work on Firefox for some reason
		number--;
		console.log(number);
		if (number == -1) { //sends final end time when window closed and url is on whitelist
			const whitelist = wList.split(',');
			const blacklist = bList.split(',');
			
			if (mode == "studymode") {
				for (var i = 0; i < whitelist.length; i++) { //only log if on whitelist
					if (session.url.indexOf(whitelist[i]) != -1) {
						var time = new Date();
						testSocket.send(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(time));
						console.log(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(time));
						count++;
					}
				}
			} else {
				var bool = false;
				for (var i = 0; i < blacklist.length; i++) { //only log if on whitelist
					if (session.url.indexOf(blacklist[i]) != -1) {
						bool = true;
					}
				}
				if (bool == false) {
					var time = new Date();
					testSocket.send(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(time));
					console.log(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(time));
					count++;
				}
			}
		}
	});

  chrome.windows.onFocusChanged.addListener(trackWindowFocus);
  chrome.tabs.onUpdated.addListener(trackTabUpdates);
  chrome.tabs.onActivated.addListener(trackActiveTab);
}

function formatTime(t) {
  var time = new Date(t);
  return time.getHours() + "*" + time.getMinutes() + "*" + time.getSeconds(); 
}

function onSessionStart(session) {
  if (session.url != "extensions" && session.url != "newtab") { //doesn't send these pages to web socket
	const whitelist = wList.split(','); //formats whitelist into array
	const blacklist = bList.split(','); //format blacklist into array
	
	if (mode == "studymode") {
		for (var i = 0; i < whitelist.length; i++) { //only log if on whitelist
			if (session.url.indexOf(whitelist[i]) != -1) {
				testSocket.send(name + ":logStartTime:" + count + ":" + session.url + ":" + formatTime(session.startTime));
				console.log(name + ":logStartTime:" + count + ":" + session.url + ":" + formatTime(session.startTime));
				count++;
			}
		}
	} else {
		var bool = false;
		for (var i = 0; i < blacklist.length; i++) { //only log if on whitelist
			if (session.url.indexOf(blacklist[i]) != -1) {
				bool = true;
			}
		}
		if (bool == false) {
			testSocket.send(name + ":logStartTime:" + count + ":" + session.url + ":" + formatTime(session.startTime));
			console.log(name + ":logStartTime:" + count + ":" + session.url + ":" + formatTime(session.startTime));
			count++;
		}
	}
  }
}

function onSessionEnd(session) {
  if (session.url != "extensions" && session.url != "newtab") {
	const whitelist = wList.split(','); //formats whitelist into array
	const blacklist = bList.split(','); //format blacklist into array
	
	if (mode == "studymode") {
		for (var i = 0; i < whitelist.length; i++) { //only log if on whitelist
			if (session.url.indexOf(whitelist[i]) != -1) {
				testSocket.send(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(session.endTime));
				console.log(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(session.endTime));
				count++;
			}
		}
	} else {
		var bool = false;
		for (var i = 0; i < blacklist.length; i++) { //only log if on whitelist
			if (session.url.indexOf(blacklist[i]) != -1) {
				bool = true;
			}
		}
		if (bool == false) {
			testSocket.send(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(session.endTime));
			console.log(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(session.endTime));
			count++;
		}
	}
  }
}