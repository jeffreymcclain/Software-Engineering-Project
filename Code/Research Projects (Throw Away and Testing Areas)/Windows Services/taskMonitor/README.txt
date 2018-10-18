taskMonitor readme

Note: I used visual studio to compile this project, so if you have visual studio installed you can open taskMonitor.sln to view the project

Source code:
------------
If you want to take a look at the code without having to use Visual Studio, then go to \taskMonitor\taskMonitor\ folder, the two main files are:

1)Service1.cs = What the service does
2)Program.cs = main, entry point

I was able to get the service up and running by reading the Microsoft documentation for the .NET framework and looking at the examples they had:
	- https://docs.microsoft.com/en-us/dotnet/framework/windows-services/
	- https://msdn.microsoft.com/en-us/library/ddhy0byf(v=vs.100).aspx
	- https://docs.microsoft.com/en-us/dotnet/framework/windows-services/service-application-programming-architecture

Also, I found a 3-video Youtube series that helped with the process of creating a service and getting the installer working properly:
	https://www.youtube.com/watch?v=uM9o8GsO_u4


INSTALLING:
-----------
-If you want to install the service to test on your computer, go to \taskMonitor\taskMonitor Setup\Release\ , double-click on the setup executable.

-Note: You might need .NET framework 4.5 to run the service, but I believe the installer also takes care of that

-Once the installer is finished, you will need to manually start the service (I didn't want it to start automatically after install just yet).
	-To do this, open up the Task Manager -> Click on the services tab -> Scroll down until you see a service named "TEST SERVICE" -> right-click it
		and start the service. If you have Firefox or Spotify (default blacklisted programs) running, after 15 seconds they will be closed. The service will continue running and checking every 15 seconds to see if these programs are running. Right-click the service to manually stop it

-If you want to take a look at the blacklisted.txt file, it will be installed in the following directory: C:\Program Files (x86)\Team Beta\taskMonitor\
	-NOTE: Program names are case-sensitive


UNINSTALLING:
-------------
-If you want to uninstall the service after testing, you should be able to uninstall by going to the Control Panel and clicking the Uninstall Program option. It will be listed as "taskMonitor"

