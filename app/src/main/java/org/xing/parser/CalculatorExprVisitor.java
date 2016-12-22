package org.xing.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.xing.parser.grammer.calculatorBaseVisitor;
import org.xing.parser.grammer.calculatorParser;

public class CalculatorExprVisitor extends calculatorBaseVisitor<String>{
	private NumberParser numParser;
	public CalculatorExprVisitor() {
		numParser = new NumberParser();
	}
	
	@Override
	public String visitExpression(calculatorParser.ExpressionContext ctx) {
		StringBuilder result = new StringBuilder();
		for(int i=0;i<ctx.getChildCount();i++) {
			ParseTree child = ctx.getChild(i);
			
			if(child instanceof TerminalNode) {
				int type = ((TerminalNode) child).getSymbol().getType();
				if(type == calculatorParser.PLUS) {
					result.append("+");
				} else {
					result.append("-");
				}
			}else {
				result.append(visit(child));
			}
		}
		return result.toString();
	}

	@Override
	public String visitMultiplyingExpression(
			calculatorParser.MultiplyingExpressionContext ctx) {
		StringBuilder result = new StringBuilder();
		for(int i=0;i<ctx.getChildCount();i++) {
			ParseTree child = ctx.getChild(i);
			
			if(child instanceof TerminalNode) {
				int type = ((TerminalNode) child).getSymbol().getType();
				if(type == calculatorParser.DIV) {
					result.append("÷");
				} else {
					result.append("×");
				}
			}else {
				result.append(visit(child));
			}
		}
		return result.toString();
	}

	@Override
	public String visitPowExpression(calculatorParser.PowExpressionContext ctx) {
		StringBuilder result = new StringBuilder();
		result.append(visit(ctx.getChild(0)));
		
		if(ctx.getChildCount() > 1) {
			for(int i=2;i<ctx.getChildCount();i+=2) {
				result.append("^");
				result.append(visit(ctx.getChild(i)));
			}
		}
		
		return result.toString();
	}
	
	@Override 
	public String visitChinaPowExpression(
			calculatorParser.ChinaPowExpressionContext ctx) { 
		StringBuilder result = new StringBuilder();
		if(ctx.getChild(0) instanceof TerminalNode) {
			int type = ((TerminalNode)ctx.getChild(0)).getSymbol().getType();
			if(type == calculatorParser.GENHAO) {
				result.append(visit(ctx.getChild(1))+ "^0.5");
			} else {
				System.err.println("未处理的终端节点:visitChinaPowExpression");
			}
		}else {
			int index = 0;
			while(index < ctx.getChildCount()) {
				result.append(visit(ctx.getChild(index)));
				if(ctx.getChild(index+2) instanceof TerminalNode) {
					int type = ((TerminalNode)ctx.getChild(2)).getSymbol().getType();
					if(type == calculatorParser.PINGFANG) {
						result.append("^2");
					}else if(type == calculatorParser.LIFANG){
						result.append("^3");
					}else if(type == calculatorParser.KAIFANG){
						result.append("^0.5");
					}else {
						System.err.println("未处理的终端节点:visitChinaPowExpression");
					}
					index += 3;
				}else {
					String pow = visit(ctx.getChild(index+2));
					result.append("^"+pow);
					index += 4; 
				}
			}
		}
		return result.toString();
	}

	@Override
	public String visitAtom(calculatorParser.AtomContext ctx) {
		if(ctx.getChildCount() == 3) {
			if(ctx.FRAC() != null) {
				return "("+visit(ctx.getChild(2)) + "/" + visit(ctx.getChild(0))+")";
			}else {
				return "("+visit(ctx.expression())+")";
			}
		} else {
			return visit(ctx.getChild(0));
		}
	}

	@Override
	public String visitFunc(calculatorParser.FuncContext ctx) {
		String funcname = ctx.funcname().getText();
		String expr = visit(ctx.getChild(1));
		
		return funcname+"("+expr+")";
	}

	@Override
	public String visitFuncname(calculatorParser.FuncnameContext ctx) {
		return null;
	}

	@Override
	public String visitNumber(calculatorParser.NumberContext ctx) {
		try{
			numParser.parse(ctx.getText());
			return numParser.getReadExpr();
		}catch(Exception ex) { 
			return null;
		}
	}
}
