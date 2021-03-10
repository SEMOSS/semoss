/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.analysis;

import java.util.*;
import prerna.sablecc2.node.*;

public class AnalysisAdapter implements Analysis
{
    private Hashtable<Node,Object> in;
    private Hashtable<Node,Object> out;

    @Override
    public Object getIn(Node node)
    {
        if(this.in == null)
        {
            return null;
        }

        return this.in.get(node);
    }

    @Override
    public void setIn(Node node, Object o)
    {
        if(this.in == null)
        {
            this.in = new Hashtable<Node,Object>(1);
        }

        if(o != null)
        {
            this.in.put(node, o);
        }
        else
        {
            this.in.remove(node);
        }
    }

    @Override
    public Object getOut(Node node)
    {
        if(this.out == null)
        {
            return null;
        }

        return this.out.get(node);
    }

    @Override
    public void setOut(Node node, Object o)
    {
        if(this.out == null)
        {
            this.out = new Hashtable<Node,Object>(1);
        }

        if(o != null)
        {
            this.out.put(node, o);
        }
        else
        {
            this.out.remove(node);
        }
    }

    @Override
    public void caseStart(Start node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARoutineConfiguration(ARoutineConfiguration node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEmptyConfiguration(AEmptyConfiguration node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAOutputRoutine(AOutputRoutine node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAssignRoutine(AAssignRoutine node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMetaRoutine(AMetaRoutine node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMainCommentRoutine(AMainCommentRoutine node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEmptyroutine(AEmptyroutine node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAScript(AScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAOtherscript(AOtherscript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAssignment(AAssignment node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMetaScriptMetaRoutine(AMetaScriptMetaRoutine node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMetaAssignmentMetaRoutine(AMetaAssignmentMetaRoutine node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMandatoryScriptchain(AMandatoryScriptchain node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABaseSubExpr(ABaseSubExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABaseSubScript(ABaseSubScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABaseAssignment(ABaseAssignment node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASimpleSubRoutineOptions(ASimpleSubRoutineOptions node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAChainSubRoutineOptions(AChainSubRoutineOptions node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAssignmentSubRoutineOptions(AAssignmentSubRoutineOptions node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASubRoutine(ASubRoutine node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANormalMasterExpr(ANormalMasterExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAComparisonMasterExpr(AComparisonMasterExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABaseExprExpr(ABaseExprExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEmbeddedRoutineExpr(AEmbeddedRoutineExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEmbeddedAssignmentExpr(AEmbeddedAssignmentExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAHelpExpr(AHelpExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseACommentExpr(ACommentExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExprComponentBaseExpr(AExprComponentBaseExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPlusBaseExpr(APlusBaseExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMinusBaseExpr(AMinusBaseExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMultBaseExpr(AMultBaseExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseADivBaseExpr(ADivBaseExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAModBaseExpr(AModBaseExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATermExprComponent(ATermExprComponent node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPowerExprComponent(APowerExprComponent node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEmbeddedScriptchainExprComponent(AEmbeddedScriptchainExprComponent node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPower(APower node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARegTermTerm(ARegTermTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANegTermTerm(ANegTermTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPosTermTerm(APosTermTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANegTerm(ANegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPosTerm(APosTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAScalarRegTerm(AScalarRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapRegTerm(AMapRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFormulaRegTerm(AFormulaRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAOperationRegTerm(AOperationRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARefRegTerm(ARefRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseADotcolRegTerm(ADotcolRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAJavaOpRegTerm(AJavaOpRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAListRegTerm(AListRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseACsvRegTerm(ACsvRegTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFormula(AFormula node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEmptyList(AEmptyList node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFilledList(AFilledList node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANoValuesList(ANoValuesList node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAValuesList(AValuesList node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAOtherExpr(AOtherExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAOperation(AOperation node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANounOpInput(ANounOpInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAInputOpInput(AInputOpInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAOtherOpInput(AOtherOpInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAGenRow(AGenRow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAOthercol(AOthercol node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExprColDef(AExprColDef node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPropColDef(APropColDef node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARelationColDef(ARelationColDef node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANoun(ANoun node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAProp(AProp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAsop(AAsop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExplicitRelationship(AExplicitRelationship node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExplicitComparatorRelationship(AExplicitComparatorRelationship node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAImplicitRelationship(AImplicitRelationship node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAImplicitRel(AImplicitRel node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExplicitRelComparator(AExplicitRelComparator node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExplicitRel(AExplicitRel node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATermComparisonExpr(ATermComparisonExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAComparisonGroupComparisonExpr(AComparisonGroupComparisonExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAComplexOrComparisonExpr(AComplexOrComparisonExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAComplexAndComparisonExpr(AComplexAndComparisonExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABasicComparisonTerm(ABasicComparisonTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABasicAndComparisonTerm(ABasicAndComparisonTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABasicOrComparisonTerm(ABasicOrComparisonTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAComparisonGroup(AComparisonGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASimpleCaseAndComparison(ASimpleCaseAndComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseALeftComplexAndComparison(ALeftComplexAndComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARightComplexAndComparison(ARightComplexAndComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABothComplexAndComparison(ABothComplexAndComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARepeatingAndComparison(ARepeatingAndComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASimpleCaseOrComparison(ASimpleCaseOrComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseALeftComplexOrComparison(ALeftComplexOrComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARightComplexOrComparison(ARightComplexOrComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABothComplexOrComparison(ABothComplexOrComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARepeatingOrComparison(ARepeatingOrComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABaseSimpleComparison(ABaseSimpleComparison node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAJavaOp(AJavaOp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARcol(ARcol node)
    {
        defaultCase(node);
    }

    @Override
    public void caseADotcol(ADotcol node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMap(AMap node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapEntry(AMapEntry node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAOtherMapEntry(AOtherMapEntry node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASimpleValues(ASimpleValues node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAListValues(AListValues node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANestedMapValues(ANestedMapValues node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapList(AMapList node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapListExtend(AMapListExtend node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWordMapKey(AWordMapKey node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVarMapKey(AVarMapKey node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAListMapExtendedInput(AListMapExtendedInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAScalarMapExtendedInput(AScalarMapExtendedInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANestedMapMapExtendedInput(ANestedMapMapExtendedInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapVarMapBaseInput(AMapVarMapBaseInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANormalScalarMapBaseInput(ANormalScalarMapBaseInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapNegNumMapBaseInput(AMapNegNumMapBaseInput node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapVar(AMapVar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapNegNum(AMapNegNum node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANumScalar(ANumScalar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABooleanScalar(ABooleanScalar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANullScalar(ANullScalar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWordOrIdScalar(AWordOrIdScalar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWordWordOrId(AWordWordOrId node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAIdWordOrId(AIdWordOrId node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWholeDecimalDecimal(AWholeDecimalDecimal node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFractionDecimalDecimal(AFractionDecimalDecimal node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWholeDecimal(AWholeDecimal node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFractionDecimal(AFractionDecimal node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNull(TNull node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNumber(TNumber node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBoolean(TBoolean node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMeta(TMeta node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTId(TId node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDot(TDot node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTSemicolon(TSemicolon node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColon(TColon node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPlus(TPlus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMinus(TMinus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMod(TMod node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPow(TPow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTWord(TWord node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTComment(TComment node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMult(TMult node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTComma(TComma node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDiv(TDiv node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTComparator(TComparator node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAndComparator(TAndComparator node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTOrComparator(TOrComparator node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEqual(TEqual node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLPar(TLPar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRPar(TRPar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLBrac(TLBrac node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRBrac(TRBrac node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLCurl(TLCurl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRCurl(TRCurl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFrameprefix(TFrameprefix node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFrameid(TFrameid node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBlank(TBlank node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTJava(TJava node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIf(TIf node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAsOp(TAsOp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCustom(TCustom node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRelType(TRelType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTHelpToken(THelpToken node)
    {
        defaultCase(node);
    }

    @Override
    public void caseEOF(EOF node)
    {
        defaultCase(node);
    }

    @Override
    public void caseInvalidToken(InvalidToken node)
    {
        defaultCase(node);
    }

    public void defaultCase(@SuppressWarnings("unused") Node node)
    {
        // do nothing
    }
}
