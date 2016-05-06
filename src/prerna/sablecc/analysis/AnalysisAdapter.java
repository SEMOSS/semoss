/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.analysis;

import java.util.*;
import prerna.sablecc.node.*;

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
    public void caseAConfiguration(AConfiguration node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAColopScript(AColopScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVaropScript(AVaropScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAROpScript(AROpScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExprScript(AExprScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAHelpScript(AHelpScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPanelopScript(APanelopScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAScript(AScript node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAddColumnColop(AAddColumnColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARemcolColop(ARemcolColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASetcolColop(ASetcolColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPivotcolColop(APivotcolColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFiltercolColop(AFiltercolColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFocuscolColop(AFocuscolColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAUnfocusColop(AUnfocusColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAImportColop(AImportColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAliasColop(AAliasColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAImportDataColop(AImportDataColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAUnfiltercolColop(AUnfiltercolColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARemoveDataColop(ARemoveDataColop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPanelVizPanelop(APanelVizPanelop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPanelCommentPanelop(APanelCommentPanelop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPanelClonePanelop(APanelClonePanelop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPanelViz(APanelViz node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPanelComment(APanelComment node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPanelClone(APanelClone node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPanelSetBuilder(APanelSetBuilder node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAddColumn(AAddColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARemColumn(ARemColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASetColumn(ASetColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPivotColumn(APivotColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFilterColumn(AFilterColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAUnfilterColumn(AUnfilterColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFocusColumn(AFocusColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAUnfocus(AUnfocus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAImportColumn(AImportColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAliasColumn(AAliasColumn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAImportData(AImportData node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAApiImportBlock(AApiImportBlock node)
    {
        defaultCase(node);
    }

    @Override
    public void caseACsvTableImportBlock(ACsvTableImportBlock node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPastedDataImportBlock(APastedDataImportBlock node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPastedData(APastedData node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARemoveData(ARemoveData node)
    {
        defaultCase(node);
    }

    @Override
    public void caseADecimal(ADecimal node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExprGroup(AExprGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAApiBlock(AApiBlock node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASelector(ASelector node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAColWhere(AColWhere node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAColDefColDefOrCsvRow(AColDefColDefOrCsvRow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseACsvColDefOrCsvRow(ACsvColDefOrCsvRow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAColWhereGroup(AColWhereGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWhereClause(AWhereClause node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWhereStatement(AWhereStatement node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARelationDef(ARelationDef node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARelationGroup(ARelationGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARelationClause(ARelationClause node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAIfBlock(AIfBlock node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAColGroup(AColGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAKeyvalue(AKeyvalue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAKeyvalueGroup(AKeyvalueGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMapObj(AMapObj node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAGroupBy(AGroupBy node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAColDef(AColDef node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATableDef(ATableDef node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVarDef(AVarDef node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVarop(AVarop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseACsvRow(ACsvRow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEasyRow(AEasyRow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEasyGroup(AEasyGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseACsvTable(ACsvTable node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAColCsv(AColCsv node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANumWordOrNum(ANumWordOrNum node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAlphaWordOrNum(AAlphaWordOrNum node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExprWordOrNum(AExprWordOrNum node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFlexSelectorRow(AFlexSelectorRow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATermGroup(ATermGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFormula(AFormula node)
    {
        defaultCase(node);
    }

    @Override
    public void caseACsvGroup(ACsvGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExprRow(AExprRow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAJOp(AJOp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAROp(AROp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAHelp(AHelp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATermExpr(ATermExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPlusExpr(APlusExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMinusExpr(AMinusExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMultExpr(AMultExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExpr(AExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseADivExpr(ADivExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAModExpr(AModExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEExprExpr(AEExprExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMathFun(AMathFun node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExtendedExpr(AExtendedExpr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANumberTerm(ANumberTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAExprTerm(AExprTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVarTerm(AVarTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAColTerm(AColTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAApiTerm(AApiTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATabTerm(ATabTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWcsvTerm(AWcsvTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATerm(ATerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAlphaTerm(AAlphaTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMathFunTerm(AMathFunTerm node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNumber(TNumber node)
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
    public void caseTCol(TCol node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTComparator(TComparator node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColadd(TColadd node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTApi(TApi node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMath(TMath node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColjoin(TColjoin node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColprefix(TColprefix node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTTablePrefix(TTablePrefix node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTValprefix(TValprefix node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColremove(TColremove node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColfilter(TColfilter node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColunfilter(TColunfilter node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColimport(TColimport node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColset(TColset node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColpivot(TColpivot node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColfocus(TColfocus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColalias(TColalias node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCollink(TCollink node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTShowHide(TShowHide node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMod(TMod node)
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
    public void caseTLBracket(TLBracket node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRBracket(TRBracket node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLCurlBracket(TLCurlBracket node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRCurlBracket(TRCurlBracket node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTGroup(TGroup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBlank(TBlank node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTSpace(TSpace node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEqual(TEqual node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNewline(TNewline node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTJava(TJava node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTR(TR node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPython(TPython node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTProc(TProc node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTThis(TThis node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNull(TNull node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTImportType(TImportType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRelType(TRelType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDataimporttoken(TDataimporttoken node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDataremovetoken(TDataremovetoken node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLiteral(TLiteral node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTHelpToken(THelpToken node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCodeblock(TCodeblock node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTWord(TWord node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPanelviz(TPanelviz node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPanelcomment(TPanelcomment node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPanelclone(TPanelclone node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPanelsetbuilder(TPanelsetbuilder node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFileText(TFileText node)
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
