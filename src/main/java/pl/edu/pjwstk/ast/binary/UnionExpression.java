package pl.edu.pjwstk.ast.binary;

import edu.pjwstk.jps.ast.IExpression;
import edu.pjwstk.jps.ast.binary.IUnionExpression;
import edu.pjwstk.jps.visitor.ASTVisitor;

public class UnionExpression extends BinaryExpression implements IUnionExpression {

    public UnionExpression(IExpression leftExpression, IExpression rightExpression) {
        super(leftExpression, rightExpression);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitUnionExpression(this);
    }
}
