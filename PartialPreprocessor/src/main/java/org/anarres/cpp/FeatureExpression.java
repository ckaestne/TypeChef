//package org.anarres.cpp;
//
//
//public abstract class FeatureExpression {
//
//	public static class And extends BinaryFeatureExpression {
//
//		public And(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"&&");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) != 0 && rhs.evaluate(macros) != 0 ? 1
//					: 0;
//		}
//
//		@Override
//		public FeatureExpression simplify() {
//			if (lhs == BASE)
//				return rhs.simplify();
//			if (rhs == BASE)
//				return lhs.simplify();
//			if (lhs == DEAD || rhs == DEAD)
//				return DEAD;
//			return super.simplify();
//		}
//	}
//
//	private static class BaseFeature extends FeatureExpression {
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return 1;
//		}
//		
//		@Override
//		public String print() {
//			return "1";
//		}
//	}
//
//	abstract static class BinaryFeatureExpression extends FeatureExpression {
//		final FeatureExpression lhs, rhs;
//		final private String opStr;
//
//		public BinaryFeatureExpression(FeatureExpression left,
//				FeatureExpression right, String opStr) {
//			this.lhs = left;
//			this.rhs = right;
//			this.opStr=opStr;
//		}
//		@Override
//		public String print() {
//			return lhs+" "+opStr+" "+rhs;
//		}
//	}
//
//	 static class BitAnd extends BinaryFeatureExpression {
//
//		public BitAnd(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"&");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) & rhs.evaluate(macros);
//		}
//	}
//
//	 static class BitOr extends BinaryFeatureExpression {
//
//		public BitOr(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"|");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) | rhs.evaluate(macros);
//		}
//	}
//
//	static class CharacterLit extends FeatureExpression {
//
//		final Character _char;
//
//		public CharacterLit(Character value) {
//			this._char = value;
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return _char.charValue();
//		}
//
//		@Override
//		public String print() {
//			return "'"+_char.toString()+"'";
//		}
//	}
//
//	static class Complement extends FeatureExpression {
//		final FeatureExpression expr;
//
//		public Complement(FeatureExpression expr) {
//			this.expr = expr;
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return ~expr.evaluate(macros);
//		}
//		
//		@Override
//		public String print() {
//			return "~"+expr;
//		}
//
//	}
//	private static class DeadFeature extends FeatureExpression {
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return 0;
//		}
//		@Override
//		public String print() {
//			return "0";
//		}
//	}
//
//	static class Defined extends FeatureExpression {
//		final String feature;
//
//		public Defined(String feature) {
//			this.feature = feature;
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return macros.get(feature) != null ? 1 : 0;
//		}
//
//		@Override
//		public String print() {
//			return "defined(" + feature + ")";
//		}
//
//	}
//
//	public static class Division extends BinaryFeatureExpression {
//
//		public Division(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"/");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			if (rhs.evaluate(macros) == 0) {
//				// error(op, "Division by zero");
//				return 0;
//			} else {
//				return lhs.evaluate(macros) / rhs.evaluate(macros);
//			}
//		}
//
//	}
//
//	public static class Equals extends BinaryFeatureExpression {
//
//		public Equals(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"==");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) == rhs.evaluate(macros) ? 1 : 0;
//		}
//	}
//
//	public static class GreaterThan extends BinaryFeatureExpression {
//
//		public GreaterThan(FeatureExpression left, FeatureExpression right) {
//			super(left, right,">");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) > rhs.evaluate(macros) ? 1 : 0;
//		}
//	}
//
//	public static class GreaterThanEquals extends BinaryFeatureExpression {
//
//		public GreaterThanEquals(FeatureExpression left, FeatureExpression right) {
//			super(left, right,">=");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) >= rhs.evaluate(macros) ? 1 : 0;
//		}
//	}
//
//	static class IntegerLit extends FeatureExpression {
//
//		final long val;
//
//		public IntegerLit(long longValue) {
//			val = longValue;
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return val;
//		}
//		
//		@Override
//		public String print() {
//			return Long.toString(val);
//		}
//
//	}
//
//	public static class LessThan extends BinaryFeatureExpression {
//
//		public LessThan(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"<");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) < rhs.evaluate(macros) ? 1 : 0;
//		}
//	}
//
//	public static class LessThanEquals extends BinaryFeatureExpression {
//
//		public LessThanEquals(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"<=");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) <= rhs.evaluate(macros) ? 1 : 0;
//		}
//	}
//
//	public static class Minus extends BinaryFeatureExpression {
//
//		public Minus(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"-");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) - rhs.evaluate(macros);
//		}
//	}
//
//	public static class Mult extends BinaryFeatureExpression {
//
//		public Mult(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"*");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) * rhs.evaluate(macros);
//		}
//	}
//
//	static class Neg extends FeatureExpression {
//		final FeatureExpression expr;
//
//		public Neg(FeatureExpression expr) {
//			this.expr = expr;
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return -expr.evaluate(macros);
//		}
//		
//		@Override
//		public String print() {
//			return "-"+expr.print();
//		}
//
//	}
//
//	public static class Not extends FeatureExpression {
//		final FeatureExpression expr;
//
//		public Not(FeatureExpression expr) {
//			this.expr = expr;
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return expr.evaluate(macros) == 0 ? 1 : 0;
//		}
//
//		@Override
//		public FeatureExpression simplify() {
//			if (expr instanceof Not)
//				return ((Not) expr).expr.simplify();
//			return super.simplify();
//		}
//
//		@Override
//		public String print() {
//			return "!" + expr;
//		}
//	}
//
//	public static class NotEquals extends BinaryFeatureExpression {
//
//		public NotEquals(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"!=");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) != rhs.evaluate(macros) ? 1 : 0;
//		}
//	}
//
//	public static class Or extends BinaryFeatureExpression {
//
//		public Or(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"||");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) != 0 || rhs.evaluate(macros) != 0 ? 1
//					: 0;
//		}
//		
//		@Override
//		public FeatureExpression simplify() {
//			if (lhs == DEAD)
//				return rhs.simplify();
//			if (rhs == DEAD)
//				return lhs.simplify();
//			if (lhs == BASE || rhs == BASE)
//				return BASE;
//			return super.simplify();
//		}
//	}
//
//	public static class Plus extends BinaryFeatureExpression {
//
//		public Plus(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"+");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) + rhs.evaluate(macros);
//		}
//	}
//
//	public static class Pwr extends BinaryFeatureExpression {
//
//		public Pwr(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"^");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) ^ rhs.evaluate(macros);
//		}
//	}
//
//	public static class ShiftLeft extends BinaryFeatureExpression {
//
//		public ShiftLeft(FeatureExpression left, FeatureExpression right) {
//			super(left, right,"<<");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros)<< rhs.evaluate(macros);
//		}
//	}
//
//	public static class ShiftRight extends BinaryFeatureExpression {
//
//		public ShiftRight(FeatureExpression left, FeatureExpression right) {
//			super(left, right,">>");
//		}
//
//		@Override
//		public long evaluate(MacroStorage macros) {
//			return lhs.evaluate(macros) >> rhs.evaluate(macros);
//		}
//	}
//
//	public static final FeatureExpression BASE = new BaseFeature();
//
//	public static final FeatureExpression DEAD = new DeadFeature();
//
//	abstract public long evaluate(MacroStorage macros);
//
//	public FeatureExpression simplify() {
//		return this;
//	}
//	
//	@Override
//	public String toString() {
//		return print();
//	}
//	protected abstract String print();
//}
