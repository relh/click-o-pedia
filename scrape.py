import urllib2
response = urllib2.urlopen('http://en.wikipedia.org/wiki/User:West.andrew.g/Popular_pages')
html = response.read()
print html

import re
i = 1
while i < 5:#5000:
	a = re.compile(str(i)+'</td>\\n<td><a href=\"/wiki/\w+')#\"")
	print a.search(html).group(0)
	print i
	i += 1


	#<td>"+str(i)+"</td><td><a href=\"