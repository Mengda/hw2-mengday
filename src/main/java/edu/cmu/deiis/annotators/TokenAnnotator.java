package edu.cmu.deiis.annotators;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.*;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.deiis.types.*;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;

public class TokenAnnotator extends JCasAnnotator_ImplBase {

  private String[] baseAnnotatorList = null;

  private String thisProcessorClassName = null;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    baseAnnotatorList = (String[]) aContext.getConfigParameterValue("Annotations");
    thisProcessorClassName = this.getClass().getName();
  }

  private TokenizerFactory<Word> factory = PTBTokenizerFactory.newTokenizerFactory();

  private void AddAnnotation(JCas aJCas, String annotationClassString) throws Exception {
    Class<Annotation> annotationClass = (Class<Annotation>) Class.forName(annotationClassString);
    
    Field f = annotationClass.getDeclaredField("type");
    
    int typeIndexID =  (Integer) f.get(null);

    FSIndex index = aJCas.getAnnotationIndex(typeIndexID);
    FSIterator it = index.iterator();

    while (it.hasNext()) {
      Annotation baseAnnotator = (Annotation) it.next();
      String baseAnnotatorString = baseAnnotator.getCoveredText();
      int baseAnnotatorBegin = baseAnnotator.getBegin();
      Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(baseAnnotatorString));
      for (Word word : tokenizer.tokenize()) {
        int wordBegin = baseAnnotatorBegin + word.beginPosition();
        int wordEnd = baseAnnotatorBegin + word.endPosition();
        
        Token annotator = new Token(aJCas);
        annotator.addToIndexes();
        annotator.setBegin(wordBegin);
        annotator.setEnd(wordEnd);
        annotator.setCasProcessorId(thisProcessorClassName);
        annotator.setConfidence(1);
      }
    }

  }

  @Override
  public void process(JCas aJCas) {

    for (String s : baseAnnotatorList) {
      try {
        AddAnnotation(aJCas, s);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    FSIterator it = aJCas.getAnnotationIndex(Token.type).iterator();
    while(it.hasNext()){
    	Token token = (Token) it.next();
    	System.out.println(token.getCoveredText());
    }

  }
}
