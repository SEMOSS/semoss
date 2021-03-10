/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.analysis;

import prerna.sablecc2.node.*;

public interface Analysis extends Switch
{
    Object getIn(Node node);
    void setIn(Node node, Object o);
    Object getOut(Node node);
    void setOut(Node node, Object o);

    void caseStart(Start node);
    void caseARoutineConfiguration(ARoutineConfiguration node);
    void caseAEmptyConfiguration(AEmptyConfiguration node);
    void caseAOutputRoutine(AOutputRoutine node);
    void caseAAssignRoutine(AAssignRoutine node);
    void caseAMetaRoutine(AMetaRoutine node);
    void caseAMainCommentRoutine(AMainCommentRoutine node);
    void caseAEmptyroutine(AEmptyroutine node);
    void caseAScript(AScript node);
    void caseAOtherscript(AOtherscript node);
    void caseAAssignment(AAssignment node);
    void caseAMetaScriptMetaRoutine(AMetaScriptMetaRoutine node);
    void caseAMetaAssignmentMetaRoutine(AMetaAssignmentMetaRoutine node);
    void caseAMandatoryScriptchain(AMandatoryScriptchain node);
    void caseABaseSubExpr(ABaseSubExpr node);
    void caseABaseSubScript(ABaseSubScript node);
    void caseABaseAssignment(ABaseAssignment node);
    void caseASimpleSubRoutineOptions(ASimpleSubRoutineOptions node);
    void caseAChainSubRoutineOptions(AChainSubRoutineOptions node);
    void caseAAssignmentSubRoutineOptions(AAssignmentSubRoutineOptions node);
    void caseASubRoutine(ASubRoutine node);
    void caseANormalMasterExpr(ANormalMasterExpr node);
    void caseAComparisonMasterExpr(AComparisonMasterExpr node);
    void caseABaseExprExpr(ABaseExprExpr node);
    void caseAEmbeddedRoutineExpr(AEmbeddedRoutineExpr node);
    void caseAEmbeddedAssignmentExpr(AEmbeddedAssignmentExpr node);
    void caseAHelpExpr(AHelpExpr node);
    void caseACommentExpr(ACommentExpr node);
    void caseAExprComponentBaseExpr(AExprComponentBaseExpr node);
    void caseAPlusBaseExpr(APlusBaseExpr node);
    void caseAMinusBaseExpr(AMinusBaseExpr node);
    void caseAMultBaseExpr(AMultBaseExpr node);
    void caseADivBaseExpr(ADivBaseExpr node);
    void caseAModBaseExpr(AModBaseExpr node);
    void caseATermExprComponent(ATermExprComponent node);
    void caseAPowerExprComponent(APowerExprComponent node);
    void caseAEmbeddedScriptchainExprComponent(AEmbeddedScriptchainExprComponent node);
    void caseAPower(APower node);
    void caseARegTermTerm(ARegTermTerm node);
    void caseANegTermTerm(ANegTermTerm node);
    void caseAPosTermTerm(APosTermTerm node);
    void caseANegTerm(ANegTerm node);
    void caseAPosTerm(APosTerm node);
    void caseAScalarRegTerm(AScalarRegTerm node);
    void caseAMapRegTerm(AMapRegTerm node);
    void caseAFormulaRegTerm(AFormulaRegTerm node);
    void caseAOperationRegTerm(AOperationRegTerm node);
    void caseARefRegTerm(ARefRegTerm node);
    void caseADotcolRegTerm(ADotcolRegTerm node);
    void caseAJavaOpRegTerm(AJavaOpRegTerm node);
    void caseAListRegTerm(AListRegTerm node);
    void caseACsvRegTerm(ACsvRegTerm node);
    void caseAFormula(AFormula node);
    void caseAEmptyList(AEmptyList node);
    void caseAFilledList(AFilledList node);
    void caseANoValuesList(ANoValuesList node);
    void caseAValuesList(AValuesList node);
    void caseAOtherExpr(AOtherExpr node);
    void caseAOperation(AOperation node);
    void caseANounOpInput(ANounOpInput node);
    void caseAInputOpInput(AInputOpInput node);
    void caseAOtherOpInput(AOtherOpInput node);
    void caseAGenRow(AGenRow node);
    void caseAOthercol(AOthercol node);
    void caseAExprColDef(AExprColDef node);
    void caseAPropColDef(APropColDef node);
    void caseARelationColDef(ARelationColDef node);
    void caseANoun(ANoun node);
    void caseAProp(AProp node);
    void caseAAsop(AAsop node);
    void caseAExplicitRelationship(AExplicitRelationship node);
    void caseAExplicitComparatorRelationship(AExplicitComparatorRelationship node);
    void caseAImplicitRelationship(AImplicitRelationship node);
    void caseAImplicitRel(AImplicitRel node);
    void caseAExplicitRelComparator(AExplicitRelComparator node);
    void caseAExplicitRel(AExplicitRel node);
    void caseATermComparisonExpr(ATermComparisonExpr node);
    void caseAComparisonGroupComparisonExpr(AComparisonGroupComparisonExpr node);
    void caseAComplexOrComparisonExpr(AComplexOrComparisonExpr node);
    void caseAComplexAndComparisonExpr(AComplexAndComparisonExpr node);
    void caseABasicComparisonTerm(ABasicComparisonTerm node);
    void caseABasicAndComparisonTerm(ABasicAndComparisonTerm node);
    void caseABasicOrComparisonTerm(ABasicOrComparisonTerm node);
    void caseAComparisonGroup(AComparisonGroup node);
    void caseASimpleCaseAndComparison(ASimpleCaseAndComparison node);
    void caseALeftComplexAndComparison(ALeftComplexAndComparison node);
    void caseARightComplexAndComparison(ARightComplexAndComparison node);
    void caseABothComplexAndComparison(ABothComplexAndComparison node);
    void caseARepeatingAndComparison(ARepeatingAndComparison node);
    void caseASimpleCaseOrComparison(ASimpleCaseOrComparison node);
    void caseALeftComplexOrComparison(ALeftComplexOrComparison node);
    void caseARightComplexOrComparison(ARightComplexOrComparison node);
    void caseABothComplexOrComparison(ABothComplexOrComparison node);
    void caseARepeatingOrComparison(ARepeatingOrComparison node);
    void caseABaseSimpleComparison(ABaseSimpleComparison node);
    void caseAJavaOp(AJavaOp node);
    void caseARcol(ARcol node);
    void caseADotcol(ADotcol node);
    void caseAMap(AMap node);
    void caseAMapEntry(AMapEntry node);
    void caseAOtherMapEntry(AOtherMapEntry node);
    void caseASimpleValues(ASimpleValues node);
    void caseAListValues(AListValues node);
    void caseANestedMapValues(ANestedMapValues node);
    void caseAMapList(AMapList node);
    void caseAMapListExtend(AMapListExtend node);
    void caseAWordMapKey(AWordMapKey node);
    void caseAVarMapKey(AVarMapKey node);
    void caseAListMapExtendedInput(AListMapExtendedInput node);
    void caseAScalarMapExtendedInput(AScalarMapExtendedInput node);
    void caseANestedMapMapExtendedInput(ANestedMapMapExtendedInput node);
    void caseAMapVarMapBaseInput(AMapVarMapBaseInput node);
    void caseANormalScalarMapBaseInput(ANormalScalarMapBaseInput node);
    void caseAMapNegNumMapBaseInput(AMapNegNumMapBaseInput node);
    void caseAMapVar(AMapVar node);
    void caseAMapNegNum(AMapNegNum node);
    void caseANumScalar(ANumScalar node);
    void caseABooleanScalar(ABooleanScalar node);
    void caseANullScalar(ANullScalar node);
    void caseAWordOrIdScalar(AWordOrIdScalar node);
    void caseAWordWordOrId(AWordWordOrId node);
    void caseAIdWordOrId(AIdWordOrId node);
    void caseAWholeDecimalDecimal(AWholeDecimalDecimal node);
    void caseAFractionDecimalDecimal(AFractionDecimalDecimal node);
    void caseAWholeDecimal(AWholeDecimal node);
    void caseAFractionDecimal(AFractionDecimal node);

    void caseTNull(TNull node);
    void caseTNumber(TNumber node);
    void caseTBoolean(TBoolean node);
    void caseTMeta(TMeta node);
    void caseTId(TId node);
    void caseTDot(TDot node);
    void caseTSemicolon(TSemicolon node);
    void caseTColon(TColon node);
    void caseTPlus(TPlus node);
    void caseTMinus(TMinus node);
    void caseTMod(TMod node);
    void caseTPow(TPow node);
    void caseTWord(TWord node);
    void caseTComment(TComment node);
    void caseTMult(TMult node);
    void caseTComma(TComma node);
    void caseTDiv(TDiv node);
    void caseTComparator(TComparator node);
    void caseTAndComparator(TAndComparator node);
    void caseTOrComparator(TOrComparator node);
    void caseTEqual(TEqual node);
    void caseTLPar(TLPar node);
    void caseTRPar(TRPar node);
    void caseTLBrac(TLBrac node);
    void caseTRBrac(TRBrac node);
    void caseTLCurl(TLCurl node);
    void caseTRCurl(TRCurl node);
    void caseTFrameprefix(TFrameprefix node);
    void caseTFrameid(TFrameid node);
    void caseTBlank(TBlank node);
    void caseTJava(TJava node);
    void caseTIf(TIf node);
    void caseTAsOp(TAsOp node);
    void caseTCustom(TCustom node);
    void caseTRelType(TRelType node);
    void caseTHelpToken(THelpToken node);
    void caseEOF(EOF node);
    void caseInvalidToken(InvalidToken node);
}
