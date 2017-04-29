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
    void caseAConfiguration(AConfiguration node);
    void caseAOutputRoutine(AOutputRoutine node);
    void caseAAssignRoutine(AAssignRoutine node);
    void caseAScriptchain(AScriptchain node);
    void caseAOtherscript(AOtherscript node);
    void caseAAssignment(AAssignment node);
    void caseAExpressionScript(AExpressionScript node);
    void caseAEmbeddedAssignmentScript(AEmbeddedAssignmentScript node);
    void caseAEmbeddedAssignment(AEmbeddedAssignment node);
    void caseATermExpr(ATermExpr node);
    void caseAPlusExpr(APlusExpr node);
    void caseAMinusExpr(AMinusExpr node);
    void caseAMultExpr(AMultExpr node);
    void caseADivExpr(ADivExpr node);
    void caseAModExpr(AModExpr node);
    void caseAPowExpr(APowExpr node);
    void caseAScalarTerm(AScalarTerm node);
    void caseAFormulaTerm(AFormulaTerm node);
    void caseAOpformulaTerm(AOpformulaTerm node);
    void caseAFrameopTerm(AFrameopTerm node);
    void caseAJavaOpTerm(AJavaOpTerm node);
    void caseAROpTerm(AROpTerm node);
    void caseAListTerm(AListTerm node);
    void caseACsvTerm(ACsvTerm node);
    void caseAFormula(AFormula node);
    void caseAList(AList node);
    void caseAOtherExpr(AOtherExpr node);
    void caseAExprColDef(AExprColDef node);
    void caseARefColDef(ARefColDef node);
    void caseADotcolColDef(ADotcolColDef node);
    void caseAFilterColDef(AFilterColDef node);
    void caseAPropColDef(APropColDef node);
    void caseARelationColDef(ARelationColDef node);
    void caseAOthercol(AOthercol node);
    void caseAGenRow(AGenRow node);
    void caseAPlainRow(APlainRow node);
    void caseAOperationFormula(AOperationFormula node);
    void caseARcol(ARcol node);
    void caseADotcol(ADotcol node);
    void caseAMinusPosOrNeg(AMinusPosOrNeg node);
    void caseAPlusPosOrNeg(APlusPosOrNeg node);
    void caseAWholeDecimalDecimal(AWholeDecimalDecimal node);
    void caseAFractionDecimalDecimal(AFractionDecimalDecimal node);
    void caseAWholeDecimal(AWholeDecimal node);
    void caseAFractionDecimal(AFractionDecimal node);
    void caseAWordWordOrId(AWordWordOrId node);
    void caseAIdWordOrId(AIdWordOrId node);
    void caseANumScalar(ANumScalar node);
    void caseAWordOrIdScalar(AWordOrIdScalar node);
    void caseABooleanScalar(ABooleanScalar node);
    void caseAProp(AProp node);
    void caseASelectors(ASelectors node);
    void caseAProjectors(AProjectors node);
    void caseALabels(ALabels node);
    void caseAProps(AProps node);
    void caseATooltips(ATooltips node);
    void caseAJoins(AJoins node);
    void caseAGeneric(AGeneric node);
    void caseASelectNoun(ASelectNoun node);
    void caseAProjectNoun(AProjectNoun node);
    void caseALabelsNoun(ALabelsNoun node);
    void caseATooltipsNoun(ATooltipsNoun node);
    void caseAOthersNoun(AOthersNoun node);
    void caseAPropsNoun(APropsNoun node);
    void caseACodeNoun(ACodeNoun node);
    void caseAOthernoun(AOthernoun node);
    void caseAFrameop(AFrameop node);
    void caseAAsop(AAsop node);
    void caseAFilter(AFilter node);
    void caseARelationship(ARelationship node);
    void caseAJavaOp(AJavaOp node);
    void caseAROp(AROp node);

    void caseTNumber(TNumber node);
    void caseTBoolean(TBoolean node);
    void caseTSort(TSort node);
    void caseTId(TId node);
    void caseTDot(TDot node);
    void caseTCodeAlpha(TCodeAlpha node);
    void caseTSemicolon(TSemicolon node);
    void caseTColon(TColon node);
    void caseTPlus(TPlus node);
    void caseTMinus(TMinus node);
    void caseTMod(TMod node);
    void caseTPow(TPow node);
    void caseTQuote(TQuote node);
    void caseTWord(TWord node);
    void caseTMult(TMult node);
    void caseTComma(TComma node);
    void caseTDiv(TDiv node);
    void caseTComparator(TComparator node);
    void caseTEqual(TEqual node);
    void caseTSelectorid(TSelectorid node);
    void caseTOptionid(TOptionid node);
    void caseTProjectid(TProjectid node);
    void caseTPropid(TPropid node);
    void caseTLabelid(TLabelid node);
    void caseTJoinid(TJoinid node);
    void caseTTooltipid(TTooltipid node);
    void caseTFrameid(TFrameid node);
    void caseTLPar(TLPar node);
    void caseTRPar(TRPar node);
    void caseTLBrac(TLBrac node);
    void caseTRBrac(TRBrac node);
    void caseTFrameprefix(TFrameprefix node);
    void caseTBlank(TBlank node);
    void caseTOutput(TOutput node);
    void caseTJava(TJava node);
    void caseTR(TR node);
    void caseTIf(TIf node);
    void caseTAsOp(TAsOp node);
    void caseTCustom(TCustom node);
    void caseTRelType(TRelType node);
    void caseEOF(EOF node);
    void caseInvalidToken(InvalidToken node);
}
