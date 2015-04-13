#!/usr/bin/python

from pyfann import libfann
import csv


ann = libfann.neural_net()
ann.create_from_file("Higgs2.net")

answers = []
myList=[]

def normalize():
	for i in range(len(myList[0])):
		minimum = min(myList[0])
		maximum = max(myList[0])
		for j in range(len(myList)):
			x = myList[j][i]
			if(x<minimum):
				minimum = x
		
		for k in range(len(myList)):
			x = myList[k][i]
			x -= minimum
			myList[k][i] = x

        for l in range(len(myList)):
        	x = myList[l][i]
        	if(x>maximum):
        		maximum = x

        for m in range(len(myList)):
        	x = myList[m][i]
        	x = x/maximum
        	myList[m][i]=x

        


with open('higgs_testing_inputs.csv','rb') as csvfile:
	reader = csv.reader(csvfile, delimiter=',', quotechar ='|')
	for row in reader:
		myList.append(row[0:])

# for i in range(len(myList)):
# 	for feature in myList[i]:
# 		feature = long(feature)


# print myList[:5]
for vector in myList:
	for i in range(len(vector)):
		vector[i] = float(vector[i])
	# vector.append(1.0)

normalize()

print myList[1]
for vector in myList:
	answer = ann.run(vector)
	answers.append(answer)


f = open('Fanswers.txt', 'w')
for answer in answers:
	final = answer[0]
	if(final < 0.5):
		final = 0.0
	if(final >= 0.5):
		final = 1.0

	f.write(str(final))
	f.write("\n")
f.close()

print "Done"


