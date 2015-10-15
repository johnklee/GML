package ml.rg

/**
 * Logistic Regression Methods Aggregation Class 
 * 
 * @see
 * 		http://localhost/jforum/posts/list/2230.page
 * 		https://github.com/libing360/machine-learning-in-action/tree/master/Ch05
 * 
 * @author «¶¼y
 *
 */
class LogisticRG {
	def loadDataSet(File trnFile, boolean addX0=true)
	{
		def dataMat = [], labelMat = []
		trnFile.eachLine{ line->
			def lineArr =[]
			def curLine = line.trim().split('\t') as String[]
			if(addX0) lineArr.add(1.0)
			(0..(curLine.size()-2)).each{
				lineArr.add(Double.valueOf(curLine[it]))
			}
			dataMat.add(lineArr)
			labelMat.add(Double.valueOf(curLine[-1]))
		}
		
		return new Tuple(dataMat, labelMat)
	}
}
