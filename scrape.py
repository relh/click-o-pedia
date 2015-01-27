import urllib2
import re

f = open("./parsed1k", 'r')
g = open("./parsed1kREAL", 'w')

for line in f:
	string = line.split("\n")[0]
 	g.write("\"" + string + "\"," + "\n");

# ---

# f = open("./unparsed1k", 'r')
# g = open("./parsed1k", 'w')
# for line in f:
# 	string = line.split('\t')[1]
# 	print string
# 	g.write(string + "\n");

# --- 

# response = urllib2.urlopen('http://en.wikipedia.org/wiki/User:West.andrew.g/Popular_pages')
# html = response.read()
# # print html

# i = 1
# f = open("./top5000", 'w')
# while i < 5000:
# 	lazyItem = re.compile(str(i)+'</td>\\n<td><a href=\"/wiki/[^"]+')#\"")
# 	search = lazyItem.search(html)
# 	if search:
# 		search = search.group(0)
# 		actualTitle = search.split("wiki",1)[-1] 
# 		print actualTitle
# 		f.write(actualTitle+"\n")
# 		print i
# 	i += 1

# <td>"+str(i)+"</td><td><a href=\"