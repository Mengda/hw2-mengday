package edu.cmu.deiis.annotators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.*;

public class AnswerScoreAnnotator extends JCasAnnotator_ImplBase {

	private String thisProcessorClassName = null;

	/**
	 * Generates Annotation AnswerScore, gives a score to an Answer sentence.
	 * Score is calculated based on N-gram matching.
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		thisProcessorClassName = this.getClass().getName();
		FSIterator<Annotation> NGramit = aJCas.getAnnotationIndex(NGram.type)
				.iterator();

		HashMap<Annotation, ArrayList<NGram>> NGramAnnotationMap = new HashMap<Annotation, ArrayList<NGram>>();

		Annotation question = null;

		while (NGramit.hasNext()) {
			NGram nGram = (NGram) NGramit.next();
			Annotation annotation = nGram.getOrigin();
			if (NGramAnnotationMap.get(annotation) == null) {
				NGramAnnotationMap.put(annotation, new ArrayList<NGram>());
			}
			if (question == null && annotation.getClass() == Question.class) {
				question = annotation;
			}
			NGramAnnotationMap.get(annotation).add(nGram);
		}

		ArrayList<NGram> questionNGramArrayList = NGramAnnotationMap
				.get(question);

		Set<Annotation> annotationKeyList = NGramAnnotationMap.keySet();
		annotationKeyList.remove(question);

		ArrayList<AnswerScore> answerScoreList = new ArrayList<AnswerScore>();
		for (Annotation answer : annotationKeyList) {
			Double matchedNGramScore = 0.;
			Double totalNGramScore = 0.;
			for (NGram aNGram : NGramAnnotationMap.get(answer)) {
				Double score = (double) aNGram.getN();
				totalNGramScore += score;
				for (NGram qNGram : questionNGramArrayList) {
					if (aNGram.getCoveredText().equals(qNGram.getCoveredText())) {
						matchedNGramScore += score;
						break;
					}
				}
			}
			AnswerScore annotation = new AnswerScore(aJCas);
			annotation.setCasProcessorId(thisProcessorClassName);
			annotation.setAnswer((Answer) answer);
			annotation.setBegin(answer.getBegin());
			annotation.setEnd(answer.getEnd());
			annotation.setConfidence(1);
			annotation.setScore(matchedNGramScore / totalNGramScore);
			annotation.addToIndexes();
			answerScoreList.add(annotation);
		}

		Collections.sort(answerScoreList, new Comparator<AnswerScore>() {
			public int compare(AnswerScore a, AnswerScore b) {
				return -Double.compare(a.getScore(), b.getScore());
			}
		});

		System.out.print("Q " + question.getCoveredText());

		for (AnswerScore a : answerScoreList) {
			char c = a.getAnswer().getIsCorrect() ? '+' : '-';
			System.out
					.format("%c %.4f %s", c, a.getScore(), a.getCoveredText());
		}
		int correctCount = 0;
		int halfCount = (1 + answerScoreList.size()) / 2;
		for (int i = 0; i < halfCount; ++i) {
			if (answerScoreList.get(i).getAnswer().getIsCorrect())
				++correctCount;
		}
		System.out.format("Precision at %d : %f%n%n", halfCount, correctCount
				/ (halfCount + 0.));
	}
}
