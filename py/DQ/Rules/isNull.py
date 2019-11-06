import pandas as pd
def isNull(frameWrapper, rule):
	df = frameWrapper.cache['data']
	currCol = rule['col']
	currRule = rule['rule']
	temp = df[currCol]
	totLength = len(temp)
	totErrors = totLength - sum(pd.notnull(temp) + 0)
	totCorrect = totLength - totErrors
	toPaint = "'NaN'"
	# Create DataFrame 
	data = {'Columns': [currCol], 'Errors':[totErrors], 'Valid': [totCorrect], 'Total': [totLength], 'Rules': [currRule], 'Description': [''], 'toColor': [toPaint]} 
	ruleDf = pd.DataFrame(data) 
	return ruleDf 