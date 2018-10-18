import psutil
#pip install psutil

from pprint import pprint as pp

class BetaWatchService:


    def __init__(self):
        __programs = self.updatePrograms() #list of running programs format: programName, application information tuple
        pass

    def isBlacklisted(self):
        #get active programs
        #foreach p in programs
            #if blacklisted, kill
        pass

    def getProgramWhitelist(self):
        whitelist = {}
        return whitelist


    def updatePrograms(self):
        activePrograms = {}
        processIDs = psutil.pids()
        for i in processIDs:
            p = psutil.Process(i)
            if (p.status() == 'running') and (p.name() not in activePrograms):
                whitelist = self.getProgramWhitelist()
                if( p.name() not in whitelist):
                    activePrograms[p.name()] = (p.name,0)
                else:
                    activePrograms[p.name()] = (p,whitelist[p.name()])

        return activePrograms

    def monitorRuntimes(self):
        #figure out the while loop

        runningPrograms = self.updatePrograms()

service = BetaWatchService()
print(service.updatePrograms())
