package pl.edu.pjwstk.interpreter.envs;

import edu.pjwstk.jps.ast.IExpression;
import edu.pjwstk.jps.ast.auxname.IAsExpression;
import edu.pjwstk.jps.ast.auxname.IGroupAsExpression;
import edu.pjwstk.jps.ast.binary.*;
import edu.pjwstk.jps.ast.terminal.*;
import edu.pjwstk.jps.ast.unary.*;
import edu.pjwstk.jps.datastore.*;
import edu.pjwstk.jps.interpreter.envs.IInterpreter;
import edu.pjwstk.jps.result.*;
import pl.edu.pjwstk.interpreter.exception.WrongTypeException;
import pl.edu.pjwstk.interpreter.qres.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class Interpreter implements IInterpreter {

    private QresStack stack;
    private ISBAStore store;

    protected Interpreter(QresStack stack, ISBAStore store) {
        stack = this.stack;
        store = this.store;
    }

    @Override
    public IAbstractQueryResult eval(IExpression queryTreeRoot) {
        return null;
    }

    @Override
    public void visitAsExpression(IAsExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitGroupAsExpression(IGroupAsExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitAllExpression(IForAllExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitAndExpression(IAndExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        if (!(left instanceof IBooleanResult && right instanceof IBooleanResult)) {
            throw new WrongTypeException("Only Boolean is allowed");
        }

        stack.push(new BooleanResult(((IBooleanResult) left).getValue() && ((IBooleanResult) right).getValue()));
    }

    @Override
    public void visitAnyExpression(IForAnyExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitCloseByExpression(ICloseByExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitCommaExpression(ICommaExpression expr) {
        BagResult eres = new BagResult();
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult e2Res = stack.pop();
        IAbstractQueryResult e1Res = stack.pop();

        for (ISingleResult e1 : getResultList(e1Res)) {
            for (ISingleResult e2 : getResultList(e2Res)) {
                List<ISingleResult> struct = new ArrayList<ISingleResult>();
                if (e1 instanceof IStructResult) {
                    struct.addAll(((IStructResult) e1).elements());
                } else {
                    struct.add(e1);
                }
                if (e2 instanceof IStructResult) {
                    struct.addAll(((IStructResult) e2).elements());
                } else {
                    struct.add(e2);
                }
                eres.getElements().add(new StructResult(struct));
            }
        }

        stack.push(eres);

    }

    @Override
    public void visitDivideExpression(IDivideExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        boolean isDoubleInstance = false;
        Double result = 0.0;
        if (left instanceof IIntegerResult) {
            result += ((IIntegerResult) left).getValue();
        } else if (left instanceof IDoubleResult) {
            result += ((IDoubleResult) left).getValue();
            isDoubleInstance = true;
        } else {
            throw new WrongTypeException("Only Integer or Double are allowed. " + left.getClass() + " was used");
        }

        if (right instanceof IIntegerResult) {
            int value = ((IIntegerResult) right).getValue();
            if (0 == value) {
                throw new ArithmeticException("Division by zero");
            }
            result /= value;
        } else if (right instanceof IDoubleResult) {
            Double value = ((IDoubleResult) right).getValue();
            if (0 == value) {
                throw new ArithmeticException("Division by zero");
            }
            result /= value;
            isDoubleInstance = true;
        } else {
            throw new WrongTypeException("Only Integer or Double are allowed. " + right.getClass() + " was used");
        }

        if (isDoubleInstance) {
            stack.push(new DoubleResult(result));
        } else {
            stack.push(new IntegerResult(result.intValue()));
        }
    }

    @Override
    public void visitDotExpression(IDotExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitEqualsExpression(IEqualsExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        stack.push(new BooleanResult(left.equals(right)));
    }

    @Override
    public void visitGreaterThanExpression(IGreaterThanExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        if (left instanceof IStringResult && right instanceof IStringResult) {
            int result = ((IStringResult) left).getValue().compareTo(((IStringResult) right).getValue());

            if (result < 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
            return;
        }

        if (left instanceof IIntegerResult && right instanceof IIntegerResult) {
            int result = ((IIntegerResult) left).getValue().compareTo(((IIntegerResult) right).getValue());

            if (result < 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
            return;
        }

        if (left instanceof IDoubleResult && right instanceof IDoubleResult) {
            int result = ((IDoubleResult) left).getValue().compareTo(((IDoubleResult) right).getValue());

            if (result < 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
        }
    }

    @Override
    public void visitGreaterOrEqualThanExpression(IGreaterOrEqualThanExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        if (left instanceof IStringResult && right instanceof IStringResult) {
            int result = ((IStringResult) left).getValue().compareTo(((IStringResult) right).getValue());

            if (result <= 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
            return;
        }

        if (left instanceof IIntegerResult && right instanceof IIntegerResult) {
            int result = ((IIntegerResult) left).getValue().compareTo(((IIntegerResult) right).getValue());

            if (result <= 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
            return;
        }

        if (left instanceof IDoubleResult && right instanceof IDoubleResult) {
            int result = ((IDoubleResult) left).getValue().compareTo(((IDoubleResult) right).getValue());

            if (result <= 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
        }
    }

    @Override
    public void visitInExpression(IInExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitIntersectExpression(IIntersectExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitJoinExpression(IJoinExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitLessOrEqualThanExpression(ILessOrEqualThanExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        if (left instanceof IStringResult && right instanceof IStringResult) {
            int result = ((IStringResult) left).getValue().compareTo(((IStringResult) right).getValue());

            if (result >= 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
            return;
        }

        if (left instanceof IIntegerResult && right instanceof IIntegerResult) {
            int result = ((IIntegerResult) left).getValue().compareTo(((IIntegerResult) right).getValue());

            if (result >= 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
            return;
        }

        if (left instanceof IDoubleResult && right instanceof IDoubleResult) {
            int result = ((IDoubleResult) left).getValue().compareTo(((IDoubleResult) right).getValue());

            if (result >= 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
        }
    }

    @Override
    public void visitLessThanExpression(ILessThanExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        if (left instanceof IStringResult && right instanceof IStringResult) {
            int result = ((IStringResult) left).getValue().compareTo(((IStringResult) right).getValue());

            if (result > 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
            return;
        }

        if (left instanceof IIntegerResult && right instanceof IIntegerResult) {
            int result = ((IIntegerResult) left).getValue().compareTo(((IIntegerResult) right).getValue());

            if (result > 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
            return;
        }

        if (left instanceof IDoubleResult && right instanceof IDoubleResult) {
            int result = ((IDoubleResult) left).getValue().compareTo(((IDoubleResult) right).getValue());

            if (result > 0) {
                stack.push(new BooleanResult(true));
            } else {
                stack.push(new BooleanResult(false));
            }
        }
    }

    @Override
    public void visitMinusExpression(IMinusExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        boolean isDoubleInstance = false;
        Double result = 0.0;
        if (left instanceof IIntegerResult) {
            result += ((IIntegerResult) left).getValue();
        } else if (left instanceof IDoubleResult) {
            result += ((IDoubleResult) left).getValue();
            isDoubleInstance = true;
        } else {
            throw new WrongTypeException("Only Integer or Double are allowed. " + left.getClass() + " was used");
        }

        if (right instanceof IIntegerResult) {
            result -= ((IIntegerResult) right).getValue();
        } else if (right instanceof IDoubleResult) {
            result -= ((IDoubleResult) right).getValue();
            isDoubleInstance = true;
        } else {
            throw new WrongTypeException("Only Integer or Double are allowed. " + right.getClass() + " was used");
        }

        if (isDoubleInstance) {
            stack.push(new DoubleResult(result));
        } else {
            stack.push(new IntegerResult(result.intValue()));
        }
    }

    @Override
    public void visitMinusSetExpression(IMinusSetExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();
    }

    @Override
    public void visitModuloExpression(IModuloExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitMultiplyExpression(IMultiplyExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        boolean isDoubleInstance = false;
        Double result = 0.0;
        if (left instanceof IIntegerResult) {
            result += ((IIntegerResult) left).getValue();
        } else if (left instanceof IDoubleResult) {
            result += ((IDoubleResult) left).getValue();
            isDoubleInstance = true;
        } else {
            throw new WrongTypeException("Only Integer or Double are allowed. " + left.getClass() + " was used");
        }

        if (right instanceof IIntegerResult) {
            result *= ((IIntegerResult) right).getValue();
        } else if (right instanceof IDoubleResult) {
            result *= ((IDoubleResult) right).getValue();
            isDoubleInstance = true;
        } else {
            throw new WrongTypeException("Only Integer or Double are allowed. " + right.getClass() + " was used");
        }

        if (isDoubleInstance) {
            stack.push(new DoubleResult(result));
        } else {
            stack.push(new IntegerResult(result.intValue()));
        }
    }

    @Override
    public void visitNotEqualsExpression(INotEqualsExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        stack.push(new BooleanResult(!left.equals(right)));
    }

    @Override
    public void visitOrderByExpression(IOrderByExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitOrExpression(IOrExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        if (!(left instanceof IBooleanResult && right instanceof IBooleanResult)) {
            throw new WrongTypeException("Only Boolean is allowed");
        }

        stack.push(new BooleanResult(((IBooleanResult) left).getValue() || ((IBooleanResult) right).getValue()));
    }

    @Override
    public void visitPlusExpression(IPlusExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        if (left instanceof IStringResult || right instanceof IStringResult) {
            stack.push(new StringResult(((IStringResult) left).getValue() + ((IStringResult) right).getValue()));
        }

        boolean isDoubleInstance = false;
        Double result = 0.0;
        if (left instanceof IIntegerResult) {
            result += ((IIntegerResult) left).getValue();
        } else if (left instanceof IDoubleResult) {
            result += ((IDoubleResult) left).getValue();
            isDoubleInstance = true;
        } else {
            throw new WrongTypeException("Only Integer or Double are allowed. " + left.getClass() + " was used");
        }

        if (right instanceof IIntegerResult) {
            result += ((IIntegerResult) right).getValue();
        } else if (right instanceof IDoubleResult) {
            result += ((IDoubleResult) right).getValue();
            isDoubleInstance = true;
        } else {
            throw new WrongTypeException("Only Integer or Double are allowed. " + right.getClass() + " was used");
        }

        if (isDoubleInstance) {
            stack.push(new DoubleResult(result));
        } else {
            stack.push(new IntegerResult(result.intValue()));
        }
    }

    @Override
    public void visitUnionExpression(IUnionExpression expr) {

        BagResult eres = new BagResult();

        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        eres.getElements().addAll(getResultList(left));
        eres.getElements().addAll(getResultList(right));
        stack.push(eres);
    }

    @Override
    public void visitWhereExpression(IWhereExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitXORExpression(IXORExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);

        IAbstractQueryResult right = stack.pop();
        IAbstractQueryResult left = stack.pop();

        try {
            right = getResult(right);
            left = getResult(left);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        left = doDereference(left);
        right = doDereference(right);

        if (!(left instanceof IBooleanResult && right instanceof IBooleanResult)) {
            throw new WrongTypeException("Only Boolean is allowed");
        }

        if (((IBooleanResult) right).getValue() && !((IBooleanResult) left).getValue()) {
            stack.push(new BooleanResult(true));
            return;
        }

        if (!((IBooleanResult) right).getValue() && ((IBooleanResult) left).getValue()) {
            stack.push(new BooleanResult(true));
            return;
        }

        stack.push(new BooleanResult(false));
    }

    @Override
    public void visitBooleanTerminal(IBooleanTerminal expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitDoubleTerminal(IDoubleTerminal expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitIntegerTerminal(IIntegerTerminal expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitNameTerminal(INameTerminal expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitStringTerminal(IStringTerminal expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitBagExpression(IBagExpression expr) {

        BagResult eres = new BagResult();
        expr.getInnerExpression().accept(this);

        IAbstractQueryResult result = stack.pop();

        eres.getElements().addAll(getResultList(result));

        stack.push(eres);
    }

    @Override
    public void visitCountExpression(ICountExpression expr) {
        expr.getInnerExpression().accept(this);

        IAbstractQueryResult result = stack.pop();
        List<ISingleResult> eres = getResultList(result);

        stack.push(new IntegerResult(eres.size()));
    }

    @Override
    public void visitExistsExpression(IExistsExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitMaxExpression(IMaxExpression expr) {
        expr.getInnerExpression().accept(this);

        IAbstractQueryResult result = stack.pop();
        List<ISingleResult> eres = getResultList(result);

        if (0 == eres.size()) {
            throw new RuntimeException("Empty collection!");
        }

        boolean isDoubleInstance = false;
        double max = Double.MIN_VALUE;

        for (ISingleResult element : eres) {
            element = (ISingleResult) doDereference(element);

            if (element instanceof IIntegerResult) {
                int value = ((IIntegerResult) element).getValue();
                if (max < value) {
                    max = value;
                }
            } else if (element instanceof IDoubleResult) {
                Double value = ((IDoubleResult) element).getValue();
                isDoubleInstance = true;
                if (max < value) {
                    max = value;
                }
            } else {
                throw new WrongTypeException("Only Integer or Double are allowed. " + element.getClass() + " was used");
            }
        }

        if (isDoubleInstance) {
            stack.push(new DoubleResult(max));
        } else {
            stack.push(new IntegerResult((int) max));
        }
    }

    @Override
    public void visitMinExpression(IMinExpression expr) {
        expr.getInnerExpression().accept(this);

        IAbstractQueryResult result = stack.pop();
        List<ISingleResult> eres = getResultList(result);

        if (0 == eres.size()) {
            throw new RuntimeException("Empty collection!");
        }

        boolean isDoubleInstance = false;
        double min = Double.MAX_VALUE;

        for (ISingleResult element : eres) {
            element = (ISingleResult) doDereference(element);

            if (element instanceof IIntegerResult) {
                int value = ((IIntegerResult) element).getValue();
                if (min > value) {
                    min = value;
                }
            } else if (element instanceof IDoubleResult) {
                Double value = ((IDoubleResult) element).getValue();
                isDoubleInstance = true;
                if (min > value) {
                    min = value;
                }
            } else {
                throw new WrongTypeException("Only Integer or Double are allowed. " + element.getClass() + " was used");
            }
        }

        if (isDoubleInstance) {
            stack.push(new DoubleResult(min));
        } else {
            stack.push(new IntegerResult((int) min));
        }
    }

    @Override
    public void visitNotExpression(INotExpression expr) {
        expr.getInnerExpression().accept(this);

        IAbstractQueryResult result = stack.pop();

        try {
            result = getResult(result);
        } catch (RuntimeException e) {
            throw new WrongTypeException("Unable to retrieve a single value");
        }

        result = doDereference(result);

        if (result instanceof IBooleanResult) {
            stack.push(new BooleanResult(((IBooleanResult) result).getValue()));
        }

        throw new WrongTypeException("Illegal type for operation");
    }

    @Override
    public void visitStructExpression(IStructExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitSumExpression(ISumExpression expr) {
        expr.getInnerExpression().accept(this);

        IAbstractQueryResult result = stack.pop();
        List<ISingleResult> eres = getResultList(result);

        boolean isDoubleInstance = false;
        Double sum = 0.0;

        for (ISingleResult element : eres) {
            element = (ISingleResult) doDereference(element);

            if (element instanceof IIntegerResult) {
                sum += ((IIntegerResult) element).getValue();
            } else if (element instanceof IDoubleResult) {
                sum += ((IDoubleResult) element).getValue();
                isDoubleInstance = true;
            } else {
                throw new WrongTypeException("Only Integer or Double are allowed. " + element.getClass() + " was used");
            }
        }

        if (isDoubleInstance) {
            stack.push(new DoubleResult(sum));
        } else {
            stack.push(new IntegerResult(sum.intValue()));
        }
    }

    @Override
    public void visitUniqueExpression(IUniqueExpression expr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitAvgExpression(IAvgExpression expr) {
    }

    private ISingleResult getResult(IAbstractQueryResult result) {
        if (result instanceof ISingleResult) {
            if (result instanceof IStructResult) {
                List<ISingleResult> list = ((IStructResult) result).elements();
                if (list.size() == 1) {
                    ISingleResult singleResult = list.get(0);

                    if (singleResult instanceof IStructResult) {
                        return getResult(singleResult);
                    }

                    return singleResult;
                } else {
                    throw new WrongTypeException();
                }
            }

            return (ISingleResult) result;
        }

        if (result instanceof IBagResult) {
            Collection<ISingleResult> collection = ((IBagResult) result).getElements();
            Iterator<ISingleResult> iterator = collection.iterator();
            if (iterator.hasNext()) {
                ISingleResult singleResult = iterator.next();
                if (singleResult instanceof IStructResult) {
                    return getResult(singleResult);
                }
                return singleResult;
            } else {
                throw new WrongTypeException();
            }
        }

        if (result instanceof ISequenceResult) {
            List<ISingleResult> list = ((ISequenceResult) result).getElements();
            if (list.size() == 1) {
                ISingleResult singleResult = list.get(0);

                if (singleResult instanceof IStructResult) {
                    return getResult(singleResult);
                }

                return singleResult;
            } else {
                throw new WrongTypeException();
            }
        }
        throw new WrongTypeException();
    }

    private List<ISingleResult> getResultList(IAbstractQueryResult result) {
        List<ISingleResult> results = new ArrayList<ISingleResult>();

        if (result instanceof ISingleResult) {
            if (result instanceof IStructResult) {
                results.addAll(((IStructResult) result).elements());
            } else {
                results.add((SingleResult) result);
            }
        }

        if (result instanceof IBagResult) {
            results.addAll(((IBagResult) result).getElements());
        }

        if (result instanceof ISequenceResult) {
            results.addAll(((ISequenceResult) result).getElements());
        }

        return results;
    }

    private IAbstractQueryResult doDereference(IAbstractQueryResult result) {

        ISingleResult singleResult;
        try {
            singleResult = getResult(result);
        } catch (RuntimeException e) {
            return result;
        }

        if (singleResult instanceof IReferenceResult) {
            IOID oid = ((IReferenceResult) singleResult).getOIDValue();
            ISBAObject object = store.retrieve(oid);

            if (object instanceof IBooleanObject) {
                return new BooleanResult(((IBooleanObject) object).getValue());
            }

            if (object instanceof IDoubleResult) {
                return new DoubleResult(((IDoubleResult) object).getValue());
            }

            if (object instanceof IIntegerObject) {
                return new IntegerResult(((IIntegerObject) object).getValue());
            }

            if (object instanceof IStringObject) {
                return new StringResult(((IStringObject) object).getValue());
            }

            return new ReferenceResult(object.getOID());
        }

        return singleResult;

    }
}
