This program allows you to run a websocket server or client.

to run a server make sure that the server toggle button on top is toggled

Server functionality
	The server has three modes of operating.
		The first of which is when it gets a message don't do anything.
		
		The second is to echo whatever message it got back to the client.
		
		The third is that it will Autorespond with a predefined message from predefined keywords
			In the "Auto Responses" the user can predefine phrases that the server will recognize and will respond with the corresponding response
			The phrases are case and white space sensitive
			The user also has the option of checking the "Unknown Command Responce" checkbox which will send the content of the textbox to the right of the checkbox if 
				a phrase was sent that is not in the table
			
			example: Input: hi Output: bye
	
	Server can also send messages to clients by entering text in the bottom textbox and clicking the "Send" button

Known Bugs:
	The serve may lockup if a client is connected to it and the user clicked the "Close" button
		it is recommended to close all client connections before closeing server