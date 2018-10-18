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
	console.log(windowId);
	if (mode == "funmode" || mode == "studymode") { //only track if FunMode or StudyMode
		if (windowId !== -1) {
		  browser.windows.getCurrent({ populate: true }, function(window) {
			var activeTab = window.tabs.filter(function(tab) {
			  return tab.active;
			})[0];
			activeTab.url = activeTab.url.split('/')[2]; //formats tab url to "www.reddit.com"
			console.log(activeTab.url + " " + session.url);
			if (typeof activeTab.url == "undefined") { //Firefox newtab page has undefined url
				activeTab.url = "newtab";
				console.log(activeTab.url);
			}
			if (activeTab && activeTab.id != session.tabId && activeTab.url != session.url) {
			  console.log(activeTab.id + " " + session.tabId);
			  console.log(activeTab.url + " " + session.url);
			  startSession(activeTab);
			}
		  });
		}
    } else {
		endSession(); //sends final end session info if security level is changed
	}
  }

  //starts new session when user changes tabs, if tabs have different domains
  function trackActiveTab(activeInfo) { 
	if (mode == "funmode" || mode == "studymode") { //only track if FunMode or StudyMode
    browser.tabs.get(activeInfo.tabId, function(tab) {
		tab.url = tab.url.split('/')[2]; //formats tab url to "www.reddit.com"
		if (!browser.runtime.lastError && tab.id != session.tabId && tab.url != session.url) {
			console.log("tab.url: " + tab.url);
			if (typeof tab.url == "undefined") { //Firefox newtab page has undefined url
				tab.url = "newtab";
				console.log(tab.url);
			}
			console.log("session.url: " + session.url);
			startSession(tab);	
		}
    });
	} else {
		endSession(); //sends final end session info if security level is changed
	}
  }

  //starts a new session when current tab changes domains
  function trackTabUpdates(tabId, changeInfo, tab) { 
	if (mode == "funmode" || mode == "studymode") {
		tab.url = tab.url.split('/')[2]; //formats tab url to "www.reddit.com"
		if (tab.active && changeInfo.status == "loading") {
			if (Object.keys(session).length == 1 || tab.url != session.url) {
				console.log(tab.url);
				if (typeof tab.url == "undefined") { //Firefox newtab page has undefined url
					tab.url = "newtab";
					console.log(tab.url);
				}
				console.log(session.url);
				browser.windows.get(tab.windowId, function(window) {
					console.log("testing2");
					if (!browser.runtime.lastError /*&& window.focused*/) {
						console.log("tab url changed");
						startSession(tab);
						//console.log(session.url);
					}
				});
			}
		}
	} else {
		endSession();
	}
  }

  browser.windows.onFocusChanged.addListener(trackWindowFocus);
  browser.tabs.onUpdated.addListener(trackTabUpdates);
  browser.tabs.onActivated.addListener(trackActiveTab);
}

function formatTime(t) {
  var time = new Date(t);
  return time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds(); 
}

function onSessionStart(session) {
  console.log("START", formatTime(session.startTime), session.url);
  if (session.url != "extensions" && session.url != "newtab") { //doesn't send these pages to web socket
	const whitelist = wList.split(','); //formats whitelist into array
	for (var i = 0; i < whitelist.length; i++) { //only log if on whitelist
		if (session.url.indexOf(whitelist[i]) != -1) {
			testSocket.send(name + ":logStartTime:" + count + ":" + session.url + ":" + formatTime(session.startTime));
			count++;
		}
	}
  }
}

function onSessionEnd(session) {
  console.log("END", formatTime(session.endTime), session.url);
  if (session.url != "extensions" && session.url != "newtab") {
	const whitelist = wList.split(','); //formats whitelist into array
	for (var i = 0; i < whitelist.length; i++) { //only log if on whitelist
		if (session.url.indexOf(whitelist[i]) != -1) {
			testSocket.send(name + ":logEndTime:" + count + ":" + session.url + ":" + formatTime(session.endTime));
			count++;
		}
	}
  }
}