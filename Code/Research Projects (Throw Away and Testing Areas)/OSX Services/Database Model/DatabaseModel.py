#!/usr/bin/env python

#include pyodbc #required for sql database access
#pip install pyodbc
#also includes install of OBDC driver in OSX
#brew update
#brew instal unixodbc
#brew install freetds --with-unixodbc

#Notes to self: lists are mutable, tuples are not mutable
#To do:
#  Implement all methods
#  Implement as singleton

class DatabaseModel (object) :
    """A class to manage the database access of the BetaWatch program
    
    Attributes:
        __programWhitelist: A list of tuples representing the individual programs whitelisted for a user, and the time permitted (childUser, program, time).
        __programBlacklist: a list of tuples representing the individual programs blacklisted for a user (childUser, program)
    
    
    """

#--------------------class variables------------------------
    #remote server access setup
    port = 8765
    server = 'localhost'
    server_address = (server, port)
    #sql database setup
    __database = 'DatabaseName'
    __dbUserName = 'name'
    __dbPassword = 'password'
    __connection
    __cursor
    
    #private lists from database
    __programWhitelist = [] #list of tuples (user, program, time)
    __programBlacklist = [] #list of tuples (user, program)
    __websiteWhitelist = [] #list of tuples (user, website, time)
    __websiteBlacklist = [] #list of tuples (user, website, time)
    __logs = [] #list of log entries in tuples (logNumber, source, user, action, message)
    __timeRestrictions = [] #list of tuples (user, time)
    __lastLogNumber = 0
    #globals in __init__
    #__connection  #connection to SQL server
    #__cursor      #used to make queries to SQL server
    
    
    
    #initialize database Model
    def __init__(self):
        #start connection to database or file system
        
        #file system setup (for testing)
        programWhitelistFile = open("pwl.txt", "r")
        programBlacklistFile = open("pbl.txt", "r")
        websiteWhitelistFile = open("wwl.txt", "r")
        websiteBlacklistFile = open("wbl.txt", "r")        
        logFile = open("log.txt", "r")
        self.__lastLogTime
        
        #server system setup (to be implemented)
        
        #sql database setup
        self.__connection = pyodbc.connect('DRIVER={ODBC Driver 13 for SQL Server};SERVER='+server+';PORT='+port+';DATABASE='+database+';UID='+username+';PWD='+ password)
        self.__cursor = cnxn.cursor()        
        
        #populate whitelists and blacklists, populate time restrictions
        
        
        
        
#-----------------------Accessors--------------------------
        
    #gets all blacklist entries for user
    def getBlacklist(user):
        #fetch all blacklist entries (apps and websites)
        blacklist = self.getProgramBlacklist(user) + self.getWebsiteBlacklist(user)
        return blacklist

    def getProgramBlacklist(user):
        #fetch all programs blacklisted for user
        programs = []
        return programs
    
    def getWebsiteBlacklist(user):
        #fetch all blacklisted websites for user	
        websites = []
        return websites
    
    def getWhitelist(user):
        #fetch all whitelisted websites and programs
        whitelist = []
        whitelist.extend(self.getProgramWhitelist(user))
        whitelist.extend(self.getWebsiteWhitelist(user))
        return whitelist
    
    def getProgramWhitelist(user):
        #fetch all whitelisted programs for a child user
        return __programWhitelist
    
    def getWebsiteWhitelist(user):
        #fetch all whitelisted websites for a child user
        return __websiteWhitelist
    
    def getLogs():
        #returns all log entries
        return __logs
    
    def getLogs(user):
        #returns all log entries for user
        entriesForUser = []
        return entriesForUser
    
    def getLogs(user, time):
        #returns all log entries for user starting at time
        entriesForUser = self.getLogs(user)
        #remove entries before time
        return entriesForUser
    
    def getUsers(adminUser):
        #returns the names of all child users related to adminUser
        childUsers = []
        childUsers.sort()
        return childUsers
    
    def getRequests(adminUser):
        #returns all requests (answered and unanswered) for all child users related to adminUser
        requests = []
        return requests
    
    def getSpecialPermissions(username):
        #gets all  special permissions for childUser
        permissions = []
        return permissions
    
#------------------------------------mutators-----------------------------
    
    def addAdminUser(username, password):
        #adds username and a password into administrator list
        #returns 1 if successful
        #returns 0 if username is already in list
        #returns -1 if username password combination is already in the list
        isAdded = -1
        return isAdded
    
    def addChildUser(parentUsername, childUsername):
        #adds parentUsername and childUsername to a monitored list
        #returns 1 if successful
        #returns 0 if the childUsername and parentUsername combination is already in the list
        #returns -1 if parentUsername is not in the administrator list
        isAdded = -1
        return isAdded
    
    def addWebsiteBlacklist(parentUsername, childUsername, website):
        #adds website to childUsername's blacklist
        return
    
    def addWebsiteWhitelist(parentUsername, childUsername, website, time):
        #adds website to childUsername's whitelist with permitted time
        return
    
    def addProgramBlacklist(parentUsername, childUsername, program):
        #adds website to childUsername's blacklist
        return
    
    def addProgramWhitelist(parentUsername, childUsername, program, time):
        #adds website to childUsername's whitelist with permitted time
        return
    
    def addProgramSpecialPermission(parentUsername, childUsername, program, time):
        #adds additional time to access program for time amount for childUsername
        return
    
    def addWebsiteSpecialPermission(parentUsername, childUsername, website, time):
        #adds additional time to access website for time amount for childUsername
        return
    
#---------------------------------special Acessors--------------------------

    def checkLogin(username, password):
        #check if username and password are correct
        #Note: calls userLogin for admin user immediately
        #return 1 if successful
        #return 0 if wrong password
        #return -1 if username does not exist
        isLoggedIn = -1
        return isLoggedIn
    
    def userLogin(username, time):
        #creates a log entry when a user (both child and admin) logs into the system
        return
    
    def userLogout(username, time):
        #creates a log entry when a user (both child and admin) logs out of the system
        return
    
    def logWebsiteTimeout(username, time, website):
        #creates a log entry when a child user uses all time permitted for a website
        return
    
    def logProgramTimeout(username, time, program):
        #creates a log entry when a chld user uses all time permitted for a program
        return
    
    
    
    