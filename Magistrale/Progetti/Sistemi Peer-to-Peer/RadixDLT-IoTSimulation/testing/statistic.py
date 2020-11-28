import os
import csv
import numpy as np
import scipy.stats
import matplotlib.pyplot as plt
plt.style.use('seaborn-whitegrid')


def mean_confidence_interval(data, confidence=0.95):
    a = 1.0 * np.array(data)
    n = len(a)
    m, se = np.mean(a), scipy.stats.sem(a)
    h = se * scipy.stats.t.ppf((1 + confidence) / 2., n-1)
    return m, m-h, m+h


startingDir = 'data/output'
plotTestNumber = 12
totalRequests = 447

singleTestData = []
allLatencies = []
allErrors = 0
errorsData = []
tipsData = []
tipsDataSTD = []
powsData = []
powsDataSTD = []

plotData = {}


path = os.walk(startingDir)
next(path)
for directory in path:
    tempTestData = {
        'name': directory[0].split('/')[-1],
        'tipsValue': [],
        'powValue': [],
        'errors': 0
    }
    for csvFilename in directory[2]:
        with open(directory[0]+'/'+csvFilename, 'r') as csvFile:
            reader = csv.reader(csvFile)
            for row in reader:
                srt = int(row[1])
                tips = int(row[2])
                fin = int(row[3])
                if fin is -1:
                    tempTestData['errors'] += 1
                    allErrors += 1
                else:
                    tipsValue = tips - srt
                    powValue = fin - tips
                    tempTestData['powValue'].append(powValue)
                    tempTestData['tipsValue'].append(tipsValue)
                    allLatencies.append(tipsValue+powValue)

                    latence = fin - srt
                    if latence in plotData.keys():
                        plotData[latence] += 1
                    else:
                        plotData[latence] = 1
        csvFile.close()

    errorsNotWritten = totalRequests - \
        len(tempTestData['powValue']) - tempTestData['errors']
    tempTestData['errors'] += errorsNotWritten
    allErrors += errorsNotWritten

    singleTestData.append(tempTestData)

print('Avg= ' + str(round(np.mean(allLatencies), 4)))
print('Err= ' + str(100 * round((allErrors / (totalRequests * len(singleTestData))), 4)) + '%')
print(mean_confidence_interval(allLatencies))

fig = plt.figure()
ax = plt.axes()

sortedData = {}
for k, v in sorted(plotData.items()):
    sortedData[k] = v

mean = int(round(np.mean(allLatencies), 4))
plt.plot(range(len(sortedData)), list(sortedData.values()), color='#b42526')
plt.axvline(x = mean, color='#252525')
plt.show()
