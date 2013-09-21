package edu.cmu.deiis.annotators;

import java.util.ArrayList;
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
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		thisProcessorClassName = this.getClass().getName();
		FSIterator<NGram> NGramit = (FSIterator<NGram>) aJCas.getAnnotationIndex(NGram.type);
		
		HashMap<Annotation, ArrayList<NGram>> NGramAnnotationMap = new HashMap<Annotation, ArrayList<NGram>>();
		
		Question question = null;
		
		while (NGramit.hasNext()) {
			NGram nGram = NGramit.next();
			Annotation annotation = nGram.getOrigion();
			if(NGramAnnotationMap.get(annotation)==null){
				NGramAnnotationMap.put(annotation, new ArrayList<NGram>());
			}
			if(annotation.getClass() == Question.class){
				question = (Question) annotation;
			}
			NGramAnnotationMap.get(annotation).add(nGram);
		}
		
		Set<Annotation> annotationKeyList = NGramAnnotationMap.keySet();
		annotationKeyList.remove(question);
		
		for(Annotation answer : annotationKeyList){
			Double matchedNGramScore = 0.;
			Double totalNGramScore = 0.;
			for(NGram aNGram:NGramAnnotationMap.get(answer)){
				Double score = (double) aNGram.getN();
				totalNGramScore+=score;
				for(NGram qNGram : NGramAnnotationMap.get(question)){
					if(aNGram.getCoveredText().equals(qNGram.getCoveredText())){
						matchedNGramScore+=score;
						break;
					}
				}
			}
			AnswerScore annotation = new AnswerScore(aJCas);
			annotation.setCasProcessorId(thisProcessorClassName);
			annotation.setAnswer((Answer)answer);
			annotation.setBegin(answer.getBegin());
			annotation.setEnd(answer.getEnd());
			annotation.setConfidence(1);
			annotation.setScore(matchedNGramScore/totalNGramScore);
			annotation.addToIndexes();
		}
	}

}
