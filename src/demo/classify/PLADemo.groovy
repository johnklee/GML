package demo.classify

import ml.classify.PLA
import ml.classify.PLAClassify
import ml.data.ui.DataInXYChart

import org.jfree.ui.RefineryUtilities

import flib.util.TimeStr


// 1)¡@Prepare Training Data
def x = [[1,7], [1,2], [1,4], [-1,3], [-4,-2], [-3,2], [3,-2], [-2, -11], [2.5, -15], [-1, -12], [1, 22]]
def y = [1,1,1,-1,-1,-1,1, -1, 1, 1, -1]

//DataInXYChart demo = new DataInXYChart("Training Data", x, y)
//demo.pack();
//RefineryUtilities.centerFrameOnScreen(demo);
//demo.setVisible(true);

// 2) Training
PLA pla = new PLA()
PLAClassify cfy = pla.pocket(x, y)
printf("\t[Info] Weighting Matrix(%d/%s):\n", cfy.loop, TimeStr.ToString(cfy.sp))
cfy.w.eachWithIndex{v, i->printf("\t\tw[%d]=%s\n", i, v)}


// 3) Predicting
def t = [[1,3], [-4,1], [2,2], [3,6], [-1,9], [3, 39]]
def r = [1, -1, 1, 1, -1, -1]
def p = []
t.eachWithIndex{ v, i->
	e = cfy.classify(v)
	printf("\t[Info] %s is classified as %d\n", v, e)
	if(e==r[i]) p.add(e) // Correct
	else p.add(3) // Miss
}
t.addAll(x)
p.addAll(y)

// 4) Show Predicting Result
DataInXYChart demo = new DataInXYChart("Training Data", t, p, cfy.w)
demo.pack();
RefineryUtilities.centerFrameOnScreen(demo);
demo.setVisible(true);