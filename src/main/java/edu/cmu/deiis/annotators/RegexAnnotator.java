package edu.cmu.deiis.annotators;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

public class RegexAnnotator extends JCasAnnotator_ImplBase {

	public void initialize(UimaContext aContext) {
		String[] patternStrings = (String[]) aContext
				.getConfigParameterValue("Patterns");
		String test = (String) aContext.getConfigParameterValue("name");

		for (String s : patternStrings) {
			System.out.println(s);
		}
		System.out.println(test);
	}

	@Override
	public void process(JCas aJCas) {
		// TODO Auto-generated method stub
		
	}

}
