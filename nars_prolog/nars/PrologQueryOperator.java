package nars;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nars.core.Parameters;
import nars.entity.Task;
import nars.io.Symbols;
import nars.io.Texts;
import nars.language.Inheritance;
import nars.language.Product;
import nars.language.Term;
import nars.operator.Operation;
import nars.operator.Operator;
import nars.prolog.Int;
import nars.prolog.InvalidTheoryException;
import nars.prolog.NoSolutionException;
import nars.prolog.Prolog;
import nars.prolog.SolveInfo;
import nars.prolog.Struct;
import nars.prolog.Theory;
import nars.prolog.Var;
import nars.core.Memory;
import nars.prolog.MalformedGoalException;

/**
 * 
 * 
 * Usage:
 * (^prologQuery, "database(Query,0).", prolog0, "Query", #0)
 * 
 * @author me
 */


public class PrologQueryOperator extends Operator {
    private final PrologContext context;

    
    
    public PrologQueryOperator(PrologContext context) {
        super("^prologQuery");
        this.context = context;
    }

    @Override
    protected List<Task> execute(Operation operation, Term[] args, Memory memory) {
        if (args.length < 2) {
            // TODO< error report >
            return null;
        }
        
        if (((args.length - 2) % 2) != 0) {
            // TODO< error report >
            return null;
        }
        
        // check if 1st parameter is a string
        if (!(args[0] instanceof Term)) {
            // TODO< report error >
            return null;
        }
        
        // check if 2nd parameter is a string
        if (!(args[1] instanceof Term)) {
            // TODO< report error >
            return null;
        }
        
        
        // try to retrive the prolog interpreter instance by the name of it
        if (!context.prologInterpreters.containsKey(args[1])) {
            // TODO< report error >
            return null;
        }
        
        Prolog prologInterpreter = context.prologInterpreters.get(args[1]);
        
        
        
        Term queryTerm = (Term)args[0];
        String query = getStringOfTerm(queryTerm);
        
        
        
        // get all variablenames
        // prolog must resolve the variables and assign values to them
        String[] variableNames = getVariableNamesOfArgs(args);
        
        // execute
        nars.prolog.Term[] resolvedVariableValues = prologParseAndExecuteAndDereferenceInput(prologInterpreter, query, variableNames);
       
       
       
        // TODO< convert the result from the prolog to strings >
        memory.output(Prolog.class, query + " | TODO");
        //memory.output(Prolog.class, query + " | " + result);
       
        // set result values
        Term[] resultTerms = getResultVariablesFromPrologVariables(resolvedVariableValues, args, memory);
       
        // create evaluation result
        int i;
        
        Term[] resultInnerProductTerms = new Term[2 + resultTerms.length*2];
        resultInnerProductTerms[0] = args[0];
        resultInnerProductTerms[1] = args[1];
        
        for (i = 0; i < resultTerms.length; i++ ) {
            resultInnerProductTerms[2+i*2+0] = args[2+i*2];
            resultInnerProductTerms[2+i*2+1] = resultTerms[i];
        }
        
        Inheritance operatorInheritance = Operation.make(
            Product.make(resultInnerProductTerms, memory),
            this,
            memory
        );
        
        //  create the nars result and return it
        Inheritance resultInheritance = Inheritance.make(
            operatorInheritance,
            new Term("prolog_evaluation"),
            memory
        );
        
        
        memory.output(Task.class, resultInheritance);
        
        ArrayList<Task> results = new ArrayList<>(1);
        results.add(memory.newTask(resultInheritance, Symbols.JUDGMENT_MARK, 1f, 0.99f, Parameters.DEFAULT_JUDGMENT_PRIORITY, Parameters.DEFAULT_JUDGMENT_DURABILITY));
               
        return results;
    }
    
    static private String[] getVariableNamesOfArgs(Term[] args) {
        int numberOfVariables = (args.length - 2) / 2;
        int variableI;
       
        String[] variableNames = new String[numberOfVariables];
       
        for( variableI = 0; variableI < numberOfVariables; variableI++ ) {
            Term termWithVariableName = args[2+2*variableI];
           
            if( !(termWithVariableName instanceof Term) ) {
                throw new RuntimeException("Result Variable Name is not an term!");
            }
           
            variableNames[variableI] = getStringOfTerm(termWithVariableName);
        }
       
        return variableNames;
    }
   
    // tries to convert the Term (which must be a string) to a string with the content
    static public String getStringOfTerm(Term term) {
        // escape sign codes
        String string = term.name().toString();
        string = Texts.unescape(string).toString();
        if (string.charAt(0) != '"') {
            throw new RuntimeException("term is not a string as expected!");
        }
       
        string = string.substring(1, string.length()-1);
       
        // because the text can contain quoted text
        string = unescape(string);
       
        return string;
    }
    
    
    static private Term[] getResultVariablesFromPrologVariables(nars.prolog.Term[] prologVariables, Term[] args, Memory memory) {
        int numberOfVariables = (args.length - 2) / 2;
        int variableI;
       
        Term[] resultTerms = new Term[numberOfVariables];
       
        for( variableI = 0; variableI < numberOfVariables; variableI++ ) {
            resultTerms[variableI] = convertPrologTermToNarsTermRecursive(prologVariables[variableI], memory);
        }
        
        return resultTerms;
    }
    
    // TODO< return status/ error/sucess >
    private nars.prolog.Term[] prologParseAndExecuteAndDereferenceInput(Prolog prolog, String goal, String[] dereferencingVariableNames) {
        SolveInfo solution;
        
        try {
            solution = prolog.solve(goal);
        }
        catch (MalformedGoalException exception) {
            // TODO< error handing >
            throw new RuntimeException("MalformedGoalException");
        }
        
        if (solution == null ) {
            return null; // TODO error
        }
        
        if (!solution.isSuccess()) {
            throw new RuntimeException("Query was not successful");
        }
        
        nars.prolog.Term solutionTerm;
        
        try {
            solutionTerm = solution.getSolution();
        }
        catch (NoSolutionException exception) {
            throw new RuntimeException("Query had no solution");
        }
        
        nars.prolog.Term[] resultArray;

        resultArray = new nars.prolog.Term[dereferencingVariableNames.length];
        

        int variableI;

        for( variableI = 0; variableI < dereferencingVariableNames.length; variableI++ ) {
            // get variable and dereference
            //  get the variable which has the name
            Var variableTerm = getVariableByNameRecursive(solutionTerm, dereferencingVariableNames[variableI]);

            if( variableTerm == null )
            {
                return null; // error
            }

            variableTerm.resolveTerm();
            nars.prolog.Term dereferencedTerm = variableTerm.getTerm();

            resultArray[variableI] = dereferencedTerm;
        }
        
        
        return resultArray;
    }
    
    // tries to convert a prolog term to a nars term
    // a chained Prolog Struct List will be converted to a Nars-Product (because it maps good to a list and nars can kinda understand it)
    // throws a exception if the term type is not handable
    static private Term convertPrologTermToNarsTermRecursive(nars.prolog.Term prologTerm, Memory memory) {
        if( prologTerm instanceof Int ) {
            Int prologIntegerTerm = (Int)prologTerm;

            return new Term(String.valueOf(prologIntegerTerm.intValue()));
        }
        else if( prologTerm instanceof nars.prolog.Double ) {
            nars.prolog.Double prologDoubleTerm = (nars.prolog.Double)prologTerm;

            return new Term(String.valueOf(prologDoubleTerm.floatValue()));
        }
        else if( prologTerm instanceof nars.prolog.Float ) {
            nars.prolog.Float prologFloatTerm = (nars.prolog.Float)prologTerm;

            return new Term(String.valueOf(prologFloatTerm.floatValue()));
        }
        else if( prologTerm instanceof Struct ) {
            Struct structTerm = (Struct)prologTerm;

            // check if it is a string (has arity 0) or a list/struct (arity == 2 because lists are composed out of 2 tuples)
            if (structTerm.getArity() == 0) {
                String variableAsString = structTerm.getName();

                return new Term("\"" + variableAsString + "\"");
            }
            else if (structTerm.getArity() == 2 && structTerm.getName().equals(".")) {
                // convert the result array to a nars thingy
                ArrayList<nars.prolog.Term> structAsList = convertChainedStructToList(structTerm);
                
                // convert the list to a nars product wth the cconverted elements
                Term[] innerProductTerms = new Term[structAsList.size()];
                
                for (int i = 0; i < structAsList.size(); i++) {
                    innerProductTerms[i] = convertPrologTermToNarsTermRecursive(structAsList.get(i), memory);
                }
                
                // is wraped up in a inheritance because there can also exist operators
                // and it is better understandable by nars or other operators
                return Inheritance.make(
                    Product.make(innerProductTerms, memory),
                    new Term("prolog_list"),
                    memory
                );
            }
            else {
                // must be a operation
                // so we convert the operation to a nars term
                // in the form
                // <(*, operationName, param0, param1) --> prolog_operation>
                
                String operationName = structTerm.getName();
                
                // convert the result array to a nars thingy
                ArrayList<nars.prolog.Term> parametersAsList = convertChainedStructToList(structTerm);
                
                // convert the list to a nars product wth the cconverted elements
                Term[] innerProductTerms = new Term[1+parametersAsList.size()];
                
                for (int i = 0; i < parametersAsList.size(); i++) {
                    innerProductTerms[i+1] = convertPrologTermToNarsTermRecursive(parametersAsList.get(i), memory);
                }
                
                innerProductTerms[0] = new Term(operationName);
                
                // is wraped up in a inheritance because there can also exist operators
                // and it is better understandable by nars or other operators
                return Inheritance.make(
                    Product.make(innerProductTerms, memory),
                    new Term("prolog_operation"),
                    memory
                );
            }

            // unreachable
        }

        throw new RuntimeException("Unhandled type of result variable");
    }
    
    // tries to get a variable from a term by name
    // returns null if it wasn't found
    static private nars.prolog.Var getVariableByNameRecursive(nars.prolog.Term term, String name) {
        if( term instanceof Struct ) {
            Struct s = (Struct)term;
            for (int i = 0; i < s.getArity(); i++) {
                nars.prolog.Term iterationTerm = s.getArg(i);
                nars.prolog.Var result = getVariableByNameRecursive(iterationTerm, name);
               
                if( result != null ) {
                    return result;
                }
            }
           
            return null;
        }
        else if( term instanceof nars.prolog.Var ) {
            nars.prolog.Var termAsVar = (nars.prolog.Var)term;
            String nameOfVar = termAsVar.name().toString();
            
            if( nameOfVar.equals(name) ) {
                return termAsVar;
            }
           
            return null;
        }
        else if( term instanceof Int ) {
            return null;
        }
        else if( term instanceof nars.prolog.Double ) {
            return null;
        }
       
        throw new RuntimeException("Internal Error: Unknown prolog term!");
    }
   
    // converts a chained compound term (which contains oher compound terms) to a list
    static private ArrayList<nars.prolog.Term> convertChainedStructToList(Struct structTerm) {
        ArrayList<nars.prolog.Term> result = new ArrayList<>();
       
        Struct currentCompundTerm = structTerm;
       
        for(;;) {
            if( currentCompundTerm.getArity() == 0 ) {
                // end is reached
                break;
            }
            else if( currentCompundTerm.getArity() != 2 ) {
                throw new RuntimeException("Compound must have two or zero arguments!");
            }
           
            result.add(currentCompundTerm.getArg(0));
           
            nars.prolog.Term arg2 = currentCompundTerm.getArg(1);
            
            if (arg2.isAtom()) {
                Struct atomTerm = (Struct)arg2;
               
                if (atomTerm.getName().equals("[]")) {
                    // this is the last element of the list, we are done
                    break;
                }
                else {
                    // we are here if we converted a parameterlist of a function to a list, append the last argument which is this
                    // and return
                    
                    result.add(atomTerm);
                    
                    break;
                }
                
            }
           
            if (!(arg2 instanceof Struct)) {
                throw new RuntimeException("Second argument of Struct term is expected to be a Struct term!");
            }
           
            currentCompundTerm = (Struct)(arg2);
        }
       
        return result;
    }
   
    // tries to convert a list with integer terms to an string
    // checks also if the signs are correct
    // throws an ConversionFailedException if the conversion is not possible
    static private String tryToConvertPrologListToString(ArrayList<nars.prolog.Term> array) {
        String result = "";
       
        for( nars.prolog.Term iterationTerm : array ) {
            if( !(iterationTerm instanceof Int) ) {
                throw new PrologTheoryStringOperator.ConversionFailedException();
            }
           
            Int integerTerm = (Int)iterationTerm;
            int integer = integerTerm.intValue();
           
            if( integer > 127 || integer < 0 ) {
                throw new PrologTheoryStringOperator.ConversionFailedException();
            }
           
            result += Character.toString((char)integer);
        }
       
        return result;
    }
   
    
    
    // TODO< move into prolog utils >
    private static String unescape(String text) {
        return text.replace("\\\"", "\"");
    }   
    
    
}
