

/* First created by JCasGen Wed Sep 11 13:44:28 EDT 2013 */
package edu.cmu.deiis.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Fri Sep 20 21:29:04 EDT 2013
 * XML source: D:/eclipseWorkspace/hw2-mengday/src/main/resources/descriptors/deiis_types.xml
 * @generated */
public class Token extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Token.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Token(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: Origion

  /** getter for Origion - gets 
   * @generated */
  public Annotation getOrigion() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_Origion == null)
      jcasType.jcas.throwFeatMissing("Origion", "edu.cmu.deiis.types.Token");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_Origion)));}
    
  /** setter for Origion - sets  
   * @generated */
  public void setOrigion(Annotation v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_Origion == null)
      jcasType.jcas.throwFeatMissing("Origion", "edu.cmu.deiis.types.Token");
    jcasType.ll_cas.ll_setRefValue(addr, ((Token_Type)jcasType).casFeatCode_Origion, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    