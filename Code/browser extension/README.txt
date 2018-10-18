install links: 
https://chrome.google.com/webstore/detail/beta-watch-parental-monit/pmjnelnhjaabpglmadebnkjhhcjonggj?utm_source=chrome-app-launcher-info-dialog
https://addons.mozilla.org/en-US/firefox/addon/betawatch/?src=api

Websocket should be started before the browser, in server mode at address ws://localhost:4649/test
If the extension is started before the websocket, it will attempt to connect every 15 seconds.
If the websocket is stopped while the extension is running, the extension should attempt
to reconnect within 30 seconds, and retry every 15 seconds thereafter.

WebSocket Example:

Client: chrome:request_blacklistWebsites:0     
Server: chrome:request_blacklistWebsites:0:pornhub.com,steam.com
//pornhub and steam are added to the blacklist. any urls containing pornhub.com or steam.com are blocked.
//the blacklist has the highest precedence, so if the same url is added to the whitelist it will still be blocked
//the blacklist is always enforced regardless of security level
//the blacklist persists even if the whitelist and/or security level is changed
//furthermore, the blacklist is saved from the prior browser session.
//if the extension fails to communicate with the websocket, then it will use the last known blacklist

Client: chrome:request_whitelistWebsites:1
Server: chrome:request_whitelistWebsites:1:google,mycsueb,khanacademy
//Google, mycsueb, and Khanacademy are added to the whitelist
//the whitelist implementation depends on the current security mode

Client: chrome:request_securityLevel:2
Server: chrome:request_securityLevel:2:FunMode
//in FunMode, websites on the whitelist are time monitored
//the start and end of each website top level domain visit (google.com, mycsueb.com) is sent to the websocket

Client: chrome:request_blacklistWebsites:3
Server: chrome:request_blacklistWebsites:3:pornhub.com,xvideos.com,minecraft.com
//the blacklist is updated to these 3 websites. the full blacklist must be sent on each request.

Client: chrome:request_whitelistWebsites:3
Server: chrome:request_whiteistWebsites:3:google,schoolloop,mathisfun
//the whitelist is updated to these 3 websites. the full whitelist must be sent on each request.

Client: chrome:request_securityLevel:4
Server: chrome:request_securityLevel:4:studymode
//all websites not on the whitelist are blocked
//websites on the whitelist are still monitored for time

Client: chrome:logStartTime:5:www.google.com:22*7*25
//the user visited google.com at 10:07:25 PM
Client: chrome:logEndTime:6:www.google.com:22*15*8 
//the user left google.com at 10:15:08 PM
//times in 24-hour format, only top level domain is sent

Potential Issues:
Currently I have the request number (count) loop around to 0 after reaching 100, using:
if (count > 100) count = 0; 
I figure this will prevent a scenario where count gets infinitely large.

Opening multiple browser windows side-by-side, only the active window is logged. This 
is determined by whichever window the user is last clicked on / interacted with.

For Firefox, when the web browser is closed, the extension is immediately killed.
This means the last "end" time will not be sent to the websocket.
I notice that the websocket records when a client disconnects, so if the timestamp could be determined that would serve as the final end time.
The final end time is sent with Chrome.

Furthermore, in regards to time restrictions I would prefer if the total time was calculated server-side.
This should be able to be accomplished by doing some variation of (total += (end-start))
Once total goes over the alotted time for a given website, it can be added to the blacklist.
When a new day begins, it can be removed from the blacklist and total can be set to 0.
Doing the calculation client-side might lead to unintended behavior. If the browser is closed,
times and other data could be lost or reset to 0.

The whitelist is currently keyword based. This is because blocking based on url often breaks websites.
For example, most Google pages call googleapis.com, googleanalytics, and other similarly named services.
blocking everything except "google.com" would block these as well, breaking functionality.
Another related issue is that all required subdomains might not share the keyword, which could lead to unintended breakage.
Alternately, rogue sites could take advantage of this by inserting "google" somewhere in the url to bypass being blocked.
This shouldn't be an issue for the demo since we can choose websites beforehand, but irl it could cause issues.









