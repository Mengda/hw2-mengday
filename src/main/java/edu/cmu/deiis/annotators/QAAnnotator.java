package edu.cmu.deiis.annotators;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QAAnnotator extends JCasAnnotator_ImplBase {

  private Pattern questionPattern = Pattern.compile("^Q .*(\\r|\\n|\\r\\n)");

  private Pattern answerPattern = Pattern.compile("A (\\d) (.*)(\\r|\\n|\\r\\n)");

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    String text = aJCas.getDocumentText();
    String thisProcessorClassName = this.getClass().getName();

    int pos = 0;
    Matcher matcher = questionPattern.matcher(text);

    while (matcher.find(pos)) {
      Question annotator = new Question(aJCas);
      annotator.setBegin(matcher.start() + 2);
      pos = matcher.end();
      annotator.setEnd(pos);
      annotator.setCasProcessorId(thisProcessorClassName);
      annotator.setConfidence(1);
      annotator.addToIndexes();
    }

    matcher = answerPattern.matcher(text);

    while (matcher.find(pos)) {
      Answer annotator = new Answer(aJCas);
      annotator.setBegin(matcher.start() + 4);
      pos = matcher.end();
      annotator.setEnd(pos);
      annotator.setCasProcessorId(thisProcessorClassName);
      annotator.setConfidence(1);
      annotator.setIsCorrect(matcher.group(1).equals("1"));
      annotator.addToIndexes();
    }

  }

}
