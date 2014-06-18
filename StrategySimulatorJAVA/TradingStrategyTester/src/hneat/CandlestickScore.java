package hneat;
import java.util.List;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;


public class CandlestickScore implements CalculateScore {

	@Override
	public double calculateScore(MLMethod phenotype) {
		CandlestickEvaluation eval = new CandlestickEvaluation(phenotype);
		IntDoublePair trialResult;
		
		for(int i=0; i<100; i++) {
			eval.generateNewTest();
			trialResult = eval.query();
			double trialFitness = eval.evaluateFitness(trialResult);
			eval.addFitness(trialFitness);
		}
		
		return eval.getFitness();
	}

	@Override
	public boolean requireSingleThreaded() {
		return true;
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}

}
