/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.analysis;

import prerna.sablecc.node.*;

public interface Analysis extends Switch
{
    Object getIn(Node node);
    void setIn(Node node, Object o);
    Object getOut(Node node);
    void setOut(Node node, Object o);

    void caseStart(Start node);
    void caseAConfiguration(AConfiguration node);
    void caseAColopScript(AColopScript node);
    void caseAVaropScript(AVaropScript node);
    void caseAJOpScript(AJOpScript node);
    void caseAExprScript(AExprScript node);
    void caseAScript(AScript node);
    void caseAAddColumnColop(AAddColumnColop node);
    void caseARemcolColop(ARemcolColop node);
    void caseASetcolColop(ASetcolColop node);
    void caseAPivotcolColop(APivotcolColop node);
    void caseAFiltercolColop(AFiltercolColop node);
    void caseAFocuscolColop(AFocuscolColop node);
    void caseAUnfocusColop(AUnfocusColop node);
    void caseAImportColop(AImportColop node);
    void caseAAliasColop(AAliasColop node);
    void caseADataImportColop(ADataImportColop node);
    void caseAAddColumn(AAddColumn node);
    void caseARemColumn(ARemColumn node);
    void caseASetColumn(ASetColumn node);
    void caseAPivotColumn(APivotColumn node);
    void caseAFilterColumn(AFilterColumn node);
    void caseAFocusColumn(AFocusColumn node);
    void caseAUnfocus(AUnfocus node);
    void caseAImportColumn(AImportColumn node);
    void caseAAliasColumn(AAliasColumn node);
    void caseADataImport(ADataImport node);
    void caseADecimal(ADecimal node);
    void caseAExprGroup(AExprGroup node);
    void caseAApiBlock(AApiBlock node);
    void caseASelector(ASelector node);
    void caseAColWhere(AColWhere node);
    void caseAColDefColDefOrCsvRow(AColDefColDefOrCsvRow node);
    void caseACsvColDefOrCsvRow(ACsvColDefOrCsvRow node);
    void caseAColWhereGroup(AColWhereGroup node);
    void caseAWhereClause(AWhereClause node);
    void caseARelationDef(ARelationDef node);
    void caseARelationGroup(ARelationGroup node);
    void caseARelationClause(ARelationClause node);
    void caseAIfBlock(AIfBlock node);
    void caseAColGroup(AColGroup node);
    void caseAGroupBy(AGroupBy node);
    void caseAColDef(AColDef node);
    void caseATableDef(ATableDef node);
    void caseAVarDef(AVarDef node);
    void caseAVarop(AVarop node);
    void caseACsvRow(ACsvRow node);
    void caseAEasyRow(AEasyRow node);
    void caseAEasyGroup(AEasyGroup node);
    void caseACsvTable(ACsvTable node);
    void caseAColCsv(AColCsv node);
    void caseANumWordOrNum(ANumWordOrNum node);
    void caseAAlphaWordOrNum(AAlphaWordOrNum node);
    void caseAExprWordOrNum(AExprWordOrNum node);
    void caseAWord(AWord node);
    void caseAIdWordOrBlank(AIdWordOrBlank node);
    void caseABlankWordOrBlank(ABlankWordOrBlank node);
    void caseAFormula(AFormula node);
    void caseACsvGroup(ACsvGroup node);
    void caseAExprRow(AExprRow node);
    void caseAJOp(AJOp node);
    void caseAFactorExpr(AFactorExpr node);
    void caseAPlusExpr(APlusExpr node);
    void caseAMinusExpr(AMinusExpr node);
    void caseAMathFunExpr(AMathFunExpr node);
    void caseAEExprExpr(AEExprExpr node);
    void caseAMathFun(AMathFun node);
    void caseAExtendedExpr(AExtendedExpr node);
    void caseATermFactor(ATermFactor node);
    void caseAMultFactor(AMultFactor node);
    void caseADivFactor(ADivFactor node);
    void caseAModFactor(AModFactor node);
    void caseANumberTerm(ANumberTerm node);
    void caseAExprTerm(AExprTerm node);
    void caseAVarTerm(AVarTerm node);
    void caseAColTerm(AColTerm node);
    void caseAApiTerm(AApiTerm node);
    void caseATabTerm(ATabTerm node);
    void caseAWcsvTerm(AWcsvTerm node);
    void caseATerm(ATerm node);
    void caseAAlphaTerm(AAlphaTerm node);

    void caseTNumber(TNumber node);
    void caseTId(TId node);
    void caseTDot(TDot node);
    void caseTSemicolon(TSemicolon node);
    void caseTPlus(TPlus node);
    void caseTMinus(TMinus node);
    void caseTMult(TMult node);
    void caseTComma(TComma node);
    void caseTQuote(TQuote node);
    void caseTDiv(TDiv node);
    void caseTCol(TCol node);
    void caseTComparator(TComparator node);
    void caseTColadd(TColadd node);
    void caseTApi(TApi node);
    void caseTMath(TMath node);
    void caseTColjoin(TColjoin node);
    void caseTColprefix(TColprefix node);
    void caseTTablePrefix(TTablePrefix node);
    void caseTValprefix(TValprefix node);
    void caseTColremove(TColremove node);
    void caseTColfilter(TColfilter node);
    void caseTColimport(TColimport node);
    void caseTColset(TColset node);
    void caseTColpivot(TColpivot node);
    void caseTColfocus(TColfocus node);
    void caseTColalias(TColalias node);
    void caseTCollink(TCollink node);
    void caseTShowHide(TShowHide node);
    void caseTMod(TMod node);
    void caseTLPar(TLPar node);
    void caseTRPar(TRPar node);
    void caseTLBracket(TLBracket node);
    void caseTRBracket(TRBracket node);
    void caseTGroup(TGroup node);
    void caseTBlank(TBlank node);
    void caseTSpace(TSpace node);
    void caseTEqual(TEqual node);
    void caseTNewline(TNewline node);
    void caseTCodeblock(TCodeblock node);
    void caseTJava(TJava node);
    void caseTProc(TProc node);
    void caseTThis(TThis node);
    void caseTNull(TNull node);
    void caseTImportType(TImportType node);
    void caseTRelType(TRelType node);
    void caseTDataimporttoken(TDataimporttoken node);
    void caseTLiteral(TLiteral node);
    void caseEOF(EOF node);
    void caseInvalidToken(InvalidToken node);
}
