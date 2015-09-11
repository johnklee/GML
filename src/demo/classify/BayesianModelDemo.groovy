package demo.classify

import dm.basic.statistic.BayesianModel
import dm.basic.statistic.BayesianModel.Classify
import dm.input.SimpleIn


// 1) Training data
SimpleIn si = new SimpleIn(new File("data/weathers.dat"))

// 2) Training model
BayesianModel bm = new BayesianModel()
Classify cfy = bm.train(si)

// 3) Make prediction
def test_data = [
	'sunny,cool,high,true',
	'sunny,cool,normal,true',
	'rainy,mild,normal,false'
]
printf("\t[Info] Start evaluation classify...\n")
for(def td:test_data) {
	def rst = cfy.predict(td)
	printf("\t%s -> %s (%.02f%%)\n", td, rst.get(0), 100*rst.get(1))
}
