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
    FSIterator<Annotation> NGramit = aJCas.getAnnotationIndex(NGram.type).iterator();

    HashMap<Annotation, ArrayList<NGram>> NGramAnnotationMap = new HashMap<Annotation, ArrayList<NGram>>();

    Annotation question = null;

    while (NGramit.hasNext()) {
      NGram nGram = (NGram) NGramit.next();
      Annotation annotation = nGram.getOrigion();
      if (NGramAnnotationMap.get(annotation) == null) {
        NGramAnnotationMap.put(annotation, new ArrayList<NGram>());
      }
      if (question == null && annotation.getClass() == Question.class) {
        question = annotation;
      }
      NGramAnnotationMap.get(annotation).add(nGram);
    }

    System.out.println(question);
    ArrayList<NGram> questionNGramArrayList = NGramAnnotationMap.get(question);

    Set<Annotation> annotationKeyList = NGramAnnotationMap.keySet();
    annotationKeyList.remove(question);

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
      annotation.setScore(matchedNGramScore);
      // annotation.setScore(matchedNGramScore / totalNGramScore);
      annotation.addToIndexes();
    }
  }

}
