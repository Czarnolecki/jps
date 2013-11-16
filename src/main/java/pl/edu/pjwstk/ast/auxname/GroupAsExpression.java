package pl.edu.pjwstk.ast.auxname;

import edu.pjwstk.jps.ast.IExpression;
import edu.pjwstk.jps.ast.auxname.IGroupAsExpression;
import edu.pjwstk.jps.visitor.ASTVisitor;


public class GroupAsExpression implements IGroupAsExpression {

    String auxiliaryName;
    IExpression innerExpression;

    public GroupAsExpression(String auxiliaryName, IExpression innerExpression) {
        this.auxiliaryName = auxiliaryName;
        this.innerExpression = innerExpression;
    }

    @Override
    public String getAuxiliaryName() {
        return auxiliaryName;
    }

    @Override
    public IExpression getInnerExpression() {
        return innerExpression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitGroupAsExpression(this);
    }
}
