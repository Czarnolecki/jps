package pl.edu.pjwstk.ast.binary;

import edu.pjwstk.jps.ast.IExpression;
import edu.pjwstk.jps.ast.binary.ICommaExpression;
import edu.pjwstk.jps.ast.binary.IInExpression;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr Sukiennik
 * Date: 04.10.13
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public class CommaExpression extends BinaryExpression implements ICommaExpression {
    public CommaExpression(IExpression leftExpression, IExpression rightExpression) {
        super(leftExpression, rightExpression);
    }


}
