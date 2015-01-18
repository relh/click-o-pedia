import urllib2
import re

response = urllib2.urlopen('http://en.wikipedia.org/wiki/User:West.andrew.g/Popular_pages')
html = response.read()
# print html

i = 1
f = open("./top5000", 'w')
while i < 5000:
	lazyItem = re.compile(str(i)+'</td>\\n<td><a href=\"/wiki/[^"]+')#\"")
	search = lazyItem.search(html)
	if search:
		search = search.group(0)
		actualTitle = search.split("wiki",1)[-1] 
		print actualTitle
		f.write(actualTitle+"\n")
		print i
	i += 1

	#<td>"+str(i)+"</td><td><a href=\"