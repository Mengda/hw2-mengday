package edu.cmu.deiis.annotators;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.deiis.types.*;

public class NGramAnnotator extends JCasAnnotator_ImplBase {

	private Integer[] N = null;

	private String[] baseAnnotators = null;

	private String thisProcessorClassName = null;

	private void processNGrams(JCas aJCas, String annotationClassString,
			Integer n) throws Exception {
		Class<Annotation> annotationClass = (Class<Annotation>) Class
				.forName(annotationClassString);
		Method mGetTokens = annotationClass.getMethod("getTokens", null);
		Field f = annotationClass.getDeclaredField("type");

		int typeIndexID = (Integer) f.get(null);

		FSIndex index = aJCas.getAnnotationIndex(typeIndexID);
		FSIterator it = index.iterator();
		while (it.hasNext()) {

			Annotation baseAnnotation = (Annotation) it.next();
			FSArray tokens = (FSArray) mGetTokens.invoke(baseAnnotation, null);

			for (int i = 0; i < tokens.size() + 1 - n; ++i) {
				NGram annotation = new NGram(aJCas);
				annotation.setCasProcessorId(thisProcessorClassName);
				annotation.setConfidence(1);
				annotation.setN(n);
				annotation.addToIndexes();

				ArrayList<Token> tokenAL = new ArrayList<Token>();
				for (int j = 0; j < n; ++j) {
					tokenAL.add((Token) tokens.get(i + j));
				}

				annotation.setBegin(tokenAL.get(0).getBegin());
				annotation.setEnd(tokenAL.get(n - 1).getEnd());
				FSArray annotationTokens = new FSArray(aJCas, n);
				annotation.setElements(annotationTokens);
				for (int j = 0; j < n; ++j) {
					annotationTokens.set(j, tokenAL.get(j));
				}

				annotation.setElementType(Token.class.getName());
				annotation.setOrigion(baseAnnotation);
			}
		}

	}

	@Override
	public void process(JCas aJCas) {
		for (int n : N) {
			for (String className : baseAnnotators) {
				try {
					processNGrams(aJCas, className, n);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		N = (Integer[]) aContext.getConfigParameterValue("N");
		baseAnnotators = (String[]) aContext
				.getConfigParameterValue("BaseAnnotations");
		thisProcessorClassName = this.getClass().getName();
	}

}
