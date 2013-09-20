package edu.cmu.deiis.annotators;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.*;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.deiis.types.*;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;

public class TokenAnnotator extends JCasAnnotator_ImplBase {

	private String[] baseAnnotations = null;

	private String thisProcessorClassName = null;

	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		baseAnnotations = (String[]) aContext
				.getConfigParameterValue("Annotations");
		thisProcessorClassName = this.getClass().getName();
	}

	private TokenizerFactory<Word> factory = PTBTokenizerFactory
			.newTokenizerFactory();

	private void AddAnnotation(JCas aJCas, String annotationClassString)
			throws Exception {
		Class<Annotation> annotationClass = (Class<Annotation>) Class
				.forName(annotationClassString);
		Method m = annotationClass.getMethod("setTokens", FSArray.class);
		Field f = annotationClass.getDeclaredField("type");

		int typeIndexID = (Integer) f.get(null);

		FSIndex index = aJCas.getAnnotationIndex(typeIndexID);
		FSIterator it = index.iterator();

		while (it.hasNext()) {
			Annotation baseAnnotation = (Annotation) it.next();
			ArrayList<Token> tokens = new ArrayList<Token>();
			String baseAnnotatorString = baseAnnotation.getCoveredText();
			int baseAnnotatorBegin = baseAnnotation.getBegin();
			Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(
					baseAnnotatorString));
			for (Word word : tokenizer.tokenize()) {
				int wordBegin = baseAnnotatorBegin + word.beginPosition();
				int wordEnd = baseAnnotatorBegin + word.endPosition();

				Token annotator = new Token(aJCas);
				annotator.addToIndexes();
				annotator.setBegin(wordBegin);
				annotator.setEnd(wordEnd);
				annotator.setCasProcessorId(thisProcessorClassName);
				annotator.setConfidence(1);
				annotator.setOrigion(baseAnnotation);
				tokens.add(annotator);
			}
			FSArray tokenFSArray = new FSArray(aJCas, tokens.size());
			for(int i = 0; i < tokens.size(); ++i){
				tokenFSArray.set(i, tokens.get(i));
			}
			m.invoke(baseAnnotation, tokenFSArray);
		}
	}

	@Override
	public void process(JCas aJCas) {

		for (String s : baseAnnotations) {
			try {
				AddAnnotation(aJCas, s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
